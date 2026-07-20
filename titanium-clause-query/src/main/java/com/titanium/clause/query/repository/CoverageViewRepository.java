package com.titanium.clause.query.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.titanium.clause.query.view.CoverageView;

/**
 * 保险责任读模型仓储
 * <p>
 * CQRS 查询侧仓储，访问读模型表 {@code t_coverage_view}。租户隔离经 {@code tenantId} 条件下推。
 * 一条款可含多个责任，故提供按 {@code clauseId + tenantId} 批量查询。
 * </p>
 */
@Repository
public interface CoverageViewRepository
        extends JpaRepository<CoverageView, String>, JpaSpecificationExecutor<CoverageView> {

    /**
     * 按条款ID + 租户ID查询该条款下全部责任
     */
    List<CoverageView> findByClauseIdAndTenantId(String clauseId, String tenantId);

    /**
     * 按责任ID + 租户ID查询单个责任
     */
    Optional<CoverageView> findByCoverageIdAndTenantId(String coverageId, String tenantId);
}
