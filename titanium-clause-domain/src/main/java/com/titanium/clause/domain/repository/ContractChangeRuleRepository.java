package com.titanium.clause.domain.repository;

import com.titanium.clause.domain.entity.ContractChangeRule;
import com.titanium.clause.domain.enums.RenewalType;

import java.util.List;
import java.util.Optional;

/**
 * 合同变更规则仓储接口
 */
public interface ContractChangeRuleRepository {
    /**
     * 保存合同变更规则
     * @param contractChangeRule 合同变更规则对象
     * @return 保存后的合同变更规则对象
     */
    ContractChangeRule save(ContractChangeRule contractChangeRule);

    /**
     * 根据ID查找合同变更规则
     * @param id 合同变更规则ID
     * @param tenantId 租户ID
     * @return 合同变更规则对象
     */
    Optional<ContractChangeRule> findById(String id, String tenantId);

    /**
     * 根据续保类型查找合同变更规则
     * @param renewalType 续保类型
     * @param tenantId 租户ID
     * @return 合同变更规则列表
     */
    List<ContractChangeRule> findByRenewalType(RenewalType renewalType, String tenantId);

    /**
     * 查找所有合同变更规则
     * @param tenantId 租户ID
     * @return 合同变更规则列表
     */
    List<ContractChangeRule> findAll(String tenantId);

    /**
     * 删除合同变更规则
     * @param id 合同变更规则ID
     * @param tenantId 租户ID
     */
    void deleteById(String id, String tenantId);
}