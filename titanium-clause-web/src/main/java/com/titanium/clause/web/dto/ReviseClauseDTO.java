package com.titanium.clause.web.dto;

import lombok.Data;

/**
 * 条款修订请求
 */
@Data
public class ReviseClauseDTO {
    /**
     * 修订人
     */
    private String revisedBy;
}
