package com.titanium.clause.valueobject;

import java.time.LocalDateTime;

/**
 * 时间范围值对象
 *
 * <p>record 实现：Jackson 原生支持 record 反序列化（规范 3.4.1 值对象须为 record）。
 * 保留 {@code of} 静态工厂以兼容既有调用点。</p>
 */
public record TimeRange(LocalDateTime startTime, LocalDateTime endTime) {

    public static TimeRange of(LocalDateTime startTime, LocalDateTime endTime) {
        return new TimeRange(startTime, endTime);
    }

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
