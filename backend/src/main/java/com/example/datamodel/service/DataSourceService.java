package com.example.datamodel.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.datamodel.entity.DataSource;

import java.util.UUID;

/**
 * 数据源服务接口
 *
 * @author DataModel Team
 */
public interface DataSourceService extends IService<DataSource> {

    /**
     * 创建数据源
     *
     * @param datasource 数据源实体
     * @return 创建的数据源
     */
    DataSource createDataSource(DataSource datasource);

    /**
     * 更新数据源
     *
     * @param id 数据源ID
     * @param datasource 数据源实体
     * @return 更新后的数据源
     */
    DataSource updateDataSource(UUID id, DataSource datasource);

    /**
     * 根据ID获取数据源（密码字段不返回）
     *
     * @param id 数据源ID
     * @return 数据源实体
     */
    DataSource getDataSourceById(UUID id);

    /**
     * 根据代码获取数据源（密码字段不返回）
     *
     * @param code 数据源代码
     * @return 数据源实体
     */
    DataSource getDataSourceByCode(String code);

    /**
     * 分页查询数据源
     *
     * @param page 分页对象
     * @param type 数据源类型（可选）
     * @param domainId 业务域ID（可选）
     * @return 分页结果
     */
    IPage<DataSource> listDataSources(Page<DataSource> page, String type, UUID domainId);

    /**
     * 测试数据源连接
     *
     * @param id 数据源ID
     * @return 测试结果（true表示成功）
     */
    boolean testConnection(UUID id);

    /**
     * 启用/禁用数据源
     *
     * @param id 数据源ID
     * @param enabled 是否启用
     */
    void setEnabled(UUID id, Boolean enabled);

    /**
     * 构建连接URL
     *
     * @param datasource 数据源实体
     * @return 连接URL
     */
    String buildConnectionUrl(DataSource datasource);

    /**
     * 获取数据源下的表列表
     *
     * @param datasourceId 数据源ID
     * @param schemaName Schema名称（可选）
     * @return 表名列表
     */
    java.util.List<String> getTableList(UUID datasourceId, String schemaName);

    /**
     * 获取表的字段信息
     *
     * @param datasourceId 数据源ID
     * @param schemaName Schema名称（可选）
     * @param tableName 表名
     * @return 字段信息列表
     */
    java.util.List<com.example.datamodel.core.datasource.TableColumnInfo> getTableColumns(UUID datasourceId, String schemaName, String tableName);

    /**
     * 获取数据源的Schema列表（如果支持）
     *
     * @param datasourceId 数据源ID
     * @return Schema名称列表
     */
    java.util.List<String> getSchemaList(UUID datasourceId);
}

