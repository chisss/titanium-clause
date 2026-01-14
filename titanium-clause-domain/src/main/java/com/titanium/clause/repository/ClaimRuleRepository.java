package com.titanium.clause.repository;

import com.titanium.clause.entity.ClaimRule;

import java.util.List;
import java.util.Optional;

/**
 * 理赔规则仓储接口
 */
public interface ClaimRuleRepository {
    /**
     * 保存理赔规则
     * @param claimRule 理赔规则对象
     * @return 保存后的理赔规则对象
     */
    ClaimRule save(ClaimRule claimRule);

    /**
     * 根据ID查找理赔规则
     * @param id 理赔规则ID
     * @param tenantId 租户ID
     * @return 理赔规则对象
     */
    Optional<ClaimRule> findById(String id, String tenantId);

    /**
     * 查找所有理赔规则
     * @param tenantId 租户ID
     * @return 理赔规则列表
     */
    List<ClaimRule> findAll(String tenantId);

    /**
     * 删除理赔规则
     * @param id 理赔规则ID
     * @param tenantId 租户ID
     */
    void deleteById(String id, String tenantId);
}