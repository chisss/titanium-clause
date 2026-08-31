package com.titanium.clause.web.controller;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.titanium.clause.application.query.ClauseAppQueryService;
import com.titanium.clause.application.service.ClauseApplicationService;
import com.titanium.clause.common.context.TenantContext;
import com.titanium.clause.common.enums.ApprovalType;
import com.titanium.clause.common.exception.ClauseInvalidStatusException;
import com.titanium.clause.common.exception.ClauseNotFoundException;
import com.titanium.clause.entity.Coverage;
import com.titanium.clause.query.result.ClauseQueryResult;
import com.titanium.clause.valueobject.ClauseId;
import com.titanium.clause.web.assembler.CoverageAssembler;
import com.titanium.clause.web.dto.ActivateClauseDTO;
import com.titanium.clause.web.dto.ApproveClauseDTO;
import com.titanium.clause.web.dto.ArchiveClauseDTO;
import com.titanium.clause.web.dto.CoverageDTO;
import com.titanium.clause.web.dto.CreateClauseDTO;
import com.titanium.clause.web.dto.InactivateClauseDTO;
import com.titanium.clause.web.dto.RejectClauseDTO;
import com.titanium.clause.web.dto.ReviseClauseDTO;
import com.titanium.clause.web.dto.SubmitApprovalDTO;
import com.titanium.clause.web.dto.UpdateClauseDTO;
import com.titanium.clause.web.mapper.ClauseWebMapper;
import com.titanium.clause.web.response.ClauseVO;
import com.titanium.clause.web.response.CoverageVO;
import com.titanium.metadata.enums.clause.ClauseEnum;
import com.titanium.metadata.enums.insurance.InsuranceCategory;
import com.titanium.metadata.enums.insurance.InsuranceLine;
import com.titanium.metadata.enums.insurance.InsuranceProductType;
import com.titanium.metadata.errorcode.SystemErrorCode;
import com.titanium.metadata.exception.DomainException;

import lombok.RequiredArgsConstructor;

/**
 * 条款控制器（后台/端上 HTTP 入口）
 * <p>
 * 面向管理后台/端上，路径 {@code /web/v1/clauses}，入参为 web 层 {@code XxxRequest}、出参
 * {@link ClauseVO}，<b>不 implements ClauseApi</b>（远程契约由 {@code ClauseApiProvider} 承接）。
 * 写入口经 {@link ClauseApplicationService} 编排（校验/取号），读入口经 {@link ClauseAppQueryService}
 * 查读模型返回 {@link ClauseQueryResult}，由 {@link ClauseWebMapper} 转为展示 VO。Controller 零业务逻辑。
 * </p>
 */
@RestController
@RequestMapping("/web/v1/clauses")
@RequiredArgsConstructor
public class ClauseController {

    private final ClauseApplicationService clauseApplicationService;
    private final ClauseAppQueryService    clauseAppQueryService;
    private final ClauseWebMapper          clauseWebMapper;
    private final CoverageAssembler        coverageAssembler;

