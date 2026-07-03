package com.titanium.clause.event;

import java.time.LocalDateTime;

import com.titanium.clause.entity.Coverage;
import com.titanium.clause.valueobject.ClauseId;

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
