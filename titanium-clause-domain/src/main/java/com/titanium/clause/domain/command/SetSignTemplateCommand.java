package com.titanium.clause.domain.command;

import com.titanium.clause.domain.entity.ClauseSignTemplate;
import com.titanium.clause.domain.valueobject.ClauseId;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

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
