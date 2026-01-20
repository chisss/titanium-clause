package com.titanium.clause.domain.valueobject;

import lombok.Value;

import java.time.LocalDateTime;

/**
 * 时间范围值对象
 */
@Value(staticConstructor = "of")
public class TimeRange {
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    /**
     * 检查当前时间是否在时间范围内
     * @return 当前时间是否在时间范围内
     */
    public boolean isInCurrentTime() {
        LocalDateTime now = LocalDateTime.now();
        return (startTime == null || now.isAfter(startTime) || now.isEqual(startTime)) &&
               (endTime == null || now.isBefore(endTime) || now.isEqual(endTime));
    }

    /**
     * 检查指定时间是否在时间范围内
     * @param time 指定时间
     * @return 指定时间是否在时间范围内
     */
    public boolean isInRange(LocalDateTime time) {
        if (time == null) {
            return false;
        }
        return (startTime == null || time.isAfter(startTime) || time.isEqual(startTime)) &&
               (endTime == null || time.isBefore(endTime) || time.isEqual(endTime));
    }
}