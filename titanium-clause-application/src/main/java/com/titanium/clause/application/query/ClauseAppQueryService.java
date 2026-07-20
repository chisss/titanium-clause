package com.titanium.clause.application.query;

import java.util.List;
import java.util.Optional;

import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.stereotype.Service;

import com.titanium.clause.query.query.FindAllClausesQuery;
import com.titanium.clause.query.query.FindClauseByCodeQuery;
import com.titanium.clause.query.query.FindClauseByIdQuery;
import com.titanium.clause.query.query.FindClausesByStatusQuery;
import com.titanium.clause.query.query.FindClausesByTypeQuery;
import com.titanium.clause.query.query.FindCoveragesByClauseIdQuery;
import com.titanium.clause.query.query.FindPremiumRuleByClauseIdAndVersionQuery;
import com.titanium.clause.query.query.FindPremiumRuleByClauseIdQuery;
import com.titanium.clause.query.result.ClauseQueryResult;
import com.titanium.clause.query.result.CoverageQueryResult;
import com.titanium.clause.query.result.PremiumRuleQueryResult;
import com.titanium.metadata.enums.InsuranceType;
import com.titanium.metadata.enums.clause.ClauseEnum;

import jakarta.annotation.Resource;

/**
 * 条款查询服务（CQRS 读侧入口）
 * <p>
 * 读写分离落地：经 {@link QueryGateway} 派发查询到读侧 {@code ClauseQueryHandler}，
 * 查询 {@code ClauseView} 读模型，<b>不再回退到写模型聚合 {@code Clause}</b>。 读侧与写侧彻底解耦，查询走独立优化的读模型表
 * {@code t_clause_view}。
 * </p>
 */
@Service
public class ClauseAppQueryService {

    @Resource
    private QueryGateway queryGateway;

    /**
     * 根据ID查询条款（读模型）
     */
    public Optional<ClauseQueryResult> findById(String clauseId, String tenantId) {
        ClauseQueryResult result = queryGateway
                .query(new FindClauseByIdQuery(clauseId, tenantId), ResponseTypes.instanceOf(ClauseQueryResult.class))
                .join();
        return Optional.ofNullable(result);
    }

    /**
     * 根据条款代码查询条款（读模型）
     */
    public Optional<ClauseQueryResult> findByCode(String clauseCode, String tenantId) {
        ClauseQueryResult result = queryGateway
                .query(new FindClauseByCodeQuery(clauseCode, tenantId),
                        ResponseTypes.instanceOf(ClauseQueryResult.class))
                .join();
        return Optional.ofNullable(result);
    }

    /**
     * 根据状态查询条款列表（读模型）
     */
    public List<ClauseQueryResult> findByStatus(ClauseEnum.ClauseStatus status, String tenantId) {
        return queryGateway.query(new FindClausesByStatusQuery(status, tenantId),
                ResponseTypes.multipleInstancesOf(ClauseQueryResult.class)).join();
    }

    /**
     * 根据险种类型查询条款列表（读模型）
     */
    public List<ClauseQueryResult> findByType(InsuranceType insuranceType, String tenantId) {
        return queryGateway.query(new FindClausesByTypeQuery(insuranceType, tenantId),
                ResponseTypes.multipleInstancesOf(ClauseQueryResult.class)).join();
    }

    /**
     * 查询全部条款列表（读模型）
     */
    public List<ClauseQueryResult> findAll(String tenantId) {
        return queryGateway.query(new FindAllClausesQuery(tenantId),
                ResponseTypes.multipleInstancesOf(ClauseQueryResult.class)).join();
    }

    /**
     * 查询某条款下的全部保险责任（读模型）
     */
    public List<CoverageQueryResult> findCoveragesByClauseId(String clauseId, String tenantId) {
        return queryGateway.query(new FindCoveragesByClauseIdQuery(clauseId, tenantId),
                ResponseTypes.multipleInstancesOf(CoverageQueryResult.class)).join();
    }

    /**
     * 查询某条款的缴费规则（读模型，含四维年龄性别费率表）
     */
    public Optional<PremiumRuleQueryResult> findPremiumRuleByClauseId(String clauseId, String tenantId) {
        PremiumRuleQueryResult result = queryGateway
                .query(new FindPremiumRuleByClauseIdQuery(clauseId, tenantId),
                        ResponseTypes.instanceOf(PremiumRuleQueryResult.class))
                .join();
        return Optional.ofNullable(result);
    }

    /**
     * 查询某条款的缴费规则（支持按费率表编码+版本精确匹配，BILL-2）
     * <p>
     * 版本查询策略：tableCode 和 version 均为 null 时返回默认规则（向后兼容）；
     * tableCode 非空但 version 为 null 时返回该 tableCode 最新版；
     * 均非空时精确匹配。
     * </p>
     *
     * @param clauseId  条款ID
     * @param tableCode 费率表编码（可选）
     * @param version   费率表版本（可选）
     * @param tenantId  租户ID
     * @return 缴费规则查询结果，未找到时为空
     */
    public Optional<PremiumRuleQueryResult> findPremiumRuleByClauseIdAndVersion(String clauseId, String tableCode,
                                                                                 String version, String tenantId) {
        PremiumRuleQueryResult result = queryGateway
                .query(new FindPremiumRuleByClauseIdAndVersionQuery(clauseId, tableCode, version, tenantId),
                        ResponseTypes.instanceOf(PremiumRuleQueryResult.class))
                .join();
        return Optional.ofNullable(result);
    }
}
