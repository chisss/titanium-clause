package com.titanium.clause.domain.entity;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 理赔规则实体类
 */
@Data
@NoArgsConstructor
public class ClaimRule {
    // 报案时效天数
    private Integer       reportDeadlineDays;
    // 理赔所需材料
    private String        requiredMaterials;
    // 理赔结案时效天数
    private Integer       settlementPeriodDays;
    // 赔付比例
    private String        payoutRatio;
    // 免赔额
    private String        deductibleAmount;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
