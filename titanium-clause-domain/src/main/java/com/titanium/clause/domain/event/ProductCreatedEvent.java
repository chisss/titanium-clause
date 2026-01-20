package com.titanium.clause.domain.event;

import com.titanium.clause.domain.valueobject.InsuranceProductId;
import com.titanium.clause.domain.valueobject.ProductCode;
import com.titanium.clause.domain.valueobject.ProductName;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * 保险产品创建事件
 */
public record ProductCreatedEvent(
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
        String createdBy,
        LocalDateTime createdAt,
        String updatedBy,
        LocalDateTime updatedAt
) {
}