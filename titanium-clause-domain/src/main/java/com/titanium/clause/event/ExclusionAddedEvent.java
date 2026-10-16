package com.titanium.clause.event;

import java.time.LocalDateTime;

import org.axonframework.serialization.Revision;

import com.titanium.clause.entity.Exclusion;
import com.titanium.clause.valueobject.ClauseId;

/**
 * 责任免除已添加事件
 */
@Revision("1")
public record ExclusionAddedEvent(
        ClauseId clauseId,
        Exclusion exclusion,
        String updatedBy,
        LocalDateTime updatedAt
) {
}
