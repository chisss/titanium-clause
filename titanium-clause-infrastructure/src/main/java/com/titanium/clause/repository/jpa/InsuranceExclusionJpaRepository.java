package com.titanium.clause.repository.jpa;

import com.titanium.clause.entity.InsuranceExclusionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * 责任免除JPA仓储接口
 */
public interface InsuranceExclusionJpaRepository extends JpaRepository<InsuranceExclusionEntity, String> {
    /**
     * 根据责任免除类型和租户ID查找责任免除
     * @param type 责任免除类型
     * @param tenantId 租户ID
     * @return 责任免除实体列表
     */
    List<InsuranceExclusionEntity> findByTypeAndTenantId(String type, String tenantId);

    /**
     * 根据状态和租户ID查找责任免除
     * @param status 责任免除状态
     * @param tenantId 租户ID
     * @return 责任免除实体列表
     */
    List<InsuranceExclusionEntity> findByStatusAndTenantId(String status, String tenantId);

    /**
     * 根据租户ID查找所有责任免除
     * @param tenantId 租户ID
     * @return 责任免除实体列表
     */
    List<InsuranceExclusionEntity> findByTenantId(String tenantId);

    /**
     * 根据租户ID和是否删除查找责任免除
     * @param tenantId 租户ID
     * @param isDeleted 是否删除
     * @return 责任免除实体列表
     */
    List<InsuranceExclusionEntity> findByTenantIdAndIsDeleted(String tenantId, Integer isDeleted);

    /**
     * 删除责任免除
     * @param id 责任免除ID
     * @param tenantId 租户ID
     */
    @Query("DELETE FROM InsuranceExclusionEntity e WHERE e.id = :id AND e.tenantId = :tenantId")
    void deleteByIdAndTenantId(@Param("id") String id, @Param("tenantId") String tenantId);
}