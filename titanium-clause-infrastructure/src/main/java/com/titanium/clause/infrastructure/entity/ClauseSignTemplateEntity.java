package com.titanium.clause.infrastructure.entity;

import java.time.LocalDateTime;

import com.titanium.clause.common.enums.SignTemplateType;
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

    @Enumerated(EnumType.STRING)
    @Column(name = "template_type", length = 32, nullable = false)
    private SignTemplateType templateType;

    @Column(name = "template_content", columnDefinition = "TEXT", nullable = false)
    private String templateContent;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 32, nullable = false)
    private CommonStatus status;

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
