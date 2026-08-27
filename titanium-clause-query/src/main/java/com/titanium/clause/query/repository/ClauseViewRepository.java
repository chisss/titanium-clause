package com.titanium.clause.query.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.titanium.clause.query.view.ClauseView;
import com.titanium.metadata.enums.clause.ClauseEnum;
import com.titanium.metadata.enums.insurance.InsuranceProductType;

/**
 * 条款读模型仓储
 * <p>
 * CQRS 查询侧仓储，访问读模型表 {@code t_clause_view}。租户隔离经 {@code tenantId} 条件下推。
 * {@code ...TenantIdIn} 系列支持「当前租户 + 平台公共租户」的平台默认回退查询（见
 * {@link com.titanium.clause.common.constant.ClauseConstants#PLATFORM_TENANT}）。
 * </p>
 */
@Repository
public interface ClauseViewRepository
        extends JpaRepository<ClauseView, String>, JpaSpecificationExecutor<ClauseView> {

    Optional<ClauseView> findByClauseIdAndTenantId(String clauseId, String tenantId);

    Optional<ClauseView> findByClauseCodeAndTenantId(String clauseCode, String tenantId);

    List<ClauseView> findByStatusAndTenantId(ClauseEnum.ClauseStatus status, String tenantId);

    List<ClauseView> findByInsuranceTypeAndTenantId(InsuranceProductType insuranceType, String tenantId);

    List<ClauseView> findByTenantId(String tenantId);

    // ===== 平台默认回退：当前租户 + 平台公共租户 =====

    Optional<ClauseView> findByClauseIdAndTenantIdIn(String clauseId, Collection<String> tenantIds);

    Optional<ClauseView> findByClauseCodeAndTenantIdIn(String clauseCode, Collection<String> tenantIds);

    List<ClauseView> findByStatusAndTenantIdIn(ClauseEnum.ClauseStatus status, Collection<String> tenantIds);

    List<ClauseView> findByInsuranceTypeAndTenantIdIn(InsuranceProductType insuranceType, Collection<String> tenantIds);

    List<ClauseView> findByTenantIdIn(Collection<String> tenantIds);
}
