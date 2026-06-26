package com.titanium.clause.domain.event;

import java.time.LocalDateTime;

import com.titanium.clause.domain.valueobject.ClauseId;
import com.titanium.clause.domain.valueobject.Version;

/**
 * 条款审批流程已提交事件（独立审批聚合 ClauseApprovalProcess）
 *
 * @param approvalId    审批流程ID
 * @param clauseId      关联的条款ID
 * @param clauseVersion 关联的条款版本
 * @param submittedBy   提交人
 * @param tenantId      租户ID
 * @param submittedAt   提交时间
 */
public record ClauseApprovalSubmittedEvent(
        String approvalId,
        ClauseId clauseId,
        Version clauseVersion,
        String submittedBy,
        String tenantId,
        LocalDateTime submittedAt
) {
}
