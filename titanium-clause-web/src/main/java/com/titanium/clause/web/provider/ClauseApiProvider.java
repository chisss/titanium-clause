package com.titanium.clause.web.provider;

import java.util.List;

import org.springframework.web.bind.annotation.RestController;

import com.titanium.clause.api.ClauseApi;
import com.titanium.clause.api.dto.ActivateClauseDTO;
import com.titanium.clause.api.dto.ClauseDTO;
import com.titanium.clause.api.dto.CreateClauseDTO;
import com.titanium.clause.api.dto.InactivateClauseDTO;
import com.titanium.clause.api.dto.UpdateClauseDTO;
import com.titanium.clause.application.query.ClauseAppQueryService;
import com.titanium.clause.application.service.ClauseApplicationService;
import com.titanium.clause.query.result.ClauseQueryResult;
import com.titanium.clause.valueobject.ClauseId;
import com.titanium.clause.web.mapper.ClauseWebMapper;
import com.titanium.metadata.enums.InsuranceType;
import com.titanium.metadata.enums.clause.ClauseEnum;

import lombok.RequiredArgsConstructor;

/**
 * 条款契约实现（Provider）
 * <p>
 * 承接 {@link ClauseApi} Feign 契约，面向其它微服务的远程调用。路径由 {@link ClauseApi} 的
 * {@code @RequestMapping("/api/v1/clauses")} 唯一定义，本类通过 {@code implements} 继承，
 * <b>不重复标注、不篡改</b>。职责仅为协议转换（api DTO → 应用层入参、读模型结果 → 对外 DTO）+ 调用
 * 应用层门面，零业务逻辑。与面向后台/端上的 {@code ClauseController} 平行收敛到同一
 * {@link ClauseApplicationService}。
 * </p>
 */
@RestController
@RequiredArgsConstructor
public class ClauseApiProvider implements ClauseApi {

    private final ClauseApplicationService clauseApplicationService;
    private final ClauseAppQueryService    clauseAppQueryService;
    private final ClauseWebMapper          clauseWebMapper;

    @Override
    public ClauseDTO createClause(CreateClauseDTO dto, String tenantId) {
        ClauseId clauseId = clauseApplicationService.createClause(
                dto.getClauseCode(), dto.getClauseName(), dto.getClauseType(),
                dto.getContent(), dto.getDescription(), dto.getInsuranceType(),
                dto.getEffectiveDate(), dto.getExpiryDate(), dto.getCreatedBy(), tenantId);
        return findDtoOrThrow(clauseId.getValue(), tenantId);
    }

    @Override
    public ClauseDTO updateClause(String clauseId, UpdateClauseDTO dto, String tenantId) {
        clauseApplicationService.updateClause(clauseId, dto.getClauseName(), dto.getClauseType(),
                dto.getContent(), dto.getDescription(), dto.getInsuranceType(),
                dto.getEffectiveDate(), dto.getExpiryDate(), dto.getUpdatedBy(), tenantId);
        return findDtoOrThrow(clauseId, tenantId);
    }

    @Override
    public ClauseDTO getClauseById(String clauseId, String tenantId) {
        return findDtoOrThrow(clauseId, tenantId);
    }

    @Override
    public List<ClauseDTO> getClauses(String status, String clauseType, String tenantId) {
        List<ClauseQueryResult> results;
        if (status != null) {
            results = clauseAppQueryService.findByStatus(ClauseEnum.ClauseStatus.fromCode(status), tenantId);
        } else if (clauseType != null) {
            results = clauseAppQueryService.findByType(InsuranceType.fromCode(clauseType), tenantId);
        } else {
            results = clauseAppQueryService.findAll(tenantId);
        }
        return results.stream().map(clauseWebMapper::toDTO).toList();
    }

    @Override
    public ClauseDTO activateClause(String clauseId, ActivateClauseDTO dto, String tenantId) {
        clauseApplicationService.activateClause(clauseId, dto.getActivatedBy(), tenantId);
        return findDtoOrThrow(clauseId, tenantId);
    }

    @Override
    public ClauseDTO inactivateClause(String clauseId, InactivateClauseDTO dto, String tenantId) {
        clauseApplicationService.inactivateClause(clauseId, dto.getInactivatedBy(), tenantId);
        return findDtoOrThrow(clauseId, tenantId);
    }

    /**
     * 按ID查询读模型并转对外 DTO，未命中抛异常（由全局兜底转 404）
     */
    private ClauseDTO findDtoOrThrow(String clauseId, String tenantId) {
        return clauseAppQueryService.findById(clauseId, tenantId)
                .map(clauseWebMapper::toDTO)
                .orElseThrow(() -> new IllegalStateException("条款不存在: " + clauseId));
    }
}
