package com.titanium.clause.event;

import java.time.LocalDateTime;

import com.titanium.clause.entity.ClauseNotification;
import com.titanium.clause.valueobject.ClauseId;

/**
 * 条款告知已添加事件
 */
public record NotificationAddedEvent(
        ClauseId clauseId,
        ClauseNotification notification,
        String updatedBy,
        LocalDateTime updatedAt
) {
}
