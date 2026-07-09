package com.titanium.clause.web.mapper;

import org.mapstruct.Mapper;

import com.titanium.clause.api.dto.ClauseDTO;
import com.titanium.clause.query.result.ClauseQueryResult;
import com.titanium.clause.web.response.ClauseResponse;

/**
 * 条款 Web 层对象映射器（MapStruct）
 * <p>
 * 边界协议转换枢纽：读侧查询结果 {@link ClauseQueryResult} → 展示 {@link ClauseResponse}（Controller 用）、
 * 读侧查询结果 → 对外 {@link ClauseDTO}（Provider 用）。写侧编排的入参构造由 {@code ClauseApplicationService}
 * 承担（校验条款代码唯一性、生成 {@code ClauseId}、加载聚合），故写门面保留标量入参，本映射器只承担
 * 读模型结果 → VO/DTO 的结构翻译。字段结构一致，直接按名映射。
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
    ClauseResponse toVO(ClauseQueryResult result);

    /**
     * 读模型结果 → 对外 DTO（Provider 用）
     *
     * @param result 读模型查询结果
     * @return 条款 DTO
     */
    ClauseDTO toDTO(ClauseQueryResult result);
}
