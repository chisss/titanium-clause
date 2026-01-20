package com.titanium.clause.controller;

import java.util.List;

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
import com.titanium.clause.application.query.ClauseQueryService;
import com.titanium.clause.application.service.ClauseApplicationService;
import com.titanium.clause.domain.aggregate.Clause;
import com.titanium.clause.domain.query.GetClauseAllQuery;
import com.titanium.clause.domain.query.GetClauseByCodeQuery;
import com.titanium.clause.domain.query.GetClauseByIdQuery;
import com.titanium.clause.domain.query.GetClausesByStatusQuery;
import com.titanium.clause.domain.query.GetClausesByTypeQuery;
import com.titanium.clause.domain.valueobject.ClauseId;
import com.titanium.clause.infrastructure.config.TenantContext;

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
     *
     * @param request 创建条款请求
     * @return 创建的条款DTO
     */
    @PostMapping
    public ResponseEntity<ClauseDTO> createClause(@RequestBody CreateClauseRequest request) {
        String tenantId = TenantContext.getCurrentTenant();
        ClauseId clauseId = clauseApplicationService.createClause(request.getClauseCode(), request.getClauseName(),
                request.getClauseType(), request.getContent(), request.getDescription(), request.getEffectiveDate(),
                request.getExpiryDate(), request.getCreatedBy(), tenantId);
        // 查询创建后的条款
        var clause = clauseQueryService.handle(new GetClauseByIdQuery(clauseId, tenantId))
                .orElseThrow(() -> new RuntimeException("创建条款失败"));
        return new ResponseEntity<>(toDTO(clause), HttpStatus.CREATED);
    }

    /**
     * 根据ID查询条款
     *
     * @param clauseId 条款ID
     * @return 条款DTO
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
     *
     * @param clauseCode 条款代码
     * @return 条款DTO
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
     *
     * @param clauseId 条款ID
     * @param request 更新条款请求
     * @return 更新后的条款DTO
     */
    @PutMapping("/{clauseId}")
    public ResponseEntity<ClauseDTO> updateClause(@PathVariable String clauseId,
                                                  @RequestBody UpdateClauseRequest request) {
        String tenantId = TenantContext.getCurrentTenant();
        clauseApplicationService.updateClause(clauseId, request.getClauseName(), request.getClauseType(),
                request.getContent(), request.getDescription(), request.getEffectiveDate(), request.getExpiryDate(),
                request.getUpdatedBy(), tenantId);
        // 查询更新后的条款
        var clause = clauseQueryService.handle(new GetClauseByIdQuery(new ClauseId(clauseId), tenantId))
                .orElseThrow(() -> new RuntimeException("条款不存在"));
        return ResponseEntity.ok(toDTO(clause));
    }

    /**
     * 激活条款
     *
     * @param clauseId 条款ID
     * @param request 激活条款请求
     * @return 响应实体
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
     *
     * @param clauseId 条款ID
     * @param request 停用条款请求
     * @return 响应实体
     */
    @PutMapping("/{clauseId}/inactivate")
    public ResponseEntity<Void> inactivateClause(@PathVariable String clauseId,
                                                 @RequestBody InactivateClauseRequest request) {
        String tenantId = TenantContext.getCurrentTenant();
        clauseApplicationService.inactivateClause(clauseId, request.getInactivatedBy(), tenantId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 根据状态查询条款列表
     *
     * @param status 条款状态
     * @return 条款DTO列表
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<ClauseDTO>> getClausesByStatus(@PathVariable String status) {
        String tenantId = TenantContext.getCurrentTenant();
        var clauses = clauseQueryService.queryClausesByStatus(new GetClausesByStatusQuery(status, tenantId))
                .orElseThrow(() -> new RuntimeException("条款不存在"));
        var clauseDTOs = clauses.stream().map(this::toDTO).toList();
        return ResponseEntity.ok(clauseDTOs);
    }

    /**
     * 根据类型查询条款列表
     *
     * @param clauseType 条款类型
     * @return 条款DTO列表
     */
    @GetMapping("/type/{clauseType}")
    public ResponseEntity<List<ClauseDTO>> getClausesByType(@PathVariable String clauseType) {
        String tenantId = TenantContext.getCurrentTenant();
        var clauses = clauseQueryService.queryClausesByType(new GetClausesByTypeQuery(clauseType, tenantId))
                .orElseThrow(() -> new RuntimeException("条款不存在"));
        var clauseDTOs = clauses.stream().map(this::toDTO).toList();
        return ResponseEntity.ok(clauseDTOs);
    }

    /**
     * 查询所有条款
     *
     * @return 条款DTO列表
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
     *
     * @param clauseId 条款ID
     * @return 响应实体
     */
    @DeleteMapping("/{clauseId}")
    public ResponseEntity<Void> deleteClause(@PathVariable String clauseId) {
        String tenantId = TenantContext.getCurrentTenant();
        clauseApplicationService.deleteClause(clauseId, tenantId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 将领域对象转换为DTO
     *
     * @param clause 领域对象
     * @return DTO对象
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
