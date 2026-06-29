package com.titanium.clause.domain.event;

import java.time.LocalDateTime;

import com.titanium.clause.domain.valueobject.ClauseId;

/**
 * 条款已归档事件
 */
public record ClauseArchivedEvent(
        ClauseId clauseId,
        String archivedBy,
        LocalDateTime archivedAt
) {
}
