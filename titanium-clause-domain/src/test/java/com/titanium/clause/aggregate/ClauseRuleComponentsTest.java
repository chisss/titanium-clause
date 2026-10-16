package com.titanium.clause.aggregate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.axonframework.test.aggregate.AggregateTestFixture;
import org.axonframework.test.aggregate.FixtureConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.titanium.clause.command.AddCoverageCommand;
import com.titanium.clause.command.AddExclusionCommand;
import com.titanium.clause.command.ChangeClauseStatusCommand;
import com.titanium.clause.command.RemoveCoverageCommand;
import com.titanium.clause.command.RemoveExclusionCommand;
import com.titanium.clause.command.SetClaimRuleCommand;
import com.titanium.clause.command.SetContractChangeRuleCommand;
import com.titanium.clause.command.SetPremiumRuleCommand;
import com.titanium.clause.command.UpdateClauseCommand;
import com.titanium.clause.command.UpdateCoverageCommand;
import com.titanium.clause.common.enums.CoverageType;
import com.titanium.clause.common.enums.ExclusionType;
import com.titanium.clause.common.enums.RenewalType;
import com.titanium.clause.common.exception.ClauseOperationNotAllowedException;
import com.titanium.clause.entity.ClaimEvent;
import com.titanium.clause.entity.ClaimRule;
import com.titanium.clause.entity.ContractChangeRule;
import com.titanium.clause.entity.Coverage;
import com.titanium.clause.entity.Exclusion;
import com.titanium.clause.entity.PremiumRule;
import com.titanium.clause.event.ClaimRuleSetEvent;
import com.titanium.clause.event.ClauseCreatedEvent;
import com.titanium.clause.event.ClauseStatusChangedEvent;
import com.titanium.clause.event.ClauseSubmittedForApprovalEvent;
import com.titanium.clause.event.ClauseUpdatedEvent;
import com.titanium.clause.event.ContractChangeRuleSetEvent;
import com.titanium.clause.event.CoverageAddedEvent;
import com.titanium.clause.event.CoverageRemovedEvent;
import com.titanium.clause.event.ExclusionAddedEvent;
import com.titanium.clause.event.ExclusionRemovedEvent;
import com.titanium.clause.event.PremiumRuleSetEvent;
import com.titanium.clause.valueobject.AgeGenderRate;
import com.titanium.clause.valueobject.ClauseCode;
import com.titanium.clause.valueobject.ClauseId;
import com.titanium.clause.valueobject.ClauseName;
import com.titanium.clause.valueobject.CoverageId;
import com.titanium.clause.valueobject.CoverageTrigger;
import com.titanium.clause.valueobject.Deductible;
import com.titanium.clause.valueobject.ExclusionId;
import com.titanium.clause.valueobject.PayoutRule;
import com.titanium.clause.valueobject.Reimbursement;
import com.titanium.clause.valueobject.Version;
import com.titanium.metadata.enums.CommonStatus;
import com.titanium.metadata.enums.clause.ClauseEnum;
import com.titanium.metadata.enums.insurance.InsuranceProductType;

/**
 * 规则组件成员实体（{@link ClauseRuleComponents}）测试
 * <p>
 * AXN-02（m26-03）将八类规则组件命令与状态自 {@link Clause} 聚合根下沉至 {@code @AggregateMember} 成员实体，
 * 本测试锁死下沉后的行为等价性与新机制的牙口：① 八类组件命令经 Axon 按命令类型路由到成员并被处理；
 * ② 成员状态镜像 {@code status} 随根的每次状态迁移同步——非 DRAFT 时组件命令一律被拒（漏同步则静默放行、
 * 无异常可察，是本次下沉最易回归的点，故按三条迁移路径分别断言）；③ 组件事件的业务状态由成员重建
 * （根侧同名处理器只维护审计字段，双监听为设计意图）；④ {@code ClauseUpdatedEvent} 载荷中 null 表示
 * 「本次未变更」的保留语义由成员承接；⑤ 对外 getter 仍为不可变视图（DSN-02）；
 * ⑥ {@code validateClaim} 经成员读取规则组件。
 * </p>
 * <p>
 * 聚合命令处理器内部用 {@link LocalDateTime#now()} 生成时间戳，事件比对时忽略各组件的 {@code updatedAt}。
 * </p>
 */
class ClauseRuleComponentsTest {

