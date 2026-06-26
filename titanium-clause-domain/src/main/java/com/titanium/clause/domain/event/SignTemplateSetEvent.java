package com.titanium.clause.domain.event;

import com.titanium.clause.domain.entity.ClauseSignTemplate;
import com.titanium.clause.domain.valueobject.ClauseId;

import java.time.LocalDateTime;

/**
 * 签署模板已设置事件
 */
public record SignTemplateSetEvent(
        ClauseId clauseId,
        ClauseSignTemplate signTemplate,
        String updatedBy,
        LocalDateTime updatedAt
) {
}
