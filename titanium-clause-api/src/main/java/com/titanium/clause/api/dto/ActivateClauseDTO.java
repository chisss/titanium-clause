package com.titanium.clause.api.dto;

import lombok.Data;

/**
 * 激活条款远程入参 DTO（跨服务 Feign 契约）
 */
@Data
public class ActivateClauseDTO {
    /** 激活人 */
    private String activatedBy;
}
