package com.titanium.clause.entity;

import java.time.LocalDateTime;

import com.titanium.clause.common.enums.RenewalType;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 合同变更规则实体类
 */
@Data
@NoArgsConstructor
public class ContractChangeRule {
    // 退保现金价值规则
    private String        surrenderCashValueRule;
    // 续保类型（保证续保/不保证续保）
    private RenewalType   renewalType;
    // 复效条件
    private String        reinstatementCondition;
    // 等待期天数
    private Integer       waitingPeriodDays;
    // 犹豫期天数
    private Integer       freeLookPeriodDays;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
