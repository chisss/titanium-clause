package com.titanium.clause.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.titanium.clause.api.dto.ClauseDTO;
import com.titanium.clause.api.request.ActivateClauseRequest;
import com.titanium.clause.api.request.CreateClauseRequest;
import com.titanium.clause.api.request.InactivateClauseRequest;
import com.titanium.clause.api.request.UpdateClauseRequest;
import com.titanium.clause.application.query.ClauseQueryService;
import com.titanium.clause.application.service.ClauseApplicationService;
import com.titanium.clause.domain.aggregate.Clause;
import com.titanium.clause.domain.query.GetClauseAllQuery;
import com.titanium.clause.domain.query.GetClauseByCodeQuery;
import com.titanium.clause.domain.query.GetClauseByIdQuery;
import com.titanium.clause.domain.query.GetClausesByStatusQuery;
import com.titanium.clause.domain.query.GetClausesByTypeQuery;
import com.titanium.clause.domain.valueobject.ClauseId;
import com.titanium.clause.domain.enums.ApprovalType;
import com.titanium.clause.infrastructure.config.TenantContext;
import com.titanium.metadata.enums.InsuranceType;
import com.titanium.metadata.enums.clause.ClauseEnum;

import lombok.RequiredArgsConstructor;

/**
 * 条款控制器
 */
@RestController
@RequestMapping("/api/clauses")
@RequiredArgsConstructor
public class ClauseController {
    private final ClauseApplicationService clauseApplicationService;
    private final ClauseQueryService       clauseQueryService;

    /**
     * 创建条款
     */
    @PostMapping
    public ResponseEntity<ClauseDTO> createClause(@RequestBody CreateClauseRequest request) {
        String tenantId = TenantContext.getCurrentTenant();
        ClauseId clauseId = clauseApplicationService.createClause(
                request.getClauseCode(), request.getClauseName(), request.getClauseType(),
                request.getContent(), request.getDescription(), request.getInsuranceType(),
                request.getEffectiveDate(), request.getExpiryDate(), request.getCreatedBy(), tenantId);
        var clause = clauseQueryService.handle(new GetClauseByIdQuery(clauseId, tenantId))
                .orElseThrow(() -> new RuntimeException("创建条款失败"));
        return new ResponseEntity<>(toDTO(clause), HttpStatus.CREATED);
    }

    /**
     * 根据ID查询条款
     */
    @GetMapping("/{clauseId}")
    public ResponseEntity<ClauseDTO> getClauseById(@PathVariable String clauseId) {
        String tenantId = TenantContext.getCurrentTenant();
        var clause = clauseQueryService.handle(new GetClauseByIdQuery(new ClauseId(clauseId), tenantId))
                .orElseThrow(() -> new RuntimeException("条款不存在"));
        return ResponseEntity.ok(toDTO(clause));
    }

    /**
     * 根据条款代码查询条款
     */
    @GetMapping("/code/{clauseCode}")
    public ResponseEntity<ClauseDTO> getClauseByCode(@PathVariable String clauseCode) {
        String tenantId = TenantContext.getCurrentTenant();
        var clause = clauseQueryService.queryClauseByCode(new GetClauseByCodeQuery(clauseCode, tenantId))
                .orElseThrow(() -> new RuntimeException("条款不存在"));
        return ResponseEntity.ok(toDTO(clause));
    }

    /**
     * 更新条款
     */
    @PutMapping("/{clauseId}")
    public ResponseEntity<ClauseDTO> updateClause(@PathVariable String clauseId,
                                                  @RequestBody UpdateClauseRequest request) {
        String tenantId = TenantContext.getCurrentTenant();
        clauseApplicationService.updateClause(clauseId, request.getClauseName(), request.getClauseType(),
                request.getContent(), request.getDescription(), request.getInsuranceType(),
                request.getEffectiveDate(), request.getExpiryDate(), request.getUpdatedBy(), tenantId);
        var clause = clauseQueryService.handle(new GetClauseByIdQuery(new ClauseId(clauseId), tenantId))
                .orElseThrow(() -> new RuntimeException("条款不存在"));
        return ResponseEntity.ok(toDTO(clause));
    }

