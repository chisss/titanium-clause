package com.titanium.clause.infrastructure.repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.titanium.clause.aggregate.Clause;
import com.titanium.clause.infrastructure.entity.ClauseEntity;
import com.titanium.clause.infrastructure.repository.jpa.ClauseJpaRepository;
import com.titanium.clause.repository.ClauseRepository;
import com.titanium.clause.valueobject.ClauseCode;
import com.titanium.clause.valueobject.ClauseId;
import com.titanium.clause.valueobject.ClauseName;
import com.titanium.metadata.enums.InsuranceType;
import com.titanium.metadata.enums.clause.ClauseEnum;

import lombok.RequiredArgsConstructor;

/**
 * 条款仓储实现类
 */
@Repository
@RequiredArgsConstructor
public class ClauseRepositoryImpl implements ClauseRepository {
    private final ClauseJpaRepository clauseJpaRepository;

    @Override
    public Clause save(Clause clause) {
        ClauseEntity entity = toEntity(clause);
        ClauseEntity savedEntity = clauseJpaRepository.save(entity);
        return toDomain(savedEntity);
    }

    @Override
    public Optional<Clause> findById(ClauseId clauseId, String tenantId) {
        return clauseJpaRepository.findById(clauseId.getValue())
                .filter(entity -> entity.getTenantId().equals(tenantId))
                .map(this::toDomain);
    }

    @Override
    public Optional<Clause> findByCode(ClauseCode clauseCode, String tenantId) {
        // 默认查找最新版本的条款
        return clauseJpaRepository.findByClauseCodeAndTenantId(clauseCode.getValue(), tenantId)
                .stream()
                .findFirst()
                .map(this::toDomain);
    }

    @Override
    public List<Clause> findByStatus(ClauseEnum.ClauseStatus status, String tenantId) {
        return clauseJpaRepository.findByStatusAndTenantId(status, tenantId)
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Clause> findByType(InsuranceType insuranceType, String tenantId) {
        return clauseJpaRepository.findByInsuranceTypeAndTenantId(insuranceType, tenantId)
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Clause> findAll(String tenantId) {
        return clauseJpaRepository.findByTenantIdAndIsDeleted(tenantId, 0)
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(ClauseId clauseId, String tenantId) {
        clauseJpaRepository.deleteByIdAndTenantId(clauseId.getValue(), tenantId);
    }

    /**
     * 将领域对象转换为实体对象
     * @param clause 领域对象
     * @return 实体对象
     */
    private ClauseEntity toEntity(Clause clause) {
        ClauseEntity entity = new ClauseEntity();
        entity.setId(clause.getClauseId().getValue());
        entity.setClauseCode(clause.getClauseCode().getValue());
        entity.setClauseName(clause.getClauseName().getValue());
        entity.setClauseType(clause.getClauseType());
        entity.setInsuranceType(clause.getInsuranceType());
        entity.setVersion("1.0"); // 默认版本
        entity.setContent(clause.getContent());
        entity.setStatus(clause.getStatus());
        entity.setEffectiveDate(clause.getEffectiveDate());
        entity.setExpireDate(clause.getExpiryDate());
        entity.setTenantId(clause.getTenantId());
        entity.setCreatedBy(clause.getCreatedBy());
        entity.setCreateTime(clause.getCreateTime());
        entity.setUpdatedBy(clause.getUpdatedBy());
        entity.setUpdateTime(clause.getUpdateTime());
        entity.setIsDeleted(0); // 默认未删除
        return entity;
    }

    /**
     * 将实体对象转换为领域对象
     * @param entity 实体对象
     * @return 领域对象
     */
    private Clause toDomain(ClauseEntity entity) {
        // 基类审计字段（tenantId/createTime/updateTime）无 setter，经 SuperBuilder 构建
        Clause clause = Clause.builder()
                .tenantId(entity.getTenantId())
                .createTime(entity.getCreateTime())
                .updateTime(entity.getUpdateTime())
                .build();
        clause.setClauseId(ClauseId.fromString(entity.getId()));
        clause.setClauseCode(ClauseCode.fromString(entity.getClauseCode()));
        clause.setClauseName(ClauseName.fromString(entity.getClauseName()));
        clause.setClauseType(entity.getClauseType());
        clause.setInsuranceType(entity.getInsuranceType());
        clause.setContent(entity.getContent());
        clause.setStatus(entity.getStatus());
        clause.setDescription(null); // 数据库中没有该字段
        clause.setEffectiveDate(entity.getEffectiveDate());
        clause.setExpiryDate(entity.getExpireDate());
        clause.setCreatedBy(entity.getCreatedBy());
        clause.setUpdatedBy(entity.getUpdatedBy());
        return clause;
    }
}
