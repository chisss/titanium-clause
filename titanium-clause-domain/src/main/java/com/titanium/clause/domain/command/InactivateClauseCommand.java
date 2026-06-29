package com.titanium.clause.domain.command;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import com.titanium.clause.domain.valueobject.ClauseId;

/**
 * 停用条款命令
 */
public record InactivateClauseCommand(
        @TargetAggregateIdentifier
        ClauseId clauseId,
        String updatedBy
) {
}