    private FixtureConfiguration<Clause> fixture;

    private static final ClauseId   CLAUSE_ID     = ClauseId.fromString("clause-001");
    private static final String     TENANT_ID     = "tenant-001";
    private static final CoverageId COVERAGE_ID   = CoverageId.fromString("cov-001");
    private static final ExclusionId EXCLUSION_ID = ExclusionId.fromString("exc-001");

    private static final ClauseCode CLAUSE_CODE = ClauseCode.fromString("CL-001");
    private static final ClauseName CLAUSE_NAME = ClauseName.fromString("测试条款");

    private static final LocalDateTime EFFECTIVE_DATE = LocalDateTime.of(2026, 9, 1, 0, 0);
    private static final LocalDateTime EXPIRY_DATE    = LocalDateTime.of(2027, 8, 31, 0, 0);
    private static final LocalDateTime CLAIM_TIME     = LocalDateTime.of(2026, 10, 16, 0, 0);

    /** 触发条件为 DEATH 的保险责任（与下方理赔事件的 triggerCondition 匹配） */
    private static final Coverage COVERAGE = coverageTriggeredByString("DEATH");

    /** 免责规则编码 EX-HIT：理赔事件 otherInfo 含该编码即命中 */
    private static final Exclusion EXCLUSION = new Exclusion(EXCLUSION_ID, "既往症免责", CommonStatus.ENABLED,
            ExclusionType.PRE_EXISTING, "EX-HIT", null, Boolean.FALSE, EFFECTIVE_DATE, EFFECTIVE_DATE);

    private static final PremiumRule PREMIUM_RULE = fixedMethodPremiumRule();

    private static final ClaimRule CLAIM_RULE = claimRuleWithReportDeadline(30);

    private static final ContractChangeRule CONTRACT_CHANGE_RULE = unsetContractChangeRule();

    /** 事件载荷中「规则组件未提供」的具名保留值：null 表示本次未设置 / 未变更，由聚合按保留语义处理 */
    private static final PremiumRule NO_PREMIUM_RULE = null;
    private static final ClaimRule NO_CLAIM_RULE = null;
    private static final ContractChangeRule NO_CONTRACT_CHANGE_RULE = null;

    /** 事件载荷中「集合未提供」的具名保留值：null 表示本次未变更 */
    private static final Map<CoverageId, Coverage> NO_COVERAGES = null;
    private static final Map<ExclusionId, Exclusion> NO_EXCLUSIONS = null;

    /** 命中保险责任的理赔事件（状态/时间/触发条件均匹配，未命中任何免责） */
    private static final ClaimEvent MATCHING_CLAIM_EVENT = new ClaimEvent("claim-001", "DEATH", "10000", null,
            "无附加信息", CLAIM_TIME);

    /** 命中免责规则的理赔事件（otherInfo 含免责规则编码 EX-HIT） */
    private static final ClaimEvent EXCLUDED_CLAIM_EVENT = new ClaimEvent("claim-002", "DEATH", "10000", null, "EX-HIT",
            CLAIM_TIME);

    /** 时间戳由聚合内部 now() 生成、比对时须忽略的事件类型 */
    private static final Class<?>[] EVENTS_WITH_GENERATED_TIMESTAMP = { CoverageAddedEvent.class,
            CoverageRemovedEvent.class, ExclusionAddedEvent.class, ExclusionRemovedEvent.class,
            PremiumRuleSetEvent.class, ClaimRuleSetEvent.class, ContractChangeRuleSetEvent.class,
            ClauseUpdatedEvent.class };

    @BeforeEach
    void setUp() {
        fixture = new AggregateTestFixture<>(Clause.class);
        for (Class<?> eventType : EVENTS_WITH_GENERATED_TIMESTAMP) {
            fixture.registerIgnoredField(eventType, "updatedAt");
        }
    }

    // ==================== 组件夹具（具名工厂：未设置参数逐个具名赋值，避免连续裸 null） ====================

