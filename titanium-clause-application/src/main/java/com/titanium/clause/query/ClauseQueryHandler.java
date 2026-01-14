package com.titanium.clause.query;

import java.util.Optional;

import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

import com.titanium.clause.aggregate.Clause;
import com.titanium.clause.repository.ClauseRepository;

/**
 * 条款查询处理器
 */
@Component
public class ClauseQueryHandler {

    private final ClauseRepository clauseRepository;

    public ClauseQueryHandler(ClauseRepository clauseRepository) {
        this.clauseRepository = clauseRepository;
    }

    /**
     * 处理获取条款查询
     * 
     * @param query 获取条款查询
     * @return 条款领域对象
     */
    @QueryHandler
    public Optional<Clause> handle(GetClauseQuery query) {
        return clauseRepository.findById(query.clauseId(), query.tenantId());
    }
}
