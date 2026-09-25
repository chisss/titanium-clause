package com.titanium.clause.web.dto.approval;

import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 提交条款审批请求
 */
@Data
public class SubmitApprovalDTO {
    /**
     * 提交人
     */
    @Size(max = 64)
    private String submittedBy;
}
