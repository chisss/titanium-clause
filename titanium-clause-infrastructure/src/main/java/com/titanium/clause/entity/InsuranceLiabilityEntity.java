package com.titanium.clause.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 保险责任实体类
 */
@Entity
@Table(name = "t_insurance_liability",
        indexes = {
            @Index(name = "idx_liability_code", columnList = "code"),
            @Index(name = "idx_liability_status", columnList = "status"),
            @Index(name = "idx_liability_tenant_id", columnList = "tenant_id")
        }
)
@Data
@NoArgsConstructor
public class InsuranceLiabilityEntity {
    @Id
    @Column(name = "id", length = 32, nullable = false)
    private String id;

    @Column(name = "code", length = 50, nullable = false)
    private String code;

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Column(name = "coverage", precision = 18, scale = 2)
    private Double coverage;

    @Column(name = "premium_rate", precision = 18, scale = 6)
    private Double premiumRate;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "status", length = 32, nullable = false)
    private String status;

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