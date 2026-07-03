package com.titanium.clause.command;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import com.titanium.clause.entity.ClauseNotification;
import com.titanium.clause.valueobject.ClauseId;

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
