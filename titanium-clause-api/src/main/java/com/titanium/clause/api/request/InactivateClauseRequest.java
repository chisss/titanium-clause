package com.titanium.clause.api.request;

import lombok.Data;

/**
 * 停用条款远程入参 DTO（跨服务 Feign 契约）
 */
@Data
public class InactivateClauseRequest {
    /** 停用人 */
    private String inactivatedBy;
}
