package com.titanium.clause.web.assembler;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.titanium.clause.common.enums.CoverageType;
import com.titanium.clause.entity.Coverage;
import com.titanium.clause.query.result.CoverageQueryResult;
import com.titanium.clause.valueobject.CoverageId;
import com.titanium.clause.valueobject.CoverageTrigger;
import com.titanium.clause.valueobject.Deductible;
import com.titanium.clause.valueobject.PayoutRule;
import com.titanium.clause.web.dto.CoverageDTO;
import com.titanium.clause.web.response.CoverageVO;
import com.titanium.metadata.enums.CommonStatus;
import com.titanium.metadata.enums.clause.CoverageTriggerType;
import com.titanium.metadata.enums.clause.PayoutType;

/**
 * 保险责任装配器（web 层，前端 DTO → 领域实体 {@link Coverage}）
 * <p>
 * 责任的装配含条件构造（按 {@link PayoutType} 选择不同 {@link PayoutRule} 工厂）与嵌套值对象组装，
 * 非字段直映，故用专类装配而非声明式 MapStruct（规避「@Mapper 内 default 手工 new」的伪 MapStruct 反模式）。
 * </p>
 * <p>
 * 医疗险特有且共享值对象未建模的参数（社保外比例、等待期天数、日津贴、免赔/给付天数）统一落
 * {@link CoverageTrigger} 的 {@code params} 扩展 Map——该 Map 是领域为「结构化扩展参数」预留的入口，
 * 借此支撑参考电子保单结构而不改动 metadata/clause 的共享值对象定义，避免影响事件存储与其它域。
 * </p>
 */
@Component
public class CoverageAssembler {

    /** trigger.params 扩展键：社保外报销比例 0-1 */
    public static final String PARAM_OUT_SOCIAL_RATIO = "outSocialRatio";
    /** trigger.params 扩展键：等待期天数 */
    public static final String PARAM_WAITING_DAYS = "waitingPeriodDays";
    /** trigger.params 扩展键：日津贴金额（元/天） */
    public static final String PARAM_DAILY_AMOUNT = "dailyAmount";
    /** trigger.params 扩展键：免赔天数 */
    public static final String PARAM_DEDUCTIBLE_DAYS = "deductibleDays";
    /** trigger.params 扩展键：每次最高赔付天数 */
    public static final String PARAM_MAX_DAYS_PER_CLAIM = "maxDaysPerClaim";
    /** trigger.params 扩展键：累计最高赔付天数 */
    public static final String PARAM_MAX_DAYS_TOTAL = "maxDaysTotal";

    /**
     * 将前端责任 DTO 装配为领域责任实体。
     *
     * @param dto 前端责任配置
     * @return 领域责任实体
     */
    public Coverage toCoverage(CoverageDTO dto) {
        LocalDateTime now = LocalDateTime.now();
        CoverageType type = dto.getCoverageType() != null
                ? CoverageType.fromCode(dto.getCoverageType()) : CoverageType.MEDICAL;
        PayoutType payoutType = dto.getPayoutType() != null
                ? PayoutType.fromCode(dto.getPayoutType()) : PayoutType.REIMBURSEMENT;

        return new Coverage(
                CoverageId.of(UUID.randomUUID().toString().replace("-", "")),
                dto.getCoverageCode(),
                dto.getCoverageName(),
                null,
                dto.getDescription(),
                CommonStatus.ENABLED,
                type,
                dto.getCoverageAmount(),
                null,
                null,
                dto.getIsAdditional() != null ? dto.getIsAdditional() : Boolean.FALSE,
                null,
                buildTrigger(dto),
                buildPayoutRule(dto, payoutType),
                now,
                now);
    }

    /**
     * 组装结构化赔付触发条件，医疗特有扩展参数落 {@code params}。
     */
    private CoverageTrigger buildTrigger(CoverageDTO dto) {
        CoverageTriggerType triggerType = dto.getTriggerType() != null
                ? CoverageTriggerType.fromCode(dto.getTriggerType()) : CoverageTriggerType.MEDICAL_EXPENSE;

        Map<String, Object> params = new LinkedHashMap<>();
        putIfNotNull(params, PARAM_OUT_SOCIAL_RATIO, dto.getOutSocialRatio());
        putIfNotNull(params, PARAM_WAITING_DAYS, dto.getWaitingPeriodDays());
        putIfNotNull(params, PARAM_DAILY_AMOUNT, dto.getDailyAmount());
        putIfNotNull(params, PARAM_DEDUCTIBLE_DAYS, dto.getDeductibleDays());
        putIfNotNull(params, PARAM_MAX_DAYS_PER_CLAIM, dto.getMaxDaysPerClaim());
        putIfNotNull(params, PARAM_MAX_DAYS_TOTAL, dto.getMaxDaysTotal());

        return new CoverageTrigger(triggerType, dto.getDescription(), null, params, null);
    }

