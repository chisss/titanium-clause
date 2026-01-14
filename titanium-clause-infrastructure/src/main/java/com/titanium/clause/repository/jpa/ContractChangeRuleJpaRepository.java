package com.titanium.clause.repository.jpa;

import com.titanium.clause.entity.ContractChangeRuleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * 合同变更规则JPA仓储接口
 */
public interface ContractChangeRuleJpaRepository extends JpaRepository<ContractChangeRuleEntity, String> {
    /**
     * 根据续保类型和租户ID查找合同变更规则
     * @param renewalType 续保类型
     * @param tenantId 租户ID
     * @return 合同变更规则实体列表
     */
    List<ContractChangeRuleEntity> findByRenewalTypeAndTenantId(String renewalType, String tenantId);

    /**
     * 根据租户ID查找所有合同变更规则
     * @param tenantId 租户ID
     * @return 合同变更规则实体列表
     */
    List<ContractChangeRuleEntity> findByTenantId(String tenantId);

    /**
     * 根据租户ID和是否删除查找合同变更规则
     * @param tenantId 租户ID
     * @param isDeleted 是否删除
     * @return 合同变更规则实体列表
     */
    List<ContractChangeRuleEntity> findByTenantIdAndIsDeleted(String tenantId, Integer isDeleted);

    /**
     * 删除合同变更规则
     * @param id 合同变更规则ID
     * @param tenantId 租户ID
     */
    @Query("DELETE FROM ContractChangeRuleEntity c WHERE c.id = :id AND c.tenantId = :tenantId")
    void deleteByIdAndTenantId(@Param("id") String id, @Param("tenantId") String tenantId);
}