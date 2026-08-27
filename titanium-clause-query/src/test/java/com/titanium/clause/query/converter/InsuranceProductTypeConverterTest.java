package com.titanium.clause.query.converter;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.titanium.metadata.enums.insurance.InsuranceProductType;

/** 险种类型读模型转换器测试。 */
class InsuranceProductTypeConverterTest {

    private final InsuranceProductTypeConverter converter = new InsuranceProductTypeConverter();

    @Test
    void shouldReadKnownProductTypeCodes() {
        assertThat(converter.convertToEntityAttribute("TERM_LIFE"))
                .isEqualTo(InsuranceProductType.TERM_LIFE);
        assertThat(converter.convertToEntityAttribute("MEDICAL"))
                .isEqualTo(InsuranceProductType.MEDICAL);
    }

    @Test
    void shouldIgnoreHistoricalInsuranceLineCode() {
        assertThat(converter.convertToEntityAttribute("LIFE")).isNull();
    }

    @Test
    void shouldPersistProductTypeCodeAndPreserveNulls() {
        assertThat(converter.convertToDatabaseColumn(InsuranceProductType.TERM_LIFE))
                .isEqualTo("TERM_LIFE");
        assertThat(converter.convertToDatabaseColumn(null)).isNull();
        assertThat(converter.convertToEntityAttribute(null)).isNull();
    }
}
