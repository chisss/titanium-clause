package com.titanium.clause.web.assembler;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import com.titanium.clause.api.response.CoverageResponse;
import com.titanium.clause.query.result.CoverageQueryResult;
import com.titanium.clause.valueobject.CoverageTrigger;
import com.titanium.clause.valueobject.Deductible;
import com.titanium.clause.valueobject.PayoutRule;
import com.titanium.metadata.enums.BaseEnum;

/**
 * 保险责任对外响应装配器
 * <p>
 * 把读侧 {@link CoverageQueryResult} 装配为 Feign 契约 {@link CoverageResponse}，核心工作是
 * <b>把结构化值对象中的跨域必需要素拍平为标量字段</b>：免赔（{@link Deductible} 的类型/额度/比例）、
 * 赔付比例与上限（{@link PayoutRule}）、责任级等待期（{@link CoverageTrigger#params()} 中的
 * {@code waitingPeriodDays}）。
 * </p>
 * <p>
 * 🔴 <b>为何不用 MapStruct</b>：本装配含「从 Map 按键取值并做类型收窄」的取数逻辑（等待期藏在
 * {@code trigger.params} 里），非同名字段的结构翻译，声明式映射无法表达。此类是规约允许的
 * {@code XxxAssembler}（复杂对象组装专类），非「伪 MapStruct」。
 * </p>
 */
@Component
public class CoverageResponseAssembler {

    /** 触发条件参数中的等待期键名（与条款种子数据约定一致） */
    private static final String PARAM_WAITING_PERIOD_DAYS = "waitingPeriodDays";

    /**
     * 读模型查询结果 → 对外响应。
     *
     * @param result 责任查询结果
     * @return 责任响应；入参为空时返回 null
     */
    public CoverageResponse toResponse(CoverageQueryResult result) {
        if (result == null) {
            return null;
        }
        CoverageResponse response = new CoverageResponse();
        response.setCoverageId(result.getCoverageId());
        response.setClauseId(result.getClauseId());
        response.setCoverageCode(result.getCoverageCode());
        response.setCoverageName(result.getCoverageName());
        response.setCoverageType(code(result.getCoverageType()));
        response.setCoverageAmount(result.getCoverageAmount());
        response.setDescription(result.getDescription());
        response.setTriggerType(code(result.getTriggerType()));
        response.setPayoutType(code(result.getPayoutType()));
        response.setAdditional(result.getIsAdditional());
        response.setMainCoverageId(result.getMainCoverageId());
        applyPayoutRule(response, result.getPayoutRule());
        response.setWaitingPeriodDays(extractWaitingPeriodDays(result.getTrigger()));
        return response;
    }

    /**
     * 赔付规则 → 免赔与赔付比例标量字段。
     */
    private void applyPayoutRule(CoverageResponse response, PayoutRule payoutRule) {
        if (payoutRule == null) {
            return;
        }
        response.setReimbursementRatio(payoutRule.reimbursementRatio());
        response.setMaxPayout(payoutRule.maxPayout());
        Deductible deductible = payoutRule.deductible();
        if (deductible != null) {
            response.setDeductibleType(code(deductible.type()));
            response.setDeductibleAmount(deductible.amount());
            response.setDeductibleRatio(deductible.ratio());
        }
    }

    /**
     * 从触发条件的结构化参数中提取责任级等待期天数。
     * <p>
     * 等待期是「责任维度」的属性（一般医疗 30 天、恶性肿瘤 90 天可各不相同），存于
     * {@code trigger.params}。缺省或非数值时返回 0（无等待期）。
     * </p>
     */
    private Integer extractWaitingPeriodDays(CoverageTrigger trigger) {
        if (trigger == null || trigger.params() == null) {
            return 0;
        }
        Object value = trigger.params().get(PARAM_WAITING_PERIOD_DAYS);
        if (value instanceof Number number) {
            return number.intValue();
        }
        if (value instanceof String text && !text.isBlank()) {
            try {
                return Integer.valueOf(text.trim());
            } catch (NumberFormatException ignored) {
                return 0;
            }
        }
        return 0;
    }

    /**
     * 枚举取 code（空安全）。
     */
    private String code(BaseEnum value) {
        return value != null ? value.getCode() : null;
    }

    /**
     * BigDecimal 空安全取值（保留给后续扩展的数值字段使用）。
     */
    protected BigDecimal orZero(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }
}