    /**
     * 按赔付类型选择赔付规则工厂，装配结构化赔付规则。
     */
    private PayoutRule buildPayoutRule(CoverageDTO dto, PayoutType payoutType) {
        Deductible deductible = dto.getDeductibleAmount() != null
                ? Deductible.fixedAmount(dto.getDeductibleAmount()) : Deductible.none();

        return switch (payoutType) {
            case REIMBURSEMENT -> PayoutRule.reimbursement(dto.getReimbursementRatio(), deductible, dto.getMaxPayout());
            case PROPORTIONAL -> PayoutRule.proportional(dto.getProportion());
            case ACTUAL_LOSS -> PayoutRule.actualLoss(deductible, dto.getMaxPayout());
            case FIXED, CASH_VALUE -> PayoutRule.fixed(dto.getCoverageAmount());
            case PERIODIC -> PayoutRule.periodic(
                    new PayoutRule.PeriodicPayoutTerms("DAILY", null, null, null, null, dto.getDailyAmount()));
        };
    }

    /**
     * 仅当值非空时放入扩展参数 Map，避免 null 污染 params。
     */
    private void putIfNotNull(Map<String, Object> params, String key, Object value) {
        if (value != null) {
            params.put(key, value);
        }
    }

    // ==================== 读侧反向装配：查询结果 → 展示 VO ====================

    /**
     * 将责任查询结果拍平为前端展示 VO：结构化 {@code PayoutRule}/{@code CoverageTrigger}
     * 还原为扁平字段，医疗特有参数从 {@code trigger.params} 扩展 Map 中取回。
     *
     * @param result 读模型责任查询结果
     * @return 责任展示 VO
     */
    public CoverageVO toVO(CoverageQueryResult result) {
        CoverageVO vo = new CoverageVO();
        vo.setCoverageId(result.getCoverageId());
        vo.setClauseId(result.getClauseId());
        vo.setCoverageCode(result.getCoverageCode());
        vo.setCoverageName(result.getCoverageName());
        vo.setCoverageType(result.getCoverageType() != null ? result.getCoverageType().getCode() : null);
        vo.setTriggerType(result.getTriggerType() != null ? result.getTriggerType().getCode() : null);
        vo.setPayoutType(result.getPayoutType() != null ? result.getPayoutType().getCode() : null);
        vo.setCoverageAmount(result.getCoverageAmount());
        vo.setDescription(result.getDescription());
        vo.setIsAdditional(result.getIsAdditional());

        PayoutRule payoutRule = result.getPayoutRule();
        if (payoutRule != null) {
            vo.setReimbursementRatio(payoutRule.reimbursementRatio());
            vo.setProportion(payoutRule.proportion());
            vo.setMaxPayout(payoutRule.maxPayout());
            if (payoutRule.deductible() != null) {
                vo.setDeductibleAmount(payoutRule.deductible().amount());
            }
        }

        CoverageTrigger trigger = result.getTrigger();
        if (trigger != null && trigger.params() != null) {
            Map<String, Object> params = trigger.params();
            vo.setOutSocialRatio(toBigDecimal(params.get(PARAM_OUT_SOCIAL_RATIO)));
            vo.setDailyAmount(toBigDecimal(params.get(PARAM_DAILY_AMOUNT)));
            vo.setWaitingPeriodDays(toInteger(params.get(PARAM_WAITING_DAYS)));
            vo.setDeductibleDays(toInteger(params.get(PARAM_DEDUCTIBLE_DAYS)));
            vo.setMaxDaysPerClaim(toInteger(params.get(PARAM_MAX_DAYS_PER_CLAIM)));
            vo.setMaxDaysTotal(toInteger(params.get(PARAM_MAX_DAYS_TOTAL)));
        }
        return vo;
    }

    /**
     * 扩展参数值 → BigDecimal（JSON 反序列化后可能为 Number/String，空安全）。
     */
    private BigDecimal toBigDecimal(Object value) {
        if (value == null) {
            return null;
        }
        return new BigDecimal(value.toString());
    }

    /**
     * 扩展参数值 → Integer（空安全）。
     */
    private Integer toInteger(Object value) {
        if (value == null) {
            return null;
        }
        return Integer.valueOf(value.toString().split("\\.")[0]);
    }
}
