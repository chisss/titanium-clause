package com.titanium.clause.application.query;

import java.util.List;
import java.util.Optional;

import org.axonframework.queryhandling.QueryGateway;
import org.springframework.stereotype.Component;

import com.titanium.clause.domain.aggregate.Clause;
import com.titanium.clause.domain.query.GetClauseAllQuery;
import com.titanium.clause.domain.query.GetClauseByCodeQuery;
import com.titanium.clause.domain.query.GetClauseByIdQuery;
import com.titanium.clause.domain.query.GetClausesByStatusQuery;
import com.titanium.clause.domain.query.GetClausesByTypeQuery;

/**
 * 条款查询处理器
 */
@Component
public class ClauseQueryService {

    private final QueryGateway queryGateway;

    public ClauseQueryService(QueryGateway queryGateway) {
        this.queryGateway = queryGateway;

    }

    /**
     * 处理获取条款查询
     *
     * @param query 获取条款查询
     * @return 条款领域对象
     */
    public Optional<Clause> handle(GetClauseByIdQuery query) {
        return queryGateway.query(query, Optional.class).join();
    }

    public Optional<Clause> queryClauseByCode(GetClauseByCodeQuery query) {
        return queryGateway.query(query, Optional.class).join();
    }

    public Optional<List<Clause>> queryClausesByStatus(GetClausesByStatusQuery query) {
        return queryGateway.query(query, Optional.class).join();
    }

    public Optional<List<Clause>> queryClausesByType(GetClausesByTypeQuery query) {
        return queryGateway.query(query, Optional.class).join();
    }

    public Optional<List<Clause>> queryAllClauses(GetClauseAllQuery query) {
        return queryGateway.query(query, Optional.class).join();
    }
}
