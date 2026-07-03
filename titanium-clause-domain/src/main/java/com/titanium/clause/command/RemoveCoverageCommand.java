package com.titanium.clause.command;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import com.titanium.clause.valueobject.ClauseId;
import com.titanium.clause.valueobject.CoverageId;

/**
 * 移除保险责任命令
 */
public record RemoveCoverageCommand(
        @TargetAggregateIdentifier
        ClauseId clauseId,
        CoverageId coverageId,
        String updatedBy
) {
}
