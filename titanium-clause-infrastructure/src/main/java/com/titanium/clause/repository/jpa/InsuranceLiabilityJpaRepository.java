package com.titanium.clause.repository.jpa;

import com.titanium.clause.entity.InsuranceLiabilityEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * 保险责任JPA仓库接口
 */
public interface InsuranceLiabilityJpaRepository extends JpaRepository<InsuranceLiabilityEntity, String> {
    /**
     * 根据ID和租户ID查找保险责任
     * @param id 责任ID
     * @param tenantId 租户ID
     * @return 保险责任实体
     */
    Optional<InsuranceLiabilityEntity> findByIdAndTenantId(String id, String tenantId);

    /**
     * 根据代码和租户ID查找保险责任
     * @param code 责任代码
     * @param tenantId 租户ID
     * @return 保险责任实体
     */
    Optional<InsuranceLiabilityEntity> findByCodeAndTenantId(String code, String tenantId);

    /**
     * 根据状态和租户ID查找保险责任
     * @param status 状态
     * @param tenantId 租户ID
     * @return 保险责任实体列表
     */
    List<InsuranceLiabilityEntity> findByStatusAndTenantId(String status, String tenantId);

    /**
     * 根据租户ID查找所有保险责任
     * @param tenantId 租户ID
     * @return 保险责任实体列表
     */
    List<InsuranceLiabilityEntity> findByTenantId(String tenantId);

    /**
     * 根据ID和租户ID删除保险责任
     * @param id 责任ID
     * @param tenantId 租户ID
     */
    void deleteByIdAndTenantId(@Param("id") String id, @Param("tenantId") String tenantId);
}