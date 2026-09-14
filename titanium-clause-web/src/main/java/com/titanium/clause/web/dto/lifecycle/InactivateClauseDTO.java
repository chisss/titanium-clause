package com.titanium.clause.web.dto.lifecycle;

import lombok.Data;

/**
 * 停用条款请求
 */
@Data
public class InactivateClauseDTO {
    /**
     * 更新人
     */
    private String updatedBy;
}