    /**
     * 激活条款
     */
    @PutMapping("/{clauseId}/activate")
    public ResponseEntity<Void> activateClause(@PathVariable String clauseId,
                                               @RequestBody ActivateClauseRequest request) {
        String tenantId = TenantContext.getCurrentTenant();
        clauseApplicationService.activateClause(clauseId, request.getActivatedBy(), tenantId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 停用条款
     */
    @PutMapping("/{clauseId}/inactivate")
    public ResponseEntity<Void> inactivateClause(@PathVariable String clauseId,
                                                 @RequestBody InactivateClauseRequest request) {
        String tenantId = TenantContext.getCurrentTenant();
        clauseApplicationService.inactivateClause(clauseId, request.getInactivatedBy(), tenantId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 提交条款审批
     */
    @PutMapping("/{clauseId}/submit-approval")
    public ResponseEntity<Void> submitForApproval(@PathVariable String clauseId,
                                                  @RequestBody Map<String, String> request) {
        String tenantId = TenantContext.getCurrentTenant();
        clauseApplicationService.submitForApproval(clauseId, request.get("submittedBy"), tenantId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 审批通过条款
     */
    @PutMapping("/{clauseId}/approve")
    public ResponseEntity<Void> approveClause(@PathVariable String clauseId,
                                              @RequestBody Map<String, String> request) {
        String tenantId = TenantContext.getCurrentTenant();
        clauseApplicationService.approveClause(clauseId, ApprovalType.fromCode(request.get("approvalType")),
                request.get("approverId"), request.get("approverName"), request.get("comment"), tenantId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 审批驳回条款
     */
    @PutMapping("/{clauseId}/reject")
    public ResponseEntity<Void> rejectClause(@PathVariable String clauseId,
                                             @RequestBody Map<String, String> request) {
        String tenantId = TenantContext.getCurrentTenant();
        clauseApplicationService.rejectClause(clauseId, ApprovalType.fromCode(request.get("approvalType")),
                request.get("approverId"), request.get("approverName"), request.get("comment"), tenantId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 条款修订（基于当前版本创建新DRAFT版本）
     */
    @PostMapping("/{clauseId}/revise")
    public ResponseEntity<Map<String, String>> reviseClause(@PathVariable String clauseId,
                                                            @RequestBody Map<String, String> request) {
        String tenantId = TenantContext.getCurrentTenant();
        ClauseId newClauseId = clauseApplicationService.reviseClause(clauseId, request.get("revisedBy"), tenantId);
        return new ResponseEntity<>(Map.of("newClauseId", newClauseId.getValue()), HttpStatus.CREATED);
    }

    /**
     * 条款归档
     */
    @PutMapping("/{clauseId}/archive")
    public ResponseEntity<Void> archiveClause(@PathVariable String clauseId,
                                              @RequestBody Map<String, String> request) {
        String tenantId = TenantContext.getCurrentTenant();
        clauseApplicationService.archiveClause(clauseId, request.get("archivedBy"), tenantId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 根据状态查询条款列表
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<ClauseDTO>> getClausesByStatus(@PathVariable String status) {
        String tenantId = TenantContext.getCurrentTenant();
        var clauses = clauseQueryService.queryClausesByStatus(
                        new GetClausesByStatusQuery(ClauseEnum.ClauseStatus.fromCode(status), tenantId))
                .orElseThrow(() -> new RuntimeException("条款不存在"));
        var clauseDTOs = clauses.stream().map(this::toDTO).toList();
        return ResponseEntity.ok(clauseDTOs);
    }

    /**
     * 根据类型查询条款列表
     */
    @GetMapping("/type/{clauseType}")
    public ResponseEntity<List<ClauseDTO>> getClausesByType(@PathVariable String clauseType) {
        String tenantId = TenantContext.getCurrentTenant();
        var clauses = clauseQueryService.queryClausesByType(
                        new GetClausesByTypeQuery(InsuranceType.fromCode(clauseType), tenantId))
                .orElseThrow(() -> new RuntimeException("条款不存在"));
        var clauseDTOs = clauses.stream().map(this::toDTO).toList();
        return ResponseEntity.ok(clauseDTOs);
    }

    /**
     * 查询所有条款
     */
    @GetMapping
    public ResponseEntity<List<ClauseDTO>> getAllClauses() {
        String tenantId = TenantContext.getCurrentTenant();
        var clauses = clauseQueryService.queryAllClauses(new GetClauseAllQuery(tenantId))
                .orElseThrow(() -> new RuntimeException("条款不存在"));
        var clauseDTOs = clauses.stream().map(this::toDTO).toList();
        return ResponseEntity.ok(clauseDTOs);
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
     * 将领域对象转换为DTO
     */
    private ClauseDTO toDTO(Clause clause) {
        ClauseDTO dto = new ClauseDTO();
        dto.setClauseId(clause.getClauseId().getValue());
        dto.setClauseCode(clause.getClauseCode().getValue());
        dto.setClauseName(clause.getClauseName().getValue());
        dto.setClauseType(clause.getClauseType());
        dto.setContent(clause.getContent());
        dto.setDescription(clause.getDescription());
        dto.setStatus(clause.getStatus());
        dto.setVersion(clause.getVersion() != null ? clause.getVersion().getValue() : null);
        dto.setInsuranceType(clause.getInsuranceType());
        dto.setParentClauseId(clause.getParentClauseId() != null ? clause.getParentClauseId().getValue() : null);
        dto.setEffectiveDate(clause.getEffectiveDate());
        dto.setExpiryDate(clause.getExpiryDate());
        dto.setCreatedBy(clause.getCreatedBy());
        dto.setCreatedAt(clause.getCreatedAt());
        dto.setUpdatedBy(clause.getUpdatedBy());
        dto.setUpdatedAt(clause.getUpdatedAt());
        dto.setTenantId(clause.getTenantId());
        return dto;
    }
}
