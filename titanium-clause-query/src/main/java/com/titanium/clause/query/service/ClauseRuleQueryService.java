package com.titanium.clause.query.service;

import java.util.List;
import java.util.Optional;

import com.titanium.clause.query.result.CoverageQueryResult;
import com.titanium.clause.query.result.PremiumRuleQueryResult;

/**
 * 条款规则组件查询服务（CQRS 读侧）
 * <p>
 * 查询由事件投影维护的规则组件读模型表 {@code t_coverage_view} / {@code t_premium_rule_view}，
 * 返回稳定 DTO 契约。补齐此前保险责任(Coverage)与缴费规则(PremiumRule)仅存于 Axon 事件流、
 * 读模型不可查的缺口，为 billing 保费计算与前端展示提供数据入口。
 * </p>
 */
public interface ClauseRuleQueryService {

    /**
     * 查询某条款下的全部保险责任
     */
    List<CoverageQueryResult> getCoveragesByClauseId(String clauseId, String tenantId);

    /**
     * 查询某条款的缴费规则（含四维年龄性别费率表，默认查询）
     *
     * @param clauseId 条款ID
     * @param tenantId 租户ID
     * @return 缴费规则查询结果，未配置时为空
     */
    Optional<PremiumRuleQueryResult> getPremiumRuleByClauseId(String clauseId, String tenantId);

    /**
     * 查询某条款的缴费规则（支持按费率表编码+版本精确匹配，BILL-2）
     * <p>
     * 版本查询策略：
     * <ul>
     *   <li>tableCode 和 version 均为 {@code null}：返回该条款的默认费率规则（首条，向后兼容）</li>
     *   <li>tableCode 非空但 version 为 {@code null}：返回该 tableCode 的最新版本（按 version DESC）</li>
     *   <li>tableCode 和 version 均非空：精确匹配，未找到时返回空</li>
     * </ul>
     * </p>
     *
     * @param clauseId  条款ID
     * @param tableCode 费率表编码（可选）
     * @param version   费率表版本（可选）
     * @param tenantId  租户ID
     * @return 缴费规则查询结果，未找到时为空
     */
    Optional<PremiumRuleQueryResult> getPremiumRuleByClauseIdAndVersion(String clauseId, String tableCode,
                                                                         String version, String tenantId);
}
