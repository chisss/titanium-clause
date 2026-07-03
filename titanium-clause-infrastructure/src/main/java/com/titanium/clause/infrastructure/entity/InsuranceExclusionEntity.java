package com.titanium.clause.infrastructure.entity;

import java.time.LocalDateTime;

import com.titanium.clause.common.enums.ExclusionType;
import com.titanium.metadata.enums.CommonStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 责任免除实体类
 */
@Entity
@Table(name = "t_insurance_exclusion",
        indexes = {
            @Index(name = "idx_exclusion_type", columnList = "type"),
            @Index(name = "idx_exclusion_rule_code", columnList = "exclusion_rule_code"),
            @Index(name = "idx_exclusion_status", columnList = "status"),
            @Index(name = "idx_exclusion_tenant_id", columnList = "tenant_id")
        }
)
@Data
@NoArgsConstructor
public class InsuranceExclusionEntity {
    @Id
    @Column(name = "id", length = 32, nullable = false)
    private String id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = 32, nullable = false)
    private ExclusionType type;

    @Column(name = "exclusion_rule_code", length = 50, nullable = false)
    private String exclusionRuleCode;

    @Column(name = "is_mandatory", nullable = false)
    private Boolean isMandatory;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 32, nullable = false)
    private CommonStatus status;

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
