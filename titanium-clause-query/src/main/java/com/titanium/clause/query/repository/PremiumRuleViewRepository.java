package com.titanium.clause.query.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.titanium.clause.query.view.PremiumRuleView;

/**
 * 缴费规则读模型仓储
 * <p>
 * CQRS 查询侧仓储，访问读模型表 {@code t_premium_rule_view}。租户隔离经 {@code tenantId} 条件下推。
 * 一条款一费率规则，主键即 {@code clauseId}，按 {@code clauseId + tenantId} 精确查询。
 * </p>
 * <p>
 * <b>版本查询扩展（BILL-2）</b>：新增 {@code tableCode} 和 {@code tableVersion} 字段支持多版本费率表查询。
 * </p>
 */
@Repository
public interface PremiumRuleViewRepository
        extends JpaRepository<PremiumRuleView, String>, JpaSpecificationExecutor<PremiumRuleView> {

    /**
     * 按条款ID + 租户ID查询缴费规则（默认查询，向后兼容）
     */
    Optional<PremiumRuleView> findByClauseIdAndTenantId(String clauseId, String tenantId);

    /**
     * 按条款ID + 费率表编码 + 版本 + 租户ID精确查询缴费规则（BILL-2）
     *
     * @param clauseId     条款ID
     * @param tableCode    费率表编码
     * @param tableVersion 费率表版本
     * @param tenantId     租户ID
     * @return 缴费规则读模型，未找到时为空
     */
    Optional<PremiumRuleView> findByClauseIdAndTableCodeAndTableVersionAndTenantId(String clauseId, String tableCode,
                                                                                    String tableVersion,
                                                                                    String tenantId);

    /**
     * 按条款ID + 费率表编码 + 租户ID查询（取最新版本，按 tableVersion DESC，BILL-2）
     *
     * @param clauseId  条款ID
     * @param tableCode 费率表编码
     * @param tenantId  租户ID
     * @return 缴费规则读模型，未找到时为空
     */
    Optional<PremiumRuleView> findFirstByClauseIdAndTableCodeAndTenantIdOrderByTableVersionDesc(String clauseId,
                                                                                                 String tableCode,
                                                                                                 String tenantId);
}
