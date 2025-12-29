package com.example.datamodel.dto;

import com.example.datamodel.entity.DataSource;
import lombok.Data;

import java.util.UUID;

/**
 * 对象类型-数据源表关联信息DTO
 *
 * @author DataModel Team
 */
@Data
public class ObjectTypeDataSourceTable {
    /**
     * 关联ID
     */
    private UUID id;

    /**
     * 对象类型ID
     */
    private UUID objectTypeId;

    /**
     * 数据源完整信息
     */
    private DataSource datasource;

    /**
     * 表名
     */
    private String tableName;

    /**
     * Schema名称
     */
    private String schemaName;

    /**
     * 是否作为对象实例的存储库
     */
    private Boolean isStorage;

    /**
     * 是否为默认数据源
     */
    private Boolean isDefault;

    /**
     * 优先级
     */
    private Integer priority;

    /**
     * 描述
     */
    private String description;
}

