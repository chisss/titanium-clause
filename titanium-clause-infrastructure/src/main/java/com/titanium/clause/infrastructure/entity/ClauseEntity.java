package com.titanium.clause.infrastructure.entity;

import java.time.LocalDateTime;

import com.titanium.common.jpa.BaseEntity;
import com.titanium.metadata.enums.InsuranceType;
import com.titanium.metadata.enums.clause.ClauseEnum;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 条款实体类
 * <p>继承 BaseEntity，复用租户ID、创建/更新时间、创建/更新人、逻辑删除等公共审计字段。</p>
 */
@Entity
@Table(name = "t_clause",
        uniqueConstraints = {
            @UniqueConstraint(name = "uk_clause_code_version_tenant", columnNames = {"clause_code", "version", "tenant_id"})
        },
        indexes = {
            @Index(name = "idx_insurance_type", columnList = "insurance_type"),
            @Index(name = "idx_clause_type", columnList = "clause_type"),
            @Index(name = "idx_status", columnList = "status"),
            @Index(name = "idx_tenant_id", columnList = "tenant_id"),
            @Index(name = "idx_parent_clause", columnList = "parent_clause_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
public class ClauseEntity extends BaseEntity {
    @Id
    @Column(name = "id", length = 32, nullable = false)
    private String id;

    @Column(name = "clause_code", length = 64, nullable = false)
    private String clauseCode;

    @Column(name = "clause_name", length = 128, nullable = false)
    private String clauseName;

    @Enumerated(EnumType.STRING)
    @Column(name = "clause_type", length = 32)
    private ClauseEnum.ClauseType clauseType;

    @Enumerated(EnumType.STRING)
    @Column(name = "insurance_type", length = 32, nullable = false)
    private InsuranceType insuranceType;

    @Column(name = "version", length = 32, nullable = false)
    private String version;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 32, nullable = false)
    private ClauseEnum.ClauseStatus status;

    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "effective_date")
    private LocalDateTime effectiveDate;

    @Column(name = "expire_date")
    private LocalDateTime expireDate;

    @Column(name = "parent_clause_id", length = 32)
    private String parentClauseId;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
}
