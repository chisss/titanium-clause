package com.titanium.clause.query.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;

import com.titanium.clause.query.repository.CoverageViewRepository;
import com.titanium.clause.query.repository.PremiumRuleViewRepository;
import com.titanium.clause.query.result.CoverageQueryResult;
import com.titanium.clause.query.result.PremiumRuleQueryResult;
import com.titanium.clause.query.service.ClauseRuleQueryService;
import com.titanium.clause.query.view.CoverageView;
import com.titanium.clause.query.view.PremiumRuleView;
import com.titanium.clause.valueobject.AgeGenderRate;
import com.titanium.clause.valueobject.CoverageTrigger;
import com.titanium.clause.valueobject.PayoutRule;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 条款规则组件查询服务实现（CQRS 读侧）
 * <p>
 * 查询读模型表 {@code t_coverage_view} / {@code t_premium_rule_view}（由 {@code ClauseProjectionEventHandler}
 * 投影维护），JSON 列反序列化还原结构化值对象后组装为稳定 DTO 返回，禁止直接返回读模型实体。
 * </p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ClauseRuleQueryServiceImpl implements ClauseRuleQueryService {

    private final CoverageViewRepository    coverageViewRepository;
    private final PremiumRuleViewRepository premiumRuleViewRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CoverageQueryResult> getCoveragesByClauseId(String clauseId, String tenantId) {
        return coverageViewRepository.findByClauseIdAndTenantId(clauseId, tenantId).stream()
                .map(this::toCoverageResult)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PremiumRuleQueryResult> getPremiumRuleByClauseId(String clauseId, String tenantId) {
        return premiumRuleViewRepository.findByClauseIdAndTenantId(clauseId, tenantId).map(this::toPremiumRuleResult);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PremiumRuleQueryResult> getPremiumRuleByClauseIdAndVersion(String clauseId, String tableCode,
                                                                                String version, String tenantId) {
        // 版本查询策略：tableCode 和 version 均为 null → 默认查询（向后兼容）
        if (tableCode == null && version == null) {
            return getPremiumRuleByClauseId(clauseId, tenantId);
        }

        // tableCode 非空但 version 为 null → 取该 tableCode 最新版
        if (tableCode != null && version == null) {
            return premiumRuleViewRepository
                    .findFirstByClauseIdAndTableCodeAndTenantIdOrderByTableVersionDesc(clauseId, tableCode, tenantId)
                    .map(this::toPremiumRuleResult);
        }

        // tableCode 和 version 均非空 → 精确匹配
        if (tableCode != null && version != null) {
            return premiumRuleViewRepository
                    .findByClauseIdAndTableCodeAndTableVersionAndTenantId(clauseId, tableCode, version, tenantId)
                    .map(this::toPremiumRuleResult);
        }

        // version 非空但 tableCode 为 null（不合理场景，返回空）
        log.warn("费率表版本查询参数不合理: clauseId={}, tableCode=null, version={}", clauseId, version);
        return Optional.empty();
    }

    // ==================== 转换方法：读模型 → DTO（JSON 列反序列化还原值对象） ====================

    /**
     * 保险责任读模型 → 查询结果 DTO，结构化触发/赔付规则从 JSON 反序列化
     */
    private CoverageQueryResult toCoverageResult(CoverageView view) {
        CoverageQueryResult result = new CoverageQueryResult();
        result.setCoverageId(view.getCoverageId());
        result.setClauseId(view.getClauseId());
        result.setCoverageCode(view.getCoverageCode());
        result.setCoverageName(view.getCoverageName());
        result.setCoverageType(view.getCoverageType());
        result.setCoverageAmount(view.getCoverageAmount());
        result.setDescription(view.getDescription());
        result.setTriggerType(view.getTriggerType());
        result.setPayoutType(view.getPayoutType());
        result.setIsAdditional(view.getIsAdditional());
        result.setMainCoverageId(view.getMainCoverageId());
        result.setTrigger(parse(view.getTriggerJson(), CoverageTrigger.class));
        result.setPayoutRule(parse(view.getPayoutRuleJson(), PayoutRule.class));
        result.setTenantId(view.getTenantId());
        return result;
    }

    /**
     * 缴费规则读模型 → 查询结果 DTO，四维费率表/职业系数表从 JSON 反序列化
     */
    private PremiumRuleQueryResult toPremiumRuleResult(PremiumRuleView view) {
        PremiumRuleQueryResult result = new PremiumRuleQueryResult();
        result.setClauseId(view.getClauseId());
        result.setCalculationMethod(view.getCalculationMethod());
        result.setBasePremium(view.getBasePremium());
        result.setPremiumRate(view.getPremiumRate());
        result.setPaymentMethod(view.getPaymentMethod());
        result.setPaymentTerm(view.getPaymentTerm());
        result.setGracePeriodDays(view.getGracePeriodDays());
        result.setBaseRate(view.getBaseRate());
        result.setNcdCoefficient(view.getNcdCoefficient());
        result.setRuleSetCode(view.getRuleSetCode());
        result.setTableCode(view.getTableCode());
        result.setTableVersion(view.getTableVersion());
        result.setAgeGenderRates(parseList(view.getAgeGenderRatesJson()));
        result.setOccupationCoefficients(parseCoefficients(view.getOccupationCoefficientsJson()));
        result.setTenantId(view.getTenantId());
        return result;
    }

    /**
     * JSON 字符串 → 值对象（null 安全）
     */
    private <T> T parse(String json, Class<T> type) {
        return json != null ? JSON.parseObject(json, type) : null;
    }

    /**
     * JSON 数组字符串 → 年龄性别费率表（null 安全）
     */
    private List<AgeGenderRate> parseList(String json) {
        return json != null ? JSON.parseArray(json, AgeGenderRate.class) : null;
    }

    /**
     * JSON 对象字符串 → 职业系数表（null 安全，保留泛型 Map 结构）
     */
    private Map<String, BigDecimal> parseCoefficients(String json) {
        return json != null ? JSON.parseObject(json, new TypeReference<Map<String, BigDecimal>>() {
        }) : null;
    }
}
