package com.example.datamodel.core.datasource.impl;

import com.example.datamodel.core.datasource.AbstractJdbcConnector;
import com.example.datamodel.entity.DataSource;
import org.springframework.stereotype.Component;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;

/**
 * MySQL数据源连接器
 *
 * @author DataModel Team
 */
@Component
public class MySQLConnector extends AbstractJdbcConnector {

    @Override
    public String getTypeCode() {
        return "MYSQL";
    }

    @Override
    public String getDisplayName() {
        return "MySQL";
    }

    @Override
    public String buildConnectionUrl(DataSource datasource) {
        return String.format("jdbc:mysql://%s:%d/%s?useSSL=false&serverTimezone=UTC",
            datasource.getHost(),
            datasource.getPort(),
            datasource.getDatabaseName() != null ? datasource.getDatabaseName() : "");
    }

    @Override
    public String getDriverClass() {
        return "com.mysql.cj.jdbc.Driver";
    }

    @Override
    public Integer getDefaultPort() {
        return 3306;
    }

    @Override
    public boolean requiresDatabase() {
        return true;
    }

    @Override
    public boolean requiresSchema() {
        return false;
    }

    /**
     * MySQL中，catalog = database，schema = null
     * 所以normalizeSchemaName返回null
     */
    @Override
    protected String normalizeSchemaName(DatabaseMetaData metaData, String schemaName) throws SQLException {
        return null; // MySQL不使用schema，使用catalog（database）
    }

    /**
     * MySQL使用catalog（即databaseName），需要从DataSource中获取
     */
    @Override
    protected String normalizeCatalogName(DatabaseMetaData metaData, String catalog) throws SQLException {
        // MySQL中catalog就是databaseName，如果连接时已指定，则使用连接的catalog
        return catalog;
    }
}

