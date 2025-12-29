package com.example.datamodel.core;

import com.example.datamodel.core.datasource.DataSourceConnectionManager;
import com.example.datamodel.entity.BusinessDomain;
import com.example.datamodel.entity.DataSource;
import com.example.datamodel.entity.ObjectType;
import com.example.datamodel.entity.Property;
import com.example.datamodel.exception.BusinessException;
import com.example.datamodel.service.BusinessDomainService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * 动态表管理器
 * 负责根据对象类型定义动态创建和管理数据库表
 *
 * @author DataModel Team
 */
@Slf4j
@Component
public class DynamicTableManager {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private BusinessDomainService businessDomainService;

    @Autowired
    private DataSourceConnectionManager dataSourceConnectionManager;

    /**
     * 根据对象类型创建实例表（在平台主数据库中）
     * 表名格式：{domain_code}_{objectTypeName}
     *
     * @param objectType 对象类型
     * @param properties 属性列表
     */
    public void createInstanceTable(ObjectType objectType, List<Property> properties) {
        String tableName = generateInstanceTableName(objectType);
        
        // 检查表是否已存在
        if (tableExists(objectType)) {
            log.warn("实例表已存在：{}", tableName);
            return;
        }

        StringBuilder sql = new StringBuilder();
        sql.append("CREATE TABLE IF NOT EXISTS ").append(tableName).append(" (\n");
        sql.append("    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),\n");
        sql.append("    class_id UUID NOT NULL,\n");
        sql.append("    datasource_id UUID,\n");
        sql.append("    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,\n");
        sql.append("    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,\n");
        sql.append("    created_by VARCHAR(255),\n");
        sql.append("    updated_by VARCHAR(255)");

        // 系统保留字段，这些字段不应该从属性定义中添加
        Set<String> reservedFields = new HashSet<>(Arrays.asList(
            "id", "class_id", "datasource_id", "created_at", "updated_at", "created_by", "updated_by"
        ));

        // 添加属性字段
        if (properties != null && !properties.isEmpty()) {
            for (Property property : properties) {
                String fieldName = property.getName().toLowerCase();
                if (reservedFields.contains(fieldName)) {
                    continue; // 跳过保留字段
                }
                sql.append(",\n    ");
                sql.append(fieldName).append(" ");
                sql.append(getPostgresType(property.getDataType()));
                
                if (property.getRequired() != null && property.getRequired()) {
                    sql.append(" NOT NULL");
                }
                
                if (property.getDefaultValue() != null && !property.getDefaultValue().isEmpty()) {
                    sql.append(" DEFAULT ").append(formatDefaultValue(property.getDefaultValue(), property.getDataType()));
                }
            }
        }
        
        sql.append("\n)");

        // 创建索引
        sql.append(";\n");
        String indexSuffix = sanitizeTableName(tableName);
        sql.append("CREATE INDEX IF NOT EXISTS idx_").append(indexSuffix).append("_class_id ON ").append(tableName).append("(class_id);\n");
        sql.append("CREATE INDEX IF NOT EXISTS idx_").append(indexSuffix).append("_datasource_id ON ").append(tableName).append("(datasource_id);");

        jdbcTemplate.execute(sql.toString());
        log.info("创建实例表成功：{}", tableName);
    }

    /**
     * 删除实例表
     *
     * @param objectType 对象类型
     */
    public void dropInstanceTable(ObjectType objectType) {
        String tableName = generateInstanceTableName(objectType);
        String sql = "DROP TABLE IF EXISTS " + tableName;
        jdbcTemplate.execute(sql);
        log.info("删除实例表成功：{}", tableName);
    }

    /**
     * 删除实例表（使用对象类型ID，需要ObjectType才能正确工作）
     *
     * @deprecated 使用 dropInstanceTable(ObjectType) 代替
     * @param objectTypeId 对象类型ID
     */
    @Deprecated
    public void dropInstanceTable(UUID objectTypeId) {
        log.warn("使用了已废弃的方法 dropInstanceTable(UUID)，建议使用 dropInstanceTable(ObjectType)");
        // 此方法无法正确工作，因为需要ObjectType才能生成正确的表名
    }

