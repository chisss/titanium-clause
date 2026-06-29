package com.titanium.clause.domain.event;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.titanium.clause.domain.entity.ClaimRule;
import com.titanium.clause.domain.entity.ClauseNotification;
import com.titanium.clause.domain.entity.ClauseSignTemplate;
import com.titanium.clause.domain.entity.ContractChangeRule;
import com.titanium.clause.domain.entity.Coverage;
import com.titanium.clause.domain.entity.Exclusion;
import com.titanium.clause.domain.entity.PremiumRule;
import com.titanium.clause.domain.valueobject.ClauseCode;
import com.titanium.clause.domain.valueobject.ClauseId;
import com.titanium.clause.domain.valueobject.ClauseName;
import com.titanium.clause.domain.valueobject.CoverageId;
import com.titanium.clause.domain.valueobject.ExclusionId;
import com.titanium.clause.domain.valueobject.Version;
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
