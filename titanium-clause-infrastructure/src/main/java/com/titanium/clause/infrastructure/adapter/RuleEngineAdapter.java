package com.titanium.clause.infrastructure.adapter;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.titanium.clause.port.RuleEnginePort;
import com.titanium.ruleengine.api.RuleEngineApi;
import com.titanium.ruleengine.api.dto.RuleExecutionResultDTO;
import com.titanium.ruleengine.api.response.ApiResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 规则引擎端口适配器（driven adapter，位于 infrastructure）
 * <p>
 * 实现 {@link RuleEnginePort}，经 Feign 客户端 {@link RuleEngineApi} 调用规则引擎域执行规则集，
 * 并把规则引擎的 {@link RuleExecutionResultDTO} 防腐翻译为条款域值对象 {@link RuleEnginePort.RuleEvaluationResult}。
 * </p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RuleEngineAdapter implements RuleEnginePort {

    /** 规则引擎决策：通过 */
    private static final String DECISION_PASS = "PASS";

    private final RuleEngineApi ruleEngineApi;

    @Override
    public RuleEvaluationResult execute(String ruleSetCode, Map<String, Object> variables, String tenantId) {
        log.info("调用规则引擎执行规则集: ruleSetCode={}, tenantId={}", ruleSetCode, tenantId);
        ApiResponse<RuleExecutionResultDTO> response = ruleEngineApi.execute(ruleSetCode, variables, tenantId);
        if (response == null || response.getData() == null || response.getData().getDecision() == null) {
            // 规则引擎无结论时保守放行本地判定（不因外部不可用阻断），由调用方结合本地规则决策
            return new RuleEvaluationResult(true, null);
        }
        String decisionCode = response.getData().getDecision().getCode();
        return new RuleEvaluationResult(DECISION_PASS.equals(decisionCode), decisionCode);
    }
}
