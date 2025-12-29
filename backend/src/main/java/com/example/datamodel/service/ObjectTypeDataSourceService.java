package com.example.datamodel.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.datamodel.dto.ObjectTypeDataSourceTable;
import com.example.datamodel.entity.DataSource;
import com.example.datamodel.entity.ObjectTypeDataSource;

import java.util.List;
import java.util.UUID;

/**
 * 对象类型-数据源关联服务接口
 *
 * @author DataModel Team
 */
public interface ObjectTypeDataSourceService extends IService<ObjectTypeDataSource> {

    /**
     * 为对象类型添加数据源表关联
     *
     * @param objectTypeId 对象类型ID
     * @param datasourceId 数据源ID
     * @param tableName 表名（is_storage=false时必填，is_storage=true时可为空，表名由系统生成）
     * @param schemaName Schema名称（可选）
     * @param isStorage 是否作为对象实例的存储库（true时表名由系统生成，false时需要指定tableName）
     * @param isDefault 是否为默认数据源
     * @param priority 优先级
     * @return 关联实体
     */
    ObjectTypeDataSource addDataSourceTableToObjectType(UUID objectTypeId, UUID datasourceId,
                                                         String tableName, String schemaName,
                                                         Boolean isStorage, Boolean isDefault, Integer priority);

    /**
     * 移除对象类型的数据源表关联（通过关联ID）
     *
     * @param id 关联ID
     */
    void removeDataSourceTableById(UUID id);

    /**
     * 设置默认数据源表
     *
     * @param objectTypeId 对象类型ID
     * @param id 关联ID
     */
    void setDefaultDataSourceTable(UUID objectTypeId, UUID id);

    /**
     * 获取对象类型关联的所有数据源表
     *
     * @param objectTypeId 对象类型ID
     * @return 数据源表列表（按优先级排序）
     */
    List<ObjectTypeDataSourceTable> getDataSourceTablesByObjectTypeId(UUID objectTypeId);

    /**
     * 获取对象类型的默认数据源表
     *
     * @param objectTypeId 对象类型ID
     * @return 默认数据源表
     */
    ObjectTypeDataSourceTable getDefaultDataSourceTable(UUID objectTypeId);

    /**
     * 获取对象类型的存储库列表（is_storage = true）
     *
     * @param objectTypeId 对象类型ID
     * @return 存储库列表
     */
    List<ObjectTypeDataSourceTable> getStorageDatasourcesByObjectTypeId(UUID objectTypeId);

    /**
     * 获取对象类型的默认存储库（is_storage = true 且 is_default = true）
     *
     * @param objectTypeId 对象类型ID
     * @return 默认存储库，如果没有则返回null
     */
    ObjectTypeDataSourceTable getDefaultStorageDatasource(UUID objectTypeId);

    /**
     * 为对象类型添加数据源（兼容旧方法，不推荐使用）
     *
     * @param objectTypeId 对象类型ID
     * @param datasourceId 数据源ID
     * @param isDefault 是否为默认数据源
     * @param priority 优先级
     * @return 关联实体
     * @deprecated 请使用 addDataSourceTableToObjectType，需要指定 tableName
     */
    @Deprecated
    ObjectTypeDataSource addDataSourceToObjectType(UUID objectTypeId, UUID datasourceId,
                                                     Boolean isDefault, Integer priority);

    /**
     * 移除对象类型的数据源关联（兼容旧方法）
     *
     * @param objectTypeId 对象类型ID
     * @param datasourceId 数据源ID
     * @deprecated 请使用 removeDataSourceTableById
     */
    @Deprecated
    void removeDataSourceFromObjectType(UUID objectTypeId, UUID datasourceId);

    /**
     * 设置默认数据源（兼容旧方法）
     *
     * @param objectTypeId 对象类型ID
     * @param datasourceId 数据源ID
     * @deprecated 请使用 setDefaultDataSourceTable
     */
    @Deprecated
    void setDefaultDataSource(UUID objectTypeId, UUID datasourceId);

    /**
     * 获取对象类型关联的所有数据源（兼容旧方法）
     *
     * @param objectTypeId 对象类型ID
     * @return 数据源列表（按优先级排序）
     * @deprecated 请使用 getDataSourceTablesByObjectTypeId
     */
    @Deprecated
    List<DataSource> getDataSourcesByObjectTypeId(UUID objectTypeId);

    /**
     * 获取对象类型的默认数据源（兼容旧方法）
     *
     * @param objectTypeId 对象类型ID
     * @return 默认数据源
     * @deprecated 请使用 getDefaultDataSourceTable
     */
    @Deprecated
    DataSource getDefaultDataSource(UUID objectTypeId);
}

