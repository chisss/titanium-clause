package com.titanium.clause.domain.command;

import com.titanium.clause.domain.entity.ClauseNotification;
import com.titanium.clause.domain.valueobject.ClauseId;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

/**
 * 添加条款告知命令
 */
public record AddNotificationCommand(
        @TargetAggregateIdentifier
        ClauseId clauseId,
        ClauseNotification notification,
        String updatedBy
) {
}
