package com.titanium.clause.domain.event;

import com.titanium.clause.domain.entity.ApprovalRecord;
import com.titanium.clause.domain.valueobject.ClauseId;

import java.time.LocalDateTime;

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
