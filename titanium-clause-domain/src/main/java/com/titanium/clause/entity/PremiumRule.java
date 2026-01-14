package com.titanium.clause.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 缴费规则实体类
 */
@Data
@NoArgsConstructor
public class PremiumRule {
    // 保费计算方式（如固定金额/费率计算）
    private String        calculationMethod;
    // 基础保费（固定金额时使用）
    private BigDecimal    basePremium;
    // 费率（费率计算时使用）
    private BigDecimal    premiumRate;
    // 缴费方式（趸交/年缴/月缴）
    private String        paymentMethod;
    // 缴费年限
    private Integer       paymentTerm;
    // 宽限期天数
    private Integer       gracePeriodDays;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
