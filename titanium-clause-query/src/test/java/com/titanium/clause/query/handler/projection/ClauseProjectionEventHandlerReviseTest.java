package com.titanium.clause.query.handler.projection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.titanium.clause.common.enums.CoverageType;
import com.titanium.clause.entity.Coverage;
import com.titanium.clause.entity.PremiumRule;
import com.titanium.clause.event.ClauseRevisedEvent;
import com.titanium.clause.event.CoverageAddedEvent;
import com.titanium.clause.event.CoverageRemovedEvent;
import com.titanium.clause.query.mapper.ClauseRuleViewMapper;
import com.titanium.clause.query.mapper.ClauseViewMapper;
import com.titanium.clause.query.repository.ClauseViewRepository;
import com.titanium.clause.query.repository.CoverageViewRepository;
import com.titanium.clause.query.repository.PremiumRuleViewRepository;
import com.titanium.clause.query.view.ClauseView;
import com.titanium.clause.query.view.CoverageView;
import com.titanium.clause.query.view.PremiumRuleView;
import com.titanium.clause.valueobject.ClauseCode;
import com.titanium.clause.valueobject.ClauseId;
import com.titanium.clause.valueobject.ClauseName;
import com.titanium.clause.valueobject.CoverageId;
import com.titanium.clause.valueobject.Version;
import com.titanium.common.number.BusinessNumberGenerator;
import com.titanium.metadata.enums.CommonStatus;
import com.titanium.metadata.enums.clause.ClauseEnum;
import com.titanium.metadata.enums.insurance.InsuranceProductType;

/**
 * 条款修订读模型投影测试（缺口 G9：修订→新版本责任随版本变更）
 * <p>
 * 校验读侧三处修订语义：
 * <ol>
 *   <li>修订事件为新版本条款建立 DRAFT 读模型记录，并<b>复制继承的责任/费率读模型</b>到新版本行；</li>
 *   <li>责任添加/更新按 {@code (coverageId, clauseId)} 复合主键定位，修订版本修改继承责任不会改写原条款版本行；</li>
 *   <li>责任移除按复合主键仅删所属版本的行，不再误删原条款版本的责任读模型。</li>
 * </ol>
 * </p>
 */
class ClauseProjectionEventHandlerReviseTest {

    private ClauseViewRepository      clauseViewRepository;
    private ClauseViewMapper          clauseViewMapper;
    private CoverageViewRepository    coverageViewRepository;
    private PremiumRuleViewRepository premiumRuleViewRepository;
    private ClauseRuleViewMapper      clauseRuleViewMapper;
    private ClauseProjectionEventHandler handler;

    private static final ClauseId   ORIGINAL_CLAUSE_ID = ClauseId.fromString("clause-001");
    private static final ClauseId   NEW_CLAUSE_ID      = ClauseId.fromString("clause-002");
    private static final CoverageId COVERAGE_ID        = CoverageId.fromString("cov-001");
    private static final String     TENANT_ID          = "tenant-001";

    private static final LocalDateTime NOW = LocalDateTime.of(2026, 9, 4, 10, 0);

    private static final Coverage COVERAGE = new Coverage(
            COVERAGE_ID, "C001", "身故责任", null, "身故保障责任",
            CommonStatus.ENABLED, CoverageType.DEATH, null, null, null,
            Boolean.FALSE, null, null, null, NOW, NOW);

    private static final PremiumRule PREMIUM_RULE = new PremiumRule(
            "RATE", null, null, "YEARLY", 20, 60, null, List.of(), null, Map.of(), null, null, null, NOW, NOW);

    @BeforeEach
    void setUp() {
        clauseViewRepository = mock(ClauseViewRepository.class);
        clauseViewMapper = mock(ClauseViewMapper.class);
        coverageViewRepository = mock(CoverageViewRepository.class);
        premiumRuleViewRepository = mock(PremiumRuleViewRepository.class);
        clauseRuleViewMapper = mock(ClauseRuleViewMapper.class);
        handler = new ClauseProjectionEventHandler(clauseViewRepository, clauseViewMapper, coverageViewRepository,
                premiumRuleViewRepository, clauseRuleViewMapper, mock(BusinessNumberGenerator.class));
    }

    private ClauseRevisedEvent revisedEvent() {
        return new ClauseRevisedEvent(ORIGINAL_CLAUSE_ID, NEW_CLAUSE_ID, ClauseCode.fromString("CL-001"),
                ClauseName.fromString("测试条款"), ClauseEnum.ClauseType.MAIN, "条款内容", "描述",
                InsuranceProductType.HOUSEHOLD_PROPERTY, Version.of("V1.1"), NOW, NOW.plusYears(1),
                Map.of(COVERAGE_ID, COVERAGE), Map.of(), PREMIUM_RULE, null, null, List.of(), null, TENANT_ID,
                "reviser", NOW);
    }

