package com.titanium.clause.event;

import java.time.LocalDateTime;

import com.titanium.clause.entity.ClauseSignTemplate;
import com.titanium.clause.valueobject.ClauseId;

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
