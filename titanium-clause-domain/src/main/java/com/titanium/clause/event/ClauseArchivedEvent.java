package com.titanium.clause.event;

import java.time.LocalDateTime;

import org.axonframework.serialization.Revision;

import com.titanium.clause.valueobject.ClauseId;

/**
 * 条款已归档事件
 */
@Revision("1")
public record ClauseArchivedEvent(
        ClauseId clauseId,
        String archivedBy,
        LocalDateTime archivedAt
) {
}
