package com.titanium.clause.aggregate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.axonframework.test.aggregate.AggregateTestFixture;
import org.axonframework.test.aggregate.FixtureConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.titanium.clause.command.ReviseClauseCommand;
import com.titanium.clause.common.enums.CoverageType;
import com.titanium.clause.common.exception.ClauseInvalidStatusException;
import com.titanium.clause.entity.Coverage;
import com.titanium.clause.event.ClauseCreatedEvent;
import com.titanium.clause.event.ClauseRevisedEvent;
import com.titanium.clause.event.ClauseStatusChangedEvent;
import com.titanium.clause.event.ClauseSubmittedForApprovalEvent;
import com.titanium.clause.event.CoverageAddedEvent;
import com.titanium.clause.valueobject.ClauseCode;
import com.titanium.clause.valueobject.ClauseId;
import com.titanium.clause.valueobject.ClauseName;
import com.titanium.clause.valueobject.CoverageId;
import com.titanium.clause.valueobject.Version;
import com.titanium.metadata.enums.CommonStatus;
import com.titanium.metadata.enums.clause.ClauseEnum;
import com.titanium.metadata.enums.insurance.InsuranceProductType;

/**
 * 条款修订（修订→新版本）聚合根测试
 * <p>
 * 校验缺口 G9 修复后的修订机制：仅 ACTIVE 条款可发起修订；修订经 {@code AggregateLifecycle.createNew}
 * 创建独立新版本聚合（DRAFT、版本号 +1、parentClauseId 溯源原条款），规则组件（责任等）随修订继承，
 * 原条款状态保持 ACTIVE 不变。聚合命令处理器内部使用 {@link LocalDateTime#now()} 生成时间戳，
 * 事件比对时忽略修订时间字段。
 * </p>
 */
class ClauseReviseTest {

    private FixtureConfiguration<Clause> fixture;

    private static final ClauseId   CLAUSE_ID     = ClauseId.fromString("clause-001");
    private static final ClauseId   NEW_CLAUSE_ID = ClauseId.fromString("clause-002");
    private static final String     TENANT_ID     = "tenant-001";
    private static final CoverageId COVERAGE_ID   = CoverageId.fromString("cov-001");

    private static final LocalDateTime EFFECTIVE_DATE = LocalDateTime.of(2026, 9, 1, 0, 0);
    private static final LocalDateTime EXPIRY_DATE    = LocalDateTime.of(2027, 8, 31, 0, 0);

    /** 条款已携带的保险责任（修订应完整继承） */
    private static final Coverage COVERAGE = new Coverage(
            COVERAGE_ID, "C001", "身故责任", null, "身故保障责任",
            CommonStatus.ENABLED, CoverageType.DEATH, null, null, null,
            Boolean.FALSE, null, null, null, EFFECTIVE_DATE, EFFECTIVE_DATE);

    @BeforeEach
    void setUp() {
        fixture = new AggregateTestFixture<>(Clause.class);
        // 聚合内部 now() 生成的时间戳为非确定值，事件比对时忽略
        fixture.registerIgnoredField(ClauseRevisedEvent.class, "revisedAt");
    }

    /**
     * 构造一个已创建（DRAFT）的条款事件
     */
    private ClauseCreatedEvent draftCreatedEvent() {
        return new ClauseCreatedEvent(CLAUSE_ID, ClauseCode.fromString("CL-001"), ClauseName.fromString("测试条款"),
                ClauseEnum.ClauseType.MAIN, "条款内容", ClauseEnum.ClauseStatus.DRAFT, "描述",
                InsuranceProductType.HOUSEHOLD_PROPERTY,
                Version.of("V1.0"), null, EFFECTIVE_DATE, EXPIRY_DATE, Map.of(), Map.of(), null, null, null, TENANT_ID,
                "creator", EFFECTIVE_DATE, "creator", EFFECTIVE_DATE);
    }

    @Test
    void activeClauseRevisionCreatesNewVersionWithInheritedCoverages() {
        fixture.given(draftCreatedEvent(),
                new CoverageAddedEvent(CLAUSE_ID, COVERAGE, "creator", EFFECTIVE_DATE),
                new ClauseStatusChangedEvent(CLAUSE_ID, ClauseEnum.ClauseStatus.ACTIVE, "approver", EFFECTIVE_DATE))
                .when(new ReviseClauseCommand(CLAUSE_ID, NEW_CLAUSE_ID, "reviser"))
                .expectEvents(new ClauseRevisedEvent(CLAUSE_ID, NEW_CLAUSE_ID, ClauseCode.fromString("CL-001"),
                        ClauseName.fromString("测试条款"), ClauseEnum.ClauseType.MAIN, "条款内容", "描述",
                        InsuranceProductType.HOUSEHOLD_PROPERTY, Version.of("V1.1"), EFFECTIVE_DATE, EXPIRY_DATE,
                        Map.of(COVERAGE_ID, COVERAGE), Map.of(), null, null, null, List.of(), null, TENANT_ID,
                        "reviser", null));
    }

    @Test
    void draftClauseCannotBeRevised() {
        fixture.given(draftCreatedEvent())
                .when(new ReviseClauseCommand(CLAUSE_ID, NEW_CLAUSE_ID, "reviser"))
                .expectException(ClauseInvalidStatusException.class)
                .expectNoEvents();
    }

    @Test
    void pendingApprovalClauseCannotBeRevised() {
        fixture.given(draftCreatedEvent(),
                new CoverageAddedEvent(CLAUSE_ID, COVERAGE, "creator", EFFECTIVE_DATE),
                new ClauseSubmittedForApprovalEvent(CLAUSE_ID, "creator", EFFECTIVE_DATE))
                .when(new ReviseClauseCommand(CLAUSE_ID, NEW_CLAUSE_ID, "reviser"))
                .expectException(ClauseInvalidStatusException.class)
                .expectNoEvents();
    }
}
