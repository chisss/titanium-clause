package com.titanium.clause.query.handler.query;

import java.util.List;

import org.axonframework.config.ProcessingGroup;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

import com.titanium.clause.query.query.FindCoveragesByClauseIdQuery;
import com.titanium.clause.query.query.FindPremiumRuleByClauseIdAndVersionQuery;
import com.titanium.clause.query.query.FindPremiumRuleByClauseIdQuery;
import com.titanium.clause.query.result.CoverageQueryResult;
import com.titanium.clause.query.result.PremiumRuleQueryResult;
import com.titanium.clause.query.service.ClauseRuleQueryService;

import lombok.RequiredArgsConstructor;

/**
 * 条款规则组件查询处理器（CQRS 读侧 Axon 查询处理）
 * <p>
 * 接收 {@code FindCoveragesByClauseIdQuery}/{@code FindPremiumRuleByClauseIdQuery}，委托
 * {@link ClauseRuleQueryService} 查询责任/费率读模型并返回 DTO（费率不存在时返回 {@code null}）。
 * </p>
 */
@Component
@RequiredArgsConstructor
@ProcessingGroup("clause-query-group")
public class ClauseRuleQueryHandler {

    private final ClauseRuleQueryService clauseRuleQueryService;

    @QueryHandler
    public List<CoverageQueryResult> handle(FindCoveragesByClauseIdQuery query) {
        return clauseRuleQueryService.getCoveragesByClauseId(query.clauseId(), query.tenantId());
    }

    @QueryHandler
    public PremiumRuleQueryResult handle(FindPremiumRuleByClauseIdQuery query) {
        return clauseRuleQueryService.getPremiumRuleByClauseId(query.clauseId(), query.tenantId()).orElse(null);
    }

    /**
     * 按费率表编码+版本查询缴费规则（BILL-2）
     */
    @QueryHandler
    public PremiumRuleQueryResult handle(FindPremiumRuleByClauseIdAndVersionQuery query) {
        return clauseRuleQueryService
                .getPremiumRuleByClauseIdAndVersion(query.clauseId(), query.tableCode(), query.version(),
                        query.tenantId())
                .orElse(null);
    }
}
