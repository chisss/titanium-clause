package com.titanium.clause.infrastructure.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 签约模板实体类
 */
@Entity
@Table(name = "t_clause_sign_template",
        indexes = {
            @Index(name = "idx_status", columnList = "status"),
            @Index(name = "idx_template_type", columnList = "template_type"),
            @Index(name = "idx_tenant_id", columnList = "tenant_id")
        }
)
@Data
@NoArgsConstructor
public class ClauseSignTemplateEntity {
    @Id
    @Column(name = "id", length = 32, nullable = false)
    private String id;

    @Column(name = "tenant_id", length = 32, nullable = false)
    private String tenantId;

    @Column(name = "template_name", length = 128, nullable = false)
    private String templateName;

    @Column(name = "template_type", length = 32, nullable = false)
    private String templateType;

    @Column(name = "template_content", columnDefinition = "TEXT", nullable = false)
    private String templateContent;

    @Column(name = "status", length = 32, nullable = false)
    private String status;

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