    /**
     * 创建条款
     */
    @PostMapping
    public ResponseEntity<ClauseVO> createClause(@RequestBody CreateClauseDTO request) {
        String tenantId = TenantContext.getCurrentTenant();
        ClauseId clauseId = clauseApplicationService.createClause(
                request.getClauseCode(), request.getClauseName(), request.getClauseType(),
                request.getContent(), request.getDescription(), request.getInsuranceType(),
                request.getEffectiveDate(), request.getExpiryDate(), request.getCreatedBy(), tenantId);
        // 读模型为异步投影，命令返回后未必落库；直接回显已受理的写入（含生成的 clauseId、初始 DRAFT 状态），
        // 避免同步查最终一致投影未命中而误报「创建条款失败」。租户由请求上下文补齐。
        ClauseVO response = clauseWebMapper.toCreatedVO(request, clauseId.value());
        response.setTenantId(tenantId);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * 根据ID查询条款
     */
    @GetMapping("/{clauseId}")
    public ResponseEntity<ClauseVO> getClauseById(@PathVariable String clauseId) {
        String tenantId = TenantContext.getCurrentTenant();
        return ResponseEntity.ok(findVoOrThrow(clauseId, tenantId, "条款不存在"));
    }

    /**
     * 根据条款代码查询条款
     */
    @GetMapping("/code/{clauseCode}")
    public ResponseEntity<ClauseVO> getClauseByCode(@PathVariable String clauseCode) {
        String tenantId = TenantContext.getCurrentTenant();
        ClauseQueryResult result = clauseAppQueryService.findByCode(clauseCode, tenantId)
                .orElseThrow(() -> new ClauseNotFoundException("条款不存在"));
        return ResponseEntity.ok(clauseWebMapper.toVO(result));
    }

    /**
     * 更新条款
     */
    @PutMapping("/{clauseId}")
    public ResponseEntity<ClauseVO> updateClause(@PathVariable String clauseId,
                                                       @RequestBody UpdateClauseDTO request) {
        String tenantId = TenantContext.getCurrentTenant();
        clauseApplicationService.updateClause(clauseId, request.getClauseName(), request.getClauseType(),
                request.getContent(), request.getDescription(), request.getInsuranceType(),
                request.getEffectiveDate(), request.getExpiryDate(), request.getUpdatedBy(), tenantId);
        return ResponseEntity.ok(findVoOrThrow(clauseId, tenantId, "条款不存在"));
    }

    /**
     * 激活条款
     */
    @PutMapping("/{clauseId}/activate")
    public ResponseEntity<Void> activateClause(@PathVariable String clauseId,
                                               @RequestBody ActivateClauseDTO request) {
        String tenantId = TenantContext.getCurrentTenant();
        clauseApplicationService.activateClause(clauseId, request.getUpdatedBy(), tenantId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 停用条款
     */
    @PutMapping("/{clauseId}/inactivate")
    public ResponseEntity<Void> inactivateClause(@PathVariable String clauseId,
                                                 @RequestBody InactivateClauseDTO request) {
        String tenantId = TenantContext.getCurrentTenant();
        clauseApplicationService.inactivateClause(clauseId, request.getUpdatedBy(), tenantId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 提交条款审批
     */
    @PutMapping("/{clauseId}/submit-approval")
    public ResponseEntity<Void> submitForApproval(@PathVariable String clauseId,
                                                  @RequestBody SubmitApprovalDTO request) {
        String tenantId = TenantContext.getCurrentTenant();
        clauseApplicationService.submitForApproval(clauseId, request.getSubmittedBy(), tenantId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 审批通过条款
     */
    @PutMapping("/{clauseId}/approve")
    public ResponseEntity<Void> approveClause(@PathVariable String clauseId,
                                              @RequestBody ApproveClauseDTO request) {
        String tenantId = TenantContext.getCurrentTenant();
        clauseApplicationService.approveClause(clauseId, ApprovalType.fromCode(request.getApprovalType()),
                request.getApproverId(), request.getApproverName(), request.getComment(), tenantId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 审批驳回条款
     */
    @PutMapping("/{clauseId}/reject")
    public ResponseEntity<Void> rejectClause(@PathVariable String clauseId,
                                             @RequestBody RejectClauseDTO request) {
        String tenantId = TenantContext.getCurrentTenant();
        clauseApplicationService.rejectClause(clauseId, ApprovalType.fromCode(request.getApprovalType()),
                request.getApproverId(), request.getApproverName(), request.getComment(), tenantId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 条款修订（基于当前版本创建新DRAFT版本）
     */
    @PostMapping("/{clauseId}/revise")
    public ResponseEntity<Map<String, String>> reviseClause(@PathVariable String clauseId,
                                                            @RequestBody ReviseClauseDTO request) {
        String tenantId = TenantContext.getCurrentTenant();
        ClauseId newClauseId = clauseApplicationService.reviseClause(clauseId, request.getRevisedBy(), tenantId);
        return new ResponseEntity<>(Map.of("newClauseId", newClauseId.value()), HttpStatus.CREATED);
    }

    /**
     * 条款归档
     */
    @PutMapping("/{clauseId}/archive")
    public ResponseEntity<Void> archiveClause(@PathVariable String clauseId,
                                              @RequestBody ArchiveClauseDTO request) {
        String tenantId = TenantContext.getCurrentTenant();
        clauseApplicationService.archiveClause(clauseId, request.getArchivedBy(), tenantId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 根据状态查询条款列表
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<ClauseVO>> getClausesByStatus(@PathVariable String status) {
        String tenantId = TenantContext.getCurrentTenant();
        List<ClauseVO> responses = clauseAppQueryService
                .findByStatus(ClauseEnum.ClauseStatus.fromCode(status), tenantId)
                .stream().map(clauseWebMapper::toVO).toList();
        return ResponseEntity.ok(responses);
    }

    /**
     * 根据类型查询条款列表
     */
    @GetMapping("/type/{clauseType}")
    public ResponseEntity<List<ClauseVO>> getClausesByType(@PathVariable String clauseType) {
        String tenantId = TenantContext.getCurrentTenant();
        List<ClauseVO> responses = clauseAppQueryService
                .findByType(InsuranceProductType.fromCode(clauseType), tenantId)
                .stream().map(clauseWebMapper::toVO).toList();
        return ResponseEntity.ok(responses);
    }

    /**
     * 查询所有条款
     */
    @GetMapping
    public ResponseEntity<Page<ClauseVO>> getAllClauses(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize) {
        String tenantId = TenantContext.getCurrentTenant();
        ClauseEnum.ClauseStatus statusEnum = parseStatus(status);
        List<InsuranceProductType> insuranceTypes = insuranceTypesForCategory(category);
        Page<ClauseVO> responses = clauseAppQueryService.findClauses(name, code, statusEnum, insuranceTypes, tenantId,
                Math.max(pageNum - 1, 0), pageSize).map(clauseWebMapper::toVO);
        return ResponseEntity.ok(responses);
    }

    private ClauseEnum.ClauseStatus parseStatus(String status) {
        if (status == null || status.isBlank()) {
            return null;
        }
        ClauseEnum.ClauseStatus result = ClauseEnum.ClauseStatus.fromCode(status.trim());
        if (result == null) {
            throw new ClauseInvalidStatusException("无效的条款状态: " + status);
        }
        return result;
    }

    private List<InsuranceProductType> insuranceTypesForCategory(String category) {
        if (category == null || category.isBlank()) {
            return null;
        }
        String code = category.trim();
        InsuranceProductType productType = InsuranceProductType.fromCode(code);
        if (productType != null) {
            return List.of(productType);
        }
        InsuranceLine line = InsuranceLine.fromCode(code);
        if (line != null) {
            return InsuranceProductType.byLine(line);
        }
        InsuranceCategory insuranceCategory = InsuranceCategory.fromCode(code);
        if (insuranceCategory != null) {
            return InsuranceProductType.byCategory(insuranceCategory);
        }
        throw new DomainException(SystemErrorCode.PARAM_INVALID, "无效的险种分类: " + category);
    }

    /**
     * 删除条款
     */
    @DeleteMapping("/{clauseId}")
    public ResponseEntity<Void> deleteClause(@PathVariable String clauseId) {
        String tenantId = TenantContext.getCurrentTenant();
        clauseApplicationService.deleteClause(clauseId, tenantId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 为条款新增保险责任
     * <p>
     * 前端扁平 {@link CoverageDTO} 经 {@link CoverageAssembler} 装配为领域责任实体，
     * 医疗特有参数落 {@code CoverageTrigger.params} 扩展 Map。
     * </p>
     */
    @PostMapping("/{clauseId}/coverages")
    public ResponseEntity<Void> addCoverage(@PathVariable String clauseId,
                                            @RequestBody CoverageDTO request) {
        String tenantId = TenantContext.getCurrentTenant();
        Coverage coverage = coverageAssembler.toCoverage(request);
        clauseApplicationService.addCoverage(clauseId, coverage, request.getUpdatedBy(), tenantId);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    /**
     * 移除条款下的指定保险责任
     */
    @DeleteMapping("/{clauseId}/coverages/{coverageId}")
    public ResponseEntity<Void> removeCoverage(@PathVariable String clauseId,
                                               @PathVariable String coverageId) {
        String tenantId = TenantContext.getCurrentTenant();
        clauseApplicationService.removeCoverage(clauseId, coverageId, null, tenantId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 查询条款下的保险责任列表（读模型），拍平为前端展示 VO
     */
    @GetMapping("/{clauseId}/coverages")
    public ResponseEntity<List<CoverageVO>> listCoverages(@PathVariable String clauseId) {
        String tenantId = TenantContext.getCurrentTenant();
        List<CoverageVO> responses = clauseAppQueryService.findCoveragesByClauseId(clauseId, tenantId)
                .stream().map(coverageAssembler::toVO).toList();
        return ResponseEntity.ok(responses);
    }

    /**
     * 按ID查询读模型并转展示 VO，未命中抛异常
     */
    private ClauseVO findVoOrThrow(String clauseId, String tenantId, String message) {
        ClauseQueryResult result = clauseAppQueryService.findById(clauseId, tenantId)
                .orElseThrow(() -> new ClauseNotFoundException(message));
        return clauseWebMapper.toVO(result);
    }
}
