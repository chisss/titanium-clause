package com.titanium.clause.domain.event;

import java.time.LocalDateTime;

import com.titanium.clause.domain.entity.ApprovalRecord;

/**
 * 条款审批流程已通过事件（独立审批聚合 ClauseApprovalProcess）
 *
 * @param approvalId     审批流程ID
 * @param approvalRecord 审批记录
 * @param approverId     审批人ID
 * @param approvedAt     审批时间
 */
public record ClauseApprovalApprovedEvent(
        String approvalId,
        ApprovalRecord approvalRecord,
        String approverId,
        LocalDateTime approvedAt
) {
}
