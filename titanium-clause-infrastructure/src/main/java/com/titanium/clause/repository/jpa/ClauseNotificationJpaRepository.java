package com.titanium.clause.repository.jpa;

import com.titanium.clause.entity.ClauseNotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * 条款告知书JPA仓储接口
 */
public interface ClauseNotificationJpaRepository extends JpaRepository<ClauseNotificationEntity, String> {
    /**
     * 根据ID和租户ID查找条款告知书
     * @param id 告知书ID
     * @param tenantId 租户ID
     * @return 条款告知书实体
     */
    Optional<ClauseNotificationEntity> findByIdAndTenantId(String id, String tenantId);

    /**
     * 根据条款ID和租户ID查找条款告知书
     * @param clauseId 条款ID
     * @param tenantId 租户ID
     * @return 条款告知书实体列表
     */
    List<ClauseNotificationEntity> findByClauseIdAndTenantId(String clauseId, String tenantId);

    /**
     * 根据状态和租户ID查找条款告知书
     * @param status 状态
     * @param tenantId 租户ID
     * @return 条款告知书实体列表
     */
    List<ClauseNotificationEntity> findByStatusAndTenantId(String status, String tenantId);

    /**
     * 根据租户ID和是否删除查找条款告知书
     * @param tenantId 租户ID
     * @param isDeleted 是否删除
     * @return 条款告知书实体列表
     */
    List<ClauseNotificationEntity> findByTenantIdAndIsDeleted(String tenantId, Integer isDeleted);

    /**
     * 根据条款ID和租户ID删除条款告知书
     * @param clauseId 条款ID
     * @param tenantId 租户ID
     */
    @Query("DELETE FROM ClauseNotificationEntity c WHERE c.clauseId = :clauseId AND c.tenantId = :tenantId")
    void deleteByClauseIdAndTenantId(@Param("clauseId") String clauseId, @Param("tenantId") String tenantId);

    /**
     * 根据ID和租户ID删除条款告知书
     * @param id 告知书ID
     * @param tenantId 租户ID
     */
    @Query("DELETE FROM ClauseNotificationEntity c WHERE c.id = :id AND c.tenantId = :tenantId")
    void deleteByIdAndTenantId(@Param("id") String id, @Param("tenantId") String tenantId);
}