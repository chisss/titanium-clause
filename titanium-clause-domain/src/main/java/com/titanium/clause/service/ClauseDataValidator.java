package com.titanium.clause.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import com.titanium.metadata.enums.clause.ClauseEnum;
import com.titanium.metadata.errorcode.ClauseErrorCode;
import com.titanium.metadata.exception.DomainException;

/**
 * 条款数据校验器（纯领域校验规则，独立校验器类）
 * <p>
 * 红线19：超过 3 个参数的 if 校验链须独立成校验器类，不堆在领域服务方法内。本类承载条款
 * 创建/更新的数据完整性与有效期校验规则，无 CommandGateway、无 Port、无基础设施依赖，可脱离
 * 容器 {@code new} 直测。校验异常统一携带 {@link ClauseErrorCode} 枚举码（红线16：异常必须携带
 * BaseErrorCode，禁止裸串）。
 * </p>
 */
@Component
public class ClauseDataValidator {

    /**
     * 验证条款数据完整性（代码/名称/类型/内容非空，有效期合法）
     *
     * @param clauseCode 条款代码
     * @param clauseName 条款名称
     * @param clauseType 条款类型
     * @param content 条款内容
     * @param effectiveDate 生效日期
     * @param expiryDate 失效日期
     */
    public void validateClauseData(String clauseCode, String clauseName, ClauseEnum.ClauseType clauseType,
                                   String content, LocalDateTime effectiveDate, LocalDateTime expiryDate) {
        if (clauseCode == null || clauseCode.trim().isEmpty()) {
            throw new DomainException(ClauseErrorCode.CLAUSE_CODE_REQUIRED);
        }
        if (clauseName == null || clauseName.trim().isEmpty()) {
            throw new DomainException(ClauseErrorCode.CLAUSE_NAME_REQUIRED);
        }
        if (clauseType == null) {
            throw new DomainException(ClauseErrorCode.CLAUSE_TYPE_REQUIRED);
        }
        if (content == null || content.trim().isEmpty()) {
            throw new DomainException(ClauseErrorCode.CLAUSE_CONTENT_REQUIRED);
        }
        validateEffectivePeriod(effectiveDate, expiryDate);
    }

    /**
     * 验证条款有效期（生效日期必填，失效日期不得早于生效日期）
     *
     * @param effectiveDate 生效日期
     * @param expiryDate 失效日期
     */
    public void validateEffectivePeriod(LocalDateTime effectiveDate, LocalDateTime expiryDate) {
        if (effectiveDate == null) {
            throw new DomainException(ClauseErrorCode.CLAUSE_EFFECTIVE_DATE_REQUIRED);
        }
        if (expiryDate != null && expiryDate.isBefore(effectiveDate)) {
            throw new DomainException(ClauseErrorCode.CLAUSE_EXPIRY_BEFORE_EFFECTIVE);
        }
    }
}
