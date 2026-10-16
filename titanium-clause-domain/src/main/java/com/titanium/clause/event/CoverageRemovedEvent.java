package com.titanium.clause.event;

import java.time.LocalDateTime;

import org.axonframework.serialization.Revision;

import com.titanium.clause.valueobject.ClauseId;
import com.titanium.clause.valueobject.CoverageId;

/**
 * 保险责任已移除事件
 */
@Revision("1")
public record CoverageRemovedEvent(
        ClauseId clauseId,
        CoverageId coverageId,
        String updatedBy,
        LocalDateTime updatedAt
) {
}
