package com.titanium.clause.web.mapper;

import org.mapstruct.Mapper;

import com.titanium.clause.api.response.ClauseResponse;
import com.titanium.clause.api.response.PremiumRuleResponse;
import com.titanium.clause.query.result.ClauseQueryResult;
import com.titanium.clause.query.result.PremiumRuleQueryResult;
import com.titanium.clause.web.response.ClauseVO;

/**
 * 条款 Web 层对象映射器（MapStruct）
 * <p>
 * 边界协议转换枢纽：读侧查询结果 {@link ClauseQueryResult} → 展示 {@link ClauseVO}（Controller 用）、
 * 读侧查询结果 → 对外 {@link ClauseResponse}（Provider 用）。写侧编排的入参构造由 {@code ClauseApplicationService}
 * 承担（校验条款代码唯一性、生成 {@code ClauseId}、加载聚合），故写门面保留标量入参，本映射器只承担
 * 读模型结果 → VO/Response 的结构翻译。字段结构一致，直接按名映射。
 * </p>
 */
@Mapper(componentModel = "spring")
public interface ClauseWebMapper {

    /**
     * 读模型结果 → 展示 VO（Controller 用）
     *
     * @param result 读模型查询结果
     * @return 条款响应 VO
     */
    ClauseVO toVO(ClauseQueryResult result);

    /**
     * 读模型结果 → 对外 Response（Provider 用）
     *
     * @param result 读模型查询结果
     * @return 条款 Response
     */
    ClauseResponse toResponse(ClauseQueryResult result);

    /**
     * 缴费规则读模型结果 → 对外 Response（Provider 用，供 billing 费率查询）
     * <p>
     * 四维年龄性别费率表 {@code List<AgeGenderRate>} → {@code List<AgeGenderRateResponse>} 由 MapStruct 按字段名
     * 自动逐项映射（两侧字段结构一致），职业系数表 {@code Map} 直接透传。
     * </p>
     *
     * @param result 缴费规则读模型查询结果
     * @return 缴费规则 Response
     */
    PremiumRuleResponse toPremiumRuleResponse(PremiumRuleQueryResult result);
}
