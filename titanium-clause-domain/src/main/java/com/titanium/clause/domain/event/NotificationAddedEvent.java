package com.titanium.clause.domain.event;

import com.titanium.clause.domain.entity.ClauseNotification;
import com.titanium.clause.domain.valueobject.ClauseId;

import java.time.LocalDateTime;

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
