package com.titanium.clause.domain.command;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import com.titanium.clause.domain.valueobject.ClauseId;
import com.titanium.clause.domain.valueobject.ExclusionId;

/**
 * 移除责任免除命令
 */
public record RemoveExclusionCommand(
        @TargetAggregateIdentifier
        ClauseId clauseId,
        ExclusionId exclusionId,
        String updatedBy
) {
}
