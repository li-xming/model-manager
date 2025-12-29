package com.example.datamodel.utils;

import lombok.extern.slf4j.Slf4j;

/**
 * 数据库字段类型到属性类型映射工具类
 *
 * @author DataModel Team
 */
@Slf4j
public class DataTypeMapper {

    /**
     * 将数据库字段类型映射到属性类型
     * 
     * @param dbDataType 数据库字段类型（如 VARCHAR, INT, BIGINT等）
     * @param datasourceType 数据源类型（如 MYSQL, POSTGRESQL等）
     * @return 属性类型（如 STRING, INTEGER, LONG等）
     */
    public static String mapToPropertyType(String dbDataType, String datasourceType) {
        if (dbDataType == null || dbDataType.isEmpty()) {
            return "STRING"; // 默认类型
        }

        String upperDataType = dbDataType.toUpperCase().trim();

        // 根据数据源类型选择映射规则
        if ("MYSQL".equalsIgnoreCase(datasourceType) || "MARIADB".equalsIgnoreCase(datasourceType)) {
            return mapMySQLType(upperDataType);
        } else if ("POSTGRESQL".equalsIgnoreCase(datasourceType)) {
            return mapPostgreSQLType(upperDataType);
        } else if ("ORACLE".equalsIgnoreCase(datasourceType)) {
            return mapOracleType(upperDataType);
        } else if ("SQL_SERVER".equalsIgnoreCase(datasourceType) || "SQLSERVER".equalsIgnoreCase(datasourceType)) {
            return mapSQLServerType(upperDataType);
        } else {
            // 默认使用通用映射规则
            return mapCommonType(upperDataType);
        }
    }

    /**
     * MySQL类型映射
     */
    private static String mapMySQLType(String dbType) {
        // 整数类型
        if (dbType.startsWith("TINYINT") || dbType.startsWith("SMALLINT") || 
            dbType.startsWith("MEDIUMINT") || "INT".equals(dbType) || "INTEGER".equals(dbType)) {
            // TINYINT(1) 通常用作布尔值
            if (dbType.contains("(1)")) {
                return "BOOLEAN";
            }
            return "INTEGER";
        }
        if ("BIGINT".equals(dbType)) {
            return "LONG";
        }

        // 浮点类型
        if ("FLOAT".equals(dbType)) {
            return "DOUBLE";
        }
        if ("DOUBLE".equals(dbType) || "DECIMAL".equals(dbType) || "NUMERIC".equals(dbType)) {
            return "DOUBLE";
        }

        // 字符串类型
        if (dbType.startsWith("CHAR") || dbType.startsWith("VARCHAR")) {
            return "STRING";
        }
        if (dbType.startsWith("TEXT") || "LONGTEXT".equals(dbType) || "MEDIUMTEXT".equals(dbType)) {
            return "TEXT";
        }

        // 日期时间类型
        if ("DATE".equals(dbType)) {
            return "DATE";
        }
        if ("DATETIME".equals(dbType) || "TIMESTAMP".equals(dbType) || "TIME".equals(dbType)) {
            return "TIMESTAMP";
        }

        // 布尔类型
        if ("BOOLEAN".equals(dbType) || "BOOL".equals(dbType)) {
            return "BOOLEAN";
        }

        // 二进制类型
        if (dbType.startsWith("BLOB") || "LONGBLOB".equals(dbType) || "MEDIUMBLOB".equals(dbType)) {
            return "BLOB";
        }

        // JSON类型
        if ("JSON".equals(dbType)) {
            return "JSON";
        }

        // 默认返回STRING
        log.warn("未知的MySQL类型：{}，默认映射为STRING", dbType);
        return "STRING";
    }

    /**
     * PostgreSQL类型映射
     */
    private static String mapPostgreSQLType(String dbType) {
        // 整数类型
        if ("SMALLINT".equals(dbType) || "INTEGER".equals(dbType) || "INT".equals(dbType)) {
            return "INTEGER";
        }
        if ("BIGINT".equals(dbType)) {
            return "LONG";
        }

        // 浮点类型
        if ("REAL".equals(dbType)) {
            return "DOUBLE";
        }
        if ("DOUBLE PRECISION".equals(dbType) || "NUMERIC".equals(dbType) || "DECIMAL".equals(dbType)) {
            return "DOUBLE";
        }

        // 字符串类型
        if ("CHAR".equals(dbType) || dbType.startsWith("VARCHAR") || "CHARACTER VARYING".equals(dbType)) {
            return "STRING";
        }
        if ("TEXT".equals(dbType)) {
            return "TEXT";
        }

        // 日期时间类型
        if ("DATE".equals(dbType)) {
            return "DATE";
        }
        if ("TIMESTAMP".equals(dbType) || "TIMESTAMPTZ".equals(dbType) || "TIMESTAMP WITH TIME ZONE".equals(dbType)) {
            return "TIMESTAMP";
        }

        // 布尔类型
        if ("BOOLEAN".equals(dbType) || "BOOL".equals(dbType)) {
            return "BOOLEAN";
        }

        // 二进制类型
        if ("BYTEA".equals(dbType)) {
            return "BLOB";
        }

        // UUID类型
        if ("UUID".equals(dbType)) {
            return "UUID";
        }

        // JSON类型
        if ("JSON".equals(dbType) || "JSONB".equals(dbType)) {
            return "JSON";
        }

        // 默认返回STRING
        log.warn("未知的PostgreSQL类型：{}，默认映射为STRING", dbType);
        return "STRING";
    }

