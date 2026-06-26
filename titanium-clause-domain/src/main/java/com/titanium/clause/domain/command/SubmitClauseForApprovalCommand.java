package com.titanium.clause.domain.command;

import com.titanium.clause.domain.valueobject.ClauseId;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

/**
 * 提交条款审批命令
 */
public record SubmitClauseForApprovalCommand(
        @TargetAggregateIdentifier
        ClauseId clauseId,
        String submittedBy
) {
}
