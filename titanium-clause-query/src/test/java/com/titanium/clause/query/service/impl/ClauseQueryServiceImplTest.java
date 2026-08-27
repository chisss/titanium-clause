package com.titanium.clause.query.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.titanium.clause.query.repository.ClauseViewRepository;
import com.titanium.clause.query.result.ClauseQueryResult;
import com.titanium.clause.query.view.ClauseView;
import com.titanium.metadata.enums.clause.ClauseEnum;
import com.titanium.metadata.enums.insurance.InsuranceProductType;

/** 条款后台组合查询服务测试。 */
@ExtendWith(MockitoExtension.class)
class ClauseQueryServiceImplTest {

    @Mock
    private ClauseViewRepository repository;

    private ClauseQueryServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new ClauseQueryServiceImpl(repository);
    }

    @Test
    void shouldReturnEmptyPageWhenResolvedInsuranceTypesAreEmpty() {
        Page<ClauseQueryResult> result = service.getClauses(null, null, null, List.of(), "tenant-a", 0, 20);

        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isZero();
        verify(repository, never()).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void shouldApplySafePageBoundsAndMapTotalElements() {
        ClauseView view = clauseView("clause-1", "tenant-a", InsuranceProductType.MEDICAL);
        when(repository.findAll(any(Specification.class), any(Pageable.class)))
                .thenAnswer(invocation -> new PageImpl<>(List.of(view), invocation.getArgument(1), 312));

        Page<ClauseQueryResult> result = service.getClauses(" 医疗%_ ", " C-001 ",
                ClauseEnum.ClauseStatus.ACTIVE, List.of(InsuranceProductType.MEDICAL), "tenant-a", -3, 500);

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(repository).findAll(any(Specification.class), pageableCaptor.capture());
        Pageable pageable = pageableCaptor.getValue();
        assertThat(pageable.getPageNumber()).isZero();
        assertThat(pageable.getPageSize()).isEqualTo(200);
        assertThat(pageable.getSort().getOrderFor("createTime")).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(312);
        assertThat(result.getContent()).singleElement().satisfies(item -> {
            assertThat(item.getClauseId()).isEqualTo("clause-1");
            assertThat(item.getInsuranceType()).isEqualTo(InsuranceProductType.MEDICAL);
            assertThat(item.getTenantId()).isEqualTo("tenant-a");
        });
    }

    @Test
    void shouldUseDefaultPageSizeWhenSizeIsNotPositive() {
        when(repository.findAll(any(Specification.class), any(Pageable.class)))
                .thenAnswer(invocation -> Page.empty(invocation.getArgument(1)));

        service.getClauses(null, null, null, null, "tenant-a", 2, 0);

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(repository).findAll(any(Specification.class), pageableCaptor.capture());
        assertThat(pageableCaptor.getValue().getPageNumber()).isEqualTo(2);
        assertThat(pageableCaptor.getValue().getPageSize()).isEqualTo(20);
    }

    private ClauseView clauseView(String clauseId, String tenantId, InsuranceProductType insuranceType) {
        LocalDateTime now = LocalDateTime.of(2026, 8, 12, 10, 0);
        ClauseView view = new ClauseView();
        view.setClauseId(clauseId);
        view.setClauseCode("C-001");
        view.setClauseName("医疗保险条款");
        view.setStatus(ClauseEnum.ClauseStatus.ACTIVE);
        view.setInsuranceType(insuranceType);
        view.setTenantId(tenantId);
        view.setCreateTime(now);
        view.setUpdateTime(now);
        return view;
    }
}
