package com.titanium.clause.web.response;

import java.math.BigDecimal;

import lombok.Data;

/**
 * 保险责任展示 VO（后端 → 前端，条款配置页「保险责任」列表/回显）
 * <p>
 * 与 {@link com.titanium.clause.web.dto.CoverageDTO} 结构对称：把读模型的结构化
 * {@code PayoutRule}/{@code CoverageTrigger} 拍平回前端易用的扁平字段，医疗特有参数从
 * {@code trigger.params} 扩展 Map 中还原。
 * </p>
 */
@Data
public class CoverageVO {

    /** 责任ID */
    private String coverageId;

    /** 所属条款ID */
    private String clauseId;

    /** 责任编码 */
    private String coverageCode;

    /** 责任名称 */
    private String coverageName;

    /** 责任类型 code */
    private String coverageType;

    /** 赔付触发类型 code */
    private String triggerType;

    /** 赔付类型 code */
    private String payoutType;

    /** 最高保额 */
    private BigDecimal coverageAmount;

    /** 单次/年度赔付上限 */
    private BigDecimal maxPayout;

    /** 社保内报销比例 0-1 */
    private BigDecimal reimbursementRatio;

    /** 社保外报销比例 0-1 */
    private BigDecimal outSocialRatio;

    /** 比例赔付比例 0-1（PROPORTIONAL 使用） */
    private BigDecimal proportion;

    /** 年免赔额 */
    private BigDecimal deductibleAmount;

    /** 等待期天数 */
    private Integer waitingPeriodDays;

    /** 日津贴金额（元/天） */
    private BigDecimal dailyAmount;

    /** 免赔天数 */
    private Integer deductibleDays;

    /** 每次最高赔付天数 */
    private Integer maxDaysPerClaim;

    /** 累计最高赔付天数 */
    private Integer maxDaysTotal;

    /** 责任描述 */
    private String description;

    /** 是否附加责任 */
    private Boolean isAdditional;
}
