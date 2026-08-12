package com.titanium.clause.api;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import com.titanium.clause.api.request.ActivateClauseRequest;
import com.titanium.clause.api.request.CreateClauseRequest;
import com.titanium.clause.api.request.InactivateClauseRequest;
import com.titanium.clause.api.request.UpdateClauseRequest;
import com.titanium.clause.api.response.ClauseResponse;
import com.titanium.clause.api.response.CoverageResponse;
import com.titanium.clause.api.response.PremiumRuleResponse;

/**
 * 条款聚合对外契约（Feign）
 * <p>
 * 命名主键为聚合根 {@code Clause}，承载条款的跨服务远程调用。契约路径遵从内部服务远程调用规约
 * {@code /api/v1/clauses}，由 web 层 {@code ClauseApiProvider} 实现，路径不得篡改。所有方法透传
 * {@code X-Tenant-Id} 请求头贯穿多租户上下文，入参使用 Request、出参使用 Response。
 * </p>
 * <p>
 * 同域多个 {@code @FeignClient} 的 {@code name} 相同，必须各配唯一 {@code contextId}，否则
 * Spring 启动报「Multiple @FeignClient with the same name」Bean 冲突。原 {@code ClauseClient}
 * （Client 后缀）为老式命名的冗余契约，已重命名为本接口。
 * </p>
 */
@FeignClient(name = "titanium-clause-service", contextId = "clauseApi", path = "/api/v1/clauses")
public interface ClauseApi {

    /**
     * 创建条款
     *
     * @param request 创建条款 Request
     * @param tenantId 租户ID
     * @return 条款 Response
     */
    @PostMapping
    ClauseResponse createClause(@RequestBody CreateClauseRequest request,
                           @RequestHeader("X-Tenant-Id") String tenantId);

    /**
     * 更新条款
     *
     * @param clauseId 条款ID
     * @param request 更新条款 Request
     * @param tenantId 租户ID
     * @return 条款 Response
     */
    @PutMapping("/{clauseId}")
    ClauseResponse updateClause(@PathVariable("clauseId") String clauseId,
                           @RequestBody UpdateClauseRequest request,
                           @RequestHeader("X-Tenant-Id") String tenantId);

    /**
     * 获取条款详情
     *
     * @param clauseId 条款ID
     * @param tenantId 租户ID
     * @return 条款 Response
     */
    @GetMapping("/{clauseId}")
    ClauseResponse getClauseById(@PathVariable("clauseId") String clauseId,
                            @RequestHeader("X-Tenant-Id") String tenantId);

    /**
     * 获取条款列表
     *
     * @param status 状态码
     * @param clauseType 条款/险种类型码
     * @param tenantId 租户ID
     * @return 条款 Response 列表
     */
    @GetMapping
    List<ClauseResponse> getClauses(@RequestParam(required = false) String status,
                               @RequestParam(required = false) String clauseType,
                               @RequestHeader("X-Tenant-Id") String tenantId);

    /**
     * 查询条款的缴费规则（含四维年龄性别费率表，支持版本精确匹配）
     * <p>
     * billing 域保费计算与费率表查询的数据入口：经条款读模型（{@code t_premium_rule_view}）返回结构化费率，
     * 未配置费率规则时返回 {@code null}。
     * </p>
     * <p>
     * <b>版本查询扩展（BILL-2）</b>：{@code tableCode} 和 {@code version} 可选参数支持按费率表编码+版本精确匹配。
     * 若均为 {@code null} 则返回该条款的默认费率规则（向后兼容）；若指定 {@code tableCode} 但 {@code version} 为 {@code null}，
     * 则返回该 tableCode 的最新版本；若均指定则精确匹配，未找到时返回 {@code null}。
     * </p>
     *
     * @param clauseId  条款ID
     * @param tableCode 费率表编码（可选，支持多版本费率表）
     * @param version   费率表版本（可选，配合 tableCode 使用）
     * @param tenantId  租户ID
     * @return 缴费规则 Response，未配置时为 {@code null}
     */
    @GetMapping("/{clauseId}/premium-rule")
    PremiumRuleResponse getPremiumRuleByClauseId(@PathVariable("clauseId") String clauseId,
                                            @RequestParam(required = false) String tableCode,
                                            @RequestParam(required = false) String version,
                                            @RequestHeader("X-Tenant-Id") String tenantId);

    /**
     * 激活条款
     *
     * @param clauseId 条款ID
     * @param request 激活条款 Request
     * @param tenantId 租户ID
     * @return 条款 Response
     */
    @PutMapping("/{clauseId}/activate")
    ClauseResponse activateClause(@PathVariable("clauseId") String clauseId,
                             @RequestBody ActivateClauseRequest request,
                             @RequestHeader("X-Tenant-Id") String tenantId);

    /**
     * 停用条款
     *
     * @param clauseId 条款ID
     * @param request 停用条款 Request
     * @param tenantId 租户ID
     * @return 条款 Response
     */
    @PutMapping("/{clauseId}/inactivate")
    ClauseResponse inactivateClause(@PathVariable("clauseId") String clauseId,
                               @RequestBody InactivateClauseRequest request,
                               @RequestHeader("X-Tenant-Id") String tenantId);

    /**
     * 查询条款下的保险责任清单（供 policy 出单装配责任快照、claim 定责调用）
     * <p>
     * 出单时 policy 域取此清单冻结为保单责任快照（L4），含责任保额、免赔、赔付比例与责任级等待期——
     * 这些是理赔定责的依据。结构化触发条件与赔付规则中的跨域必需要素已拍平为标量字段，
     * 调用方无需依赖条款域内部值对象类型。
     * </p>
     *
     * @param clauseId 条款ID
     * @param tenantId 租户ID
     * @return 保险责任列表，无责任时为空列表
     */
    @GetMapping("/{clauseId}/coverages")
    List<CoverageResponse> getCoveragesByClauseId(@PathVariable("clauseId") String clauseId,
                                             @RequestHeader("X-Tenant-Id") String tenantId);
}
