package com.titanium.clause.command;

import com.titanium.clause.valueobject.InsuranceLiabilityId;
import com.titanium.clause.valueobject.LiabilityCode;
import com.titanium.clause.valueobject.LiabilityName;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

/**
 * 更新保险责任命令
 */
public record UpdateLiabilityCommand(
        @TargetAggregateIdentifier
        InsuranceLiabilityId liabilityId,
        LiabilityCode code,
        LiabilityName name,
        Double coverage,
        Double premiumRate,
        String description,
        String status,
        String tenantId,
        String updatedBy
) {
}