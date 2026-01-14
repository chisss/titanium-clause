package com.titanium.clause.service;

import java.time.LocalDateTime;

import com.titanium.clause.aggregate.Clause;
import com.titanium.clause.constant.ClauseConstants;
import com.titanium.clause.exception.ClauseDuplicateException;
import com.titanium.clause.exception.ClauseExpiredException;
import com.titanium.clause.exception.ClauseInvalidStatusException;
import com.titanium.clause.repository.ClauseRepository;
import com.titanium.clause.valueobject.ClauseCode;
import com.titanium.clause.valueobject.ClauseId;

import lombok.RequiredArgsConstructor;

/**
 * 条款领域服务
 */
@RequiredArgsConstructor
public class ClauseDomainService {
    private final ClauseRepository clauseRepository;

    /**
     * 验证条款代码是否唯一
     *
     * @param clauseCode 条款代码
     * @param tenantId 租户ID
     * @param excludeClauseId 排除的条款ID（用于更新场景）
     */
    public void validateClauseCodeUnique(ClauseCode clauseCode, String tenantId, ClauseId excludeClauseId) {
        var existingClause = clauseRepository.findByCode(clauseCode, tenantId);
        if (existingClause.isPresent()
                && (excludeClauseId == null || !existingClause.get().getClauseId().equals(excludeClauseId))) {
            throw new ClauseDuplicateException("条款代码已存在: " + clauseCode.getValue());
        }
    }

    /**
     * 验证条款有效期
     *
     * @param effectiveDate 生效日期
     * @param expiryDate 失效日期
     */
    public void validateClauseEffectivePeriod(LocalDateTime effectiveDate, LocalDateTime expiryDate) {
        if (effectiveDate == null) {
            throw new IllegalArgumentException("条款生效日期不能为空");
        }
        if (expiryDate != null && expiryDate.isBefore(effectiveDate)) {
            throw new IllegalArgumentException("条款失效日期不能早于生效日期");
        }
    }

    /**
     * 检查条款是否已过期
     *
     * @param clause 条款对象
     */
    public void checkClauseExpired(Clause clause) {
        if (ClauseConstants.CLAUSE_STATUS_EXPIRED.equals(clause.getStatus())) {
            throw new ClauseExpiredException("条款已过期: " + clause.getClauseName().getValue());
        }
        if (clause.getExpiryDate() != null && clause.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new ClauseExpiredException("条款已过期: " + clause.getClauseName().getValue());
        }
    }

    /**
     * 验证条款状态变更是否合法
     *
     * @param currentStatus 当前状态
     * @param newStatus 新状态
     */
    public void validateStatusChange(String currentStatus, String newStatus) {
        // 相同状态不需要变更
        if (currentStatus.equals(newStatus)) {
            return;
        }

        // 状态变更规则验证
        switch (currentStatus) {
            case ClauseConstants.CLAUSE_STATUS_DRAFT -> {
                if (!ClauseConstants.CLAUSE_STATUS_ACTIVE.equals(newStatus)
                        && !ClauseConstants.CLAUSE_STATUS_INACTIVE.equals(newStatus)) {
                    throw new ClauseInvalidStatusException("草稿状态的条款只能变更为激活或停用状态");
                }
            }
            case ClauseConstants.CLAUSE_STATUS_ACTIVE -> {
                if (!ClauseConstants.CLAUSE_STATUS_INACTIVE.equals(newStatus)
                        && !ClauseConstants.CLAUSE_STATUS_EXPIRED.equals(newStatus)) {
                    throw new ClauseInvalidStatusException("激活状态的条款只能变更为停用或过期状态");
                }
            }
            case ClauseConstants.CLAUSE_STATUS_INACTIVE -> {
                if (!ClauseConstants.CLAUSE_STATUS_ACTIVE.equals(newStatus)) {
                    throw new ClauseInvalidStatusException("停用状态的条款只能变更为激活状态");
                }
            }
            case ClauseConstants.CLAUSE_STATUS_EXPIRED -> {
                throw new ClauseInvalidStatusException("过期状态的条款不能变更状态");
            }
            default -> {
                throw new ClauseInvalidStatusException("无效的条款状态: " + currentStatus);
            }
        }
    }

    /**
     * 验证条款数据完整性
     *
     * @param clauseCode 条款代码
     * @param clauseName 条款名称
     * @param clauseType 条款类型
     * @param content 条款内容
     * @param effectiveDate 生效日期
     * @param expiryDate 失效日期
     */
    public void validateClauseData(String clauseCode, String clauseName, String clauseType, String content,
                                   LocalDateTime effectiveDate, LocalDateTime expiryDate) {
        if (clauseCode == null || clauseCode.trim().isEmpty()) {
            throw new IllegalArgumentException("条款代码不能为空");
        }
        if (clauseName == null || clauseName.trim().isEmpty()) {
            throw new IllegalArgumentException("条款名称不能为空");
        }
        if (clauseType == null || clauseType.trim().isEmpty()) {
            throw new IllegalArgumentException("条款类型不能为空");
        }
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("条款内容不能为空");
        }
        validateClauseEffectivePeriod(effectiveDate, expiryDate);
    }

    /**
     * 验证条款更新状态
     *
     * @param status 当前状态
     */
    public void validateClauseUpdateStatus(String status) {
        if (ClauseConstants.CLAUSE_STATUS_EXPIRED.equals(status)) {
            throw new ClauseInvalidStatusException("过期状态的条款不能更新");
        }
    }

    /**
     * 检查条款是否可以激活
     *
     * @param clause 条款对象
     * @return 是否可以激活
     */
    public boolean canActivateClause(Clause clause) {
        return ClauseConstants.CLAUSE_STATUS_DRAFT.equals(clause.getStatus())
                || ClauseConstants.CLAUSE_STATUS_INACTIVE.equals(clause.getStatus());
    }

    /**
     * 检查条款是否可以删除
     *
     * @param clause 条款对象
     * @return 是否可以删除
     */
    public boolean canDeleteClause(Clause clause) {
        return ClauseConstants.CLAUSE_STATUS_DRAFT.equals(clause.getStatus())
                || ClauseConstants.CLAUSE_STATUS_INACTIVE.equals(clause.getStatus());
    }
}