    /**
     * 检查表是否存在（使用对象类型）
     *
     * @param objectType 对象类型
     * @return 表是否存在
     */
    public boolean tableExists(ObjectType objectType) {
        String tableName = generateInstanceTableName(objectType);
        String sql = "SELECT EXISTS (SELECT FROM information_schema.tables WHERE table_schema = 'public' AND table_name = ?)";
        Boolean exists = jdbcTemplate.queryForObject(sql, Boolean.class, tableName);
        return exists != null && exists;
    }

    /**
     * 检查表是否存在（使用对象类型ID，需要ObjectType才能正确工作）
     *
     * @deprecated 使用 tableExists(ObjectType) 代替
     * @param objectTypeId 对象类型ID
     * @return 表是否存在
     */
    @Deprecated
    public boolean tableExists(UUID objectTypeId) {
        log.warn("使用了已废弃的方法 tableExists(UUID)，建议使用 tableExists(ObjectType)");
        // 此方法无法正确工作，因为需要ObjectType才能生成正确的表名
        // 保留此方法仅用于向后兼容，但建议尽快迁移
        return false;
    }

    /**
     * 检查表是否存在（使用对象类型名称，兼容旧代码）
     * 
     * @deprecated 使用 tableExists(ObjectType) 或 tableExists(UUID) 代替
     * @param objectTypeName 对象类型名称
     * @return 表是否存在
     */
    @Deprecated
    public boolean tableExists(String objectTypeName) {
        // 此方法保留用于向后兼容，但建议使用新的基于ID的方法
        // 注意：此方法无法准确定位表，因为现在使用ID作为表名标识
        log.warn("使用了已废弃的方法 tableExists(String)，建议使用 tableExists(UUID) 或 tableExists(ObjectType)");
        String tableName = "instances_" + objectTypeName.toLowerCase();
        String sql = "SELECT EXISTS (SELECT FROM information_schema.tables WHERE table_schema = 'public' AND table_name = ?)";
        Boolean exists = jdbcTemplate.queryForObject(sql, Boolean.class, tableName);
        return exists != null && exists;
    }

    /**
     * 添加列到实例表
     *
     * @param objectType 对象类型
     * @param property 属性
     */
    public void addColumn(ObjectType objectType, Property property) {
        String tableName = generateInstanceTableName(objectType);
        
        // 系统保留字段，不允许添加
        Set<String> reservedFields = new HashSet<>(Arrays.asList(
            "id", "class_id", "created_at", "updated_at", "created_by", "updated_by"
        ));
        
        String fieldName = property.getName().toLowerCase();
        if (reservedFields.contains(fieldName)) {
            log.warn("跳过保留字段：{}", fieldName);
            return;
        }

        StringBuilder sql = new StringBuilder();
        sql.append("ALTER TABLE ").append(tableName).append(" ADD COLUMN IF NOT EXISTS ");
        sql.append(fieldName).append(" ");
        sql.append(getPostgresType(property.getDataType()));
        
        if (property.getRequired() != null && property.getRequired()) {
            sql.append(" NOT NULL");
        }
        
        if (property.getDefaultValue() != null && !property.getDefaultValue().isEmpty()) {
            sql.append(" DEFAULT ").append(formatDefaultValue(property.getDefaultValue(), property.getDataType()));
        }

        jdbcTemplate.execute(sql.toString());
        log.info("添加列成功：{}.{}", tableName, fieldName);
    }

    /**
     * 从实例表删除列
     *
     * @param objectType 对象类型
     * @param columnName 列名
     */
    public void dropColumn(ObjectType objectType, String columnName) {
        String tableName = generateInstanceTableName(objectType);
        String sql = "ALTER TABLE " + tableName + " DROP COLUMN IF EXISTS " + columnName.toLowerCase();
        jdbcTemplate.execute(sql);
        log.info("删除列成功：{}.{}", tableName, columnName);
    }

