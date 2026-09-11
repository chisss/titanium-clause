package com.titanium.clause.aggregate;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.util.Map;

import org.axonframework.test.aggregate.AggregateTestFixture;
import org.axonframework.test.aggregate.FixtureConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.titanium.clause.command.ActivateClauseCommand;
import com.titanium.clause.command.ApproveClauseCommand;
import com.titanium.clause.command.ChangeClauseStatusCommand;
import com.titanium.clause.common.enums.ApprovalType;
import com.titanium.clause.common.exception.ClauseInvalidStatusException;
import com.titanium.clause.event.ClauseApprovedEvent;
import com.titanium.clause.event.ClauseCreatedEvent;
import com.titanium.clause.event.ClauseStatusChangedEvent;
import com.titanium.clause.event.ClauseSubmittedForApprovalEvent;
import com.titanium.clause.valueobject.ClauseCode;
import com.titanium.clause.valueobject.ClauseId;
import com.titanium.clause.valueobject.ClauseName;
import com.titanium.clause.valueobject.Version;
import com.titanium.metadata.enums.clause.ClauseEnum;
import com.titanium.metadata.enums.insurance.InsuranceProductType;

/**
 * 条款状态机守护测试（审批不可绕过）
 * <p>
 * 锁死「首次生效的唯一入口是审批通过」：待审批（PENDING_APPROVAL）条款既不能被
 * {@link ActivateClauseCommand} 直接激活，也不能经 {@link ChangeClauseStatusCommand} 直达 ACTIVE——
 * 二者都会造出「无审批结论、无审批记录却已生效」的合规黑洞。同时回归校验合法迁移未被误伤：
 * 待审批仍可撤销提交回草稿、审批通过仍可生效、停用条款仍可重新启用。
 * </p>
 * <p>
 * 聚合命令处理器内部用 {@link LocalDateTime#now()} 生成时间戳、用 UUID 生成审批记录ID，事件比对时忽略。
 * </p>
 */
class ClauseStatusTransitionTest {

    private FixtureConfiguration<Clause> fixture;

    private static final ClauseId CLAUSE_ID = ClauseId.fromString("clause-001");
    private static final String   TENANT_ID = "tenant-001";

    @BeforeEach
    void setUp() {
        fixture = new AggregateTestFixture<>(Clause.class);
        fixture.registerIgnoredField(ClauseStatusChangedEvent.class, "updatedAt");
        fixture.registerIgnoredField(ClauseApprovedEvent.class, "approvedAt");
        // 审批记录ID为随机 UUID，整字段忽略；审批内容由 ApproveClauseCommand 处理器决定，非本测试关注点
        fixture.registerIgnoredField(ClauseApprovedEvent.class, "approvalRecord");
    }

    /** 已创建（DRAFT）的条款 */
    private ClauseCreatedEvent draftCreatedEvent() {
        LocalDateTime now = LocalDateTime.now();
        return new ClauseCreatedEvent(CLAUSE_ID, ClauseCode.fromString("CL-001"), ClauseName.fromString("测试条款"),
                ClauseEnum.ClauseType.MAIN, "条款内容", ClauseEnum.ClauseStatus.DRAFT, "描述",
                InsuranceProductType.HOUSEHOLD_PROPERTY, Version.of("V1.0"), null, now, now.plusYears(1), Map.of(),
                Map.of(), null, null, null, TENANT_ID, "creator", now, "creator", now);
    }

    /** 已提交审批（PENDING_APPROVAL）的条款 */
    private ClauseSubmittedForApprovalEvent submittedEvent() {
        return new ClauseSubmittedForApprovalEvent(CLAUSE_ID, "submitter", LocalDateTime.now());
    }

    @Test
    @DisplayName("待审批条款不可直接激活：拒绝且不产生任何事件")
    void pendingApprovalClauseCannotBeActivated() {
        fixture.given(draftCreatedEvent(), submittedEvent())
                .when(new ActivateClauseCommand(CLAUSE_ID, "operator"))
                .expectException(ClauseInvalidStatusException.class)
                .expectNoEvents();
    }

    @Test
    @DisplayName("待审批条款不可经状态变更直达 ACTIVE：拒绝且不产生任何事件")
    void pendingApprovalClauseCannotChangeStatusToActive() {
        fixture.given(draftCreatedEvent(), submittedEvent())
                .when(new ChangeClauseStatusCommand(CLAUSE_ID, ClauseEnum.ClauseStatus.ACTIVE, "operator"))
                .expectException(ClauseInvalidStatusException.class)
                .expectNoEvents();
    }

    @Test
    @DisplayName("审批通过仍是首次生效的唯一入口：ACTIVE 并留下审批记录")
    void approvalRemainsTheOnlyPathToActive() {
        fixture.given(draftCreatedEvent(), submittedEvent())
                .when(new ApproveClauseCommand(CLAUSE_ID, ApprovalType.LEGAL, "approver", "审批人", "同意"))
                .expectEvents(new ClauseApprovedEvent(CLAUSE_ID, null, "approver", null, TENANT_ID))
                .expectState(clause -> assertEquals(ClauseEnum.ClauseStatus.ACTIVE, clause.getStatus()));
    }

    @Test
    @DisplayName("待审批条款仍可撤销提交回到草稿")
    void pendingApprovalClauseCanBeWithdrawnToDraft() {
        fixture.given(draftCreatedEvent(), submittedEvent())
                .when(new ChangeClauseStatusCommand(CLAUSE_ID, ClauseEnum.ClauseStatus.DRAFT, "operator"))
                .expectEvents(new ClauseStatusChangedEvent(CLAUSE_ID, ClauseEnum.ClauseStatus.DRAFT, "operator", null,
                        TENANT_ID));
    }

    @Test
    @DisplayName("停用条款仍可重新启用（重新启用非首次生效，不需审批）")
    void inactiveClauseCanStillBeReactivated() {
        fixture.given(draftCreatedEvent(),
                new ClauseStatusChangedEvent(CLAUSE_ID, ClauseEnum.ClauseStatus.INACTIVE, "operator",
                        LocalDateTime.now(), TENANT_ID))
                .when(new ActivateClauseCommand(CLAUSE_ID, "operator"))
                .expectEvents(new ClauseStatusChangedEvent(CLAUSE_ID, ClauseEnum.ClauseStatus.ACTIVE, "operator", null,
                        TENANT_ID));
    }

    @Test
    @DisplayName("草稿条款不可直接激活：必须先提交审批")
    void draftClauseCannotBeActivated() {
        fixture.given(draftCreatedEvent())
                .when(new ActivateClauseCommand(CLAUSE_ID, "operator"))
                .expectException(ClauseInvalidStatusException.class)
                .expectNoEvents();
    }
}
