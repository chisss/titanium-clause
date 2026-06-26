package com.titanium.clause.domain.event;

import java.time.LocalDateTime;

import com.titanium.clause.domain.entity.ApprovalRecord;

/**
 * 条款审批流程已驳回事件（独立审批聚合 ClauseApprovalProcess）
 *
 * @param approvalId     审批流程ID
 * @param approvalRecord 审批记录
 * @param rejectedBy     驳回人ID
 * @param rejectedAt     驳回时间
 */
public record ClauseApprovalRejectedEvent(
        String approvalId,
        ApprovalRecord approvalRecord,
        String rejectedBy,
        LocalDateTime rejectedAt
) {
}
