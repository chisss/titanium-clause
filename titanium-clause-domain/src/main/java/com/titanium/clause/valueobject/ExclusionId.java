package com.titanium.clause.valueobject;

import lombok.Value;

/**
 * 责任免除ID值对象
 */
@Value(staticConstructor = "of")
public class ExclusionId {
    private String value;

    public static ExclusionId fromString(String value) {
        return of(value);
    }
}