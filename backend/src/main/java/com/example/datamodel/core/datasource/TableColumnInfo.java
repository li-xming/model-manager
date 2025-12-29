package com.example.datamodel.core.datasource;

import lombok.Data;

/**
 * 表字段信息
 *
 * @author DataModel Team
 */
@Data
public class TableColumnInfo {
    /**
     * 字段名
     */
    private String columnName;

    /**
     * 数据类型
     */
    private String dataType;

    /**
     * 长度
     */
    private Integer length;

    /**
     * 是否可空
     */
    private Boolean nullable;

    /**
     * 默认值
     */
    private String defaultValue;

    /**
     * 是否主键
     */
    private Boolean primaryKey;

    /**
     * 注释
     */
    private String comment;

    /**
     * 精度（用于DECIMAL等类型）
     */
    private Integer scale;
}

