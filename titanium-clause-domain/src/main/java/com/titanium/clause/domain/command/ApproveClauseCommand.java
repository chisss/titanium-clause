package com.titanium.clause.domain.command;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import com.titanium.clause.domain.enums.ApprovalType;
import com.titanium.clause.domain.valueobject.ClauseId;

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
