package com.titanium.clause.api;

import com.titanium.clause.api.request.ActivateClauseRequest;
import com.titanium.clause.api.dto.ClauseDTO;
import com.titanium.clause.api.request.CreateClauseRequest;
import com.titanium.clause.api.request.InactivateClauseRequest;
import com.titanium.clause.api.request.UpdateClauseRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 条款服务远程调用接口
 */
@FeignClient(name = "titanium-clause-service")
public interface ClauseClient {
    /**
     * 创建条款
     *
     * @param request 创建条款请求
     * @return 条款DTO
     */
    @PostMapping("/api/clauses")
    ClauseDTO createClause(@RequestBody CreateClauseRequest request);

    /**
     * 更新条款
     *
     * @param clauseId 条款ID
     * @param request  更新条款请求
     * @return 条款DTO
     */
    @PutMapping("/api/clauses/{clauseId}")
    ClauseDTO updateClause(@PathVariable String clauseId, @RequestBody UpdateClauseRequest request);

    /**
     * 获取条款详情
     *
     * @param clauseId 条款ID
     * @return 条款DTO
     */
    @GetMapping("/api/clauses/{clauseId}")
    ClauseDTO getClauseById(@PathVariable String clauseId);

    /**
     * 获取条款列表
     *
     * @param status     状态
     * @param clauseType 条款类型
     * @return 条款DTO列表
     */
    @GetMapping("/api/clauses")
    List<ClauseDTO> getClauses(@RequestParam(required = false) String status, @RequestParam(required = false) String clauseType);

    /**
     * 激活条款
     *
     * @param clauseId 条款ID
     * @param request  激活条款请求
     * @return 条款DTO
     */
    @PutMapping("/api/clauses/{clauseId}/activate")
    ClauseDTO activateClause(@PathVariable String clauseId, @RequestBody ActivateClauseRequest request);

    /**
     * 停用条款
     *
     * @param clauseId 条款ID
     * @param request  停用条款请求
     * @return 条款DTO
     */
    @PutMapping("/api/clauses/{clauseId}/inactivate")
    ClauseDTO inactivateClause(@PathVariable String clauseId, @RequestBody InactivateClauseRequest request);
}