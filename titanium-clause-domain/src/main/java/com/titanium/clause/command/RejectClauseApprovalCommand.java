package com.titanium.clause.command;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

/**
 * 审批驳回命令（面向独立审批聚合 ClauseApprovalProcess）
 *
 * @param approvalId 审批流程ID（聚合标识）
 * @param approverId 审批人ID
 * @param opinion    审批意见
 */
public record RejectClauseApprovalCommand(
        @TargetAggregateIdentifier
        String approvalId,
        String approverId,
        String opinion
) {
}
