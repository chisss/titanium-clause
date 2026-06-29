package com.titanium.clause.infrastructure.repository.jpa;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.titanium.clause.domain.enums.SignTemplateType;
import com.titanium.clause.infrastructure.entity.ClauseSignTemplateEntity;

/**
 * 签约模板JPA仓储接口
 */
public interface ClauseSignTemplateJpaRepository extends JpaRepository<ClauseSignTemplateEntity, String> {
    /**
     * 根据ID和租户ID查找签约模板
     * @param id 模板ID
     * @param tenantId 租户ID
     * @return 签约模板实体
     */
    Optional<ClauseSignTemplateEntity> findByIdAndTenantId(String id, String tenantId);

    /**
     * 根据模板类型和租户ID查找签约模板
     * @param templateType 模板类型
     * @param tenantId 租户ID
     * @return 签约模板实体列表
     */
    List<ClauseSignTemplateEntity> findByTemplateTypeAndTenantId(SignTemplateType templateType, String tenantId);

    /**
     * 根据状态和租户ID查找签约模板
     * @param status 状态
     * @param tenantId 租户ID
     * @return 签约模板实体列表
     */
    List<ClauseSignTemplateEntity> findByStatusAndTenantId(String status, String tenantId);

    /**
     * 根据模板名称和租户ID查找签约模板
     * @param templateName 模板名称
     * @param tenantId 租户ID
     * @return 签约模板实体列表
     */
    List<ClauseSignTemplateEntity> findByTemplateNameContainingAndTenantId(String templateName, String tenantId);

    /**
     * 根据租户ID和是否删除查找签约模板
     * @param tenantId 租户ID
     * @param isDeleted 是否删除
     * @return 签约模板实体列表
     */
    List<ClauseSignTemplateEntity> findByTenantIdAndIsDeleted(String tenantId, Integer isDeleted);

    /**
     * 根据模板类型和状态查找签约模板
     * @param templateType 模板类型
     * @param status 状态
     * @param tenantId 租户ID
     * @return 签约模板实体列表
     */
    List<ClauseSignTemplateEntity> findByTemplateTypeAndStatusAndTenantId(SignTemplateType templateType, String status, String tenantId);

    /**
     * 根据ID和租户ID删除签约模板
     * @param id 模板ID
     * @param tenantId 租户ID
     */
    @Query("DELETE FROM ClauseSignTemplateEntity c WHERE c.id = :id AND c.tenantId = :tenantId")
    void deleteByIdAndTenantId(@Param("id") String id, @Param("tenantId") String tenantId);
}
