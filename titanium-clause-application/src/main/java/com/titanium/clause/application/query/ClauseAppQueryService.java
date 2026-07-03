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
import com.titanium.clause.query.result.ClauseQueryResult;
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
}
