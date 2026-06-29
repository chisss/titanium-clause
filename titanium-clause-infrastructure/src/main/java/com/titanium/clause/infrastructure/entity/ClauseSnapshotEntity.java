package com.titanium.clause.infrastructure.entity;

import java.time.LocalDateTime;

import com.titanium.clause.domain.enums.SnapshotBusinessType;

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
 * 条款快照实体类
 */
@Entity
@Table(name = "t_clause_snapshot",
        indexes = {
            @Index(name = "idx_snapshot_clause", columnList = "clause_id, clause_version"),
            @Index(name = "idx_snapshot_business", columnList = "business_id")
        }
)
@Data
@NoArgsConstructor
public class ClauseSnapshotEntity {
    @Id
    @Column(name = "id", length = 32, nullable = false)
    private String id;

    @Column(name = "clause_id", length = 32, nullable = false)
    private String clauseId;

    @Column(name = "clause_version", length = 20, nullable = false)
    private String clauseVersion;

    @Column(name = "snapshot_content", columnDefinition = "JSON", nullable = false)
    private String snapshotContent;

    @Column(name = "business_id", length = 32)
    private String businessId;

    @Enumerated(EnumType.STRING)
    @Column(name = "business_type", length = 32)
    private SnapshotBusinessType businessType;

    @Column(name = "tenant_id", length = 32, nullable = false)
    private String tenantId;

    @Column(name = "create_time", nullable = false)
    private LocalDateTime createTime;
}
