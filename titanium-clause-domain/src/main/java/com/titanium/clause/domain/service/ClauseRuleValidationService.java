package com.titanium.clause.domain.service;

import org.springframework.stereotype.Component;

import com.titanium.clause.common.exception.ClauseNotFoundException;
import com.titanium.clause.domain.aggregate.Clause;
import com.titanium.clause.domain.entity.ClaimEvent;
import com.titanium.clause.domain.repository.ClauseRepository;
import com.titanium.clause.domain.valueobject.ClauseId;

import lombok.RequiredArgsConstructor;

/**
 * 条款规则校验服务
 */
@RequiredArgsConstructor
@Component
public class ClauseRuleValidationService {
    private final ClauseRepository clauseRepository;

    /**
     * 校验理赔事件是否符合条款规则
     *
     * @param clauseId 条款ID
     * @param tenantId 租户ID
     * @param claimEvent 理赔事件
     * @return 是否符合条款规则
     */
    public boolean validateClaimAgainstClause(ClauseId clauseId, String tenantId, ClaimEvent claimEvent) {
        // 1. 查找条款
        Clause clause = clauseRepository.findById(clauseId, tenantId)
                .orElseThrow(() -> new ClauseNotFoundException("条款不存在: " + clauseId.getValue()));

        // 2. 调用Clause对象的validateClaim方法进行校验
        return clause.validateClaim(claimEvent);
    }

    /**
     * 校验理赔事件是否命中保险责任
     *
     * @param clauseId 条款ID
     * @param tenantId 租户ID
     * @param claimEvent 理赔事件
     * @return 是否命中保险责任
     */
    public boolean checkClaimAgainstCoverage(ClauseId clauseId, String tenantId, ClaimEvent claimEvent) {
        // 1. 查找条款
        Clause clause = clauseRepository.findById(clauseId, tenantId)
                .orElseThrow(() -> new ClauseNotFoundException("条款不存在: " + clauseId.getValue()));

        // 2. 检查是否命中保险责任
        return clause.getCoverages().values().stream().anyMatch(coverage -> coverage.checkTriggerCondition(claimEvent));
    }

    /**
     * 校验理赔事件是否命中责任免除
     *
     * @param clauseId 条款ID
     * @param tenantId 租户ID
     * @param claimEvent 理赔事件
     * @return 是否命中责任免除
     */
    public boolean checkClaimAgainstExclusion(ClauseId clauseId, String tenantId, ClaimEvent claimEvent) {
        // 1. 查找条款
        Clause clause = clauseRepository.findById(clauseId, tenantId)
                .orElseThrow(() -> new ClauseNotFoundException("条款不存在: " + clauseId.getValue()));

        // 2. 检查是否命中责任免除
        return clause.getExclusions().values().stream().anyMatch(exclusion -> exclusion.isHitExclusion(claimEvent));
    }
}
