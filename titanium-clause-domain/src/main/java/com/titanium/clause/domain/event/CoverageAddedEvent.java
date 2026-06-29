package com.titanium.clause.domain.event;

import java.time.LocalDateTime;

import com.titanium.clause.domain.entity.Coverage;
import com.titanium.clause.domain.valueobject.ClauseId;

/**
 * 保险责任已添加事件
 */
public record CoverageAddedEvent(
        ClauseId clauseId,
        Coverage coverage,
        String updatedBy,
        LocalDateTime updatedAt
) {
}
