package com.titanium.clause.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import com.titanium.clause.aggregate.Clause;
import com.titanium.clause.common.exception.ClauseExpiredException;
import com.titanium.clause.common.exception.ClauseInvalidStatusException;
import com.titanium.metadata.enums.clause.ClauseEnum;

/**
 * 条款领域服务
 * <p>
 * 纯领域服务：只承载条款相关的无依赖业务规则（数据校验、状态流转规则），
 * 不依赖仓储/Port，取数（唯一性/存在性）与发命令属应用层编排职责。
 * </p>
 */
@Component
public class ClauseDomainService {

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
     * 检查条款状态是否可以激活
     */
    public boolean canActivateClause(ClauseEnum.ClauseStatus status) {
        return status == ClauseEnum.ClauseStatus.DRAFT
                || status == ClauseEnum.ClauseStatus.INACTIVE;
    }

    /**
     * 检查条款状态是否可以删除
     */
    public boolean canDeleteClause(ClauseEnum.ClauseStatus status) {
        return status == ClauseEnum.ClauseStatus.DRAFT
                || status == ClauseEnum.ClauseStatus.INACTIVE;
    }

    /**
     * 检查条款是否处于活跃状态
     */
    public boolean isClauseActive(ClauseEnum.ClauseStatus status) {
        return status == ClauseEnum.ClauseStatus.ACTIVE;
    }
}
