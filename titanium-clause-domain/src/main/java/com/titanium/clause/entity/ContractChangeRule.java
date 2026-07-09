package com.titanium.clause.entity;

import java.time.LocalDateTime;

import com.titanium.clause.common.enums.RenewalType;

/**
 * 合同变更规则（聚合内实体，不可变值对象）
 *
 * @param surrenderCashValueRule 退保现金价值规则
 * @param renewalType            续保类型（保证续保/不保证续保）
 * @param reinstatementCondition 复效条件
 * @param waitingPeriodDays      等待期天数
 * @param freeLookPeriodDays     犹豫期天数
 * @param createdAt              创建时间
 * @param updatedAt              更新时间
 */
public record ContractChangeRule(
        String surrenderCashValueRule,
        RenewalType renewalType,
        String reinstatementCondition,
        Integer waitingPeriodDays,
        Integer freeLookPeriodDays,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}
