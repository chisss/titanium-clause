package com.titanium.clause.infrastructure.repository;

import com.titanium.clause.domain.entity.ContractChangeRule;
import com.titanium.clause.domain.repository.ContractChangeRuleRepository;
import com.titanium.clause.infrastructure.entity.ContractChangeRuleEntity;
import com.titanium.clause.infrastructure.mapper.ContractChangeRuleMapper;
import com.titanium.clause.infrastructure.repository.jpa.ContractChangeRuleJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 合同变更规则仓储实现类
 */
@Repository
@RequiredArgsConstructor
public class ContractChangeRuleRepositoryImpl implements ContractChangeRuleRepository {
    private final ContractChangeRuleJpaRepository contractChangeRuleJpaRepository;
    private final ContractChangeRuleMapper contractChangeRuleMapper;

    @Override
    public ContractChangeRule save(ContractChangeRule contractChangeRule) {
        ContractChangeRuleEntity entity = contractChangeRuleMapper.toContractChangeRuleEntity(contractChangeRule);
        ContractChangeRuleEntity savedEntity = contractChangeRuleJpaRepository.save(entity);
        return contractChangeRuleMapper.toContractChangeRule(savedEntity);
    }

    @Override
    public Optional<ContractChangeRule> findById(String id, String tenantId) {
        return contractChangeRuleJpaRepository.findById(id)
                .filter(entity -> entity.getTenantId().equals(tenantId))
                .map(contractChangeRuleMapper::toContractChangeRule);
    }

    @Override
    public List<ContractChangeRule> findByRenewalType(String renewalType, String tenantId) {
        return contractChangeRuleJpaRepository.findByRenewalTypeAndTenantId(renewalType, tenantId)
                .stream()
                .map(contractChangeRuleMapper::toContractChangeRule)
                .collect(Collectors.toList());
    }

    @Override
    public List<ContractChangeRule> findAll(String tenantId) {
        return contractChangeRuleJpaRepository.findByTenantIdAndIsDeleted(tenantId, 0)
                .stream()
                .map(contractChangeRuleMapper::toContractChangeRule)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(String id, String tenantId) {
        contractChangeRuleJpaRepository.deleteByIdAndTenantId(id, tenantId);
    }
}