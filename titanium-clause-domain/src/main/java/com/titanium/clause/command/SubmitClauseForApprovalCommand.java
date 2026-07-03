package com.titanium.clause.command;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import com.titanium.clause.valueobject.ClauseId;

/**
 * 提交条款审批命令
 */
public record SubmitClauseForApprovalCommand(
        @TargetAggregateIdentifier
        ClauseId clauseId,
        String submittedBy
) {
}
