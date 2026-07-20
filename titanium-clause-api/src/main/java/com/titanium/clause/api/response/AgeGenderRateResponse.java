package com.titanium.clause.api.response;

import java.math.BigDecimal;

import lombok.Data;

/**
 * 年龄性别费率数据传输对象（对外契约）
 * <p>
 * 表达寿险/重疾险按四维定价的费率表条目：年龄 × 性别 × 缴费期 × 保障期。作为 api 层对外契约，
 * 不复用领域层 {@code AgeGenderRate} 值对象（api 层须自包含，不依赖领域内核）。供 billing 域费率表
 * 查询与保费计算消费。
 * </p>
 */
@Data
public class AgeGenderRateResponse {
    /** 最小年龄（含） */
    private Integer    minAge;
    /** 最大年龄（含） */
    private Integer    maxAge;
    /** 性别：M/F/ALL */
    private String     gender;
    /** 缴费期（年数，null 表示不限） */
    private Integer    paymentTerm;
    /** 保障期（年数，null 表示不限） */
    private Integer    coverageTerm;
    /** 该维度组合对应费率 */
    private BigDecimal rate;
}
