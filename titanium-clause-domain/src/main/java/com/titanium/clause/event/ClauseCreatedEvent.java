package com.titanium.clause.event;

import java.time.LocalDateTime;
import java.util.Map;

import com.titanium.clause.entity.ClaimRule;
import com.titanium.clause.entity.ContractChangeRule;
import com.titanium.clause.entity.Coverage;
import com.titanium.clause.entity.Exclusion;
import com.titanium.clause.entity.PremiumRule;
import com.titanium.clause.valueobject.ClauseCode;
import com.titanium.clause.valueobject.ClauseId;
import com.titanium.clause.valueobject.ClauseName;
import com.titanium.clause.valueobject.CoverageId;
import com.titanium.clause.valueobject.ExclusionId;
import com.titanium.clause.valueobject.Version;
import com.titanium.metadata.enums.clause.ClauseEnum;
import com.titanium.metadata.enums.insurance.InsuranceProductType;

/**
 * 条款创建事件
 */
public record ClauseCreatedEvent(
        ClauseId clauseId,
        ClauseCode clauseCode,
        ClauseName clauseName,
        ClauseEnum.ClauseType clauseType,
        String content,
        ClauseEnum.ClauseStatus status,
        String description,
        InsuranceProductType insuranceType,
        Version version,
        ClauseId parentClauseId,
        LocalDateTime effectiveDate,
        LocalDateTime expiryDate,
        Map<CoverageId, Coverage> coverages,
        Map<ExclusionId, Exclusion> exclusions,
        PremiumRule premiumRule,
        ClaimRule claimRule,
        ContractChangeRule contractChangeRule,
        String tenantId,
        String createdBy,
        LocalDateTime createdAt,
        String updatedBy,
        LocalDateTime updatedAt
) {
}
