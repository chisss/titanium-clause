package com.titanium.clause.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 合同变更规则实体类
 */
@Entity
@Table(name = "t_contract_change_rule",
        indexes = {
            @Index(name = "idx_contract_change_rule_tenant_id", columnList = "tenant_id")
        }
)
@Data
@NoArgsConstructor
public class ContractChangeRuleEntity {
    @Id
    @Column(name = "id", length = 32, nullable = false)
    private String id;

    @Column(name = "surrender_cash_value_rule", columnDefinition = "TEXT")
    private String surrenderCashValueRule;

    @Column(name = "renewal_type", length = 32, nullable = false)
    private String renewalType;

    @Column(name = "reinstatement_condition", columnDefinition = "TEXT")
    private String reinstatementCondition;

    @Column(name = "waiting_period_days")
    private Integer waitingPeriodDays;

    @Column(name = "free_look_period_days")
    private Integer freeLookPeriodDays;

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