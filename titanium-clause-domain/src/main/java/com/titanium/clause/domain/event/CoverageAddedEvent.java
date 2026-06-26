package com.titanium.clause.domain.event;

import com.titanium.clause.domain.entity.Coverage;
import com.titanium.clause.domain.valueobject.ClauseId;

import java.time.LocalDateTime;

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
