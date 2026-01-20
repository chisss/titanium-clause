package com.titanium.clause.domain.command;

import com.titanium.clause.domain.valueobject.ClauseId;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

/**
 * 激活条款命令
 */
public record ActivateClauseCommand(
        @TargetAggregateIdentifier
        ClauseId clauseId,
        String updatedBy
) {
}