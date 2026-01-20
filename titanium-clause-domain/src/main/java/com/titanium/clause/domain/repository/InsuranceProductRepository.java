package com.titanium.clause.domain.repository;

import com.titanium.clause.domain.aggregate.InsuranceProduct;
import com.titanium.clause.domain.valueobject.InsuranceProductId;
import com.titanium.clause.domain.valueobject.ProductCode;

import java.util.List;
import java.util.Optional;

/**
 * 保险产品仓库接口
 */
public interface InsuranceProductRepository {
    /**
     * 保存保险产品
     * @param product 保险产品
     * @return 保存后的保险产品
     */
    InsuranceProduct save(InsuranceProduct product);

    /**
     * 根据ID查找保险产品
     * @param productId 产品ID
     * @param tenantId 租户ID
     * @return 保险产品
     */
    Optional<InsuranceProduct> findById(InsuranceProductId productId, String tenantId);

    /**
     * 根据产品代码查找保险产品
     * @param productCode 产品代码
     * @param tenantId 租户ID
     * @return 保险产品
     */
    Optional<InsuranceProduct> findByCode(ProductCode productCode, String tenantId);

    /**
     * 根据状态查找保险产品
     * @param status 状态
     * @param tenantId 租户ID
     * @return 保险产品列表
     */
    List<InsuranceProduct> findByStatus(String status, String tenantId);

    /**
     * 根据产品类型查找保险产品
     * @param productType 产品类型
     * @param tenantId 租户ID
     * @return 保险产品列表
     */
    List<InsuranceProduct> findByType(String productType, String tenantId);

    /**
     * 查找所有保险产品
     * @param tenantId 租户ID
     * @return 保险产品列表
     */
    List<InsuranceProduct> findAll(String tenantId);

    /**
     * 删除保险产品
     * @param productId 产品ID
     * @param tenantId 租户ID
     */
    void deleteById(InsuranceProductId productId, String tenantId);
}