    /**
     * 从实例表删除列（使用对象类型ID，需要ObjectType才能正确工作）
     *
     * @deprecated 使用 dropColumn(ObjectType, String) 代替
     * @param objectTypeId 对象类型ID
     * @param columnName 列名
     */
    @Deprecated
    public void dropColumn(UUID objectTypeId, String columnName) {
        log.warn("使用了已废弃的方法 dropColumn(UUID, String)，建议使用 dropColumn(ObjectType, String)");
        // 此方法无法正确工作，因为需要ObjectType才能生成正确的表名
    }


    /**
     * 检查列是否有数据（使用对象类型ID，需要ObjectType才能正确工作）
     *
     * @deprecated 使用 hasColumnData(ObjectType, String) 代替
     * @param objectTypeId 对象类型ID
     * @param columnName 列名
     * @return 是否有数据
     */
    @Deprecated
    public boolean hasColumnData(UUID objectTypeId, String columnName) {
        log.warn("使用了已废弃的方法 hasColumnData(UUID, String)，建议使用 hasColumnData(ObjectType, String)");
        // 此方法无法正确工作，因为需要ObjectType才能生成正确的表名
        return false;
    }
    
    /**
     * 检查列是否有数据（使用对象类型）
     *
     * @param objectType 对象类型
     * @param columnName 列名
     * @return 是否有数据
     */
    public boolean hasColumnData(ObjectType objectType, String columnName) {
        String tableName = generateInstanceTableName(objectType);
        String sql = "SELECT COUNT(*) > 0 FROM " + tableName + " WHERE " + columnName.toLowerCase() + " IS NOT NULL LIMIT 1";
        Boolean hasData = jdbcTemplate.queryForObject(sql, Boolean.class);
        return hasData != null && hasData;
    }

    /**
     * 生成实例表名
     * 表名格式：{domain_code}_{objectTypeName}
     * 如果domainId为null，则使用默认前缀 "default"
     * 
     * @param objectType 对象类型
     * @return 表名
     */
    public String generateInstanceTableName(ObjectType objectType) {
        String domainCode;
        if (objectType.getDomainId() != null) {
            BusinessDomain domain = businessDomainService.getById(objectType.getDomainId());
            if (domain == null) {
                log.warn("对象类型所属业务域不存在：{}，使用默认前缀", objectType.getDomainId());
                domainCode = "default";
            } else {
                domainCode = sanitizeTableName(domain.getCode().toLowerCase());
            }
        } else {
            log.warn("对象类型 {} 未设置业务域，使用默认前缀", objectType.getName());
            domainCode = "default";
        }
        
        String objectTypeName = sanitizeTableName(objectType.getName().toLowerCase());
        
        return domainCode + "_" + objectTypeName;
    }

    /**
     * 获取实例表名（兼容旧方法）
     * 
     * @deprecated 使用 generateInstanceTableName(ObjectType) 代替
     * @param objectTypeId 对象类型ID
     * @return 表名
     */
    @Deprecated
    public String getInstanceTableName(UUID objectTypeId) {
        // 此方法保留用于向后兼容，但需要ObjectType才能正确生成表名
        // 注意：此方法可能无法正确工作，建议使用 generateInstanceTableName(ObjectType)
        log.warn("使用了已废弃的方法 getInstanceTableName(UUID)，建议使用 generateInstanceTableName(ObjectType)");
        return "instances_" + objectTypeId.toString().replace("-", "_");
    }
    
    /**
     * 清理表名，确保符合数据库命名规范
     * 只保留字母、数字、下划线，其他字符替换为下划线
     * 
     * @param name 原始名称
     * @return 清理后的名称
     */
    private String sanitizeTableName(String name) {
        if (name == null || name.isEmpty()) {
            throw new BusinessException("表名不能为空");
        }
        // 移除特殊字符，只保留字母、数字、下划线
        return name.replaceAll("[^a-z0-9_]", "_");
    }


