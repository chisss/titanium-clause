package com.titanium.clause.domain.command;

import com.titanium.clause.domain.entity.Exclusion;
import com.titanium.clause.domain.valueobject.ClauseId;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

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
