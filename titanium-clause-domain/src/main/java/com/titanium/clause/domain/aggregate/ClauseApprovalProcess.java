package com.titanium.clause.domain.aggregate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;

import com.titanium.clause.common.exception.ClauseInvalidStatusException;
import com.titanium.clause.domain.command.ApproveClauseApprovalCommand;
import com.titanium.clause.domain.command.RejectClauseApprovalCommand;
import com.titanium.clause.domain.command.SubmitClauseApprovalCommand;
import com.titanium.clause.domain.entity.ApprovalRecord;
import com.titanium.clause.domain.enums.ApprovalStatus;
import com.titanium.clause.domain.event.ClauseApprovalApprovedEvent;
import com.titanium.clause.domain.event.ClauseApprovalRejectedEvent;
import com.titanium.clause.domain.event.ClauseApprovalSubmittedEvent;
import com.titanium.clause.domain.valueobject.ClauseId;
import com.titanium.clause.domain.valueobject.Version;
import com.titanium.common.domain.BaseAggregate;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 条款审批流程聚合根
 * <p>
 * 从原 {@code Clause} 聚合根中拆分出的独立审批聚合，承载条款审批这一关注点。
 * 每一条审批流程对应某个条款指定版本的一次审批生命周期，
 * 以独立的 {@code approvalId} 作为聚合标识，使审批记录的无界增长与条款主体生命周期解耦。
 * </p>
 * <p>
 * 审批状态机：{@code PENDING → APPROVED} 或 {@code PENDING → REJECTED}。
 * 仅 PENDING 状态可执行通过/驳回，终态后不可再次流转。
 * </p>
 */
@Aggregate
@Data
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public class ClauseApprovalProcess extends BaseAggregate {

    @AggregateIdentifier
    private String                 approvalId;
    private ClauseId               clauseId;
    private Version                clauseVersion;
    /** 审批状态：PENDING/APPROVED/REJECTED */
    private ApprovalStatus         status;
    private List<ApprovalRecord>   approvalRecords = new ArrayList<>();

    // ========================== 命令处理器 ==========================

    /**
     * 提交审批命令处理器：开启一条审批流程
     */
    @CommandHandler
    public ClauseApprovalProcess(SubmitClauseApprovalCommand command) {
        AggregateLifecycle.apply(new ClauseApprovalSubmittedEvent(command.approvalId(), command.clauseId(),
                command.clauseVersion(), command.submittedBy(), command.tenantId(), LocalDateTime.now()));
    }

    /**
     * 审批通过命令处理器：仅 PENDING 状态可执行
     */
    @CommandHandler
    public void handle(ApproveClauseApprovalCommand command) {
        ensurePending("审批通过");

        ApprovalRecord record = buildRecord(command.approverId(), command.opinion(), ApprovalStatus.APPROVED);
        AggregateLifecycle.apply(new ClauseApprovalApprovedEvent(this.approvalId, record, command.approverId(),
                LocalDateTime.now()));
    }

    /**
     * 审批驳回命令处理器：仅 PENDING 状态可执行
     */
    @CommandHandler
    public void handle(RejectClauseApprovalCommand command) {
        ensurePending("审批驳回");

        ApprovalRecord record = buildRecord(command.approverId(), command.opinion(), ApprovalStatus.REJECTED);
        AggregateLifecycle.apply(new ClauseApprovalRejectedEvent(this.approvalId, record, command.approverId(),
                LocalDateTime.now()));
    }

    // ========================== 事件溯源处理器 ==========================

    @EventSourcingHandler
    public void on(ClauseApprovalSubmittedEvent event) {
        this.approvalId = event.approvalId();
        this.clauseId = event.clauseId();
        this.clauseVersion = event.clauseVersion();
        this.status = ApprovalStatus.PENDING;
        this.tenantId = event.tenantId();
        this.createTime = event.submittedAt();
        this.updateTime = event.submittedAt();
        this.approvalRecords = new ArrayList<>();
    }

    @EventSourcingHandler
    public void on(ClauseApprovalApprovedEvent event) {
        this.approvalRecords.add(event.approvalRecord());
        this.status = ApprovalStatus.APPROVED;
        this.updateTime = event.approvedAt();
    }

    @EventSourcingHandler
    public void on(ClauseApprovalRejectedEvent event) {
        this.approvalRecords.add(event.approvalRecord());
        this.status = ApprovalStatus.REJECTED;
        this.updateTime = event.rejectedAt();
    }

    // ========================== 私有辅助方法 ==========================

    /**
     * 校验当前审批流程处于待审批状态，否则不允许流转
     */
    private void ensurePending(String operation) {
        if (ApprovalStatus.PENDING != this.status) {
            throw new ClauseInvalidStatusException("仅待审批(PENDING)状态可执行" + operation + "，当前状态: " + this.status);
        }
    }

    /**
     * 构建一条审批记录
     */
    private ApprovalRecord buildRecord(String approverId, String opinion, ApprovalStatus approvalStatus) {
        ApprovalRecord record = new ApprovalRecord();
        record.setRecordId(UUID.randomUUID().toString().replace("-", ""));
        record.setApproverId(approverId);
        record.setApprovalStatus(approvalStatus);
        record.setComment(opinion);
        record.setApprovalTime(LocalDateTime.now());
        return record;
    }
}
