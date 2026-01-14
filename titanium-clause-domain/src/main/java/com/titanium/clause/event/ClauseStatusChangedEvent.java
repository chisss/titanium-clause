package com.titanium.clause.event;

import java.time.LocalDateTime;

import com.titanium.clause.valueobject.ClauseId;

/**
 * 条款状态变更事件
 *
 * @param clauseId 手动添加getter方法，解决Lombok注解处理问题
 */
public record ClauseStatusChangedEvent(ClauseId clauseId, String newStatus, String updatedBy, LocalDateTime updatedAt) {
}
