package com.titanium.clause.query.handler.query;

import java.util.List;

import org.axonframework.config.ProcessingGroup;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

import com.titanium.clause.query.query.FindAllClausesQuery;
import com.titanium.clause.query.query.FindClauseByCodeQuery;
import com.titanium.clause.query.query.FindClauseByIdQuery;
import com.titanium.clause.query.query.FindClausesByStatusQuery;
import com.titanium.clause.query.query.FindClausesByTypeQuery;
import com.titanium.clause.query.result.ClauseQueryResult;
import com.titanium.clause.query.service.ClauseQueryService;

import lombok.RequiredArgsConstructor;

/**
 * 条款查询处理器（CQRS 读侧 Axon 查询处理）
 * <p>
 * 接收 {@code FindXxxQuery}，委托 {@link ClauseQueryService} 查询读模型并返回 DTO（不存在时返回 {@code null}）。
 * </p>
 */
@Component
@RequiredArgsConstructor
@ProcessingGroup("clause-query-group")
public class ClauseQueryHandler {

    private final ClauseQueryService clauseQueryService;

    @QueryHandler
    public ClauseQueryResult handle(FindClauseByIdQuery query) {
        return clauseQueryService.getClauseById(query.clauseId(), query.tenantId()).orElse(null);
    }

    @QueryHandler
    public ClauseQueryResult handle(FindClauseByCodeQuery query) {
        return clauseQueryService.getClauseByCode(query.clauseCode(), query.tenantId()).orElse(null);
    }

    @QueryHandler
    public List<ClauseQueryResult> handle(FindClausesByStatusQuery query) {
        return clauseQueryService.getClausesByStatus(query.status(), query.tenantId());
    }

    @QueryHandler
    public List<ClauseQueryResult> handle(FindClausesByTypeQuery query) {
        return clauseQueryService.getClausesByType(query.insuranceType(), query.tenantId());
    }

    @QueryHandler
    public List<ClauseQueryResult> handle(FindAllClausesQuery query) {
        return clauseQueryService.getAllClauses(query.tenantId());
    }
}
