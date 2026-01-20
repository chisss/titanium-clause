package com.titanium.clause.domain.event;

import com.titanium.clause.domain.valueobject.InsuranceLiabilityId;
import com.titanium.clause.domain.valueobject.LiabilityCode;
import com.titanium.clause.domain.valueobject.LiabilityName;

import java.time.LocalDateTime;

/**
 * 保险责任更新事件
 */
public record LiabilityUpdatedEvent(
        InsuranceLiabilityId liabilityId,
        LiabilityCode code,
        LiabilityName name,
        Double coverage,
        Double premiumRate,
        String description,
        String status,
        String tenantId,
        String updatedBy,
        LocalDateTime updatedAt
) {
}