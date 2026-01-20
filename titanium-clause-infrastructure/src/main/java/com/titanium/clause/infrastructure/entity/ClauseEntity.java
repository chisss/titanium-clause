package com.titanium.clause.infrastructure.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 条款实体类
 */
@Entity
@Table(name = "t_clause", 
        uniqueConstraints = {
            @UniqueConstraint(name = "uk_clause_code_version_tenant", columnNames = {"clause_code", "version", "tenant_id"})
        },
        indexes = {
            @Index(name = "idx_insurance_type", columnList = "insurance_type"),
            @Index(name = "idx_status", columnList = "status"),
            @Index(name = "idx_tenant_id", columnList = "tenant_id")
        }
)
@Data
@NoArgsConstructor
public class ClauseEntity {
    @Id
    @Column(name = "id", length = 32, nullable = false)
    private String id;

    @Column(name = "tenant_id", length = 32, nullable = false)
    private String tenantId;

    @Column(name = "clause_code", length = 64, nullable = false)
    private String clauseCode;

    @Column(name = "clause_name", length = 128, nullable = false)
    private String clauseName;

    @Column(name = "insurance_type", length = 32, nullable = false)
    private String insuranceType;

    @Column(name = "version", length = 32, nullable = false)
    private String version;

    @Column(name = "status", length = 32, nullable = false)
    private String status;

    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "effective_date")
    private LocalDateTime effectiveDate;

    @Column(name = "expire_date")
    private LocalDateTime expireDate;

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
