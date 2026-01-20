package com.titanium.clause.domain.event;

import com.titanium.clause.domain.valueobject.InsuranceLiabilityId;
import com.titanium.clause.domain.valueobject.LiabilityCode;
import com.titanium.clause.domain.valueobject.LiabilityName;

import java.time.LocalDateTime;

/**
 * 保险责任创建事件
 */
public record LiabilityCreatedEvent(
        InsuranceLiabilityId liabilityId,
        LiabilityCode code,
        LiabilityName name,
        Double coverage,
        Double premiumRate,
        String description,
        String status,
        String tenantId,
        String createdBy,
        LocalDateTime createdAt,
        String updatedBy,
        LocalDateTime updatedAt
) {
}