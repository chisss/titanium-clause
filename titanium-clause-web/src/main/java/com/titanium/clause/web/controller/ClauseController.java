package com.titanium.clause.web.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.titanium.clause.api.dto.ClauseDTO;
import com.titanium.clause.api.request.ActivateClauseRequest;
import com.titanium.clause.api.request.CreateClauseRequest;
import com.titanium.clause.api.request.InactivateClauseRequest;
import com.titanium.clause.api.request.UpdateClauseRequest;
import com.titanium.clause.application.query.ClauseAppQueryService;
import com.titanium.clause.application.service.ClauseApplicationService;
import com.titanium.clause.common.context.TenantContext;
import com.titanium.clause.common.enums.ApprovalType;
import com.titanium.clause.query.result.ClauseQueryResult;
import com.titanium.clause.valueobject.ClauseId;
import com.titanium.metadata.enums.InsuranceType;
import com.titanium.metadata.enums.clause.ClauseEnum;

import lombok.RequiredArgsConstructor;

/**
 * 条款控制器
 * <p>
 * 表现层仅调用应用层 command/query 服务：写入口经 {@link ClauseApplicationService}（构造命令）， 读入口经
 * {@link ClauseAppQueryService} 查询读模型返回 {@link ClauseQueryResult}，由本类组装为对外 {@link ClauseDTO}。
 * </p>
 */
@RestController
@RequestMapping("/api/clauses")
@RequiredArgsConstructor
public class ClauseController {
    private final ClauseApplicationService clauseApplicationService;
    private final ClauseAppQueryService    clauseAppQueryService;

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
        ClauseQueryResult result = clauseAppQueryService.findById(clauseId.getValue(), tenantId)
                .orElseThrow(() -> new RuntimeException("创建条款失败"));
        return new ResponseEntity<>(toDTO(result), HttpStatus.CREATED);
    }

    /**
     * 根据ID查询条款
     */
    @GetMapping("/{clauseId}")
    public ResponseEntity<ClauseDTO> getClauseById(@PathVariable String clauseId) {
        String tenantId = TenantContext.getCurrentTenant();
        ClauseQueryResult result = clauseAppQueryService.findById(clauseId, tenantId)
                .orElseThrow(() -> new RuntimeException("条款不存在"));
        return ResponseEntity.ok(toDTO(result));
    }

    /**
     * 根据条款代码查询条款
     */
    @GetMapping("/code/{clauseCode}")
    public ResponseEntity<ClauseDTO> getClauseByCode(@PathVariable String clauseCode) {
        String tenantId = TenantContext.getCurrentTenant();
        ClauseQueryResult result = clauseAppQueryService.findByCode(clauseCode, tenantId)
                .orElseThrow(() -> new RuntimeException("条款不存在"));
        return ResponseEntity.ok(toDTO(result));
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
        ClauseQueryResult result = clauseAppQueryService.findById(clauseId, tenantId)
                .orElseThrow(() -> new RuntimeException("条款不存在"));
        return ResponseEntity.ok(toDTO(result));
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
        List<ClauseDTO> clauseDTOs = clauseAppQueryService
                .findByStatus(ClauseEnum.ClauseStatus.fromCode(status), tenantId).stream().map(this::toDTO).toList();
        return ResponseEntity.ok(clauseDTOs);
    }

    /**
     * 根据类型查询条款列表
     */
    @GetMapping("/type/{clauseType}")
    public ResponseEntity<List<ClauseDTO>> getClausesByType(@PathVariable String clauseType) {
        String tenantId = TenantContext.getCurrentTenant();
        List<ClauseDTO> clauseDTOs = clauseAppQueryService
                .findByType(InsuranceType.fromCode(clauseType), tenantId).stream().map(this::toDTO).toList();
        return ResponseEntity.ok(clauseDTOs);
    }

    /**
     * 查询所有条款
     */
    @GetMapping
    public ResponseEntity<List<ClauseDTO>> getAllClauses() {
        String tenantId = TenantContext.getCurrentTenant();
        List<ClauseDTO> clauseDTOs = clauseAppQueryService.findAll(tenantId).stream().map(this::toDTO).toList();
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
     * 将读模型查询结果转换为对外DTO
     */
    private ClauseDTO toDTO(ClauseQueryResult result) {
        ClauseDTO dto = new ClauseDTO();
        dto.setClauseId(result.getClauseId());
        dto.setClauseCode(result.getClauseCode());
        dto.setClauseName(result.getClauseName());
        dto.setClauseType(result.getClauseType());
        dto.setContent(result.getContent());
        dto.setDescription(result.getDescription());
        dto.setStatus(result.getStatus());
        dto.setVersion(result.getVersion());
        dto.setInsuranceType(result.getInsuranceType());
        dto.setParentClauseId(result.getParentClauseId());
        dto.setEffectiveDate(result.getEffectiveDate());
        dto.setExpiryDate(result.getExpiryDate());
        dto.setCreatedBy(result.getCreatedBy());
        dto.setCreatedAt(result.getCreatedAt());
        dto.setUpdatedBy(result.getUpdatedBy());
        dto.setUpdatedAt(result.getUpdatedAt());
        dto.setTenantId(result.getTenantId());
        return dto;
    }
}
