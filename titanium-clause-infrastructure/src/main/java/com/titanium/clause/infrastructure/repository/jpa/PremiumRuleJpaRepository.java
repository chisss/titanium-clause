package com.titanium.clause.infrastructure.repository.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.titanium.clause.infrastructure.entity.PremiumRuleEntity;

/**
 * 缴费规则JPA仓储接口
 */
public interface PremiumRuleJpaRepository extends JpaRepository<PremiumRuleEntity, String> {
    /**
     * 根据计算方式和租户ID查找缴费规则
     * @param calculationMethod 计算方式
     * @param tenantId 租户ID
     * @return 缴费规则实体列表
     */
    List<PremiumRuleEntity> findByCalculationMethodAndTenantId(String calculationMethod, String tenantId);

    /**
     * 根据租户ID查找所有缴费规则
     * @param tenantId 租户ID
     * @return 缴费规则实体列表
     */
    List<PremiumRuleEntity> findByTenantId(String tenantId);

    /**
     * 根据租户ID和是否删除查找缴费规则
     * @param tenantId 租户ID
     * @param isDeleted 是否删除
     * @return 缴费规则实体列表
     */
    List<PremiumRuleEntity> findByTenantIdAndIsDeleted(String tenantId, Integer isDeleted);

    /**
     * 删除缴费规则
     * @param id 缴费规则ID
     * @param tenantId 租户ID
     */
    @Query("DELETE FROM PremiumRuleEntity p WHERE p.id = :id AND p.tenantId = :tenantId")
    void deleteByIdAndTenantId(@Param("id") String id, @Param("tenantId") String tenantId);
}
