package com.titanium.clause.event;

import java.time.LocalDateTime;

import com.titanium.clause.entity.ApprovalRecord;
import com.titanium.clause.valueobject.ClauseId;

/**
 * 条款审批通过事件
 */
public record ClauseApprovedEvent(
        ClauseId clauseId,
        ApprovalRecord approvalRecord,
        String approverId,
        LocalDateTime approvedAt
) {
}