    /**
     * 在指定的数据源中创建实例表
     * 表名格式：{domain_code}_{objectTypeName}
     *
     * @param datasource 数据源
     * @param tableName 表名
     * @param objectType 对象类型
     * @param properties 属性列表
     */
    public void createInstanceTableInDatasource(DataSource datasource, String tableName,
                                                 ObjectType objectType, List<Property> properties) {
        try (Connection connection = dataSourceConnectionManager.getConnection(datasource)) {
            // 检查表是否已存在
            if (tableExistsInDatasource(connection, datasource, tableName)) {
                log.warn("数据源 {} 中实例表已存在：{}", datasource.getName(), tableName);
                return;
            }

            StringBuilder sql = new StringBuilder();
            sql.append("CREATE TABLE IF NOT EXISTS ").append(escapeTableName(datasource.getType(), tableName)).append(" (\n");
            sql.append("    id VARCHAR(36) PRIMARY KEY,\n");
            sql.append("    class_id VARCHAR(36) NOT NULL,\n");
            sql.append("    datasource_id VARCHAR(36),\n");
            sql.append("    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,\n");
            sql.append("    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,\n");
            sql.append("    created_by VARCHAR(255),\n");
            sql.append("    updated_by VARCHAR(255)");

            // 系统保留字段，这些字段不应该从属性定义中添加
            Set<String> reservedFields = new HashSet<>(Arrays.asList(
                "id", "class_id", "datasource_id", "created_at", "updated_at", "created_by", "updated_by"
            ));

            // 添加属性字段
            if (properties != null && !properties.isEmpty()) {
                for (Property property : properties) {
                    String fieldName = property.getName().toLowerCase();
                    if (reservedFields.contains(fieldName)) {
                        continue; // 跳过保留字段
                    }
                    sql.append(",\n    ");
                    sql.append(escapeTableName(datasource.getType(), fieldName)).append(" ");
                    
                    // 根据数据源类型映射数据类型
                    String dbType = mapDataTypeToDbType(property.getDataType(), datasource.getType());
                    sql.append(dbType);
                    
                    if (property.getRequired() != null && property.getRequired()) {
                        sql.append(" NOT NULL");
                    }
                    
                    if (property.getDefaultValue() != null && !property.getDefaultValue().isEmpty()) {
                        sql.append(" DEFAULT ").append(formatDefaultValueForDb(property.getDefaultValue(), property.getDataType(), datasource.getType()));
                    }
                }
            }
            
            sql.append("\n)");

            // 执行创建表SQL
            try (Statement stmt = connection.createStatement()) {
                stmt.execute(sql.toString());
                log.info("在数据源 {} 中创建实例表成功：{}", datasource.getName(), tableName);
            }
        } catch (SQLException e) {
            log.error("在数据源 {} 中创建实例表失败：{}", datasource.getName(), tableName, e);
            throw new BusinessException("在数据源中创建实例表失败：" + e.getMessage());
        }
    }

    /**
     * 检查表是否在数据源中存在
     *
     * @param connection 数据源连接
     * @param datasource 数据源
     * @param tableName 表名
     * @return 表是否存在
     */
    public boolean tableExistsInDatasource(Connection connection, DataSource datasource, String tableName) {
        try {
            // 使用 JDBC DatabaseMetaData 检查表是否存在
            java.sql.DatabaseMetaData metaData = connection.getMetaData();
            String catalog = connection.getCatalog();
            String schema = datasource.getSchemaName();
            
            // 对于不同数据库，schema/catalog的处理不同
            String actualSchema = normalizeSchemaName(metaData, schema);
            String actualCatalog = normalizeCatalogName(metaData, catalog);
            
            try (java.sql.ResultSet rs = metaData.getTables(actualCatalog, actualSchema, tableName, new String[]{"TABLE"})) {
                return rs.next();
            }
        } catch (SQLException e) {
            log.error("检查表是否存在失败：{}", tableName, e);
            return false;
        }
    }

