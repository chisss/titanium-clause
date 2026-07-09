package com.titanium.clause.port;

import java.util.Map;

/**
 * 规则引擎出口端口（driven port，与聚合平级）
 * <p>
 * 条款域在赔付触发/免责判定等场景配置了 {@code ruleSetCode} 时，复杂规则委托规则引擎域执行。
 * 本端口表达条款域所需的「按规则集编码执行规则」能力，由 infrastructure 的 Adapter 调 rule-engine 实现。
 * </p>
 */
public interface RuleEnginePort {

    /**
     * 执行规则集，返回是否通过（命中放行/拒绝的决策）。
     *
     * @param ruleSetCode 规则集编码
     * @param variables   规则求值上下文变量
     * @param tenantId    租户ID
     * @return 规则执行决策结果
     */
    RuleEvaluationResult execute(String ruleSetCode, Map<String, Object> variables, String tenantId);

    /**
     * 规则执行结果（domain 值对象，屏蔽规则引擎 DTO 细节）。
     *
     * @param passed       是否通过（决策为 PASS）
     * @param decisionCode 决策 code（PASS/REJECT/REFER）
     */
    record RuleEvaluationResult(boolean passed, String decisionCode) {
    }
}