    /**
     * 构造「仅字符串触发条件有效、其余参数未设置」的保险责任。
     * <p>
     * 未设置参数逐个具名赋值而非写作连续裸 {@code null} 序列：「连续 3+ 裸 null 实参」使读者无法把
     * null 对应回 record 参数名，增删参数后 null 错位编译照样通过（NullAlignmentUsageGuardTest 口径）。
     * </p>
     *
     * @param triggerCondition 触发条件字符串（与理赔事件比对）
     * @return 保险责任实例
     */
    private static Coverage coverageTriggeredByString(String triggerCondition) {
        String premiumRate = null;              // 保险费率：本用例不适用
        BigDecimal coverageAmount = null;       // 保险金额：本用例不适用
        String payoutRule = null;               // 赔付规则文本（旧版）：本用例不适用
        CoverageId mainCoverageId = null;       // 主险责任：本责任不挂靠主险
        CoverageTrigger trigger = null;         // 结构化触发：本用例走字符串比对路径
        PayoutRule structuredPayoutRule = null; // 结构化赔付规则：本用例不适用
        return new Coverage(COVERAGE_ID, "C001", "身故责任", premiumRate, "身故保障责任", CommonStatus.ENABLED,
                CoverageType.DEATH, coverageAmount, triggerCondition, payoutRule, Boolean.FALSE, mainCoverageId,
                trigger, structuredPayoutRule, EFFECTIVE_DATE, EFFECTIVE_DATE);
    }

    /**
     * 构造「仅计算方式为 FIXED、其余参数未设置」的缴费规则（未设置参数逐个具名赋值，理由同上）。
     *
     * @return 缴费规则实例
     */
    private static PremiumRule fixedMethodPremiumRule() {
        BigDecimal basePremium = null;                          // 基础保费：未设置
        BigDecimal premiumRate = null;                          // 保费费率：未设置
        String paymentMethod = null;                            // 缴费方式：未设置
        Integer paymentTerm = null;                             // 缴费年限：未设置
        Integer gracePeriodDays = null;                         // 宽限天数：未设置
        BigDecimal baseRate = null;                             // 基础费率：未设置
        List<AgeGenderRate> ageGenderRates = null;              // 年龄性别费率表：未设置（紧凑构造器归一化为空列表）
        BigDecimal ncdCoefficient = null;                       // NCD 系数：未设置
        Map<String, BigDecimal> occupationCoefficients = null;  // 职业系数：未设置
        String ruleSetCode = null;                              // 规则集编码：未设置
        String tableCode = null;                                // 费率表编码：未设置
        String tableVersion = null;                             // 费率表版本：未设置
        return new PremiumRule("FIXED", basePremium, premiumRate, paymentMethod, paymentTerm, gracePeriodDays,
                baseRate, ageGenderRates, ncdCoefficient, occupationCoefficients, ruleSetCode, tableCode,
                tableVersion, EFFECTIVE_DATE, EFFECTIVE_DATE);
    }

    /**
     * 构造「仅报案时限有效、其余参数未设置」的理赔规则（未设置参数逐个具名赋值，理由同上）。
     *
     * @param reportDeadlineDays 报案时限天数
     * @return 理赔规则实例
     */
    private static ClaimRule claimRuleWithReportDeadline(int reportDeadlineDays) {
        String requiredMaterials = null;        // 必备材料：未设置
        Integer settlementPeriodDays = null;    // 结案时限：未设置
        String payoutRatio = null;              // 赔付比例：未设置
        String deductibleAmount = null;         // 免赔额（文本）：未设置
        Deductible deductible = null;           // 结构化免赔：未设置
        Reimbursement reimbursement = null;     // 结构化赔付：未设置
        String ruleSetCode = null;              // 规则集编码：未设置
        return new ClaimRule(reportDeadlineDays, requiredMaterials, settlementPeriodDays, payoutRatio,
                deductibleAmount, deductible, reimbursement, ruleSetCode, EFFECTIVE_DATE, EFFECTIVE_DATE);
    }

    /**
     * 构造「全部业务参数未设置、仅有效期有效」的合同变更规则（未设置参数逐个具名赋值，理由同上）。
     *
     * @return 合同变更规则实例
     */
    private static ContractChangeRule unsetContractChangeRule() {
        String surrenderCashValueRule = null;   // 退保现金价值规则：未设置
        RenewalType renewalType = null;         // 续保类型：未设置
        String reinstatementCondition = null;   // 复效条件：未设置
        Integer waitingPeriodDays = null;       // 等待期天数：未设置
        Integer freeLookPeriodDays = null;      // 犹豫期天数：未设置
        return new ContractChangeRule(surrenderCashValueRule, renewalType, reinstatementCondition,
                waitingPeriodDays, freeLookPeriodDays, EFFECTIVE_DATE, EFFECTIVE_DATE);
    }

