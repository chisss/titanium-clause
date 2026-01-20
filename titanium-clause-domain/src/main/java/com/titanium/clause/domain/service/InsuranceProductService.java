package com.titanium.clause.domain.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.titanium.clause.domain.aggregate.InsuranceProduct;
import com.titanium.clause.domain.command.CreateProductCommand;
import com.titanium.clause.domain.command.UpdateProductCommand;
import com.titanium.clause.domain.repository.InsuranceProductRepository;
import com.titanium.clause.domain.valueobject.InsuranceProductId;
import com.titanium.clause.domain.valueobject.ProductCode;
import com.titanium.clause.domain.valueobject.ProductName;

import lombok.RequiredArgsConstructor;

/**
 * 保险产品服务类
 */
@Component
@RequiredArgsConstructor
public class InsuranceProductService {
    private final InsuranceProductRepository productRepository;

    /**
     * 创建保险产品
     * 
     * @param command 创建保险产品命令
     * @return 保险产品
     */
    @Transactional
    public InsuranceProduct createProduct(CreateProductCommand command) {
        InsuranceProduct product = new InsuranceProduct();
        product.setProductId(InsuranceProductId.fromString(command.productId().getValue()));
        product.setProductCode(ProductCode.fromString(command.productCode().getValue()));
        product.setProductName(ProductName.fromString(command.productName().getValue()));
        product.setProductType(command.productType());
        product.setProductClass(command.productClass());
        product.setDescription(command.description());
        product.setStatus(command.status());
        product.setTenantId(command.tenantId());
        product.setCreatedBy(command.createdBy());

        return productRepository.save(product);
    }

    /**
     * 更新保险产品
     * 
     * @param command 更新保险产品命令
     * @return 保险产品
     */
    @Transactional
    public InsuranceProduct updateProduct(UpdateProductCommand command) {
        Optional<InsuranceProduct> optionalProduct = productRepository
                .findById(InsuranceProductId.fromString(command.productId().getValue()), command.tenantId());

        if (optionalProduct.isEmpty()) {
            throw new IllegalArgumentException("保险产品不存在");
        }

        InsuranceProduct product = optionalProduct.get();
        product.setProductCode(ProductCode.fromString(command.productId().getValue()));
        product.setProductName(ProductName.fromString(command.productName().getValue()));
        product.setProductType(command.productType());
        product.setProductClass(command.productClass());
        product.setDescription(command.description());
        product.setStatus(command.status());
        product.setUpdatedBy(command.updatedBy());

        return productRepository.save(product);
    }

    /**
     * 根据ID查询保险产品
     * 
     * @param productId 产品ID
     * @param tenantId 租户ID
     * @return 保险产品
     */
    public Optional<InsuranceProduct> findProductById(String productId, String tenantId) {
        return productRepository.findById(InsuranceProductId.fromString(productId), tenantId);
    }

    /**
     * 根据产品代码查询保险产品
     * 
     * @param productCode 产品代码
     * @param tenantId 租户ID
     * @return 保险产品
     */
    public Optional<InsuranceProduct> findProductByCode(String productCode, String tenantId) {
        return productRepository.findByCode(ProductCode.fromString(productCode), tenantId);
    }

    /**
     * 根据状态查询保险产品
     * 
     * @param status 状态
     * @param tenantId 租户ID
     * @return 保险产品列表
     */
    public List<InsuranceProduct> findProductsByStatus(String status, String tenantId) {
        return productRepository.findByStatus(status, tenantId);
    }

    /**
     * 根据产品类型查询保险产品
     * 
     * @param productType 产品类型
     * @param tenantId 租户ID
     * @return 保险产品列表
     */
    public List<InsuranceProduct> findProductsByType(String productType, String tenantId) {
        return productRepository.findByType(productType, tenantId);
    }

    /**
     * 查询所有保险产品
     * 
     * @param tenantId 租户ID
     * @return 保险产品列表
     */
    public List<InsuranceProduct> findAllProducts(String tenantId) {
        return productRepository.findAll(tenantId);
    }

    /**
     * 删除保险产品
     * 
     * @param productId 产品ID
     * @param tenantId 租户ID
     */
    @Transactional
    public void deleteProduct(String productId, String tenantId) {
        productRepository.deleteById(InsuranceProductId.fromString(productId), tenantId);
    }

    /**
     * 为保险产品添加条款
     * 
     * @param productId 产品ID
     * @param clauseId 条款ID
     * @param tenantId 租户ID
     * @return 更新后的保险产品
     */
    @Transactional
    public InsuranceProduct addClauseToProduct(String productId, String clauseId, String tenantId) {
        Optional<InsuranceProduct> optionalProduct = productRepository
                .findById(InsuranceProductId.fromString(productId), tenantId);

        if (optionalProduct.isEmpty()) {
            throw new IllegalArgumentException("保险产品不存在");
        }

        InsuranceProduct product = optionalProduct.get();
        product.addClause(clauseId);

        return productRepository.save(product);
    }

    /**
     * 从保险产品中移除条款
     * 
     * @param productId 产品ID
     * @param clauseId 条款ID
     * @param tenantId 租户ID
     * @return 更新后的保险产品
     */
    @Transactional
    public InsuranceProduct removeClauseFromProduct(String productId, String clauseId, String tenantId) {
        Optional<InsuranceProduct> optionalProduct = productRepository
                .findById(InsuranceProductId.fromString(productId), tenantId);

        if (optionalProduct.isEmpty()) {
            throw new IllegalArgumentException("保险产品不存在");
        }

        InsuranceProduct product = optionalProduct.get();
        product.removeClause(clauseId);

        return productRepository.save(product);
    }
}
