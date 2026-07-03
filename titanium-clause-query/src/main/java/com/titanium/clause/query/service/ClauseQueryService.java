package com.titanium.clause.query.service;

import java.util.List;
import java.util.Optional;

import com.titanium.clause.query.result.ClauseQueryResult;
import com.titanium.metadata.enums.InsuranceType;
import com.titanium.metadata.enums.clause.ClauseEnum;

/**
 * 条款查询服务（CQRS 读侧）
 * <p>
 * 查询由事件投影维护的读模型表 {@code t_clause_view}，返回稳定 DTO 契约。
 * </p>
 */
public interface ClauseQueryService {

    /**
     * 根据ID查询条款
     */
    Optional<ClauseQueryResult> getClauseById(String clauseId, String tenantId);

    /**
     * 根据条款代码查询条款
     */
    Optional<ClauseQueryResult> getClauseByCode(String clauseCode, String tenantId);

    /**
     * 根据状态查询条款列表
     */
    List<ClauseQueryResult> getClausesByStatus(ClauseEnum.ClauseStatus status, String tenantId);

    /**
     * 根据险种类型查询条款列表
     */
    List<ClauseQueryResult> getClausesByType(InsuranceType insuranceType, String tenantId);

    /**
     * 查询全部条款列表
     */
    List<ClauseQueryResult> getAllClauses(String tenantId);
}