    /** 不带规则组件的已创建（DRAFT）条款事件 */
    private ClauseCreatedEvent draftCreatedEvent() {
        return draftCreatedEvent(Map.of(), Map.of());
    }

    /** 携带规则组件的已创建（DRAFT）条款事件 */
    private ClauseCreatedEvent draftCreatedEvent(Map<CoverageId, Coverage> coverages,
            Map<ExclusionId, Exclusion> exclusions) {
        return new ClauseCreatedEvent(CLAUSE_ID, CLAUSE_CODE, CLAUSE_NAME, ClauseEnum.ClauseType.MAIN, "条款内容",
                ClauseEnum.ClauseStatus.DRAFT, "描述", InsuranceProductType.HOUSEHOLD_PROPERTY, Version.of("V1.0"), null,
                EFFECTIVE_DATE, EXPIRY_DATE, coverages, exclusions, NO_PREMIUM_RULE, NO_CLAIM_RULE,
                NO_CONTRACT_CHANGE_RULE, TENANT_ID, "creator",
                EFFECTIVE_DATE, "creator", EFFECTIVE_DATE);
    }

    /** 状态迁移事件（用于构造非 DRAFT 的给定状态） */
    private ClauseStatusChangedEvent statusChangedEvent(ClauseEnum.ClauseStatus status) {
        return new ClauseStatusChangedEvent(CLAUSE_ID, status, "operator", EFFECTIVE_DATE, TENANT_ID);
    }

    // ==================== ① 八类组件命令经成员路由 ====================

    @Test
    @DisplayName("添加保险责任命令路由到成员并产生事件")
    void addCoverageCommandRoutesToMember() {
        fixture.given(draftCreatedEvent())
                .when(new AddCoverageCommand(CLAUSE_ID, COVERAGE, "operator"))
                .expectEvents(new CoverageAddedEvent(CLAUSE_ID, COVERAGE, "operator", null));
    }

    @Test
    @DisplayName("移除保险责任命令路由到成员并产生事件")
    void removeCoverageCommandRoutesToMember() {
        fixture.given(draftCreatedEvent(), new CoverageAddedEvent(CLAUSE_ID, COVERAGE, "creator", EFFECTIVE_DATE))
                .when(new RemoveCoverageCommand(CLAUSE_ID, COVERAGE_ID, "operator"))
                .expectEvents(new CoverageRemovedEvent(CLAUSE_ID, COVERAGE_ID, "operator", null));
    }

    @Test
    @DisplayName("更新保险责任命令路由到成员并产生事件")
    void updateCoverageCommandRoutesToMember() {
        fixture.given(draftCreatedEvent(), new CoverageAddedEvent(CLAUSE_ID, COVERAGE, "creator", EFFECTIVE_DATE))
                .when(new UpdateCoverageCommand(CLAUSE_ID, COVERAGE, "operator"))
                .expectEvents(new CoverageAddedEvent(CLAUSE_ID, COVERAGE, "operator", null));
    }

    @Test
    @DisplayName("添加责任免除命令路由到成员并产生事件")
    void addExclusionCommandRoutesToMember() {
        fixture.given(draftCreatedEvent())
                .when(new AddExclusionCommand(CLAUSE_ID, EXCLUSION, "operator"))
                .expectEvents(new ExclusionAddedEvent(CLAUSE_ID, EXCLUSION, "operator", null));
    }

    @Test
    @DisplayName("移除责任免除命令路由到成员并产生事件")
    void removeExclusionCommandRoutesToMember() {
        fixture.given(draftCreatedEvent(), new ExclusionAddedEvent(CLAUSE_ID, EXCLUSION, "creator", EFFECTIVE_DATE))
                .when(new RemoveExclusionCommand(CLAUSE_ID, EXCLUSION_ID, "operator"))
                .expectEvents(new ExclusionRemovedEvent(CLAUSE_ID, EXCLUSION_ID, "operator", null));
    }

    @Test
    @DisplayName("设置缴费规则命令路由到成员并产生事件")
    void setPremiumRuleCommandRoutesToMember() {
        fixture.given(draftCreatedEvent())
                .when(new SetPremiumRuleCommand(CLAUSE_ID, PREMIUM_RULE, "operator"))
                .expectEvents(new PremiumRuleSetEvent(CLAUSE_ID, PREMIUM_RULE, "operator", null));
    }

