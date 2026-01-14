package com.titanium.clause.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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