    /**
     * 转义表名/字段名（根据数据库类型）
     *
     * @param datasourceType 数据源类型
     * @param name 名称
     * @return 转义后的名称
     */
    private String escapeTableName(String datasourceType, String name) {
        // 对于不同数据库，使用不同的转义符
        if ("MYSQL".equalsIgnoreCase(datasourceType) || "MARIADB".equalsIgnoreCase(datasourceType)) {
            return "`" + name + "`";
        } else if ("POSTGRESQL".equalsIgnoreCase(datasourceType)) {
            return "\"" + name + "\"";
        } else if ("SQL_SERVER".equalsIgnoreCase(datasourceType) || "SQLSERVER".equalsIgnoreCase(datasourceType)) {
            return "[" + name + "]";
        } else if ("ORACLE".equalsIgnoreCase(datasourceType)) {
            return "\"" + name.toUpperCase() + "\"";
        } else {
            // 默认不转义
            return name;
        }
    }

    /**
     * 将属性数据类型映射到数据库类型
     *
     * @param dataType 属性数据类型
     * @param datasourceType 数据源类型
     * @return 数据库类型字符串
     */
    private String mapDataTypeToDbType(String dataType, String datasourceType) {
        if (dataType == null) {
            return "TEXT";
        }

        // 对于不同数据库，使用不同的类型映射
        switch (dataType.toUpperCase()) {
            case "STRING":
            case "VARCHAR":
                if ("MYSQL".equalsIgnoreCase(datasourceType) || "MARIADB".equalsIgnoreCase(datasourceType)) {
                    return "VARCHAR(255)";
                } else if ("POSTGRESQL".equalsIgnoreCase(datasourceType)) {
                    return "VARCHAR(255)";
                } else if ("ORACLE".equalsIgnoreCase(datasourceType)) {
                    return "VARCHAR2(255)";
                } else if ("SQL_SERVER".equalsIgnoreCase(datasourceType) || "SQLSERVER".equalsIgnoreCase(datasourceType)) {
                    return "NVARCHAR(255)";
                }
                return "VARCHAR(255)";
            case "TEXT":
                if ("MYSQL".equalsIgnoreCase(datasourceType) || "MARIADB".equalsIgnoreCase(datasourceType)) {
                    return "TEXT";
                } else if ("POSTGRESQL".equalsIgnoreCase(datasourceType)) {
                    return "TEXT";
                } else if ("ORACLE".equalsIgnoreCase(datasourceType)) {
                    return "CLOB";
                } else if ("SQL_SERVER".equalsIgnoreCase(datasourceType) || "SQLSERVER".equalsIgnoreCase(datasourceType)) {
                    return "NVARCHAR(MAX)";
                }
                return "TEXT";
            case "INTEGER":
            case "INT":
                return "INTEGER";
            case "LONG":
            case "BIGINT":
                return "BIGINT";
            case "FLOAT":
            case "REAL":
                return "REAL";
            case "DOUBLE":
                return "DOUBLE PRECISION";
            case "BOOLEAN":
            case "BOOL":
                if ("ORACLE".equalsIgnoreCase(datasourceType)) {
                    return "NUMBER(1)"; // Oracle使用NUMBER(1)表示布尔值
                }
                return "BOOLEAN";
            case "DATE":
                return "DATE";
            case "TIMESTAMP":
            case "DATETIME":
                if ("MYSQL".equalsIgnoreCase(datasourceType) || "MARIADB".equalsIgnoreCase(datasourceType)) {
                    return "DATETIME";
                } else if ("POSTGRESQL".equalsIgnoreCase(datasourceType)) {
                    return "TIMESTAMP";
                } else if ("ORACLE".equalsIgnoreCase(datasourceType)) {
                    return "TIMESTAMP";
                } else if ("SQL_SERVER".equalsIgnoreCase(datasourceType) || "SQLSERVER".equalsIgnoreCase(datasourceType)) {
                    return "DATETIME2";
                }
                return "TIMESTAMP";
            case "JSON":
            case "JSONB":
                if ("MYSQL".equalsIgnoreCase(datasourceType) || "MARIADB".equalsIgnoreCase(datasourceType)) {
                    return "JSON";
                } else if ("POSTGRESQL".equalsIgnoreCase(datasourceType)) {
                    return "JSONB";
                } else {
                    return "TEXT"; // 其他数据库使用TEXT存储JSON
                }
            case "UUID":
                if ("POSTGRESQL".equalsIgnoreCase(datasourceType)) {
                    return "UUID";
                } else {
                    return "VARCHAR(36)"; // 其他数据库使用VARCHAR存储UUID
                }
            default:
                return "TEXT";
        }
    }

