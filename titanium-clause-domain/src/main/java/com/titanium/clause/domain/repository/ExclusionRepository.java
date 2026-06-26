package com.titanium.clause.domain.repository;

import com.titanium.clause.domain.entity.Exclusion;
import com.titanium.clause.domain.enums.ExclusionType;
import com.titanium.clause.domain.valueobject.ExclusionId;
import com.titanium.metadata.enums.CommonStatus;

import java.util.List;
import java.util.Optional;

/**
 * 责任免除仓储接口
 */
public interface ExclusionRepository {
    /**
     * 保存责任免除
     * @param exclusion 责任免除对象
     * @return 保存后的责任免除对象
     */
    Exclusion save(Exclusion exclusion);

    /**
     * 根据ID查找责任免除
     * @param exclusionId 责任免除ID
     * @param tenantId 租户ID
     * @return 责任免除对象
     */
    Optional<Exclusion> findById(ExclusionId exclusionId, String tenantId);

    /**
     * 根据类型查找责任免除
     * @param type 责任免除类型
     * @param tenantId 租户ID
     * @return 责任免除列表
     */
    List<Exclusion> findByType(ExclusionType type, String tenantId);

    /**
     * 根据状态查找责任免除
     * @param status 责任免除状态
     * @param tenantId 租户ID
     * @return 责任免除列表
     */
    List<Exclusion> findByStatus(CommonStatus status, String tenantId);

    /**
     * 查找所有责任免除
     * @param tenantId 租户ID
     * @return 责任免除列表
     */
    List<Exclusion> findAll(String tenantId);

    /**
     * 删除责任免除
     * @param exclusionId 责任免除ID
     * @param tenantId 租户ID
     */
    void deleteById(ExclusionId exclusionId, String tenantId);
}