package com.titanium.clause.event;

import java.time.LocalDateTime;

import org.axonframework.serialization.Revision;

import com.titanium.clause.valueobject.ClauseId;
import com.titanium.clause.valueobject.ExclusionId;

/**
 * 责任免除已移除事件
 */
@Revision("1")
public record ExclusionRemovedEvent(
        ClauseId clauseId,
        ExclusionId exclusionId,
        String updatedBy,
        LocalDateTime updatedAt
) {
}
