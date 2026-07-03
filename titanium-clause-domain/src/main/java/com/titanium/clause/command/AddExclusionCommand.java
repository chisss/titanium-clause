package com.titanium.clause.command;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import com.titanium.clause.entity.Exclusion;
import com.titanium.clause.valueobject.ClauseId;

/**
 * 添加责任免除命令
 */
public record AddExclusionCommand(
        @TargetAggregateIdentifier
        ClauseId clauseId,
        Exclusion exclusion,
        String updatedBy
) {
}
