package com.titanium.clause.web.dto;

import java.math.BigDecimal;

import lombok.Data;

/**
 * 保险责任配置请求（前端 → 后端，条款配置页「保险责任」编辑区）
 * <p>
 * 扁平化承接前端表单，覆盖参考电子保单（众安尊享e生·中高端医疗2025版）的两族责任参数：
 * <ul>
 *   <li><b>报销型</b>（一般医疗/特定疾病医疗/特药/外购药/先进疗法）：最高保额、赔付类型 REIMBURSEMENT、
 *       社保内报销比例、社保外报销比例、年免赔额、等待期天数；</li>
 *   <li><b>津贴型</b>（住院津贴）：日津贴、免赔天数、每次最高赔付天数、累计最高赔付天数、等待期天数。</li>
 * </ul>
 * 由 {@code CoverageAssembler} 装配为领域实体 {@code Coverage}：结构化字段落 {@code PayoutRule}/{@code Deductible}，
 * 医疗特有的扩展参数（社保外比例、等待期、给付天数、日津贴）落 {@code CoverageTrigger.params}（该 Map 即领域预留的扩展点），
 * 不改动跨域共享值对象，规避事件存储反序列化风险。
 * </p>
 */
@Data
public class CoverageDTO {

    /** 责任编码（新增可空，由后端生成责任ID） */
    private String coverageCode;

    /** 责任名称 */
    private String coverageName;

    /** 责任类型 code：CRITICAL_ILLNESS/MEDICAL/ACCIDENT/DEATH */
    private String coverageType;

    /** 赔付触发类型 code：DEATH/CRITICAL_ILLNESS/MEDICAL_EXPENSE/... */
    private String triggerType;

    /** 赔付类型 code：FIXED/PROPORTIONAL/ACTUAL_LOSS/REIMBURSEMENT/PERIODIC */
    private String payoutType;

    /** 最高保额（责任保额上限） */
    private BigDecimal coverageAmount;

    /** 单次/年度赔付上限（可空，报销/按损封顶） */
    private BigDecimal maxPayout;

    /** 社保内报销比例 0-1（报销型使用，落 PayoutRule.reimbursementRatio） */
    private BigDecimal reimbursementRatio;

    /** 社保外报销比例 0-1（医疗特有，落 trigger.params.outSocialRatio） */
    private BigDecimal outSocialRatio;

    /** 比例赔付比例 0-1（PROPORTIONAL 使用） */
    private BigDecimal proportion;

    /** 年免赔额（报销/按损，落 Deductible.fixedAmount） */
    private BigDecimal deductibleAmount;

    /** 等待期天数（医疗/重疾常见 30/90 天，落 trigger.params.waitingPeriodDays） */
    private Integer waitingPeriodDays;

    /** 日津贴金额（津贴型，元/天，落 trigger.params.dailyAmount） */
    private BigDecimal dailyAmount;

    /** 免赔天数（津贴型，落 trigger.params.deductibleDays） */
    private Integer deductibleDays;

    /** 每次最高赔付天数（津贴型，落 trigger.params.maxDaysPerClaim） */
    private Integer maxDaysPerClaim;

    /** 累计最高赔付天数（津贴型，落 trigger.params.maxDaysTotal） */
    private Integer maxDaysTotal;

    /** 责任描述 */
    private String description;

    /** 是否附加责任 */
    private Boolean isAdditional;

    /** 操作人 */
    private String updatedBy;
}
