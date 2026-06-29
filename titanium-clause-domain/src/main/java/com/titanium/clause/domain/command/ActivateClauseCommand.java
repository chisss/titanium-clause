package com.titanium.clause.domain.command;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import com.titanium.clause.domain.valueobject.ClauseId;

/**
 * 激活条款命令
 */
public record ActivateClauseCommand(
        @TargetAggregateIdentifier
        ClauseId clauseId,
        String updatedBy
) {
}
