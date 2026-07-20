package com.titanium.clause.query.result;

import java.math.BigDecimal;

import com.titanium.clause.common.enums.CoverageType;
import com.titanium.clause.valueobject.CoverageTrigger;
import com.titanium.clause.valueobject.PayoutRule;
import com.titanium.metadata.enums.clause.CoverageTriggerType;
import com.titanium.metadata.enums.clause.PayoutType;

import lombok.Data;

/**
 * 保险责任查询结果（CQRS 读侧稳定返回契约）
 * <p>
 * 由 {@link com.titanium.clause.query.service.ClauseRuleQueryService} 从读模型 {@code CoverageView}
 * 组装，JSON 列反序列化还原结构化 {@link CoverageTrigger}/{@link PayoutRule} 值对象，禁止直接返回读模型实体。
 * </p>
 */
@Data
public class CoverageQueryResult {
    /** 责任ID */
    private String              coverageId;
    /** 所属条款ID */
    private String              clauseId;
    /** 责任编码 */
    private String              coverageCode;
    /** 责任名称 */
    private String              coverageName;
    /** 责任类型（重疾/医疗/意外/身故） */
    private CoverageType        coverageType;
    /** 责任保额 */
    private BigDecimal          coverageAmount;
    /** 责任描述 */
    private String              description;
    /** 赔付触发类型 */
    private CoverageTriggerType triggerType;
    /** 赔付类型 */
    private PayoutType          payoutType;
    /** 是否附加责任 */
    private Boolean             isAdditional;
    /** 关联的主险责任ID */
    private String              mainCoverageId;
    /** 结构化赔付触发条件（JSON 列反序列化还原） */
    private CoverageTrigger     trigger;
    /** 结构化赔付规则（JSON 列反序列化还原） */
    private PayoutRule          payoutRule;
    /** 租户ID */
    private String              tenantId;
}
