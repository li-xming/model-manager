package com.example.datamodel.core.datasource;

import com.example.datamodel.entity.DataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * 数据源连接器接口
 * 每种数据源类型需要实现此接口
 *
 * @author DataModel Team
 */
public interface DataSourceConnector {

    /**
     * 获取数据源类型代码
     *
     * @return 类型代码，如 "MYSQL", "POSTGRESQL" 等
     */
    String getTypeCode();

    /**
     * 获取数据源类型显示名称
     *
     * @return 显示名称，如 "MySQL", "PostgreSQL" 等
     */
    String getDisplayName();

    /**
     * 构建连接URL
     *
     * @param datasource 数据源实体
     * @return 连接URL
     */
    String buildConnectionUrl(DataSource datasource);

    /**
     * 获取JDBC驱动类（如果是JDBC数据源）
     * 返回null表示非JDBC数据源
     *
     * @return JDBC驱动类名
     */
    String getDriverClass();

    /**
     * 创建连接
     * 对于JDBC数据源，返回java.sql.Connection
     * 对于非JDBC数据源，返回适配器包装的连接对象
     *
     * @param datasource 数据源实体
     * @return 连接对象
     * @throws Exception 连接失败时抛出异常
     */
    Connection createConnection(DataSource datasource) throws Exception;

    /**
     * 测试连接
     *
     * @param datasource 数据源实体
     * @return 连接是否成功
     */
    boolean testConnection(DataSource datasource);

    /**
     * 获取默认端口
     *
     * @return 默认端口号
     */
    Integer getDefaultPort();

    /**
     * 是否需要数据库名
     *
     * @return true表示需要数据库名
     */
    boolean requiresDatabase();

    /**
     * 是否需要Schema名
     *
     * @return true表示需要Schema名
     */
    boolean requiresSchema();

    /**
     * 获取连接器描述
     *
     * @return 描述信息
     */
    default String getDescription() {
        return "";
    }

    /**
     * 获取数据源下的所有表名列表
     *
     * @param connection 数据源连接
     * @param schemaName Schema名称（可选，PostgreSQL/Oracle使用）
     * @return 表名列表
     * @throws SQLException SQL异常
     */
    default List<String> getTableList(Connection connection, String schemaName) throws SQLException {
        throw new UnsupportedOperationException("该数据源连接器不支持获取表列表");
    }

    /**
     * 获取表的字段信息
     *
     * @param connection 数据源连接
     * @param schemaName Schema名称（可选，PostgreSQL/Oracle使用）
     * @param tableName 表名
     * @return 字段信息列表
     * @throws SQLException SQL异常
     */
    default List<TableColumnInfo> getTableColumns(Connection connection, String schemaName, String tableName) throws SQLException {
        throw new UnsupportedOperationException("该数据源连接器不支持获取表字段信息");
    }

    /**
     * 获取Schema列表（如果数据源支持Schema）
     *
     * @param connection 数据源连接
     * @return Schema名称列表
     * @throws SQLException SQL异常
     */
    default List<String> getSchemaList(Connection connection) throws SQLException {
        throw new UnsupportedOperationException("该数据源连接器不支持获取Schema列表");
    }
}

