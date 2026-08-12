package com.titanium.clause.web.request;

import lombok.Data;

/**
 * 停用条款请求
 */
@Data
public class InactivateClauseRequest {
    /**
     * 更新人
     */
    private String updatedBy;
}
