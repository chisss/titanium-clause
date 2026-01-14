package com.titanium.clause.event;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

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
