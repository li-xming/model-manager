package com.example.datamodel.vo;

import com.example.datamodel.entity.Property;
import lombok.Data;

import java.util.List;

/**
 * 批量创建属性的结果
 *
 * @author DataModel Team
 */
@Data
public class BatchCreatePropertiesResult {
    /**
     * 成功创建的属性数量
     */
    private Integer created;

    /**
     * 跳过的属性数量（名称已存在）
     */
    private Integer skipped;

    /**
     * 失败的字段数量
     */
    private Integer failed;

    /**
     * 创建的属性列表
     */
    private List<Property> properties;

    /**
     * 跳过的字段列表（字段名）
     */
    private List<String> skippedColumns;

    /**
     * 失败的字段列表（字段名和错误信息）
     */
    private List<ColumnError> failedColumns;

    /**
     * 字段错误信息
     */
    @Data
    public static class ColumnError {
        private String columnName;
        private String errorMessage;
    }
}

