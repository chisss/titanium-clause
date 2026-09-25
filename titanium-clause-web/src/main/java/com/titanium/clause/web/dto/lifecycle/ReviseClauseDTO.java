package com.titanium.clause.web.dto.lifecycle;

import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 条款修订请求
 */
@Data
public class ReviseClauseDTO {
    /**
     * 修订人
     */
    @Size(max = 64)
    private String revisedBy;
}
