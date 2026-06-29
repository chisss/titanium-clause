package com.titanium.clause.domain.valueobject;

import lombok.Value;

/**
 * 保险责任ID值对象
 */
@Value(staticConstructor = "of")
public class CoverageId {
    private String value;

    public static CoverageId fromString(String value) {
        return of(value);
    }
}
