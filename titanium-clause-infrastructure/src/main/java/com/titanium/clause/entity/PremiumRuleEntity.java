package com.titanium.clause.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 缴费规则实体类
 */
@Entity
@Table(name = "t_premium_rule",
        indexes = {
            @Index(name = "idx_premium_rule_tenant_id", columnList = "tenant_id")
        }
)
@Data
@NoArgsConstructor
public class PremiumRuleEntity {
    @Id
    @Column(name = "id", length = 32, nullable = false)
    private String id;

    @Column(name = "calculation_method", length = 32, nullable = false)
    private String calculationMethod;

    @Column(name = "base_premium", precision = 18, scale = 2)
    private BigDecimal basePremium;

    @Column(name = "premium_rate", precision = 18, scale = 6)
    private BigDecimal premiumRate;

    @Column(name = "payment_method", length = 32, nullable = false)
    private String paymentMethod;

    @Column(name = "payment_term")
    private Integer paymentTerm;

    @Column(name = "grace_period_days")
    private Integer gracePeriodDays;

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