package com.titanium.clause.domain.command;

import com.titanium.clause.domain.entity.Coverage;
import com.titanium.clause.domain.valueobject.ClauseId;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

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
