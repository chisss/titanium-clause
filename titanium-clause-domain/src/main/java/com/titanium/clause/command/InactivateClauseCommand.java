package com.titanium.clause.command;

import com.titanium.clause.valueobject.ClauseId;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

/**
 * 停用条款命令
 */
public record InactivateClauseCommand(
        @TargetAggregateIdentifier
        ClauseId clauseId,
        String updatedBy
) {
}