package com.example.datamodel.utils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;

/**
 * JSON工具类
 *
 * @author DataModel Team
 */
public class JsonUtils {

    /**
     * 对象转JSON字符串
     */
    public static String toJsonString(Object obj) {
        if (obj == null) {
            return "{}";
        }
        return JSON.toJSONString(obj);
    }

    /**
     * JSON字符串转对象
     */
    public static <T> T parseObject(String json, Class<T> clazz) {
        if (json == null || json.isEmpty()) {
            return null;
        }
        return JSON.parseObject(json, clazz);
    }

    /**
     * JSON字符串转JSONObject
     */
    public static JSONObject parseObject(String json) {
        if (json == null || json.isEmpty()) {
            return new JSONObject();
        }
        return JSON.parseObject(json);
    }
}

