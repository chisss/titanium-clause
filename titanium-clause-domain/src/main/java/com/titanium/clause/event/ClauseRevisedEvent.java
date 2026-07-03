package com.titanium.clause.event;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.titanium.clause.entity.ClaimRule;
import com.titanium.clause.entity.ClauseNotification;
import com.titanium.clause.entity.ClauseSignTemplate;
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
import com.titanium.metadata.enums.InsuranceType;
import com.titanium.metadata.enums.clause.ClauseEnum;

/**
 * 条款已修订事件（新版本创建）
 */
public record ClauseRevisedEvent(
        ClauseId originalClauseId,
        ClauseId newClauseId,
        ClauseCode clauseCode,
        ClauseName clauseName,
        ClauseEnum.ClauseType clauseType,
        String content,
        String description,
        InsuranceType insuranceType,
        Version newVersion,
        LocalDateTime effectiveDate,
        LocalDateTime expiryDate,
        Map<CoverageId, Coverage> coverages,
        Map<ExclusionId, Exclusion> exclusions,
        PremiumRule premiumRule,
        ClaimRule claimRule,
        ContractChangeRule contractChangeRule,
        List<ClauseNotification> notifications,
        ClauseSignTemplate signTemplate,
        String tenantId,
        String revisedBy,
        LocalDateTime revisedAt
) {
}
