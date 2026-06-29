package com.titanium.clause.domain.command;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import com.titanium.clause.domain.entity.ClauseNotification;
import com.titanium.clause.domain.valueobject.ClauseId;

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