    @Test
    @DisplayName("设置理赔规则命令路由到成员并产生事件")
    void setClaimRuleCommandRoutesToMember() {
        fixture.given(draftCreatedEvent())
                .when(new SetClaimRuleCommand(CLAUSE_ID, CLAIM_RULE, "operator"))
                .expectEvents(new ClaimRuleSetEvent(CLAUSE_ID, CLAIM_RULE, "operator", null));
    }

    @Test
    @DisplayName("设置合同变更规则命令路由到成员并产生事件")
    void setContractChangeRuleCommandRoutesToMember() {
        fixture.given(draftCreatedEvent())
                .when(new SetContractChangeRuleCommand(CLAUSE_ID, CONTRACT_CHANGE_RULE, "operator"))
                .expectEvents(new ContractChangeRuleSetEvent(CLAUSE_ID, CONTRACT_CHANGE_RULE, "operator", null));
    }

    // ==================== ② 状态镜像：非 DRAFT 时组件命令被拒 ====================

    @Test
    @DisplayName("状态镜像同步（状态变更事件）：生效后组件命令被拒，不产生事件")
    void componentCommandRejectedAfterStatusChangedAwayFromDraft() {
        fixture.given(draftCreatedEvent(), statusChangedEvent(ClauseEnum.ClauseStatus.ACTIVE))
                .when(new AddCoverageCommand(CLAUSE_ID, COVERAGE, "operator"))
                .expectException(ClauseOperationNotAllowedException.class)
                .expectNoEvents();
    }

    @Test
    @DisplayName("状态镜像同步（提交审批事件）：待审批时组件命令被拒，不产生事件")
    void componentCommandRejectedWhilePendingApproval() {
        fixture.given(draftCreatedEvent(),
                new ClauseSubmittedForApprovalEvent(CLAUSE_ID, "submitter", EFFECTIVE_DATE))
                .when(new SetPremiumRuleCommand(CLAUSE_ID, PREMIUM_RULE, "operator"))
                .expectException(ClauseOperationNotAllowedException.class)
                .expectNoEvents();
    }

    @Test
    @DisplayName("状态镜像同步（撤销提交回草稿）：镜像随根回退，组件命令恢复放行")
    void componentCommandAllowedAgainAfterWithdrawnToDraft() {
        fixture.given(draftCreatedEvent(),
                new ClauseSubmittedForApprovalEvent(CLAUSE_ID, "submitter", EFFECTIVE_DATE),
                statusChangedEvent(ClauseEnum.ClauseStatus.DRAFT))
                .when(new AddCoverageCommand(CLAUSE_ID, COVERAGE, "operator"))
                .expectEvents(new CoverageAddedEvent(CLAUSE_ID, COVERAGE, "operator", null));
    }

    // ==================== ③④ 事件回放重建与更新载荷保留语义 ====================

    @Test
    @DisplayName("移除责任回放：成员集合同步收缩，更新载荷反映最新组件状态")
    void coverageRemovedReplayDropsCoverageFromMember() {
        fixture.given(draftCreatedEvent(), new CoverageAddedEvent(CLAUSE_ID, COVERAGE, "creator", EFFECTIVE_DATE),
                new CoverageRemovedEvent(CLAUSE_ID, COVERAGE_ID, "operator", EFFECTIVE_DATE))
                .when(new UpdateClauseCommand(CLAUSE_ID, CLAUSE_CODE, CLAUSE_NAME, ClauseEnum.ClauseType.MAIN,
                        "条款内容", "描述", InsuranceProductType.HOUSEHOLD_PROPERTY, EFFECTIVE_DATE, EXPIRY_DATE,
                        TENANT_ID, "operator"))
                .expectEvents(new ClauseUpdatedEvent(CLAUSE_ID, CLAUSE_CODE, CLAUSE_NAME, ClauseEnum.ClauseType.MAIN,
                        "条款内容", ClauseEnum.ClauseStatus.DRAFT, "描述", InsuranceProductType.HOUSEHOLD_PROPERTY,
                        EFFECTIVE_DATE, EXPIRY_DATE, Map.of(), Map.of(), NO_PREMIUM_RULE, NO_CLAIM_RULE,
                        NO_CONTRACT_CHANGE_RULE, TENANT_ID, "operator",
                        null));
    }

