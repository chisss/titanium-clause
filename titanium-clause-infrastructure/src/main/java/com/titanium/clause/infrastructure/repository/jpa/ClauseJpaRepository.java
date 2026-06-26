package com.titanium.clause.infrastructure.repository.jpa;

import com.titanium.clause.infrastructure.entity.ClauseEntity;
import com.titanium.metadata.enums.InsuranceType;
import com.titanium.metadata.enums.clause.ClauseEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * 条款JPA仓储接口
 */
public interface ClauseJpaRepository extends JpaRepository<ClauseEntity, String> {
    /**
     * 根据条款代码、版本和租户ID查找条款
     * @param clauseCode 条款代码
     * @param version 条款版本
     * @param tenantId 租户ID
     * @return 条款实体
     */
    Optional<ClauseEntity> findByClauseCodeAndVersionAndTenantId(String clauseCode, String version, String tenantId);

    /**
     * 根据条款代码和租户ID查找条款（所有版本）
     * @param clauseCode 条款代码
     * @param tenantId 租户ID
     * @return 条款实体列表
     */
    List<ClauseEntity> findByClauseCodeAndTenantId(String clauseCode, String tenantId);

    /**
     * 根据状态和租户ID查找条款
     * @param status 条款状态
     * @param tenantId 租户ID
     * @return 条款实体列表
     */
    List<ClauseEntity> findByStatusAndTenantId(ClauseEnum.ClauseStatus status, String tenantId);

    /**
     * 根据险种类型和租户ID查找条款
     * @param insuranceType 险种类型
     * @param tenantId 租户ID
     * @return 条款实体列表
     */
    List<ClauseEntity> findByInsuranceTypeAndTenantId(InsuranceType insuranceType, String tenantId);

    /**
     * 根据租户ID查找所有条款
     * @param tenantId 租户ID
     * @return 条款实体列表
     */
    List<ClauseEntity> findByTenantId(String tenantId);

    /**
     * 根据租户ID和是否删除查找条款
     * @param tenantId 租户ID
     * @param isDeleted 是否删除
     * @return 条款实体列表
     */
    List<ClauseEntity> findByTenantIdAndIsDeleted(String tenantId, Integer isDeleted);

    /**
     * 删除条款
     * @param id 条款ID
     * @param tenantId 租户ID
     */
    @Query("DELETE FROM ClauseEntity c WHERE c.id = :id AND c.tenantId = :tenantId")
    void deleteByIdAndTenantId(@Param("id") String id, @Param("tenantId") String tenantId);
}
