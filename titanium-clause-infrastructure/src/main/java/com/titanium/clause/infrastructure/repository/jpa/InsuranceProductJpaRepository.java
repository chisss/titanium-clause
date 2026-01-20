package com.titanium.clause.infrastructure.repository.jpa;

import com.titanium.clause.infrastructure.entity.InsuranceProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * 保险产品JPA仓库接口
 */
public interface InsuranceProductJpaRepository extends JpaRepository<InsuranceProductEntity, String> {
    /**
     * 根据ID和租户ID查找保险产品
     * @param id 产品ID
     * @param tenantId 租户ID
     * @return 保险产品实体
     */
    Optional<InsuranceProductEntity> findByIdAndTenantId(String id, String tenantId);

    /**
     * 根据产品代码和租户ID查找保险产品
     * @param productCode 产品代码
     * @param tenantId 租户ID
     * @return 保险产品实体
     */
    Optional<InsuranceProductEntity> findByProductCodeAndTenantId(String productCode, String tenantId);

    /**
     * 根据状态和租户ID查找保险产品
     * @param status 状态
     * @param tenantId 租户ID
     * @return 保险产品实体列表
     */
    List<InsuranceProductEntity> findByStatusAndTenantId(String status, String tenantId);

    /**
     * 根据产品类型和租户ID查找保险产品
     * @param productType 产品类型
     * @param tenantId 租户ID
     * @return 保险产品实体列表
     */
    List<InsuranceProductEntity> findByProductTypeAndTenantId(String productType, String tenantId);

    /**
     * 根据租户ID和是否删除查找保险产品
     * @param tenantId 租户ID
     * @param isDeleted 是否删除
     * @return 保险产品实体列表
     */
    List<InsuranceProductEntity> findByTenantIdAndIsDeleted(String tenantId, Integer isDeleted);

    /**
     * 根据条款ID查找关联的保险产品
     * @param clauseId 条款ID
     * @param tenantId 租户ID
     * @return 保险产品实体列表
     */
    @Query("SELECT p FROM InsuranceProductEntity p JOIN p.clauseIds c WHERE c = :clauseId AND p.tenantId = :tenantId AND p.isDeleted = 0")
    List<InsuranceProductEntity> findByClauseIdAndTenantId(@Param("clauseId") String clauseId, @Param("tenantId") String tenantId);

    /**
     * 根据ID和租户ID删除保险产品
     * @param id 产品ID
     * @param tenantId 租户ID
     */
    @Query("DELETE FROM InsuranceProductEntity p WHERE p.id = :id AND p.tenantId = :tenantId")
    void deleteByIdAndTenantId(@Param("id") String id, @Param("tenantId") String tenantId);
}