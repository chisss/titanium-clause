package com.titanium.clause.web.provider;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.titanium.clause.api.ClauseApi;
import com.titanium.clause.application.query.ClauseAppQueryService;
import com.titanium.clause.application.service.ClauseApplicationService;
import com.titanium.clause.query.result.ClauseQueryResult;
import com.titanium.clause.valueobject.ClauseId;
import com.titanium.clause.web.assembler.CoverageResponseAssembler;
import com.titanium.clause.web.mapper.ClauseWebMapper;
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
    public com.titanium.clause.api.response.ClauseResponse createClause(
            com.titanium.clause.api.request.CreateClauseRequest request, String tenantId) {
        ClauseId clauseId = clauseApplicationService.createClause(
                request.getClauseCode(), request.getClauseName(), request.getClauseType(),
                request.getContent(), request.getDescription(), request.getInsuranceType(),
                request.getEffectiveDate(), request.getExpiryDate(), request.getCreatedBy(), tenantId);
        return findResponseOrThrow(clauseId.value(), tenantId);
    }

    @Override
    public com.titanium.clause.api.response.ClauseResponse updateClause(
            String clauseId, com.titanium.clause.api.request.UpdateClauseRequest request, String tenantId) {
        clauseApplicationService.updateClause(clauseId, request.getClauseName(), request.getClauseType(),
                request.getContent(), request.getDescription(), request.getInsuranceType(),
                request.getEffectiveDate(), request.getExpiryDate(), request.getUpdatedBy(), tenantId);
        return findResponseOrThrow(clauseId, tenantId);
    }

    @Override
    public com.titanium.clause.api.response.ClauseResponse getClauseById(String clauseId, String tenantId) {
        return findResponseOrThrow(clauseId, tenantId);
    }

    @Override
    public com.titanium.clause.api.response.PremiumRuleResponse getPremiumRuleByClauseId(
            String clauseId, String tableCode, String version, String tenantId) {
        return clauseAppQueryService.findPremiumRuleByClauseIdAndVersion(clauseId, tableCode, version, tenantId)
                .map(clauseWebMapper::toPremiumRuleResponse)
                .orElse(null);
    }

    @Override
    public List<com.titanium.clause.api.response.ClauseResponse> getClauses(
            String status, String clauseType, String tenantId) {
        List<ClauseQueryResult> results;
        if (status != null) {
            results = clauseAppQueryService.findByStatus(ClauseEnum.ClauseStatus.fromCode(status), tenantId);
        } else if (clauseType != null) {
            results = clauseAppQueryService.findByType(InsuranceProductType.fromCode(clauseType), tenantId);
        } else {
            results = clauseAppQueryService.findAll(tenantId);
        }
        return results.stream().map(clauseWebMapper::toResponse).toList();
    }

    @Override
    public com.titanium.clause.api.response.ClauseResponse activateClause(
            String clauseId, com.titanium.clause.api.request.ActivateClauseRequest request, String tenantId) {
        clauseApplicationService.activateClause(clauseId, request.getActivatedBy(), tenantId);
        return findResponseOrThrow(clauseId, tenantId);
    }

    @Override
    public com.titanium.clause.api.response.ClauseResponse inactivateClause(
            String clauseId, com.titanium.clause.api.request.InactivateClauseRequest request, String tenantId) {
        clauseApplicationService.inactivateClause(clauseId, request.getInactivatedBy(), tenantId);
        return findResponseOrThrow(clauseId, tenantId);
    }

    @Override
    public List<com.titanium.clause.api.response.CoverageResponse> getCoveragesByClauseId(String clauseId,
                                                                                          String tenantId) {
        return clauseAppQueryService.findCoveragesByClauseId(clauseId, tenantId).stream()
                .map(coverageResponseAssembler::toResponse)
                .toList();
    }

    /**
     * 按ID查询读模型并转对外 Response，未命中返回 404
     */
    private com.titanium.clause.api.response.ClauseResponse findResponseOrThrow(String clauseId, String tenantId) {
        return clauseAppQueryService.findById(clauseId, tenantId)
                .map(clauseWebMapper::toResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "条款不存在: " + clauseId));
    }
}
