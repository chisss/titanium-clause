package com.titanium.clause.query.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.titanium.clause.common.tenant.PlatformTenantSupport;
import com.titanium.clause.query.mapper.ClauseQueryResultMapper;
import com.titanium.clause.query.repository.CoverageViewRepository;
import com.titanium.clause.query.repository.PremiumRuleViewRepository;
import com.titanium.clause.query.result.CoverageQueryResult;
import com.titanium.clause.query.result.PremiumRuleQueryResult;
import com.titanium.clause.query.service.ClauseRuleQueryService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 条款规则组件查询服务实现（CQRS 读侧）
 * <p>
 * 查询读模型表 {@code t_coverage_view} / {@code t_premium_rule_view}（由 {@code ClauseProjectionEventHandler}
 * 投影维护），JSON 列反序列化还原结构化值对象后组装为稳定 DTO 返回，禁止直接返回读模型实体。读模型 → DTO
 * 的结构映射（含 JSON 反序列化）收敛到 {@link ClauseQueryResultMapper}，此处不再逐字段 set。
 * </p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ClauseRuleQueryServiceImpl implements ClauseRuleQueryService {

    private final CoverageViewRepository    coverageViewRepository;
    private final PremiumRuleViewRepository premiumRuleViewRepository;
    private final ClauseQueryResultMapper   clauseQueryResultMapper;

    @Override
    @Transactional(readOnly = true)
    public List<CoverageQueryResult> getCoveragesByClauseId(String clauseId, String tenantId) {
        return coverageViewRepository.findByClauseIdAndTenantIdIn(clauseId, PlatformTenantSupport.scope(tenantId))
                .stream()
                .map(clauseQueryResultMapper::toCoverageResult)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PremiumRuleQueryResult> getPremiumRuleByClauseId(String clauseId, String tenantId) {
        return premiumRuleViewRepository.findByClauseIdAndTenantId(clauseId, tenantId)
                .map(clauseQueryResultMapper::toPremiumRuleResult);
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
                    .map(clauseQueryResultMapper::toPremiumRuleResult);
        }

        // tableCode 和 version 均非空 → 精确匹配
        if (tableCode != null && version != null) {
            return premiumRuleViewRepository
                    .findByClauseIdAndTableCodeAndTableVersionAndTenantId(clauseId, tableCode, version, tenantId)
                    .map(clauseQueryResultMapper::toPremiumRuleResult);
        }

        // version 非空但 tableCode 为 null（不合理场景，返回空）
        log.warn("费率表版本查询参数不合理: clauseId={}, tableCode=null, version={}", clauseId, version);
        return Optional.empty();
    }
}
