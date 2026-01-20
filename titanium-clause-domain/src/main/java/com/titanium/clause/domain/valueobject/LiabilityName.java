package com.titanium.clause.domain.valueobject;

import lombok.Value;

/**
 * 责任名称值对象
 */
@Value(staticConstructor = "of")
public class LiabilityName {
    private String value;

    public static LiabilityName fromString(String value) {
        return of(value);
    }
}