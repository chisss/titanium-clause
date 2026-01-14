package com.titanium.clause.valueobject;

import lombok.Value;

/**
 * 责任代码值对象
 */
@Value(staticConstructor = "of")
public class LiabilityCode {
    private String value;

    public static LiabilityCode fromString(String value) {
        return of(value);
    }
}