package com.example.datamodel.core.datasource.impl;

import com.example.datamodel.core.datasource.AbstractJdbcConnector;
import com.example.datamodel.entity.DataSource;
import org.springframework.stereotype.Component;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;

/**
 * PostgreSQL数据源连接器
 *
 * @author DataModel Team
 */
@Component
public class PostgreSQLConnector extends AbstractJdbcConnector {

    @Override
    public String getTypeCode() {
        return "POSTGRESQL";
    }

    @Override
    public String getDisplayName() {
        return "PostgreSQL";
    }

    @Override
    public String buildConnectionUrl(DataSource datasource) {
        StringBuilder url = new StringBuilder();
        url.append("jdbc:postgresql://")
           .append(datasource.getHost())
           .append(":")
           .append(datasource.getPort())
           .append("/")
           .append(datasource.getDatabaseName() != null ? datasource.getDatabaseName() : "");

        if (datasource.getSchemaName() != null && !datasource.getSchemaName().isEmpty()) {
            url.append("?currentSchema=").append(datasource.getSchemaName());
        }

        return url.toString();
    }

    @Override
    public String getDriverClass() {
        return "org.postgresql.Driver";
    }

    @Override
    public Integer getDefaultPort() {
        return 5432;
    }

    @Override
    public boolean requiresDatabase() {
        return true;
    }

    @Override
    public boolean requiresSchema() {
        return true;  // PostgreSQL建议使用Schema
    }

    /**
     * PostgreSQL使用schema，catalog为null
     * 如果没有指定schema，默认使用"public"
     */
    @Override
    protected String normalizeSchemaName(DatabaseMetaData metaData, String schemaName) throws SQLException {
        return schemaName != null && !schemaName.isEmpty() ? schemaName : "public";
    }

    /**
     * PostgreSQL中catalog为null
     */
    @Override
    protected String normalizeCatalogName(DatabaseMetaData metaData, String catalog) throws SQLException {
        return null; // PostgreSQL不使用catalog
    }
}

