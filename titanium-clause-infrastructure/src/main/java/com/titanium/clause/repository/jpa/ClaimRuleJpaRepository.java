package com.titanium.clause.repository.jpa;

import com.titanium.clause.entity.ClaimRuleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * 理赔规则JPA仓储接口
 */
public interface ClaimRuleJpaRepository extends JpaRepository<ClaimRuleEntity, String> {
    /**
     * 根据租户ID查找所有理赔规则
     * @param tenantId 租户ID
     * @return 理赔规则实体列表
     */
    List<ClaimRuleEntity> findByTenantId(String tenantId);

    /**
     * 根据租户ID和是否删除查找理赔规则
     * @param tenantId 租户ID
     * @param isDeleted 是否删除
     * @return 理赔规则实体列表
     */
    List<ClaimRuleEntity> findByTenantIdAndIsDeleted(String tenantId, Integer isDeleted);

    /**
     * 删除理赔规则
     * @param id 理赔规则ID
     * @param tenantId 租户ID
     */
    @Query("DELETE FROM ClaimRuleEntity c WHERE c.id = :id AND c.tenantId = :tenantId")
    void deleteByIdAndTenantId(@Param("id") String id, @Param("tenantId") String tenantId);
}