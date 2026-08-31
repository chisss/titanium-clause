package com.titanium.clause.query.mapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;

import com.titanium.clause.query.result.ClauseQueryResult;
import com.titanium.clause.query.result.CoverageQueryResult;
import com.titanium.clause.query.result.PremiumRuleQueryResult;
import com.titanium.clause.query.view.ClauseView;
import com.titanium.clause.query.view.CoverageView;
import com.titanium.clause.query.view.PremiumRuleView;
import com.titanium.clause.valueobject.AgeGenderRate;
import com.titanium.clause.valueobject.CoverageTrigger;
import com.titanium.clause.valueobject.PayoutRule;

/**
 * 条款查询结果映射器（MapStruct，读模型 → 查询结果 DTO）
 * <p>
 * 承担读侧「读模型 View → 稳定查询结果 DTO」的结构映射，取代查询服务实现中逐字段 set
 * （消除 red-line「>3 set 必须 MapStruct」）。同名字段自动映射，版本号/审计时间戳字段名差异
 * 以 {@link Mapping} 显式指定；结构化值对象列（{@code triggerJson}/{@code payoutRuleJson}/
 * 四维费率表/职业系数表）经 {@link Named} JSON 反序列化方法还原，与查询服务原解析语义一致。
 * </p>
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ClauseQueryResultMapper {

    /** 条款读模型 → 查询结果（版本号/审计时间戳字段名映射） */
    @Mapping(target = "version", source = "clauseVersion")
    @Mapping(target = "createdAt", source = "createTime")
    @Mapping(target = "updatedAt", source = "updateTime")
    ClauseQueryResult toResult(ClauseView view);

    /** 保险责任读模型 → 查询结果（结构化触发/赔付规则从 JSON 反序列化） */
    @Mapping(target = "trigger", source = "triggerJson", qualifiedByName = "parseTrigger")
    @Mapping(target = "payoutRule", source = "payoutRuleJson", qualifiedByName = "parsePayoutRule")
    CoverageQueryResult toCoverageResult(CoverageView view);

    /** 缴费规则读模型 → 查询结果（四维费率表/职业系数表从 JSON 反序列化） */
    @Mapping(target = "ageGenderRates", source = "ageGenderRatesJson", qualifiedByName = "parseAgeGenderRates")
    @Mapping(target = "occupationCoefficients", source = "occupationCoefficientsJson",
            qualifiedByName = "parseOccupationCoefficients")
    PremiumRuleQueryResult toPremiumRuleResult(PremiumRuleView view);

    /** JSON 字符串 → 赔付触发条件值对象（null 安全） */
    @Named("parseTrigger")
    default CoverageTrigger parseTrigger(String json) {
        return json != null ? JSON.parseObject(json, CoverageTrigger.class) : null;
    }

    /** JSON 字符串 → 赔付规则值对象（null 安全） */
    @Named("parsePayoutRule")
    default PayoutRule parsePayoutRule(String json) {
        return json != null ? JSON.parseObject(json, PayoutRule.class) : null;
    }

    /** JSON 数组字符串 → 年龄性别费率表（null 安全） */
    @Named("parseAgeGenderRates")
    default List<AgeGenderRate> parseAgeGenderRates(String json) {
        return json != null ? JSON.parseArray(json, AgeGenderRate.class) : null;
    }

    /** JSON 对象字符串 → 职业系数表（null 安全，保留泛型 Map 结构） */
    @Named("parseOccupationCoefficients")
    default Map<String, BigDecimal> parseOccupationCoefficients(String json) {
        return json != null ? JSON.parseObject(json, new TypeReference<Map<String, BigDecimal>>() {
        }) : null;
    }
}
