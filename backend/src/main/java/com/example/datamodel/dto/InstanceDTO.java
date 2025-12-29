package com.example.datamodel.dto;

import lombok.Data;

import java.util.Map;
import java.util.UUID;

/**
 * 实例DTO
 *
 * @author DataModel Team
 */
@Data
public class InstanceDTO {

    /**
     * 实例ID（可选，创建时不需要）
     */
    private UUID id;

    /**
     * 属性值映射
     */
    private Map<String, Object> properties;

    /**
     * 数据源ID（可选，如果不指定则使用对象类型的默认数据源）
     * @deprecated 使用 storageDatasourceId 代替
     */
    @Deprecated
    private UUID datasourceId;

    /**
     * 存储库数据源关联ID（可选，如果不指定则使用对象类型的默认存储库或平台存储）
     * 用于指定实例数据要存储在哪个关联的数据源（存储库）中
     */
    private UUID storageDatasourceId;
}

