package com.titanium.clause.infrastructure.adapter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.titanium.clause.port.RuleEnginePort;
import com.titanium.metadata.response.ApiResponse;
import com.titanium.ruleengine.api.RuleEngineApi;
import com.titanium.ruleengine.api.response.RuleExecutionResultResponse;
import com.titanium.ruleengine.common.enums.RuleDecision;

/**
 * 规则引擎适配器测试
 * <p>
 * 校验防腐翻译：规则引擎 PASS 决策 → passed=true；REJECT → passed=false；无结论时保守放行。
 * </p>
 */
class RuleEngineAdapterTest {

    @Test
    @DisplayName("规则引擎 PASS 决策翻译为通过")
    void shouldTranslatePassDecision() {
        RuleEngineApi api = mock(RuleEngineApi.class);
        RuleExecutionResultResponse dto = RuleExecutionResultResponse.builder().decision(RuleDecision.PASS).build();
        when(api.execute(eq("UW_001"), any(), eq("T-1"))).thenReturn(ApiResponse.success(dto));
        RuleEngineAdapter adapter = new RuleEngineAdapter(api);

        RuleEnginePort.RuleEvaluationResult result = adapter.execute("UW_001", Map.of("age", 30), "T-1");

        assertTrue(result.passed());
        assertEquals("PASS", result.decisionCode());
    }

    @Test
    @DisplayName("规则引擎 REJECT 决策翻译为不通过")
    void shouldTranslateRejectDecision() {
        RuleEngineApi api = mock(RuleEngineApi.class);
        RuleExecutionResultResponse dto = RuleExecutionResultResponse.builder().decision(RuleDecision.REJECT).build();
        when(api.execute(any(), any(), any())).thenReturn(ApiResponse.success(dto));
        RuleEngineAdapter adapter = new RuleEngineAdapter(api);

        RuleEnginePort.RuleEvaluationResult result = adapter.execute("UW_001", Map.of(), "T-1");

        assertFalse(result.passed());
        assertEquals("REJECT", result.decisionCode());
    }

    @Test
    @DisplayName("规则引擎无结论时保守放行")
    void shouldPassWhenNoResult() {
        RuleEngineApi api = mock(RuleEngineApi.class);
        when(api.execute(any(), any(), any())).thenReturn(ApiResponse.success(null));
        RuleEngineAdapter adapter = new RuleEngineAdapter(api);

        RuleEnginePort.RuleEvaluationResult result = adapter.execute("UW_001", Map.of(), "T-1");

        assertTrue(result.passed());
    }
}
