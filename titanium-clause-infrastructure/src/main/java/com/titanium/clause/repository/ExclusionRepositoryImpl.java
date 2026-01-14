package com.titanium.clause.repository;

import com.titanium.clause.entity.Exclusion;
import com.titanium.clause.entity.InsuranceExclusionEntity;
import com.titanium.clause.infrastructure.mapper.ExclusionMapper;
import com.titanium.clause.repository.jpa.InsuranceExclusionJpaRepository;
import com.titanium.clause.valueobject.ExclusionId;
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
    public List<Exclusion> findByType(String type, String tenantId) {
        return exclusionJpaRepository.findByTypeAndTenantId(type, tenantId)
                .stream()
                .map(exclusionMapper::toExclusion)
                .collect(Collectors.toList());
    }

    @Override
    public List<Exclusion> findByStatus(String status, String tenantId) {
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