package com.titanium.clause.command;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import com.titanium.clause.common.enums.ApprovalType;
import com.titanium.clause.valueobject.ClauseId;

/**
 * 审批通过条款命令
 */
public record ApproveClauseCommand(
        @TargetAggregateIdentifier
        ClauseId clauseId,
        ApprovalType approvalType,
        String approverId,
        String approverName,
        String comment
) {
}
