package com.titanium.clause.domain.event;

import java.time.LocalDateTime;

import com.titanium.clause.domain.valueobject.ClauseId;
import com.titanium.clause.domain.valueobject.CoverageId;

/**
 * 保险责任已移除事件
 */
public record CoverageRemovedEvent(
        ClauseId clauseId,
        CoverageId coverageId,
        String updatedBy,
        LocalDateTime updatedAt
) {
}
