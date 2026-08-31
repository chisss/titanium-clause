package com.titanium.clause.entity;

import java.time.LocalDateTime;

import com.titanium.clause.common.enums.ApprovalStatus;
import com.titanium.clause.common.enums.ApprovalType;

/**
 * 审批记录实体（聚合内实体，不可变值对象）
 *
 * @param recordId       审批记录ID
 * @param approvalType   审批类型: LEGAL(法务)/ACTUARIAL(精算)/MANAGEMENT(管理层)
 * @param approverId     审批人ID
 * @param approverName   审批人姓名
 * @param approvalStatus 审批状态: PENDING/APPROVED/REJECTED
 * @param comment        审批意见
 * @param approvalTime   审批时间
 */
public record ApprovalRecord(
        String         recordId,
        ApprovalType   approvalType,
        String         approverId,
        String         approverName,
        ApprovalStatus approvalStatus,
        String         comment,
        LocalDateTime  approvalTime) {
}
