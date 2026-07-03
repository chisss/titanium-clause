package com.titanium.clause.command;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import com.titanium.clause.entity.ClauseSignTemplate;
import com.titanium.clause.valueobject.ClauseId;

/**
 * 设置签署模板命令
 */
public record SetSignTemplateCommand(
        @TargetAggregateIdentifier
        ClauseId clauseId,
        ClauseSignTemplate signTemplate,
        String updatedBy
) {
}
