package com.titanium.clause.repository;

import com.titanium.clause.entity.PremiumRule;
import com.titanium.clause.entity.PremiumRuleEntity;
import com.titanium.clause.infrastructure.mapper.PremiumRuleMapper;
import com.titanium.clause.repository.jpa.PremiumRuleJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 缴费规则仓储实现类
 */
@Repository
@RequiredArgsConstructor
public class PremiumRuleRepositoryImpl implements PremiumRuleRepository {
    private final PremiumRuleJpaRepository premiumRuleJpaRepository;
    private final PremiumRuleMapper premiumRuleMapper;

    @Override
    public PremiumRule save(PremiumRule premiumRule) {
        PremiumRuleEntity entity = premiumRuleMapper.toPremiumRuleEntity(premiumRule);
        PremiumRuleEntity savedEntity = premiumRuleJpaRepository.save(entity);
        return premiumRuleMapper.toPremiumRule(savedEntity);
    }

    @Override
    public Optional<PremiumRule> findById(String id, String tenantId) {
        return premiumRuleJpaRepository.findById(id)
                .filter(entity -> entity.getTenantId().equals(tenantId))
                .map(premiumRuleMapper::toPremiumRule);
    }

    @Override
    public List<PremiumRule> findByCalculationMethod(String calculationMethod, String tenantId) {
        return premiumRuleJpaRepository.findByCalculationMethodAndTenantId(calculationMethod, tenantId)
                .stream()
                .map(premiumRuleMapper::toPremiumRule)
                .collect(Collectors.toList());
    }

    @Override
    public List<PremiumRule> findAll(String tenantId) {
        return premiumRuleJpaRepository.findByTenantIdAndIsDeleted(tenantId, 0)
                .stream()
                .map(premiumRuleMapper::toPremiumRule)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(String id, String tenantId) {
        premiumRuleJpaRepository.deleteByIdAndTenantId(id, tenantId);
    }
}