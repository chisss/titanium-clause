package com.titanium.clause.domain.event;

import java.time.LocalDateTime;

import com.titanium.clause.domain.valueobject.ClauseId;
import com.titanium.clause.domain.valueobject.ExclusionId;

/**
 * 责任免除已移除事件
 */
public record ExclusionRemovedEvent(
        ClauseId clauseId,
        ExclusionId exclusionId,
        String updatedBy,
        LocalDateTime updatedAt
) {
}
