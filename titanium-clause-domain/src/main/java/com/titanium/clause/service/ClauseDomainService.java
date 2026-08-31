package com.titanium.clause.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import com.titanium.clause.aggregate.Clause;
import com.titanium.clause.common.exception.ClauseExpiredException;
import com.titanium.clause.common.exception.ClauseInvalidStatusException;
import com.titanium.metadata.enums.clause.ClauseEnum;

import lombok.RequiredArgsConstructor;

/**
 * 条款领域服务
 * <p>
 * 纯领域服务：只承载条款相关的无依赖业务规则（数据校验、状态流转规则），
 * 不依赖仓储/Port，取数（唯一性/存在性）与发命令属应用层编排职责。
 * 数据完整性校验链已独立成 {@link ClauseDataValidator}（红线19），本类仅委托。
 * </p>
 */
@Component
@RequiredArgsConstructor
public class ClauseDomainService {

    private final ClauseDataValidator clauseDataValidator;

    /**
     * 验证条款有效期（委托 {@link ClauseDataValidator}）
     */
    public void validateClauseEffectivePeriod(LocalDateTime effectiveDate, LocalDateTime expiryDate) {
        clauseDataValidator.validateEffectivePeriod(effectiveDate, expiryDate);
    }

    /**
     * 检查条款是否已过期
     */
    public void checkClauseExpired(Clause clause) {
        if (clause.getStatus() == ClauseEnum.ClauseStatus.EXPIRED) {
            throw new ClauseExpiredException("条款已过期: " + clause.getClauseName().value());
        }
        if (clause.getExpiryDate() != null && clause.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new ClauseExpiredException("条款已过期: " + clause.getClauseName().value());
        }
    }

    /**
     * 验证条款数据完整性（委托 {@link ClauseDataValidator}，校验链已独立成校验器类）
     */
    public void validateClauseData(String clauseCode, String clauseName, ClauseEnum.ClauseType clauseType,
                                   String content, LocalDateTime effectiveDate, LocalDateTime expiryDate) {
        clauseDataValidator.validateClauseData(clauseCode, clauseName, clauseType, content, effectiveDate, expiryDate);
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
     * <p>
     * 仅停用（INACTIVE）条款可直接重新激活；草稿（DRAFT）不可直接激活，必须经审批流程
     * （提交审批 → 审批通过）才能生效，防止绕过审批的责任完整性校验。
     * </p>
     */
    public boolean canActivateClause(ClauseEnum.ClauseStatus status) {
        return status == ClauseEnum.ClauseStatus.INACTIVE;
    }

    /**
     * 检查条款状态是否可以删除
     * <p>
     * 仅草稿（DRAFT）可硬删除，与聚合根 {@code DeleteClauseCommand} 的删除前置条件一致；
     * 已生效/停用条款应走归档软删，不可硬删。
     * </p>
     */
    public boolean canDeleteClause(ClauseEnum.ClauseStatus status) {
        return status == ClauseEnum.ClauseStatus.DRAFT;
    }

    /**
     * 检查条款是否处于活跃状态
     */
    public boolean isClauseActive(ClauseEnum.ClauseStatus status) {
        return status == ClauseEnum.ClauseStatus.ACTIVE;
    }
}
