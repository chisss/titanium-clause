package com.titanium.clause.repository;

import com.titanium.clause.aggregate.InsuranceProduct;
import com.titanium.clause.entity.InsuranceProductEntity;
import com.titanium.clause.repository.jpa.InsuranceProductJpaRepository;
import com.titanium.clause.valueobject.InsuranceProductId;
import com.titanium.clause.valueobject.ProductCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 保险产品仓库实现类
 */
@Repository
@RequiredArgsConstructor
public class InsuranceProductRepositoryImpl implements InsuranceProductRepository {
    private final InsuranceProductJpaRepository productJpaRepository;

    @Override
    public InsuranceProduct save(InsuranceProduct product) {
        InsuranceProductEntity entity = toEntity(product);
        InsuranceProductEntity savedEntity = productJpaRepository.save(entity);
        return toDomain(savedEntity);
    }

    @Override
    public Optional<InsuranceProduct> findById(InsuranceProductId productId, String tenantId) {
        return productJpaRepository.findByIdAndTenantId(productId.getValue(), tenantId)
                .map(this::toDomain);
    }

    @Override
    public Optional<InsuranceProduct> findByCode(ProductCode productCode, String tenantId) {
        return productJpaRepository.findByProductCodeAndTenantId(productCode.getValue(), tenantId)
                .map(this::toDomain);
    }

    @Override
    public List<InsuranceProduct> findByStatus(String status, String tenantId) {
        return productJpaRepository.findByStatusAndTenantId(status, tenantId)
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<InsuranceProduct> findByType(String productType, String tenantId) {
        return productJpaRepository.findByProductTypeAndTenantId(productType, tenantId)
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<InsuranceProduct> findAll(String tenantId) {
        return productJpaRepository.findByTenantIdAndIsDeleted(tenantId, 0)
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(InsuranceProductId productId, String tenantId) {
        productJpaRepository.deleteByIdAndTenantId(productId.getValue(), tenantId);
    }

    /**
     * 将领域对象转换为数据库实体
     * @param product 领域对象
     * @return 数据库实体
     */
    private InsuranceProductEntity toEntity(InsuranceProduct product) {
        InsuranceProductEntity entity = new InsuranceProductEntity();
        entity.setId(product.getProductId().getValue());
        entity.setProductCode(product.getProductCode().getValue());
        entity.setProductName(product.getProductName().getValue());
        entity.setProductType(product.getProductType());
        entity.setProductClass(product.getProductClass());
        entity.setDescription(product.getDescription());
        entity.setStatus(product.getStatus());
        entity.setMainProduct(product.getMainProduct() != null ? product.getMainProduct() : 0);
        entity.setCurrency(product.getCurrency() != null ? product.getCurrency() : "CNY");
        entity.setGracePeriod(product.getGracePeriod());
        entity.setFreeLookPeriod(product.getFreeLookPeriod());
        entity.setClauseIds(product.getClauseIds());
        entity.setTenantId(product.getTenantId());
        entity.setCreateTime(product.getCreatedAt());
        entity.setUpdateTime(product.getUpdatedAt());
        entity.setCreatedBy(product.getCreatedBy());
        entity.setUpdatedBy(product.getUpdatedBy());
        entity.setIsDeleted(0); // 默认未删除
        return entity;
    }

    /**
     * 将数据库实体转换为领域对象
     * @param entity 数据库实体
     * @return 领域对象
     */
    private InsuranceProduct toDomain(InsuranceProductEntity entity) {
        InsuranceProduct product = new InsuranceProduct();
        product.setProductId(InsuranceProductId.fromString(entity.getId()));
        product.setProductCode(ProductCode.fromString(entity.getProductCode()));
        product.setProductName(com.titanium.clause.valueobject.ProductName.fromString(entity.getProductName()));
        product.setProductType(entity.getProductType());
        product.setProductClass(entity.getProductClass());
        product.setDescription(entity.getDescription());
        product.setStatus(entity.getStatus());
        product.setMainProduct(entity.getMainProduct());
        product.setCurrency(entity.getCurrency());
        product.setGracePeriod(entity.getGracePeriod());
        product.setFreeLookPeriod(entity.getFreeLookPeriod());
        product.setClauseIds(entity.getClauseIds());
        product.setTenantId(entity.getTenantId());
        product.setCreatedBy(entity.getCreatedBy());
        product.setCreatedAt(entity.getCreateTime());
        product.setUpdatedBy(entity.getUpdatedBy());
        product.setUpdatedAt(entity.getUpdateTime());
        return product;
    }
}