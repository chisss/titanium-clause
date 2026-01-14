package com.titanium.clause.repository;

import com.titanium.clause.aggregate.InsuranceLiability;
import com.titanium.clause.entity.InsuranceLiabilityEntity;
import com.titanium.clause.repository.jpa.InsuranceLiabilityJpaRepository;
import com.titanium.clause.valueobject.InsuranceLiabilityId;
import com.titanium.clause.valueobject.LiabilityCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 保险责任仓库实现类
 */
@Repository
@RequiredArgsConstructor
public class InsuranceLiabilityRepositoryImpl implements InsuranceLiabilityRepository {
    private final InsuranceLiabilityJpaRepository liabilityJpaRepository;

    @Override
    public InsuranceLiability save(InsuranceLiability liability) {
        InsuranceLiabilityEntity entity = toEntity(liability);
        InsuranceLiabilityEntity savedEntity = liabilityJpaRepository.save(entity);
        return toDomain(savedEntity);
    }

    @Override
    public Optional<InsuranceLiability> findById(InsuranceLiabilityId liabilityId, String tenantId) {
        return liabilityJpaRepository.findByIdAndTenantId(liabilityId.getValue(), tenantId)
                .map(this::toDomain);
    }

    @Override
    public Optional<InsuranceLiability> findByCode(LiabilityCode code, String tenantId) {
        return liabilityJpaRepository.findByCodeAndTenantId(code.getValue(), tenantId)
                .map(this::toDomain);
    }

    @Override
    public List<InsuranceLiability> findByStatus(String status, String tenantId) {
        return liabilityJpaRepository.findByStatusAndTenantId(status, tenantId)
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<InsuranceLiability> findAll(String tenantId) {
        return liabilityJpaRepository.findByTenantId(tenantId)
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(InsuranceLiabilityId liabilityId, String tenantId) {
        liabilityJpaRepository.deleteByIdAndTenantId(liabilityId.getValue(), tenantId);
    }

    /**
     * 将领域对象转换为数据库实体
     * @param liability 领域对象
     * @return 数据库实体
     */
    private InsuranceLiabilityEntity toEntity(InsuranceLiability liability) {
        InsuranceLiabilityEntity entity = new InsuranceLiabilityEntity();
        entity.setId(liability.getLiabilityId().getValue());
        entity.setCode(liability.getCode().getValue());
        entity.setName(liability.getName().getValue());
        entity.setCoverage(liability.getCoverage());
        entity.setPremiumRate(liability.getPremiumRate());
        entity.setDescription(liability.getDescription());
        entity.setStatus(liability.getStatus());
        entity.setTenantId(liability.getTenantId());
        entity.setCreateTime(liability.getCreatedAt());
        entity.setUpdateTime(liability.getUpdatedAt());
        entity.setCreatedBy(liability.getCreatedBy());
        entity.setUpdatedBy(liability.getUpdatedBy());
        entity.setIsDeleted(0); // 默认未删除
        return entity;
    }

    /**
     * 将数据库实体转换为领域对象
     * @param entity 数据库实体
     * @return 领域对象
     */
    private InsuranceLiability toDomain(InsuranceLiabilityEntity entity) {
        InsuranceLiability liability = new InsuranceLiability();
        liability.setLiabilityId(InsuranceLiabilityId.fromString(entity.getId()));
        liability.setCode(LiabilityCode.fromString(entity.getCode()));
        liability.setName(com.titanium.clause.valueobject.LiabilityName.fromString(entity.getName()));
        liability.setCoverage(entity.getCoverage());
        liability.setPremiumRate(entity.getPremiumRate());
        liability.setDescription(entity.getDescription());
        liability.setStatus(entity.getStatus());
        liability.setTenantId(entity.getTenantId());
        liability.setCreatedBy(entity.getCreatedBy());
        liability.setCreatedAt(entity.getCreateTime());
        liability.setUpdatedBy(entity.getUpdatedBy());
        liability.setUpdatedAt(entity.getUpdateTime());
        return liability;
    }
}