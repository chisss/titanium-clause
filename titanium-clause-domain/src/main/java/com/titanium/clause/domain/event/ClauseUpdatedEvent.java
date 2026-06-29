package com.titanium.clause.domain.event;

import java.time.LocalDateTime;
import java.util.Map;

import com.titanium.clause.domain.entity.ClaimRule;
import com.titanium.clause.domain.entity.ContractChangeRule;
import com.titanium.clause.domain.entity.Coverage;
import com.titanium.clause.domain.entity.Exclusion;
import com.titanium.clause.domain.entity.PremiumRule;
import com.titanium.clause.domain.valueobject.ClauseCode;
import com.titanium.clause.domain.valueobject.ClauseId;
import com.titanium.clause.domain.valueobject.ClauseName;
import com.titanium.clause.domain.valueobject.CoverageId;
import com.titanium.clause.domain.valueobject.ExclusionId;
import com.titanium.metadata.enums.InsuranceType;
import com.titanium.metadata.enums.clause.ClauseEnum;

/**
 * 条款更新事件
 */
public record ClauseUpdatedEvent(
        ClauseId clauseId,
        ClauseCode clauseCode,
        ClauseName clauseName,
        ClauseEnum.ClauseType clauseType,
        String content,
        ClauseEnum.ClauseStatus status,
        String description,
        InsuranceType insuranceType,
        LocalDateTime effectiveDate,
        LocalDateTime expiryDate,
        Map<CoverageId, Coverage> coverages,
        Map<ExclusionId, Exclusion> exclusions,
        PremiumRule premiumRule,
        ClaimRule claimRule,
        ContractChangeRule contractChangeRule,
        String tenantId,
        String updatedBy,
        LocalDateTime updatedAt
) {
}
