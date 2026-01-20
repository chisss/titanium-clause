package com.titanium.clause.api.request;

import lombok.Data;

/**
 * 停用条款请求DTO
 */
@Data
public class InactivateClauseRequest {
    /**
     * 停用人人
     */
    private String inactivatedBy;
}