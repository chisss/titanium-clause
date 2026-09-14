package com.titanium.clause.web.dto.approval;

import lombok.Data;

/**
 * 提交条款审批请求
 */
@Data
public class SubmitApprovalDTO {
    /**
     * 提交人
     */
    private String submittedBy;
}
