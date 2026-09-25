package com.titanium.clause.web.dto.lifecycle;

import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 停用条款请求
 */
@Data
public class InactivateClauseDTO {
    /**
     * 更新人
     */
    @Size(max = 64)
    private String updatedBy;
}