    /**
     * 格式化默认值（针对不同数据库）
     *
     * @param defaultValue 默认值
     * @param dataType 数据类型
     * @param datasourceType 数据源类型
     * @return 格式化后的默认值
     */
    private String formatDefaultValueForDb(String defaultValue, String dataType, String datasourceType) {
        if (dataType == null) {
            return "'" + defaultValue.replace("'", "''") + "'";
        }
        
        switch (dataType.toUpperCase()) {
            case "BOOLEAN":
            case "BOOL":
            case "INTEGER":
            case "INT":
            case "LONG":
            case "BIGINT":
            case "FLOAT":
            case "REAL":
            case "DOUBLE":
                return defaultValue;
            default:
                return "'" + defaultValue.replace("'", "''") + "'";
        }
    }

    /**
     * 规范化Schema名称（处理不同数据库的差异）
     */
    private String normalizeSchemaName(java.sql.DatabaseMetaData metaData, String schemaName) throws SQLException {
        String dbProductName = metaData.getDatabaseProductName().toUpperCase();
        if (dbProductName.contains("MYSQL")) {
            // MySQL使用catalog作为database，schema应该为null
            return null;
        }
        return schemaName;
    }

    /**
     * 规范化Catalog名称（处理不同数据库的差异）
     */
    private String normalizeCatalogName(java.sql.DatabaseMetaData metaData, String catalog) {
        // 对于PostgreSQL，catalog通常为null
        return catalog;
    }

    /**
     * 将数据类型转换为PostgreSQL类型（保留用于平台存储）
     *
     * @param dataType 数据类型
     * @return PostgreSQL类型
     */
    private String getPostgresType(String dataType) {
        if (dataType == null) {
            return "TEXT";
        }
        
        switch (dataType.toUpperCase()) {
            case "STRING":
            case "VARCHAR":
                return "VARCHAR(255)";
            case "TEXT":
                return "TEXT";
            case "INTEGER":
            case "INT":
                return "INTEGER";
            case "LONG":
            case "BIGINT":
                return "BIGINT";
            case "FLOAT":
            case "REAL":
                return "REAL";
            case "DOUBLE":
                return "DOUBLE PRECISION";
            case "BOOLEAN":
            case "BOOL":
                return "BOOLEAN";
            case "DATE":
                return "DATE";
            case "TIMESTAMP":
            case "DATETIME":
                return "TIMESTAMP";
            case "JSON":
            case "JSONB":
                return "JSONB";
            case "UUID":
                return "UUID";
            default:
                return "TEXT";
        }
    }

    /**
     * 格式化默认值
     *
     * @param defaultValue 默认值
     * @param dataType 数据类型
     * @return 格式化后的默认值
     */
    private String formatDefaultValue(String defaultValue, String dataType) {
        if (dataType == null) {
            return "'" + defaultValue + "'";
        }
        
        switch (dataType.toUpperCase()) {
            case "BOOLEAN":
            case "BOOL":
            case "INTEGER":
            case "INT":
            case "LONG":
            case "BIGINT":
            case "FLOAT":
            case "REAL":
            case "DOUBLE":
                return defaultValue;
            default:
                return "'" + defaultValue.replace("'", "''") + "'";
        }
    }
}
