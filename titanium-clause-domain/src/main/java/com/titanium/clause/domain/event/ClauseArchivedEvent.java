package com.titanium.clause.domain.event;

import com.titanium.clause.domain.valueobject.ClauseId;

import java.time.LocalDateTime;

/**
 * 条款已归档事件
 */
public record ClauseArchivedEvent(
        ClauseId clauseId,
        String archivedBy,
        LocalDateTime archivedAt
) {
}
