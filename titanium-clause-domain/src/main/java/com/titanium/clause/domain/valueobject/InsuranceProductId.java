package com.titanium.clause.domain.valueobject;

import lombok.Value;

import java.util.Objects;
import java.util.UUID;

/**
 * 保险产品ID值对象
 */
@Value
public class InsuranceProductId {
    String value;

    public InsuranceProductId() {
        this.value = UUID.randomUUID().toString();
    }

    public InsuranceProductId(String value) {
        Objects.requireNonNull(value, "产品ID不能为空");
        if (value.isEmpty()) {
            throw new IllegalArgumentException("产品ID不能为空");
        }
        this.value = value;
    }

    public static InsuranceProductId fromString(String value) {
        return new InsuranceProductId(value);
    }
}