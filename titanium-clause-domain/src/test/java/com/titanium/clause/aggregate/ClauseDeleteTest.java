package com.titanium.clause.aggregate;

import java.time.LocalDateTime;
import java.util.Map;

import org.axonframework.test.aggregate.AggregateTestFixture;
import org.axonframework.test.aggregate.FixtureConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.titanium.clause.command.ArchiveClauseCommand;
import com.titanium.clause.command.DeleteClauseCommand;
import com.titanium.clause.common.exception.ClauseInvalidStatusException;
import com.titanium.clause.event.ClauseCreatedEvent;
import com.titanium.clause.event.ClauseDeletedEvent;
import com.titanium.clause.event.ClauseStatusChangedEvent;
import com.titanium.clause.valueobject.ClauseCode;
import com.titanium.clause.valueobject.ClauseId;
import com.titanium.clause.valueobject.ClauseName;
import com.titanium.clause.valueobject.Version;
import com.titanium.metadata.enums.clause.ClauseEnum;
import com.titanium.metadata.enums.insurance.InsuranceProductType;

/**
 * 条款删除命令聚合根测试
 * <p>
 * 校验「删草稿」能力：DRAFT 状态可硬删除并 markDeleted；非 DRAFT 状态拒绝删除。 聚合命令处理器内部使用
 * {@link LocalDateTime#now()} 生成时间戳，事件比对时忽略删除时间字段。
 * </p>
 */
class ClauseDeleteTest {

    private FixtureConfiguration<Clause> fixture;

    private static final ClauseId CLAUSE_ID = ClauseId.fromString("clause-001");
    private static final String   TENANT_ID = "tenant-001";

    @BeforeEach
    void setUp() {
        fixture = new AggregateTestFixture<>(Clause.class);
        // 聚合内部 now() 生成的时间戳为非确定值，事件比对时忽略
        fixture.registerIgnoredField(ClauseDeletedEvent.class, "deletedAt");
    }

    /**
     * 构造一个已创建（DRAFT）的条款事件
     */
    private ClauseCreatedEvent draftCreatedEvent() {
        LocalDateTime now = LocalDateTime.now();
        return new ClauseCreatedEvent(CLAUSE_ID, ClauseCode.fromString("CL-001"), ClauseName.fromString("测试条款"),
                ClauseEnum.ClauseType.MAIN, "条款内容", ClauseEnum.ClauseStatus.DRAFT, "描述", InsuranceProductType.HOUSEHOLD_PROPERTY,
                Version.of("V1.0"), null, now, now.plusYears(1), Map.of(), Map.of(), null, null, null, TENANT_ID,
                "creator", now, "creator", now);
    }

    @Test
    void draftClauseCanBeDeleted() {
        fixture.given(draftCreatedEvent())
                .when(new DeleteClauseCommand(CLAUSE_ID, "operator"))
                .expectEvents(new ClauseDeletedEvent(CLAUSE_ID, "operator", null))
                .expectMarkedDeleted();
    }

    @Test
    void activeClauseCannotBeDeleted() {
        fixture.given(draftCreatedEvent(),
                new ClauseStatusChangedEvent(CLAUSE_ID, ClauseEnum.ClauseStatus.ACTIVE, "approver", LocalDateTime.now()))
                .when(new DeleteClauseCommand(CLAUSE_ID, "operator"))
                .expectException(ClauseInvalidStatusException.class)
                .expectNoEvents();
    }

    @Test
    void activeClauseCanStillBeArchived() {
        // 回归校验：删草稿能力恢复后，已生效条款的归档软删路径不受影响
        fixture.registerIgnoredField(com.titanium.clause.event.ClauseArchivedEvent.class, "archivedAt");
        fixture.given(draftCreatedEvent(),
                new ClauseStatusChangedEvent(CLAUSE_ID, ClauseEnum.ClauseStatus.ACTIVE, "approver", LocalDateTime.now()))
                .when(new ArchiveClauseCommand(CLAUSE_ID, "operator"))
                .expectEvents(new com.titanium.clause.event.ClauseArchivedEvent(CLAUSE_ID, "operator", null));
    }
}
