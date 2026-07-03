package com.titanium.clause.repository;

import java.util.List;
import java.util.Optional;

import com.titanium.clause.aggregate.Clause;
import com.titanium.clause.valueobject.ClauseCode;
import com.titanium.clause.valueobject.ClauseId;
import com.titanium.metadata.enums.InsuranceType;
import com.titanium.metadata.enums.clause.ClauseEnum;

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
    List<Clause> findByStatus(ClauseEnum.ClauseStatus status, String tenantId);

    /**
     * 根据险种类型查找条款
     * @param insuranceType 险种类型
     * @param tenantId 租户ID
     * @return 条款列表
     */
    List<Clause> findByType(InsuranceType insuranceType, String tenantId);

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
