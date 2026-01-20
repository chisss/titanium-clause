package com.titanium.clause.domain.command;

import com.titanium.clause.domain.valueobject.InsuranceProductId;
import com.titanium.clause.domain.valueobject.ProductCode;
import com.titanium.clause.domain.valueobject.ProductName;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.util.Set;

/**
 * 更新保险产品命令
 */
public record UpdateProductCommand(
        @TargetAggregateIdentifier
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
        String updatedBy
) {
}