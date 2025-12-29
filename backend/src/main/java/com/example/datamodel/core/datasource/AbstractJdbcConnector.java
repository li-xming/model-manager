package com.example.datamodel.core.datasource;

import com.example.datamodel.entity.DataSource;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * JDBC数据源连接器基类
 * 简化JDBC数据源的实现
 *
 * @author DataModel Team
 */
@Slf4j
public abstract class AbstractJdbcConnector implements DataSourceConnector {

    @Override
    public Connection createConnection(DataSource datasource) throws Exception {
        String url = buildConnectionUrl(datasource);
        String driverClass = getDriverClass();

        // 加载驱动
        if (driverClass != null) {
            try {
                Class.forName(driverClass);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("JDBC驱动类未找到：" + driverClass, e);
            }
        }

        // 解密密码
        String password = datasource.getPassword();
        if (com.example.datamodel.utils.PasswordUtils.isEncrypted(password)) {
            password = com.example.datamodel.utils.PasswordUtils.removeEncryptionPrefix(password);
            password = com.example.datamodel.utils.PasswordUtils.decrypt(password);
        }

        // 创建连接
        return DriverManager.getConnection(url, datasource.getUsername(), password);
    }

    @Override
    public boolean testConnection(DataSource datasource) {
        try (Connection conn = createConnection(datasource)) {
            return conn.isValid(5); // 5秒超时
        } catch (Exception e) {
            log.error("连接测试失败：{}", e.getMessage());
            return false;
        }
    }

    @Override
    public List<String> getTableList(Connection connection, String schemaName) throws SQLException {
        DatabaseMetaData metaData = connection.getMetaData();
        String catalog = connection.getCatalog();
        
        // 对于MySQL，catalog = databaseName，schemaName应该为null
        // 对于PostgreSQL，catalog为null，schemaName为schema名称
        String actualSchema = normalizeSchemaName(metaData, schemaName);
        String actualCatalog = normalizeCatalogName(metaData, catalog);
        
        List<String> tables = new ArrayList<>();
        try (ResultSet rs = metaData.getTables(actualCatalog, actualSchema, null, new String[]{"TABLE", "VIEW"})) {
            while (rs.next()) {
                String tableName = rs.getString("TABLE_NAME");
                if (tableName != null) {
                    tables.add(tableName);
                }
            }
        }
        return tables;
    }

    @Override
    public List<TableColumnInfo> getTableColumns(Connection connection, String schemaName, String tableName) throws SQLException {
        DatabaseMetaData metaData = connection.getMetaData();
        String catalog = connection.getCatalog();
        
        String actualSchema = normalizeSchemaName(metaData, schemaName);
        String actualCatalog = normalizeCatalogName(metaData, catalog);
        
        List<TableColumnInfo> columns = new ArrayList<>();
        
        // 获取字段信息
        try (ResultSet rs = metaData.getColumns(actualCatalog, actualSchema, tableName, null)) {
            while (rs.next()) {
                TableColumnInfo column = new TableColumnInfo();
                column.setColumnName(rs.getString("COLUMN_NAME"));
                column.setDataType(rs.getString("TYPE_NAME"));
                column.setLength(rs.getInt("COLUMN_SIZE"));
                column.setNullable(rs.getInt("NULLABLE") == DatabaseMetaData.columnNullable);
                column.setDefaultValue(rs.getString("COLUMN_DEF"));
                column.setComment(rs.getString("REMARKS"));
                column.setScale(rs.getInt("DECIMAL_DIGITS"));
                columns.add(column);
            }
        }
        
        // 获取主键信息
        Set<String> primaryKeys = new HashSet<>();
        try (ResultSet rs = metaData.getPrimaryKeys(actualCatalog, actualSchema, tableName)) {
            while (rs.next()) {
                primaryKeys.add(rs.getString("COLUMN_NAME"));
            }
        }
        
        // 设置主键标识
        columns.forEach(col -> col.setPrimaryKey(primaryKeys.contains(col.getColumnName())));
        
        return columns;
    }

    @Override
    public List<String> getSchemaList(Connection connection) throws SQLException {
        if (!requiresSchema()) {
            return new ArrayList<>(); // 不支持Schema的数据源返回空列表
        }
        
        DatabaseMetaData metaData = connection.getMetaData();
        List<String> schemas = new ArrayList<>();
        
        try (ResultSet rs = metaData.getSchemas()) {
            while (rs.next()) {
                String schemaName = rs.getString("TABLE_SCHEM");
                if (schemaName != null) {
                    schemas.add(schemaName);
                }
            }
        }
        return schemas;
    }

    /**
     * 规范化Schema名称
     * 不同数据库对Schema的处理不同，需要子类覆盖
     *
     * @param metaData 数据库元数据
     * @param schemaName Schema名称
     * @return 规范化后的Schema名称
     */
    protected String normalizeSchemaName(DatabaseMetaData metaData, String schemaName) throws SQLException {
        return schemaName;
    }

    /**
     * 规范化Catalog名称
     * 不同数据库对Catalog的处理不同，需要子类覆盖
     *
     * @param metaData 数据库元数据
     * @param catalog Catalog名称
     * @return 规范化后的Catalog名称
     */
    protected String normalizeCatalogName(DatabaseMetaData metaData, String catalog) throws SQLException {
        return catalog;
    }
}

