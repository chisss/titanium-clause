package com.titanium.clause.query.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.titanium.clause.common.constant.ClauseConstants;
import com.titanium.clause.common.tenant.PlatformTenantSupport;
import com.titanium.clause.query.repository.ClauseViewRepository;
import com.titanium.clause.query.result.ClauseQueryResult;
import com.titanium.clause.query.service.ClauseQueryService;
import com.titanium.clause.query.view.ClauseView;
import com.titanium.metadata.enums.clause.ClauseEnum;
import com.titanium.metadata.enums.insurance.InsuranceProductType;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 条款查询服务实现（CQRS 读侧）
 * <p>
 * 查询读模型表 {@code t_clause_view}（由 {@code ClauseProjectionEventHandler} 投影维护），
 * 组装为稳定 DTO 返回，禁止直接返回读模型实体。查询采用「当前租户 + 平台公共租户
 * ({@link ClauseConstants#PLATFORM_TENANT})」的平台默认回退，使各业务租户可选用平台预置公共条款模板。
 * </p>
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ClauseQueryServiceImpl implements ClauseQueryService {

    private final ClauseViewRepository clauseViewRepository;

    @Override
    @Transactional(readOnly = true)
    public Optional<ClauseQueryResult> getClauseById(String clauseId, String tenantId) {
        return clauseViewRepository.findByClauseIdAndTenantIdIn(clauseId, PlatformTenantSupport.scope(tenantId))
                .map(this::toResult);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ClauseQueryResult> getClauseByCode(String clauseCode, String tenantId) {
        return clauseViewRepository.findByClauseCodeAndTenantIdIn(clauseCode, PlatformTenantSupport.scope(tenantId))
                .map(this::toResult);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClauseQueryResult> getClausesByStatus(ClauseEnum.ClauseStatus status, String tenantId) {
        return clauseViewRepository.findByStatusAndTenantIdIn(status, PlatformTenantSupport.scope(tenantId)).stream()
                .map(this::toResult).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClauseQueryResult> getClausesByType(InsuranceProductType insuranceType, String tenantId) {
        return clauseViewRepository.findByInsuranceProductTypeAndTenantIdIn(insuranceType, PlatformTenantSupport.scope(tenantId))
                .stream().map(this::toResult).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClauseQueryResult> getAllClauses(String tenantId) {
        return clauseViewRepository.findByTenantIdIn(PlatformTenantSupport.scope(tenantId)).stream().map(this::toResult)
                .collect(Collectors.toList());
    }

    // ==================== 转换方法：读模型 → DTO ====================

    private ClauseQueryResult toResult(ClauseView view) {
        ClauseQueryResult result = new ClauseQueryResult();
        result.setClauseId(view.getClauseId());
        result.setClauseCode(view.getClauseCode());
        result.setClauseName(view.getClauseName());
        result.setClauseType(view.getClauseType());
        result.setContent(view.getContent());
        result.setDescription(view.getDescription());
        result.setStatus(view.getStatus());
        result.setVersion(view.getClauseVersion());
        result.setInsuranceType(view.getInsuranceType());
        result.setParentClauseId(view.getParentClauseId());
        result.setEffectiveDate(view.getEffectiveDate());
        result.setExpiryDate(view.getExpiryDate());
        result.setCreatedBy(view.getCreatedBy());
        result.setCreatedAt(view.getCreateTime());
        result.setUpdatedBy(view.getUpdatedBy());
        result.setUpdatedAt(view.getUpdateTime());
        result.setTenantId(view.getTenantId());
        return result;
    }
}
