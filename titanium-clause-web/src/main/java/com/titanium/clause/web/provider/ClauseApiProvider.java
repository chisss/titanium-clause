package com.titanium.clause.web.provider;

import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.titanium.clause.api.ClauseApi;
import com.titanium.clause.api.request.ActivateClauseRequest;
import com.titanium.clause.api.request.CreateClauseRequest;
import com.titanium.clause.api.request.InactivateClauseRequest;
import com.titanium.clause.api.request.UpdateClauseRequest;
import com.titanium.clause.api.response.clause.ClauseResponse;
import com.titanium.clause.api.response.clause.CoverageResponse;
import com.titanium.clause.api.response.rate.PremiumRuleResponse;
import com.titanium.clause.application.query.ClauseAppQueryService;
import com.titanium.clause.application.service.ClauseApplicationService;
import com.titanium.clause.common.exception.ClauseNotFoundException;
import com.titanium.clause.query.result.ClauseQueryResult;
import com.titanium.clause.valueobject.ClauseId;
import com.titanium.clause.web.assembler.CoverageResponseAssembler;
import com.titanium.clause.web.mapper.ClauseWebMapper;
import com.titanium.common.context.RequestContextHolder;
import com.titanium.metadata.enums.clause.ClauseEnum;
import com.titanium.metadata.enums.insurance.InsuranceProductType;

import lombok.RequiredArgsConstructor;

/**
 * 条款契约实现（Provider）
 * <p>
 * 承接 {@link ClauseApi} Feign 契约，面向其它微服务的远程调用。路径由 {@link ClauseApi} 的
 * {@code @RequestMapping("/api/v1/clauses")} 唯一定义，本类通过 {@code implements} 继承，
 * <b>不重复标注、不篡改</b>。职责仅为协议转换（api Request → 应用层入参、读模型结果 → 对外 Response）+ 调用
 * 应用层门面，零业务逻辑。与面向后台/端上的 {@code ClauseController} 平行收敛到同一
 * {@link ClauseApplicationService}。
 * </p>
 */
@RestController
@RequestMapping("/api/v1/clauses")
@RequiredArgsConstructor
public class ClauseApiProvider implements ClauseApi {

    private final ClauseApplicationService  clauseApplicationService;
    private final ClauseAppQueryService     clauseAppQueryService;
    private final ClauseWebMapper           clauseWebMapper;
    private final CoverageResponseAssembler coverageResponseAssembler;

    @Override
    public ClauseResponse createClause(CreateClauseRequest request) {
        ClauseId clauseId = clauseApplicationService.createClause(
                request.getClauseCode(), request.getClauseName(), request.getClauseType(),
                request.getContent(), request.getDescription(), request.getInsuranceType(),
                request.getEffectiveDate(), request.getExpiryDate(), request.getCreatedBy(), RequestContextHolder.requireTenantId());
        return findResponseOrThrow(clauseId.value());
    }

    @Override
    public ClauseResponse updateClause(String clauseId, UpdateClauseRequest request) {
        clauseApplicationService.updateClause(clauseId, request.getClauseName(), request.getClauseType(),
                request.getContent(), request.getDescription(), request.getInsuranceType(),
                request.getEffectiveDate(), request.getExpiryDate(), request.getUpdatedBy(), RequestContextHolder.requireTenantId());
        return findResponseOrThrow(clauseId);
    }

    @Override
    public ClauseResponse getClauseById(String clauseId) {
        return findResponseOrThrow(clauseId);
    }

    @Override
    public PremiumRuleResponse getPremiumRuleByClauseId(
            String clauseId, String tableCode, String version) {
        return clauseAppQueryService.findPremiumRuleByClauseIdAndVersion(clauseId, tableCode, version, RequestContextHolder.requireTenantId())
                .map(clauseWebMapper::toPremiumRuleResponse)
                .orElse(null);
    }

    @Override
    public List<ClauseResponse> getClauses(
            String status, String clauseType) {
        List<ClauseQueryResult> results;
        if (status != null) {
            results = clauseAppQueryService.findByStatus(ClauseEnum.ClauseStatus.fromCode(status), RequestContextHolder.requireTenantId());
        } else if (clauseType != null) {
            results = clauseAppQueryService.findByType(InsuranceProductType.fromCode(clauseType), RequestContextHolder.requireTenantId());
        } else {
            results = clauseAppQueryService.findAll(RequestContextHolder.requireTenantId());
        }
        return results.stream().map(clauseWebMapper::toResponse).toList();
    }

    @Override
    public ClauseResponse activateClause(String clauseId, ActivateClauseRequest request) {
        clauseApplicationService.activateClause(clauseId, request.getActivatedBy(), RequestContextHolder.requireTenantId());
        return findResponseOrThrow(clauseId);
    }

    @Override
    public ClauseResponse inactivateClause(String clauseId, InactivateClauseRequest request) {
        clauseApplicationService.inactivateClause(clauseId, request.getInactivatedBy(), RequestContextHolder.requireTenantId());
        return findResponseOrThrow(clauseId);
    }

    @Override
    public List<CoverageResponse> getCoveragesByClauseId(String clauseId) {
        return clauseAppQueryService.findCoveragesByClauseId(clauseId, RequestContextHolder.requireTenantId()).stream()
                .map(coverageResponseAssembler::toResponse)
                .toList();
    }

    /**
     * 按ID查询读模型并转对外 Response，未命中抛条款不存在异常（由全局异常处理器映射 404）
     */
    private ClauseResponse findResponseOrThrow(String clauseId) {
        return clauseAppQueryService.findById(clauseId, RequestContextHolder.requireTenantId())
                .map(clauseWebMapper::toResponse)
                .orElseThrow(() -> new ClauseNotFoundException("条款不存在: " + clauseId));
    }
}
