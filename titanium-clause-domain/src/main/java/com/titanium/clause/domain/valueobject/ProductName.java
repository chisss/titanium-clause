package com.titanium.clause.domain.valueobject;

import lombok.Value;

import java.util.Objects;

/**
 * 保险产品名称值对象
 */
@Value
public class ProductName {
    String value;

    public ProductName(String value) {
        Objects.requireNonNull(value, "产品名称不能为空");
        if (value.isEmpty()) {
            throw new IllegalArgumentException("产品名称不能为空");
        }
        if (value.length() > 100) {
            throw new IllegalArgumentException("产品名称长度不能超过100个字符");
        }
        this.value = value;
    }

    public static ProductName fromString(String value) {
        return new ProductName(value);
    }
}