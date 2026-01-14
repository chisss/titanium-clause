package com.titanium.clause.repository;

import com.titanium.clause.aggregate.InsuranceLiability;
import com.titanium.clause.valueobject.InsuranceLiabilityId;
import com.titanium.clause.valueobject.LiabilityCode;

import java.util.List;
import java.util.Optional;

/**
 * 保险责任仓库接口
 */
public interface InsuranceLiabilityRepository {
    /**
     * 保存保险责任
     * @param liability 保险责任
     * @return 保存后的保险责任
     */
    InsuranceLiability save(InsuranceLiability liability);

    /**
     * 根据ID查找保险责任
     * @param liabilityId 责任ID
     * @param tenantId 租户ID
     * @return 保险责任
     */
    Optional<InsuranceLiability> findById(InsuranceLiabilityId liabilityId, String tenantId);

    /**
     * 根据代码查找保险责任
     * @param code 责任代码
     * @param tenantId 租户ID
     * @return 保险责任
     */
    Optional<InsuranceLiability> findByCode(LiabilityCode code, String tenantId);

    /**
     * 根据状态查找保险责任
     * @param status 状态
     * @param tenantId 租户ID
     * @return 保险责任列表
     */
    List<InsuranceLiability> findByStatus(String status, String tenantId);

    /**
     * 根据租户ID查找所有保险责任
     * @param tenantId 租户ID
     * @return 保险责任列表
     */
    List<InsuranceLiability> findAll(String tenantId);

    /**
     * 根据ID删除保险责任
     * @param liabilityId 责任ID
     * @param tenantId 租户ID
     */
    void deleteById(InsuranceLiabilityId liabilityId, String tenantId);
}