package com.titanium.clause.query.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.titanium.clause.query.view.ClauseView;
import com.titanium.metadata.enums.InsuranceType;
import com.titanium.metadata.enums.clause.ClauseEnum;

/**
 * 条款读模型仓储
 * <p>
 * CQRS 查询侧仓储，访问读模型表 {@code t_clause_view}。租户隔离经 {@code tenantId} 条件下推。
 * </p>
 */
@Repository
public interface ClauseViewRepository
        extends JpaRepository<ClauseView, String>, JpaSpecificationExecutor<ClauseView> {

    Optional<ClauseView> findByClauseIdAndTenantId(String clauseId, String tenantId);

    Optional<ClauseView> findByClauseCodeAndTenantId(String clauseCode, String tenantId);

    List<ClauseView> findByStatusAndTenantId(ClauseEnum.ClauseStatus status, String tenantId);

    List<ClauseView> findByInsuranceTypeAndTenantId(InsuranceType insuranceType, String tenantId);

    List<ClauseView> findByTenantId(String tenantId);
}
