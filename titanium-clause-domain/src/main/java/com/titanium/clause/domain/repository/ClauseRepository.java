package com.titanium.clause.domain.repository;

import com.titanium.clause.domain.valueobject.ClauseId;
import com.titanium.clause.domain.valueobject.ClauseCode;
import com.titanium.clause.domain.aggregate.Clause;

import java.util.List;
import java.util.Optional;

/**
 * 条款仓储接口
 */
public interface ClauseRepository {
    /**
     * 保存条款
     * @param clause 条款对象
     * @return 保存后的条款对象
     */
    Clause save(Clause clause);

    /**
     * 根据ID查找条款
     * @param clauseId 条款ID
     * @param tenantId 租户ID
     * @return 条款对象
     */
    Optional<Clause> findById(ClauseId clauseId, String tenantId);

    /**
     * 根据条款代码查找条款
     * @param clauseCode 条款代码
     * @param tenantId 租户ID
     * @return 条款对象
     */
    Optional<Clause> findByCode(ClauseCode clauseCode, String tenantId);

    /**
     * 根据状态查找条款
     * @param status 条款状态
     * @param tenantId 租户ID
     * @return 条款列表
     */
    List<Clause> findByStatus(String status, String tenantId);

    /**
     * 根据类型查找条款
     * @param clauseType 条款类型
     * @param tenantId 租户ID
     * @return 条款列表
     */
    List<Clause> findByType(String clauseType, String tenantId);

    /**
     * 查找所有条款
     * @param tenantId 租户ID
     * @return 条款列表
     */
    List<Clause> findAll(String tenantId);

    /**
     * 删除条款
     * @param clauseId 条款ID
     * @param tenantId 租户ID
     */
    void deleteById(ClauseId clauseId, String tenantId);
}
