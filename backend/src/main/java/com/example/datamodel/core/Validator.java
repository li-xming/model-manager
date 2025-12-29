package com.example.datamodel.core;

import com.example.datamodel.entity.Property;
import com.example.datamodel.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 数据验证器
 * 验证实例数据是否符合对象类型定义
 *
 * @author DataModel Team
 */
@Slf4j
@Component
public class Validator {

    /**
     * 验证实例数据
     *
     * @param data 实例数据
     * @param properties 属性定义列表
     */
    public void validateInstance(Map<String, Object> data, List<Property> properties) {
        if (data == null) {
            data = new java.util.HashMap<>();
        }

        for (Property property : properties) {
            String propName = property.getName();
            // 尝试多种可能的键名（大小写不敏感）
            Object value = data.get(propName);
            
            if (value == null) {
                // 尝试小写
                value = data.get(propName.toLowerCase());
            }
            
            if (value == null) {
                // 尝试大小写不敏感匹配（处理驼峰命名等情况）
                for (String key : data.keySet()) {
                    if (key.equalsIgnoreCase(propName)) {
                        value = data.get(key);
                        log.debug("通过大小写不敏感匹配找到属性值: {} -> {}", propName, key);
                        break;
                    }
                }
            }
            
            // 记录调试信息
            if (property.getRequired() != null && property.getRequired()) {
                log.debug("检查必需属性: {} = {}, 数据键: {}", propName, value, data.keySet());
            }

            // 检查必需属性
            if (property.getRequired() != null && property.getRequired()) {
                if (value == null || (value instanceof String && value.toString().trim().isEmpty())) {
                    log.error("必需属性验证失败: {} = {}, 数据键: {}, 所有数据: {}", 
                            propName, value, data.keySet(), data);
                    throw new BusinessException("属性 " + propName + " 是必需的");
                }
            }

            // 如果值不为空，验证数据类型
            if (value != null) {
                validatePropertyType(value, property);
            }
        }
    }

    /**
     * 验证属性类型
     */
    private void validatePropertyType(Object value, Property property) {
        String dataType = property.getDataType();
        if (dataType == null) {
            return;
        }

        try {
            switch (dataType.toUpperCase()) {
                case "STRING":
                    if (!(value instanceof String)) {
                        throw new BusinessException("属性 " + property.getName() + " 必须是字符串类型");
                    }
                    break;
                case "INTEGER":
                    if (!(value instanceof Integer || value instanceof Long)) {
                        // 尝试转换
                        try {
                            Integer.parseInt(value.toString());
                        } catch (NumberFormatException e) {
                            throw new BusinessException("属性 " + property.getName() + " 必须是整数类型");
                        }
                    }
                    break;
                case "FLOAT":
                    if (!(value instanceof Float || value instanceof Double)) {
                        try {
                            Double.parseDouble(value.toString());
                        } catch (NumberFormatException e) {
                            throw new BusinessException("属性 " + property.getName() + " 必须是浮点数类型");
                        }
                    }
                    break;
                case "BOOLEAN":
                    if (!(value instanceof Boolean)) {
                        if (!("true".equalsIgnoreCase(value.toString()) || "false".equalsIgnoreCase(value.toString()))) {
                            throw new BusinessException("属性 " + property.getName() + " 必须是布尔类型");
                        }
                    }
                    break;
                case "JSON":
                    if (!(value instanceof Map || value instanceof List)) {
                        throw new BusinessException("属性 " + property.getName() + " 必须是JSON对象或数组");
                    }
                    break;
                default:
                    // 其他类型暂不验证
                    break;
            }
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.warn("验证属性类型时出错：{} - {}", property.getName(), e.getMessage());
        }
    }
}