    /**
     * Oracle类型映射
     */
    private static String mapOracleType(String dbType) {
        // 整数类型
        if ("NUMBER".equals(dbType) && dbType.contains(",") && dbType.contains("0")) {
            // NUMBER(p,0) 通常表示整数
            return "INTEGER";
        }
        if ("INTEGER".equals(dbType) || "INT".equals(dbType) || "SMALLINT".equals(dbType)) {
            return "INTEGER";
        }
        if ("BIGINT".equals(dbType)) {
            return "LONG";
        }

        // 浮点类型
        if ("FLOAT".equals(dbType) || "BINARY_FLOAT".equals(dbType) || "BINARY_DOUBLE".equals(dbType)) {
            return "DOUBLE";
        }
        if ("NUMBER".equals(dbType) || "NUMERIC".equals(dbType) || "DECIMAL".equals(dbType)) {
            return "DOUBLE";
        }

        // 字符串类型
        if ("CHAR".equals(dbType) || dbType.startsWith("VARCHAR") || "VARCHAR2".equals(dbType)) {
            return "STRING";
        }
        if ("CLOB".equals(dbType) || "NCLOB".equals(dbType) || "LONG".equals(dbType)) {
            return "TEXT";
        }

        // 日期时间类型
        if ("DATE".equals(dbType)) {
            return "DATE";
        }
        if ("TIMESTAMP".equals(dbType)) {
            return "TIMESTAMP";
        }

        // 二进制类型
        if ("BLOB".equals(dbType) || "BFILE".equals(dbType) || "RAW".equals(dbType)) {
            return "BLOB";
        }

        // 默认返回STRING
        log.warn("未知的Oracle类型：{}，默认映射为STRING", dbType);
        return "STRING";
    }

    /**
     * SQL Server类型映射
     */
    private static String mapSQLServerType(String dbType) {
        // 整数类型
        if ("TINYINT".equals(dbType) || "SMALLINT".equals(dbType) || "INT".equals(dbType)) {
            return "INTEGER";
        }
        if ("BIGINT".equals(dbType)) {
            return "LONG";
        }

        // 浮点类型
        if ("REAL".equals(dbType) || "FLOAT".equals(dbType)) {
            return "DOUBLE";
        }
        if ("DECIMAL".equals(dbType) || "NUMERIC".equals(dbType) || "MONEY".equals(dbType) || "SMALLMONEY".equals(dbType)) {
            return "DOUBLE";
        }

        // 字符串类型
        if ("CHAR".equals(dbType) || dbType.startsWith("VARCHAR") || "NCHAR".equals(dbType) || dbType.startsWith("NVARCHAR")) {
            return "STRING";
        }
        if ("TEXT".equals(dbType) || "NTEXT".equals(dbType)) {
            return "TEXT";
        }

        // 日期时间类型
        if ("DATE".equals(dbType)) {
            return "DATE";
        }
        if ("DATETIME".equals(dbType) || "DATETIME2".equals(dbType) || "SMALLDATETIME".equals(dbType) || 
            "TIMESTAMP".equals(dbType) || "TIME".equals(dbType)) {
            return "TIMESTAMP";
        }

        // 布尔类型（SQL Server使用BIT）
        if ("BIT".equals(dbType)) {
            return "BOOLEAN";
        }

        // 二进制类型
        if ("BINARY".equals(dbType) || "VARBINARY".equals(dbType) || "IMAGE".equals(dbType)) {
            return "BLOB";
        }

        // 默认返回STRING
        log.warn("未知的SQL Server类型：{}，默认映射为STRING", dbType);
        return "STRING";
    }

    /**
     * 通用类型映射（当无法识别数据源类型时使用）
     */
    private static String mapCommonType(String dbType) {
        // 尝试通用的类型识别
        if (dbType.contains("INT")) {
            if (dbType.contains("BIG")) {
                return "LONG";
            }
            return "INTEGER";
        }
        if (dbType.contains("FLOAT") || dbType.contains("DOUBLE") || dbType.contains("DECIMAL") || dbType.contains("NUMERIC")) {
            return "DOUBLE";
        }
        if (dbType.contains("CHAR") || dbType.contains("VARCHAR") || dbType.contains("TEXT")) {
            if (dbType.contains("TEXT") || dbType.contains("CLOB")) {
                return "TEXT";
            }
            return "STRING";
        }
        if (dbType.contains("DATE") || dbType.contains("TIME")) {
            if (dbType.contains("TIME")) {
                return "TIMESTAMP";
            }
            return "DATE";
        }
        if (dbType.contains("BOOL") || dbType.contains("BIT")) {
            return "BOOLEAN";
        }
        if (dbType.contains("BLOB") || dbType.contains("BINARY") || dbType.contains("BYTE")) {
            return "BLOB";
        }
        if (dbType.contains("JSON")) {
            return "JSON";
        }
        if ("UUID".equals(dbType)) {
            return "UUID";
        }

        // 默认返回STRING
        log.warn("未知的数据库类型：{}，默认映射为STRING", dbType);
        return "STRING";
    }
}

