package com.example.datamodel.utils;

import java.util.UUID;

/**
 * UUID工具类
 * 处理MyBatis-Plus ASSIGN_UUID生成的字符串格式UUID转换为UUID对象
 *
 * @author DataModel Team
 */
public class UUIDUtils {

    /**
     * 将对象转换为UUID
     * 支持UUID对象、字符串（带或不带连字符）的转换
     * 
     * @param value 待转换的值
     * @return UUID对象，如果value为null则返回null
     */
    public static UUID parseUUID(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof UUID) {
            return (UUID) value;
        }
        String str = value.toString().trim();
        // 处理不带连字符的32位十六进制字符串（如：14e899adc84c4fde39d7cbab49f0518a）
        if (str.length() == 32 && str.matches("[0-9a-fA-F]{32}")) {
            // 转换为标准UUID格式
            str = str.substring(0, 8) + "-" + str.substring(8, 12) + "-" + str.substring(12, 16) + "-" + str.substring(16, 20) + "-" + str.substring(20);
        }
        try {
            return UUID.fromString(str);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("无法将字符串转换为UUID: " + str, e);
        }
    }
}

