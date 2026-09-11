package com.titanium.clause.event;

import java.time.LocalDateTime;

import com.titanium.clause.entity.ApprovalRecord;
import com.titanium.clause.valueobject.ClauseId;

/**
 * 条款审批通过事件
 *
 * @param tenantId 租户ID：本事件经 {@code clause-status-changed} 主题跨域外发，消费端据此判定事件归属租户
 */
public record ClauseApprovedEvent(
        ClauseId clauseId,
        ApprovalRecord approvalRecord,
        String approverId,
        LocalDateTime approvedAt,
        String tenantId
) {
}
