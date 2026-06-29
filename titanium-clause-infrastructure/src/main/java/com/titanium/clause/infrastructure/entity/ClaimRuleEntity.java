package com.titanium.clause.infrastructure.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 理赔规则实体类
 */
@Entity
@Table(name = "t_claim_rule",
        indexes = {
            @Index(name = "idx_claim_rule_tenant_id", columnList = "tenant_id")
        }
)
@Data
@NoArgsConstructor
public class ClaimRuleEntity {
    @Id
    @Column(name = "id", length = 32, nullable = false)
    private String id;

    @Column(name = "report_deadline_days")
    private Integer reportDeadlineDays;

    @Column(name = "required_materials", columnDefinition = "TEXT")
    private String requiredMaterials;

    @Column(name = "settlement_period_days")
    private Integer settlementPeriodDays;

    @Column(name = "payout_ratio", length = 50)
    private String payoutRatio;

    @Column(name = "deductible_amount", length = 50)
    private String deductibleAmount;

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
