package com.titanium.clause.application.init;

import java.time.LocalDateTime;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.titanium.clause.command.AddCoverageCommand;
import com.titanium.clause.command.CreateClauseCommand;
import com.titanium.clause.common.enums.CoverageType;
import com.titanium.clause.entity.Coverage;
import com.titanium.clause.valueobject.ClauseCode;
import com.titanium.clause.valueobject.ClauseId;
import com.titanium.clause.valueobject.ClauseName;
import com.titanium.clause.valueobject.CoverageId;
import com.titanium.clause.valueobject.CoverageTrigger;
import com.titanium.clause.valueobject.PayoutRule;
import com.titanium.clause.valueobject.PayoutRule.PeriodicPayoutTerms;
import com.titanium.clause.valueobject.Version;
import com.titanium.metadata.enums.CommonStatus;
import com.titanium.metadata.enums.clause.ClauseEnum;
import com.titanium.metadata.enums.clause.CoverageTriggerType;
import com.titanium.metadata.enums.insurance.InsuranceProductType;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 寿险条款责任种子初始化器（CLAUSE-3）
 * <p>
 * 启动时经命令网关建立四大传统寿险（定期寿/终身寿/两全/年金）的演示条款及其保险责任，
 * 走聚合命令 → 事件 → 投影入读模型（t_clause_view / t_coverage_view），保证读写模型同源、聚合可重建。
 * 取代此前直插孤儿表 t_coverage 且挂空条款的错误种子。
 * </p>
 * <p>
 * 幂等：固定 clauseId，启动时先查读模型，已存在则跳过，避免重启重复建。
 * 平台级公共模板租户为 {@code default}。
 * </p>
 */
@Slf4j
@Component
@Order(100)
@RequiredArgsConstructor
public class LifeClauseSeedInitializer implements ApplicationRunner {

    /** 平台级公共模板租户 */
    private static final String TENANT = "default";
    private static final String OPERATOR = "system";

    private final CommandGateway commandGateway;
    private final EventStore     eventStore;

    @Override
    public void run(ApplicationArguments args) {
        seedTermLife();
        seedWholeLife();
        seedEndowment();
        seedAnnuity();
    }

    /** 定期寿险：身故 + 全残责任 */
    private void seedTermLife() {
        String clauseId = "seed_clause_term_life";
        if (exists(clauseId)) {
            return;
        }
        createClause(clauseId, "TERM_LIFE_CLAUSE", "定期寿险条款", InsuranceProductType.TERM_LIFE);
        addCoverage(clauseId, "seed_cov_term_life_death", "TERM_LIFE_DEATH", "定期寿险身故保险金",
                CoverageTriggerType.DEATH, "保障期内身故，定额给付基本保额，合同终止", PayoutRule.fixed(null));
        addCoverage(clauseId, "seed_cov_term_life_tpd", "TERM_LIFE_TPD", "定期寿险全残保险金",
                CoverageTriggerType.TOTAL_PERMANENT_DISABILITY, "保障期内全残，定额给付基本保额，合同终止",
                PayoutRule.fixed(null));
        log.info("[寿险种子] 定期寿险条款已初始化 clauseId={}", clauseId);
    }

    /** 终身寿险：身故 + 全残责任（含现金价值孰高） */
    private void seedWholeLife() {
        String clauseId = "seed_clause_whole_life";
        if (exists(clauseId)) {
            return;
        }
        createClause(clauseId, "WHOLE_LIFE_CLAUSE", "终身寿险条款", InsuranceProductType.WHOLE_LIFE);
        addCoverage(clauseId, "seed_cov_whole_life_death", "WHOLE_LIFE_DEATH", "终身寿险身故保险金",
                CoverageTriggerType.DEATH, "终身保障，身故按基本保额与现金价值孰高定额给付，合同终止",
                PayoutRule.fixed(null));
        addCoverage(clauseId, "seed_cov_whole_life_tpd", "WHOLE_LIFE_TPD", "终身寿险全残保险金",
                CoverageTriggerType.TOTAL_PERMANENT_DISABILITY, "全残按基本保额与现金价值孰高定额给付，合同终止",
                PayoutRule.fixed(null));
        log.info("[寿险种子] 终身寿险条款已初始化 clauseId={}", clauseId);
    }

