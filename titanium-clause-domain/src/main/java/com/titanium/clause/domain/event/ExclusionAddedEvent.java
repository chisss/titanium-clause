package com.titanium.clause.domain.event;

import com.titanium.clause.domain.entity.Exclusion;
import com.titanium.clause.domain.valueobject.ClauseId;

import java.time.LocalDateTime;

/**
 * 责任免除已添加事件
 */
public record ExclusionAddedEvent(
        ClauseId clauseId,
        Exclusion exclusion,
        String updatedBy,
        LocalDateTime updatedAt
) {
}
