package com.titanium.clause.event;

import com.titanium.clause.valueobject.InsuranceProductId;
import com.titanium.clause.valueobject.ProductCode;
import com.titanium.clause.valueobject.ProductName;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * 保险产品更新事件
 */
public record ProductUpdatedEvent(
        InsuranceProductId productId,
        ProductCode productCode,
        ProductName productName,
        String productType,
        String productClass,
        String description,
        String status,
        Integer mainProduct,
        String currency,
        Integer gracePeriod,
        Integer freeLookPeriod,
        Set<String> clauseIds,
        String tenantId,
        String updatedBy,
        LocalDateTime updatedAt
) {
}