    /** 两全保险：满期生存 + 身故责任（生死两全） */
    private void seedEndowment() {
        String clauseId = "seed_clause_endowment";
        if (exists(clauseId)) {
            return;
        }
        createClause(clauseId, "ENDOWMENT_CLAUSE", "两全保险条款", InsuranceProductType.ENDOWMENT);
        addCoverage(clauseId, "seed_cov_endowment_maturity", "ENDOWMENT_MATURITY", "两全保险满期生存保险金",
                CoverageTriggerType.SURVIVAL, "生存至保险期间届满，定额给付满期生存保险金，合同终止",
                PayoutRule.fixed(null));
        addCoverage(clauseId, "seed_cov_endowment_death", "ENDOWMENT_DEATH", "两全保险身故保险金",
                CoverageTriggerType.DEATH, "满期前身故，定额给付身故保险金，合同终止", PayoutRule.fixed(null));
        log.info("[寿险种子] 两全保险条款已初始化 clauseId={}", clauseId);
    }

    /** 年金保险：生存年金周期给付责任（不终止保单） */
    private void seedAnnuity() {
        String clauseId = "seed_clause_annuity";
        if (exists(clauseId)) {
            return;
        }
        createClause(clauseId, "ANNUITY_CLAUSE", "年金保险条款", InsuranceProductType.ANNUITY);
        PeriodicPayoutTerms terms = new PeriodicPayoutTerms("ANNUAL", null, null, null, null, null);
        addCoverage(clauseId, "seed_cov_annuity_survival", "ANNUITY_SURVIVAL", "年金保险生存年金",
                CoverageTriggerType.SURVIVAL, "生存至年金给付日，按约定频率周期给付生存年金，给付不终止合同",
                PayoutRule.periodic(terms));
        log.info("[寿险种子] 年金保险条款已初始化 clauseId={}", clauseId);
    }

    /**
     * 判断种子条款聚合是否已存在（幂等）。
     *
     * <p>🔴 查<b>写侧事件存储</b>而非读模型：读模型（t_clause_view）由 TrackingEventProcessor
     * 异步投影，重启时本 Runner 在主线程早于投影追平即执行，查读模型会误判"不存在"而重复发
     * {@code CreateClauseCommand}，触发 "Cannot reuse aggregate identifier"。事件存储按聚合 ID 读事件流
     * 是强一致的：只要该聚合已有任何事件即视为存在。</p>
     */
    private boolean exists(String clauseId) {
        return eventStore.readEvents(clauseId).hasNext();
    }

    /**
     * 发命令创建条款主体（DRAFT）
     * <p>
     * 险种按三级分类逐条精确标注（而非统一挂二级 LIFE），使条款与产品共用同一套险种词汇，
     * 产品配置选条款时可按三级险种精确匹配。
     * </p>
     *
     * @param insuranceType 该条款所属的三级险种
     */
    private void createClause(String clauseId, String code, String name, InsuranceProductType insuranceType) {
        LocalDateTime now = LocalDateTime.now();
        commandGateway.sendAndWait(new CreateClauseCommand(
                ClauseId.fromString(clauseId),
                ClauseCode.fromString(code),
                ClauseName.fromString(name),
                ClauseEnum.ClauseType.MAIN,
                name + "（平台级寿险责任模板）",
                name + " 演示种子条款，供产品配置选用",
                insuranceType,
                Version.fromString("1.0"),
                now,
                now.plusYears(30),
                TENANT,
                OPERATOR));
    }

    /** 发命令为条款添加一条保险责任 */
    private void addCoverage(String clauseId, String coverageId, String code, String name,
            CoverageTriggerType triggerType, String triggerDesc, PayoutRule payoutRule) {
        LocalDateTime now = LocalDateTime.now();
        Coverage coverage = new Coverage(
                CoverageId.fromString(coverageId),
                code,
                name,
                null,
                name,
                CommonStatus.ENABLED,
                CoverageType.DEATH,
                null,
                null,
                null,
                Boolean.FALSE,
                null,
                CoverageTrigger.of(triggerType, triggerDesc),
                payoutRule,
                now,
                now);
        commandGateway.sendAndWait(new AddCoverageCommand(ClauseId.fromString(clauseId), coverage, OPERATOR));
    }
}
