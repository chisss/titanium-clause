package com.titanium.clause.query.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import com.alibaba.fastjson2.JSON;

import com.titanium.clause.entity.Coverage;
import com.titanium.clause.entity.PremiumRule;
import com.titanium.clause.query.view.CoverageView;
import com.titanium.clause.query.view.PremiumRuleView;
import com.titanium.clause.valueobject.CoverageId;

/**
 * 条款规则组件读模型投影映射器（MapStruct，聚合内实体 → 读模型字段拷贝）
 * <p>
 * 承担保险责任 {@link Coverage}、缴费规则 {@link PremiumRule} 两类规则组件的实体 → View 结构映射，
 * 取代投影处理器中逐字段 set（消除 red-line「>3 set 必须 MapStruct」）。采用 {@link MappingTarget}
 * 就地更新既有/新建 View 实例，保留投影的 upsert 语义；{@link NullValuePropertyMappingStrategy#IGNORE}
 * 确保事件缺省字段不覆盖 View 既有值。
 * </p>
 * <p>
 * <b>职责边界</b>：仅做纯字段/值对象结构翻译（{@link CoverageId} 拆解为字符串、结构化
 * {@code CoverageTrigger}/{@code PayoutRule}/费率表经 {@code toJson} 整体序列化为 JSON 列）。以下含运行时
 * 副作用或需从上下文继承的字段留在投影处理器，此处对应目标字段 {@code ignore}：
 * <ul>
 *   <li>{@code clauseId}：由事件（非规则实体）携带，处理器设置；</li>
 *   <li>{@code tenantId}：规则组件事件未携带租户，从父 {@code ClauseView} 继承，处理器设置；</li>
 *   <li>审计时间戳 {@code createTime}（仅首次）/{@code updateTime}、乐观锁 {@code version}：由处理器/JPA 承接。</li>
 * </ul>
 * </p>
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ClauseRuleViewMapper {

    /**
     * 保险责任实体 → 责任读模型（就地 upsert；结构化触发/赔付规则整体序列化为 JSON 列）。
     * <p>
     * 触发类型/赔付类型从嵌套值对象抽取为标量列以支持检索；完整 {@code trigger}/{@code structuredPayoutRule}
     * 序列化 JSON 保结构。clauseId/tenantId/审计时间戳/乐观锁由处理器承接，此处 ignore。
     * </p>
     */
    @Mapping(target = "coverageId", source = "id", qualifiedByName = "coverageIdValue")
    @Mapping(target = "coverageCode", source = "code")
    @Mapping(target = "coverageName", source = "name")
    @Mapping(target = "coverageType", source = "type")
    @Mapping(target = "triggerType", source = "trigger.triggerType")
    @Mapping(target = "payoutType", source = "structuredPayoutRule.payoutType")
    @Mapping(target = "mainCoverageId", source = "mainCoverageId", qualifiedByName = "coverageIdValue")
    @Mapping(target = "triggerJson", source = "trigger", qualifiedByName = "toJson")
    @Mapping(target = "payoutRuleJson", source = "structuredPayoutRule", qualifiedByName = "toJson")
    @Mapping(target = "clauseId", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "version", ignore = true)
    void applyCoverage(@MappingTarget CoverageView view, Coverage coverage);

    /**
     * 缴费规则实体 → 费率读模型（就地 upsert；四维费率表/职业系数表整体序列化为 JSON 列）。
     * <p>
     * 标量字段（计算方式/基础保费/费率/缴费方式/年限/宽限期/NCD/规则集编码）同名自动映射；
     * clauseId/tenantId/审计时间戳/乐观锁由处理器承接，此处 ignore。
     * </p>
     */
    @Mapping(target = "ageGenderRatesJson", source = "ageGenderRates", qualifiedByName = "toJson")
    @Mapping(target = "occupationCoefficientsJson", source = "occupationCoefficients", qualifiedByName = "toJson")
    @Mapping(target = "clauseId", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "version", ignore = true)
    void applyPremiumRule(@MappingTarget PremiumRuleView view, PremiumRule premiumRule);

    /** 责任ID值对象 → 字符串（空安全；复用于 coverageId 与 mainCoverageId） */
    @Named("coverageIdValue")
    default String coverageIdValue(CoverageId coverageId) {
        return coverageId != null ? coverageId.getValue() : null;
    }

    /** 复杂值对象/集合 → JSON 字符串（null 安全，与投影处理器 toJson 语义一致） */
    @Named("toJson")
    default String toJson(Object value) {
        return value != null ? JSON.toJSONString(value) : null;
    }
}
