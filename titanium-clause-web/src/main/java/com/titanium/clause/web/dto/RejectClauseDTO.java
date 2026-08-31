package com.titanium.clause.web.dto;

import lombok.Data;

/**
 * 审批驳回条款请求
 */
@Data
public class RejectClauseDTO {
    /**
     * 审批类型码: LEGAL(法务)/ACTUARIAL(精算)/MANAGEMENT(管理层)
     */
    private String approvalType;

    /**
     * 审批人ID
     */
    private String approverId;

    /**
     * 审批人姓名
     */
    private String approverName;

    /**
     * 驳回意见
     */
    private String comment;
}
