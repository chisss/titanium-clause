package com.titanium.clause.infrastructure.entity;

import java.time.LocalDateTime;

import com.titanium.clause.domain.enums.ApprovalStatus;
import com.titanium.clause.domain.enums.ApprovalType;

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
 * 条款审批记录实体类
 */
@Entity
@Table(name = "t_clause_approval_record",
        indexes = {
            @Index(name = "idx_approval_clause", columnList = "clause_id")
        }
)
@Data
@NoArgsConstructor
public class ClauseApprovalRecordEntity {
    @Id
    @Column(name = "id", length = 32, nullable = false)
    private String id;

    @Column(name = "clause_id", length = 32, nullable = false)
    private String clauseId;

    @Enumerated(EnumType.STRING)
    @Column(name = "approval_type", length = 32, nullable = false)
    private ApprovalType approvalType;

    @Column(name = "approver_id", length = 32, nullable = false)
    private String approverId;

    @Column(name = "approver_name", length = 100)
    private String approverName;

    @Enumerated(EnumType.STRING)
    @Column(name = "approval_status", length = 32, nullable = false)
    private ApprovalStatus approvalStatus;

    @Column(name = "approval_comment", columnDefinition = "TEXT")
    private String approvalComment;

    @Column(name = "approval_time")
    private LocalDateTime approvalTime;

    @Column(name = "tenant_id", length = 32, nullable = false)
    private String tenantId;

    @Column(name = "create_time", nullable = false)
    private LocalDateTime createTime;
}
