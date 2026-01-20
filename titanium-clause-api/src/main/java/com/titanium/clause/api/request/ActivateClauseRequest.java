package com.titanium.clause.api.request;

import lombok.Data;

/**
 * 激活条款请求DTO
 */
@Data
public class ActivateClauseRequest {
    /**
     * 激活人
     */
    private String activatedBy;
}