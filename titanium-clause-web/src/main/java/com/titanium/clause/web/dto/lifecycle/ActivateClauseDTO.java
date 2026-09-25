package com.titanium.clause.web.dto.lifecycle;

import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 激活条款请求
 */
@Data
public class ActivateClauseDTO {
    /**
     * 更新人
     */
    @Size(max = 64)
    private String updatedBy;
}
