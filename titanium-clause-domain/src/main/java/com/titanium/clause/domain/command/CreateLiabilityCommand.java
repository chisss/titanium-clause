package com.titanium.clause.domain.command;

import com.titanium.clause.domain.valueobject.InsuranceLiabilityId;
import com.titanium.clause.domain.valueobject.LiabilityCode;
import com.titanium.clause.domain.valueobject.LiabilityName;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

/**
 * 创建保险责任命令
 */
public record CreateLiabilityCommand(
        @TargetAggregateIdentifier
        InsuranceLiabilityId liabilityId,
        LiabilityCode code,
        LiabilityName name,
        Double coverage,
        Double premiumRate,
        String description,
        String status,
        String tenantId,
        String createdBy
) {
}