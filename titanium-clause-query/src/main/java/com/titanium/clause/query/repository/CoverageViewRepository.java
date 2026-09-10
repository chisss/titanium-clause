package com.titanium.clause.query.repository;

import java.util.Collection;
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
 * {@code ...TenantIdIn} 支持「当前租户 + 平台公共租户」的平台默认回退查询。
 * </p>
 * <p>
 * <b>复合主键（修订→新版本机制）</b>：{@code (coverageId, clauseId)} 联合唯一。修订版本继承原条款责任
 * （责任ID复用），故单责任定位必须同时携带 clauseId 才能锁定所属版本的行，避免跨版本改写/误删。
 * </p>
 */
@Repository
public interface CoverageViewRepository
        extends JpaRepository<CoverageView, CoverageView.CoverageViewId>, JpaSpecificationExecutor<CoverageView> {

    /**
     * 按条款ID + 租户ID查询该条款下全部责任（单版本条款读模型）
     */
    List<CoverageView> findByClauseIdAndTenantId(String clauseId, String tenantId);

    /**
     * 按责任ID + 所属条款ID + 租户ID查询单个责任（复合主键定位，责任随条款版本隔离）
     */
    Optional<CoverageView> findByCoverageIdAndClauseIdAndTenantId(String coverageId, String clauseId,
                                                                  String tenantId);

    /**
     * 按责任ID + 所属条款ID + 租户ID删除单个责任（仅删除该条款版本下的责任行，不误伤其它版本）
     *
     * @return 删除行数（0 表示未命中）
     */
    long deleteByCoverageIdAndClauseIdAndTenantId(String coverageId, String clauseId, String tenantId);

    /**
     * 平台默认回退：按条款ID + 租户集合（当前租户 + 平台公共租户）查询该条款下全部责任
     */
    List<CoverageView> findByClauseIdAndTenantIdIn(String clauseId, Collection<String> tenantIds);
}
