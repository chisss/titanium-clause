package com.titanium.clause.command;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import com.titanium.clause.entity.Coverage;
import com.titanium.clause.valueobject.ClauseId;

/**
 * 添加保险责任到条款命令
 */
public record AddCoverageCommand(
        @TargetAggregateIdentifier
        ClauseId clauseId,
        Coverage coverage,
        String updatedBy
) {
}
