package com.titanium.clause.web.dto.approval;

import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 条款归档请求
 */
@Data
public class ArchiveClauseDTO {
    /**
     * 归档人
     */
    @Size(max = 64)
    private String archivedBy;
}
