package com.titanium.clause.infrastructure.repository;

import com.titanium.clause.domain.entity.Exclusion;
import com.titanium.clause.domain.enums.ExclusionType;
import com.titanium.clause.domain.repository.ExclusionRepository;
import com.titanium.clause.infrastructure.entity.InsuranceExclusionEntity;
import com.titanium.clause.infrastructure.mapper.ExclusionMapper;
import com.titanium.clause.infrastructure.repository.jpa.InsuranceExclusionJpaRepository;
import com.titanium.clause.domain.valueobject.ExclusionId;
import com.titanium.metadata.enums.CommonStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 责任免除仓储实现类
 */
@Repository
@RequiredArgsConstructor
public class ExclusionRepositoryImpl implements ExclusionRepository {
    private final InsuranceExclusionJpaRepository exclusionJpaRepository;
    private final ExclusionMapper exclusionMapper;

    @Override
    public Exclusion save(Exclusion exclusion) {
        InsuranceExclusionEntity entity = exclusionMapper.toInsuranceExclusionEntity(exclusion);
        InsuranceExclusionEntity savedEntity = exclusionJpaRepository.save(entity);
        return exclusionMapper.toExclusion(savedEntity);
    }

    @Override
    public Optional<Exclusion> findById(ExclusionId exclusionId, String tenantId) {
        return exclusionJpaRepository.findById(exclusionId.getValue())
                .filter(entity -> entity.getTenantId().equals(tenantId))
                .map(exclusionMapper::toExclusion);
    }

    @Override
    public List<Exclusion> findByType(ExclusionType type, String tenantId) {
        return exclusionJpaRepository.findByTypeAndTenantId(type, tenantId)
                .stream()
                .map(exclusionMapper::toExclusion)
                .collect(Collectors.toList());
    }

    @Override
    public List<Exclusion> findByStatus(CommonStatus status, String tenantId) {
        return exclusionJpaRepository.findByStatusAndTenantId(status, tenantId)
                .stream()
                .map(exclusionMapper::toExclusion)
                .collect(Collectors.toList());
    }

    @Override
    public List<Exclusion> findAll(String tenantId) {
        return exclusionJpaRepository.findByTenantIdAndIsDeleted(tenantId, 0)
                .stream()
                .map(exclusionMapper::toExclusion)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(ExclusionId exclusionId, String tenantId) {
        exclusionJpaRepository.deleteByIdAndTenantId(exclusionId.getValue(), tenantId);
    }
}