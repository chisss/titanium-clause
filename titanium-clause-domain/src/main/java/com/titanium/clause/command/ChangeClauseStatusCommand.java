package com.titanium.clause.command;

import com.titanium.clause.valueobject.ClauseId;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

/**
 * 变更条款状态命令
 */
public record ChangeClauseStatusCommand(
        @TargetAggregateIdentifier
        ClauseId clauseId,
        String newStatus,
        String updatedBy
) {
}