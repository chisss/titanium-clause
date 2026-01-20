package com.titanium.clause.infrastructure.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * 保险产品实体类
 */
@Entity
@Table(name = "t_insurance_product",
        indexes = {
            @Index(name = "idx_product_code", columnList = "product_code"),
            @Index(name = "idx_status", columnList = "status"),
            @Index(name = "idx_product_type", columnList = "product_type"),
            @Index(name = "idx_tenant_id", columnList = "tenant_id"),
            @Index(name = "idx_main_product", columnList = "main_product")
        }
)
@Data
@NoArgsConstructor
public class InsuranceProductEntity {
    @Id
    @Column(name = "id", length = 32, nullable = false)
    private String id;

    @Column(name = "product_code", length = 50, nullable = false)
    private String productCode;

    @Column(name = "product_name", length = 100, nullable = false)
    private String productName;

    @Column(name = "product_type", length = 32, nullable = false)
    private String productType;

    @Column(name = "product_class", length = 32, nullable = false)
    private String productClass;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "status", length = 32, nullable = false)
    private String status;

    @Column(name = "main_product", nullable = false)
    private Integer mainProduct;

    @Column(name = "currency", length = 10, nullable = false)
    private String currency;

    @Column(name = "grace_period")
    private Integer gracePeriod;

    @Column(name = "free_look_period")
    private Integer freeLookPeriod;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "t_insurance_product_clause",
            joinColumns = @JoinColumn(name = "product_id"),
            indexes = @Index(name = "idx_product_id", columnList = "product_id"))
    @Column(name = "clause_id", length = 32)
    private Set<String> clauseIds = new HashSet<>();

    @Column(name = "tenant_id", length = 32, nullable = false)
    private String tenantId;

    @Column(name = "create_time", nullable = false, updatable = false)
    private LocalDateTime createTime;

    @Column(name = "update_time", nullable = false)
    private LocalDateTime updateTime;

    @Column(name = "created_by", length = 32, nullable = false)
    private String createdBy;

    @Column(name = "updated_by", length = 32, nullable = false)
    private String updatedBy;

    @Column(name = "is_deleted", nullable = false)
    private Integer isDeleted;
}