    @Test
    @DisplayName("更新事件载荷 null 表示未变更：既有规则组件保留而非被清空")
    void updateEventWithNullComponentsKeepsExistingState() {
        fixture.given(draftCreatedEvent(), new CoverageAddedEvent(CLAUSE_ID, COVERAGE, "creator", EFFECTIVE_DATE),
                new PremiumRuleSetEvent(CLAUSE_ID, PREMIUM_RULE, "creator", EFFECTIVE_DATE),
                new ClauseUpdatedEvent(CLAUSE_ID, CLAUSE_CODE, CLAUSE_NAME, ClauseEnum.ClauseType.MAIN, "条款内容",
                        ClauseEnum.ClauseStatus.DRAFT, "描述", InsuranceProductType.HOUSEHOLD_PROPERTY, EFFECTIVE_DATE,
                        EXPIRY_DATE, NO_COVERAGES, NO_EXCLUSIONS, NO_PREMIUM_RULE, NO_CLAIM_RULE,
                        NO_CONTRACT_CHANGE_RULE, TENANT_ID, "creator", EFFECTIVE_DATE))
                .when(new ChangeClauseStatusCommand(CLAUSE_ID, ClauseEnum.ClauseStatus.PENDING_APPROVAL, "operator"))
                .expectState(clause -> {
                    assertEquals(1, clause.getCoverages().size());
                    assertEquals(PREMIUM_RULE, clause.getPremiumRule());
                });
    }

    @Test
    @DisplayName("更新保险责任要求责任已存在：不存在则拒绝且不产生事件")
    void updateCoverageRequiresExistingCoverage() {
        fixture.given(draftCreatedEvent())
                .when(new UpdateCoverageCommand(CLAUSE_ID, COVERAGE, "operator"))
                .expectException(ClauseOperationNotAllowedException.class)
                .expectNoEvents();
    }

    // ==================== ⑤ 对外视图不可变（DSN-02） ====================

    @Test
    @DisplayName("成员集合对外只暴露不可变视图：尝试修改抛 UnsupportedOperationException")
    void memberCollectionsExposedAsImmutableViews() {
        fixture.given(draftCreatedEvent(), new CoverageAddedEvent(CLAUSE_ID, COVERAGE, "creator", EFFECTIVE_DATE),
                new ExclusionAddedEvent(CLAUSE_ID, EXCLUSION, "creator", EFFECTIVE_DATE))
                .when(new SetClaimRuleCommand(CLAUSE_ID, CLAIM_RULE, "operator"))
                .expectState(clause -> {
                    assertThrows(UnsupportedOperationException.class, () -> clause.getCoverages().clear());
                    assertThrows(UnsupportedOperationException.class, () -> clause.getExclusions().clear());
                });
    }

    // ==================== ⑥ validateClaim 经成员读取规则组件 ====================

    @Test
    @DisplayName("理赔校验经成员读取规则组件：命中保险责任且未命中免责")
    void validateClaimMatchesCoverageHeldByMember() {
        fixture.given(draftCreatedEvent(Map.of(COVERAGE_ID, COVERAGE), Map.of(EXCLUSION_ID, EXCLUSION)),
                statusChangedEvent(ClauseEnum.ClauseStatus.ACTIVE))
                // 生效状态下重复置为 ACTIVE 为空操作（不改变状态、不产生事件），仅用于取得聚合实例做状态断言
                .when(new ChangeClauseStatusCommand(CLAUSE_ID, ClauseEnum.ClauseStatus.ACTIVE, "operator"))
                .expectNoEvents()
                .expectState(clause -> assertTrue(clause.validateClaim(MATCHING_CLAIM_EVENT)));
    }

    @Test
    @DisplayName("理赔校验经成员读取规则组件：命中免责规则即否定")
    void validateClaimRejectedWhenExclusionHits() {
        fixture.given(draftCreatedEvent(Map.of(COVERAGE_ID, COVERAGE), Map.of(EXCLUSION_ID, EXCLUSION)),
                statusChangedEvent(ClauseEnum.ClauseStatus.ACTIVE))
                .when(new ChangeClauseStatusCommand(CLAUSE_ID, ClauseEnum.ClauseStatus.ACTIVE, "operator"))
                .expectNoEvents()
                .expectState(clause -> assertFalse(clause.validateClaim(EXCLUDED_CLAIM_EVENT)));
    }
}
