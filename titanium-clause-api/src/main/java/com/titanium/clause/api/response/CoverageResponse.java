package com.titanium.clause.api.response;

import java.math.BigDecimal;

import lombok.Data;
/**
 * 保险责任对外响应（Feign 契约）
 * <p>
 * 供上游域（policy 出单装配责任快照、claim 定责）取条款下的保险责任明细。相较读侧
 * {@code CoverageQueryResult}，本响应把结构化值对象（触发条件 / 赔付规则）中<b>跨域必需的要素</b>
 * 拍平为标量字段（免赔类型与额度、赔付比例、责任级等待期），避免调用方依赖条款域内部值对象类型。
 * </p>
 */
@Data
public class CoverageResponse {
    /** 责任ID */
    private String     coverageId;
    /** 所属条款ID */
    private String     clauseId;
    /** 责任编码 */
    private String     coverageCode;
    /** 责任名称 */
    private String     coverageName;
    /** 责任类型码（CRITICAL_ILLNESS/MEDICAL/ACCIDENT/DEATH） */
    private String     coverageType;
    /** 责任保额（该责任的赔付上限） */
    private BigDecimal coverageAmount;
    /** 责任描述 */
    private String     description;
    /** 赔付触发类型码（DEATH/CRITICAL_ILLNESS/MEDICAL_EXPENSE 等） */
    private String     triggerType;
    /** 赔付类型码（FIXED_AMOUNT/REIMBURSEMENT/PERIODIC/CASH_VALUE） */
    private String     payoutType;
    /** 是否附加责任 */
    private Boolean    additional;
    /** 关联的主险责任ID（附加责任用） */
    private String     mainCoverageId;
    /** 免赔类型码（NONE/FIXED_AMOUNT/PROPORTIONAL） */
    private String     deductibleType;
    /** 免赔额（固定金额免赔时有值） */
    private BigDecimal deductibleAmount;
    /** 免赔比例（比例免赔时有值，0-1） */
    private BigDecimal deductibleRatio;
    /** 赔付比例（1.0 表示 100% 报销/给付） */
    private BigDecimal reimbursementRatio;
    /** 赔付上限（结构化赔付规则中的 maxPayout） */
    private BigDecimal maxPayout;
    /** 责任级等待期天数（0 表示无等待期；区别于保单级等待期） */
    private Integer    waitingPeriodDays;
}
