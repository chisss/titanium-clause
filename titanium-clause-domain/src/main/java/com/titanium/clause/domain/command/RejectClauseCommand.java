package com.titanium.clause.domain.command;

import com.titanium.clause.domain.enums.ApprovalType;
import com.titanium.clause.domain.valueobject.ClauseId;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

/**
 * 审批驳回条款命令
 */
public record RejectClauseCommand(
        @TargetAggregateIdentifier
        ClauseId clauseId,
        ApprovalType approvalType,
        String approverId,
        String approverName,
        String comment
) {
}
