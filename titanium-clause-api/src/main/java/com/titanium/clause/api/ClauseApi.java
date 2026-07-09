package com.titanium.clause.api;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.titanium.clause.api.dto.ActivateClauseDTO;
import com.titanium.clause.api.dto.ClauseDTO;
import com.titanium.clause.api.dto.CreateClauseDTO;
import com.titanium.clause.api.dto.InactivateClauseDTO;
import com.titanium.clause.api.dto.UpdateClauseDTO;

/**
 * 条款聚合对外契约（Feign）
 * <p>
 * 命名主键为聚合根 {@code Clause}，承载条款的跨服务远程调用。契约路径遵从内部服务远程调用规约
 * {@code /api/v1/clauses}，由 web 层 {@code ClauseApiProvider} 实现，路径不得篡改。所有方法透传
 * {@code X-Tenant-Id} 请求头贯穿多租户上下文，入出参一律使用 api 层 DTO。
 * </p>
 * <p>
 * 同域多个 {@code @FeignClient} 的 {@code name} 相同，必须各配唯一 {@code contextId}，否则
 * Spring 启动报「Multiple @FeignClient with the same name」Bean 冲突。原 {@code ClauseClient}
 * （Client 后缀）为老式命名的冗余契约，已重命名为本接口。
 * </p>
 */
@FeignClient(name = "titanium-clause-service", contextId = "clauseApi")
@RequestMapping("/api/v1/clauses")
public interface ClauseApi {

    /**
     * 创建条款
     *
     * @param dto 创建条款 DTO
     * @param tenantId 租户ID
     * @return 条款DTO
     */
    @PostMapping
    ClauseDTO createClause(@RequestBody CreateClauseDTO dto,
                           @RequestHeader("X-Tenant-Id") String tenantId);

    /**
     * 更新条款
     *
     * @param clauseId 条款ID
     * @param dto 更新条款 DTO
     * @param tenantId 租户ID
     * @return 条款DTO
     */
    @PutMapping("/{clauseId}")
    ClauseDTO updateClause(@PathVariable("clauseId") String clauseId,
                           @RequestBody UpdateClauseDTO dto,
                           @RequestHeader("X-Tenant-Id") String tenantId);

    /**
     * 获取条款详情
     *
     * @param clauseId 条款ID
     * @param tenantId 租户ID
     * @return 条款DTO
     */
    @GetMapping("/{clauseId}")
    ClauseDTO getClauseById(@PathVariable("clauseId") String clauseId,
                            @RequestHeader("X-Tenant-Id") String tenantId);

    /**
     * 获取条款列表
     *
     * @param status 状态码
     * @param clauseType 条款/险种类型码
     * @param tenantId 租户ID
     * @return 条款DTO列表
     */
    @GetMapping
    List<ClauseDTO> getClauses(@RequestParam(required = false) String status,
                               @RequestParam(required = false) String clauseType,
                               @RequestHeader("X-Tenant-Id") String tenantId);

    /**
     * 激活条款
     *
     * @param clauseId 条款ID
     * @param dto 激活条款 DTO
     * @param tenantId 租户ID
     * @return 条款DTO
     */
    @PutMapping("/{clauseId}/activate")
    ClauseDTO activateClause(@PathVariable("clauseId") String clauseId,
                             @RequestBody ActivateClauseDTO dto,
                             @RequestHeader("X-Tenant-Id") String tenantId);

    /**
     * 停用条款
     *
     * @param clauseId 条款ID
     * @param dto 停用条款 DTO
     * @param tenantId 租户ID
     * @return 条款DTO
     */
    @PutMapping("/{clauseId}/inactivate")
    ClauseDTO inactivateClause(@PathVariable("clauseId") String clauseId,
                               @RequestBody InactivateClauseDTO dto,
                               @RequestHeader("X-Tenant-Id") String tenantId);
}
