package com.titanium.clause.domain.valueobject;

import lombok.Value;

import java.util.Objects;

/**
 * 保险产品代码值对象
 */
@Value
public class ProductCode {
    String value;

    public ProductCode(String value) {
        Objects.requireNonNull(value, "产品代码不能为空");
        if (value.isEmpty()) {
            throw new IllegalArgumentException("产品代码不能为空");
        }
        if (value.length() > 50) {
            throw new IllegalArgumentException("产品代码长度不能超过50个字符");
        }
        this.value = value;
    }

    public static ProductCode fromString(String value) {
        return new ProductCode(value);
    }
}