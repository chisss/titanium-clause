package com.titanium.clause.infrastructure.entity;

import java.time.LocalDateTime;

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
 * 条款告知书实体类
 */
@Entity
@Table(name = "t_clause_notification",
        indexes = {
            @Index(name = "idx_clause_id", columnList = "clause_id"),
            @Index(name = "idx_status", columnList = "status"),
            @Index(name = "idx_tenant_id", columnList = "tenant_id")
        }
)
@Data
@NoArgsConstructor
public class ClauseNotificationEntity {
    @Id
    @Column(name = "id", length = 32, nullable = false)
    private String id;

    @Column(name = "tenant_id", length = 32, nullable = false)
    private String tenantId;

    @Column(name = "clause_id", length = 32, nullable = false)
    private String clauseId;

    @Column(name = "notification_title", length = 128, nullable = false)
    private String notificationTitle;

    @Column(name = "notification_content", columnDefinition = "TEXT", nullable = false)
    private String notificationContent;

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
