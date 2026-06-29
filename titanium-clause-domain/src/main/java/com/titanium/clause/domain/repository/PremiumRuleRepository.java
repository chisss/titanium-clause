package com.titanium.clause.domain.repository;

import java.util.List;
import java.util.Optional;

import com.titanium.clause.domain.entity.PremiumRule;

/**
 * 缴费规则仓储接口
 */
public interface PremiumRuleRepository {
    /**
     * 保存缴费规则
     * @param premiumRule 缴费规则对象
     * @return 保存后的缴费规则对象
     */
    PremiumRule save(PremiumRule premiumRule);

    /**
     * 根据ID查找缴费规则
     * @param id 缴费规则ID
     * @param tenantId 租户ID
     * @return 缴费规则对象
     */
    Optional<PremiumRule> findById(String id, String tenantId);

    /**
     * 根据计算方式查找缴费规则
     * @param calculationMethod 计算方式
     * @param tenantId 租户ID
     * @return 缴费规则列表
     */
    List<PremiumRule> findByCalculationMethod(String calculationMethod, String tenantId);

    /**
     * 查找所有缴费规则
     * @param tenantId 租户ID
     * @return 缴费规则列表
     */
    List<PremiumRule> findAll(String tenantId);

    /**
     * 删除缴费规则
     * @param id 缴费规则ID
     * @param tenantId 租户ID
     */
    void deleteById(String id, String tenantId);
}
