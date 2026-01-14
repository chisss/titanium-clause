package com.titanium.clause.dto;

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