    @Test
    void revisedEventCreatesDraftViewAndCopiesInheritedRuleViews() {
        when(clauseViewRepository.findByClauseIdAndTenantId(NEW_CLAUSE_ID.value(), TENANT_ID))
                .thenReturn(Optional.empty());
        when(coverageViewRepository.findByCoverageIdAndClauseIdAndTenantId(COVERAGE_ID.value(),
                NEW_CLAUSE_ID.value(), TENANT_ID)).thenReturn(Optional.empty());
        when(premiumRuleViewRepository.findByClauseIdAndTenantId(NEW_CLAUSE_ID.value(), TENANT_ID))
                .thenReturn(Optional.empty());

        handler.on(revisedEvent());

        // 新版本条款读模型：DRAFT + 租户承接 + 溯源映射
        ArgumentCaptor<ClauseView> clauseViewCaptor = ArgumentCaptor.forClass(ClauseView.class);
        verify(clauseViewRepository).save(clauseViewCaptor.capture());
        ClauseView savedClauseView = clauseViewCaptor.getValue();
        assertThat(savedClauseView.getStatus()).isEqualTo(ClauseEnum.ClauseStatus.DRAFT);
        assertThat(savedClauseView.getTenantId()).isEqualTo(TENANT_ID);
        verify(clauseViewMapper).applyRevised(any(ClauseView.class), any(ClauseRevisedEvent.class));

        // 继承责任复制到新版本行：复合主键 (coverageId, newClauseId) + 租户
        ArgumentCaptor<CoverageView> coverageViewCaptor = ArgumentCaptor.forClass(CoverageView.class);
        verify(coverageViewRepository).save(coverageViewCaptor.capture());
        CoverageView savedCoverageView = coverageViewCaptor.getValue();
        assertThat(savedCoverageView.getClauseId()).isEqualTo(NEW_CLAUSE_ID.value());
        assertThat(savedCoverageView.getTenantId()).isEqualTo(TENANT_ID);
        verify(clauseRuleViewMapper).applyCoverage(any(CoverageView.class), eq(COVERAGE));

        // 继承费率复制到新版本行：主键 clauseId = newClauseId
        ArgumentCaptor<PremiumRuleView> premiumViewCaptor = ArgumentCaptor.forClass(PremiumRuleView.class);
        verify(premiumRuleViewRepository).save(premiumViewCaptor.capture());
        PremiumRuleView savedPremiumView = premiumViewCaptor.getValue();
        assertThat(savedPremiumView.getClauseId()).isEqualTo(NEW_CLAUSE_ID.value());
        assertThat(savedPremiumView.getTenantId()).isEqualTo(TENANT_ID);
    }

    @Test
    void coverageAddedEventUpsertsByCompositeKeyScopedToEventClauseId() {
        // 修订版本（clause-002）添加/更新继承责任：租户从新版本父条款读模型继承
        ClauseView parentView = new ClauseView();
        parentView.setTenantId(TENANT_ID);
        when(clauseViewRepository.findById(NEW_CLAUSE_ID.value())).thenReturn(Optional.of(parentView));
        when(coverageViewRepository.findByCoverageIdAndClauseIdAndTenantId(COVERAGE_ID.value(),
                NEW_CLAUSE_ID.value(), TENANT_ID)).thenReturn(Optional.empty());

        handler.on(new CoverageAddedEvent(NEW_CLAUSE_ID, COVERAGE, "reviser", NOW));

        // 按 (coverageId, clauseId, tenantId) 定位 —— 非仅 coverageId，保证不串到原条款版本行
        verify(coverageViewRepository).findByCoverageIdAndClauseIdAndTenantId(COVERAGE_ID.value(),
                NEW_CLAUSE_ID.value(), TENANT_ID);
        ArgumentCaptor<CoverageView> captor = ArgumentCaptor.forClass(CoverageView.class);
        verify(coverageViewRepository).save(captor.capture());
        assertThat(captor.getValue().getClauseId()).isEqualTo(NEW_CLAUSE_ID.value());
    }

    @Test
    void coverageRemovedEventDeletesOnlyOwnVersionRow() {
        ClauseView parentView = new ClauseView();
        parentView.setTenantId(TENANT_ID);
        when(clauseViewRepository.findById(NEW_CLAUSE_ID.value())).thenReturn(Optional.of(parentView));
        when(coverageViewRepository.deleteByCoverageIdAndClauseIdAndTenantId(COVERAGE_ID.value(),
                NEW_CLAUSE_ID.value(), TENANT_ID)).thenReturn(1L);

        handler.on(new CoverageRemovedEvent(NEW_CLAUSE_ID, COVERAGE_ID, "reviser", NOW));

        // 按复合主键仅删修订版本行；原条款版本（clause-001）行不受影响
        verify(coverageViewRepository).deleteByCoverageIdAndClauseIdAndTenantId(COVERAGE_ID.value(),
                NEW_CLAUSE_ID.value(), TENANT_ID);
        verify(coverageViewRepository, never()).delete(any(CoverageView.class));
        verify(coverageViewRepository, times(0)).deleteByCoverageIdAndClauseIdAndTenantId(COVERAGE_ID.value(),
                ORIGINAL_CLAUSE_ID.value(), TENANT_ID);
    }
}
