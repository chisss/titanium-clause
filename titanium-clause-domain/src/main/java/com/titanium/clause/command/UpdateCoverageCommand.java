package com.titanium.clause.command;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import com.titanium.clause.entity.Coverage;
import com.titanium.clause.valueobject.ClauseId;

/**
 * 更新保险责任命令
 */
public record UpdateCoverageCommand(
        @TargetAggregateIdentifier
        ClauseId clauseId,
        Coverage coverage,
        String updatedBy
) {
}
