package com.titanium.clause.query.converter;

import com.titanium.metadata.enums.insurance.InsuranceProductType;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * 险种类型读模型转换器。
 * <p>
 * 历史数据可能保存二级险种线代码，无法无损映射到三级险种时按空值读取。
 * </p>
 */
@Converter
public class InsuranceProductTypeConverter implements AttributeConverter<InsuranceProductType, String> {

    @Override
    public String convertToDatabaseColumn(InsuranceProductType attribute) {
        return attribute == null ? null : attribute.getCode();
    }

    @Override
    public InsuranceProductType convertToEntityAttribute(String dbData) {
        return InsuranceProductType.fromCode(dbData);
    }
}
