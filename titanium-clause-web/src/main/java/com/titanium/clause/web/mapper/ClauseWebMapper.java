package com.titanium.clause.web.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.titanium.clause.api.response.ClauseResponse;
import com.titanium.clause.api.response.PremiumRuleResponse;
import com.titanium.clause.query.result.ClauseQueryResult;
import com.titanium.clause.query.result.PremiumRuleQueryResult;
import com.titanium.clause.web.dto.CreateClauseDTO;
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
     * 创建请求 + 生成的条款ID → 展示 VO（写入口回显用，避免同步读最终一致投影）
     * <p>
     * 条款读模型经 {@code @EventHandler} 异步投影，命令 {@code sendAndWait} 返回后投影未必落库，
     * 若立即查读模型会因未命中而误报「创建失败」。此处直接以已受理的写入语义回显：新建条款状态恒为
     * {@code DRAFT}，其余字段取自请求，{@code clauseId} 为写侧生成值。审计时间戳由投影补齐，回显暂缺无碍前端跳转。
     * </p>
     *
     * @param request  创建条款请求
     * @param clauseId 写侧生成的条款ID
     * @return 条款展示 VO
     */
    @Mapping(target = "clauseId", source = "clauseId")
    @Mapping(target = "status", constant = "DRAFT")
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "parentClauseId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    ClauseVO toCreatedVO(CreateClauseDTO request, String clauseId);

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
