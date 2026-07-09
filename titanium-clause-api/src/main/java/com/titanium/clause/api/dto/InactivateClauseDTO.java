package com.titanium.clause.api.dto;

import lombok.Data;

/**
 * 停用条款远程入参 DTO（跨服务 Feign 契约）
 */
@Data
public class InactivateClauseDTO {
    /** 停用人 */
    private String inactivatedBy;
}
