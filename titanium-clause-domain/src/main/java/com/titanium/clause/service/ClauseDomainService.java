package com.titanium.clause.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import com.titanium.clause.aggregate.Clause;
import com.titanium.clause.common.exception.ClauseDuplicateException;
import com.titanium.clause.common.exception.ClauseExpiredException;
import com.titanium.clause.common.exception.ClauseInvalidStatusException;
import com.titanium.clause.repository.ClauseRepository;
import com.titanium.clause.valueobject.ClauseCode;
import com.titanium.clause.valueobject.ClauseId;
import com.titanium.metadata.enums.clause.ClauseEnum;

import lombok.RequiredArgsConstructor;

/**
 * 条款领域服务
 */
@RequiredArgsConstructor
@Component
public class ClauseDomainService {
    private final ClauseRepository clauseRepository;

    /**
     * 验证条款代码是否唯一
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
     */
    public void checkClauseExpired(Clause clause) {
        if (clause.getStatus() == ClauseEnum.ClauseStatus.EXPIRED) {
            throw new ClauseExpiredException("条款已过期: " + clause.getClauseName().getValue());
        }
        if (clause.getExpiryDate() != null && clause.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new ClauseExpiredException("条款已过期: " + clause.getClauseName().getValue());
        }
    }

    /**
     * 验证条款状态变更是否合法
     */
    public void validateStatusChange(ClauseEnum.ClauseStatus currentStatus, ClauseEnum.ClauseStatus newStatus) {
        if (currentStatus == newStatus) {
            return;
        }

        boolean isValid = switch (currentStatus) {
            case DRAFT -> newStatus == ClauseEnum.ClauseStatus.PENDING_APPROVAL
                    || newStatus == ClauseEnum.ClauseStatus.ACTIVE
                    || newStatus == ClauseEnum.ClauseStatus.INACTIVE;
            case PENDING_APPROVAL -> newStatus == ClauseEnum.ClauseStatus.ACTIVE
                    || newStatus == ClauseEnum.ClauseStatus.DRAFT;
            case ACTIVE -> newStatus == ClauseEnum.ClauseStatus.INACTIVE
                    || newStatus == ClauseEnum.ClauseStatus.EXPIRED
                    || newStatus == ClauseEnum.ClauseStatus.ARCHIVED;
            case INACTIVE -> newStatus == ClauseEnum.ClauseStatus.ACTIVE;
            case EXPIRED, ARCHIVED -> false;
        };

        if (!isValid) {
            throw new ClauseInvalidStatusException(
                    "条款状态从 " + currentStatus.getName() + " 不允许变更为 " + newStatus.getName());
        }
    }

    /**
     * 验证条款数据完整性
     */
    public void validateClauseData(String clauseCode, String clauseName, ClauseEnum.ClauseType clauseType,
                                   String content, LocalDateTime effectiveDate, LocalDateTime expiryDate) {
        if (clauseCode == null || clauseCode.trim().isEmpty()) {
            throw new IllegalArgumentException("条款代码不能为空");
        }
        if (clauseName == null || clauseName.trim().isEmpty()) {
            throw new IllegalArgumentException("条款名称不能为空");
        }
        if (clauseType == null) {
            throw new IllegalArgumentException("条款类型不能为空");
        }
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("条款内容不能为空");
        }
        validateClauseEffectivePeriod(effectiveDate, expiryDate);
    }

    /**
     * 验证条款更新状态（仅DRAFT可更新）
     */
    public void validateClauseUpdateStatus(ClauseEnum.ClauseStatus status) {
        if (status != ClauseEnum.ClauseStatus.DRAFT) {
            throw new ClauseInvalidStatusException("仅草稿状态的条款允许更新");
        }
    }

    /**
     * 检查条款是否可以激活
     */
    public boolean canActivateClause(Clause clause) {
        return clause.getStatus() == ClauseEnum.ClauseStatus.DRAFT
                || clause.getStatus() == ClauseEnum.ClauseStatus.INACTIVE;
    }

    /**
     * 检查条款是否可以删除
     */
    public boolean canDeleteClause(Clause clause) {
        return clause.getStatus() == ClauseEnum.ClauseStatus.DRAFT
                || clause.getStatus() == ClauseEnum.ClauseStatus.INACTIVE;
    }

    /**
     * 检查条款是否处于活跃状态
     */
    public boolean isClauseActive(Clause clause) {
        return clause.getStatus() == ClauseEnum.ClauseStatus.ACTIVE;
    }
}
