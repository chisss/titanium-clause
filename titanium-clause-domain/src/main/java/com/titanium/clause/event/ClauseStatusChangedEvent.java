package com.titanium.clause.event;

import java.time.LocalDateTime;

import com.titanium.clause.valueobject.ClauseId;
import com.titanium.metadata.enums.clause.ClauseEnum;

/**
 * 条款状态变更事件
 *
 * @param clauseId 手动添加getter方法，解决Lombok注解处理问题
 */
public record ClauseStatusChangedEvent(ClauseId clauseId, ClauseEnum.ClauseStatus newStatus, String updatedBy,
        LocalDateTime updatedAt) {
}
