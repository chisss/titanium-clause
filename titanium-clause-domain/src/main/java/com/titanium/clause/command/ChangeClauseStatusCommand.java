package com.titanium.clause.command;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import com.titanium.clause.valueobject.ClauseId;
import com.titanium.metadata.enums.clause.ClauseEnum;

/**
 * 变更条款状态命令
 */
public record ChangeClauseStatusCommand(
        @TargetAggregateIdentifier
        ClauseId clauseId,
        ClauseEnum.ClauseStatus newStatus,
        String updatedBy
) {
}
