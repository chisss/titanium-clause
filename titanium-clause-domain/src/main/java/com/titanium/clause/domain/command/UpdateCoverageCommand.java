package com.titanium.clause.domain.command;

import com.titanium.clause.domain.entity.Coverage;
import com.titanium.clause.domain.valueobject.ClauseId;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

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
