package com.titanium.clause.domain.command;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import com.titanium.clause.domain.valueobject.ClauseId;
import com.titanium.clause.domain.valueobject.CoverageId;

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
