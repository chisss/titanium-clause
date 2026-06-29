package com.titanium.clause.infrastructure.repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.titanium.clause.domain.entity.ClaimRule;
import com.titanium.clause.domain.repository.ClaimRuleRepository;
import com.titanium.clause.infrastructure.entity.ClaimRuleEntity;
import com.titanium.clause.infrastructure.mapper.ClaimRuleMapper;
import com.titanium.clause.infrastructure.repository.jpa.ClaimRuleJpaRepository;

import lombok.RequiredArgsConstructor;

/**
 * 理赔规则仓储实现类
 */
@Repository
@RequiredArgsConstructor
public class ClaimRuleRepositoryImpl implements ClaimRuleRepository {
    private final ClaimRuleJpaRepository claimRuleJpaRepository;
    private final ClaimRuleMapper claimRuleMapper;

    @Override
    public ClaimRule save(ClaimRule claimRule) {
        ClaimRuleEntity entity = claimRuleMapper.toClaimRuleEntity(claimRule);
        ClaimRuleEntity savedEntity = claimRuleJpaRepository.save(entity);
        return claimRuleMapper.toClaimRule(savedEntity);
    }

    @Override
    public Optional<ClaimRule> findById(String id, String tenantId) {
        return claimRuleJpaRepository.findById(id)
                .filter(entity -> entity.getTenantId().equals(tenantId))
                .map(claimRuleMapper::toClaimRule);
    }

    @Override
    public List<ClaimRule> findAll(String tenantId) {
        return claimRuleJpaRepository.findByTenantIdAndIsDeleted(tenantId, 0)
                .stream()
                .map(claimRuleMapper::toClaimRule)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(String id, String tenantId) {
        claimRuleJpaRepository.deleteByIdAndTenantId(id, tenantId);
    }
}
