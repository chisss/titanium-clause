package com.titanium.clause.event;

import java.time.LocalDateTime;

import com.titanium.clause.valueobject.ClauseId;
import com.titanium.metadata.enums.clause.ClauseEnum;

/**
 * 条款状态变更事件
 *
 * @param clauseId 手动添加getter方法，解决Lombok注解处理问题
 * @param tenantId 租户ID：本事件经 {@code clause-status-changed} 主题跨域外发，消费端据此判定事件归属租户
 */
public record ClauseStatusChangedEvent(ClauseId clauseId, ClauseEnum.ClauseStatus newStatus, String updatedBy,
        LocalDateTime updatedAt, String tenantId) {
}
