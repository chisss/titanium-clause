package com.titanium.clause.domain.event;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

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

/**
 * 条款创建事件
 */
public record ClauseCreatedEvent(ClauseId clauseId, ClauseCode clauseCode, ClauseName clauseName, String clauseType,
                                 String content, String status, String description, LocalDateTime effectiveDate,
                                 LocalDateTime expiryDate, Set<String> productIds, Map<CoverageId, Coverage> coverages,
                                 Map<ExclusionId, Exclusion> exclusions, PremiumRule premiumRule, ClaimRule claimRule,
                                 ContractChangeRule contractChangeRule, String tenantId, String createdBy,
                                 LocalDateTime createdAt, String updatedBy, LocalDateTime updatedAt) {
}
