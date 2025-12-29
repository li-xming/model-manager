package com.example.datamodel.dto;

import lombok.Data;

import java.util.List;

/**
 * 从表批量创建属性的DTO
 *
 * @author DataModel Team
 */
@Data
public class BatchCreatePropertiesFromTableDTO {
    /**
     * 数据源ID
     */
    private String datasourceId;

    /**
     * 表名
     */
    private String tableName;

    /**
     * Schema名称（可选）
     */
    private String schemaName;

    /**
     * 要同步的字段名列表
     */
    private List<String> columnNames;

    /**
     * 是否跳过已存在的属性名（默认true）
     */
    private Boolean skipExisting;

    /**
     * 默认值设置
     */
    public Boolean getSkipExisting() {
        return skipExisting != null ? skipExisting : true;
    }
}

