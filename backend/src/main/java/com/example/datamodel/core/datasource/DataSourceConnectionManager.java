package com.example.datamodel.core.datasource;

import com.example.datamodel.entity.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 数据源连接管理器
 *
 * @author DataModel Team
 */
@Slf4j
@Component
public class DataSourceConnectionManager {

    @Autowired
    private DataSourceConnectorRegistry connectorRegistry;

    /**
     * 获取连接
     *
     * @param datasource 数据源实体
     * @return 连接对象
     * @throws SQLException 连接失败时抛出异常
     */
    public Connection getConnection(DataSource datasource) throws SQLException {
        DataSourceConnector connector = connectorRegistry.getConnector(datasource.getType());
        try {
            return connector.createConnection(datasource);
        } catch (Exception e) {
            throw new SQLException("创建数据源连接失败：" + e.getMessage(), e);
        }
    }

    /**
     * 构建连接URL
     *
     * @param datasource 数据源实体
     * @return 连接URL
     */
    public String buildConnectionUrl(DataSource datasource) {
        // 如果用户提供了完整的URL，直接使用
        if (datasource.getConnectionUrl() != null && !datasource.getConnectionUrl().isEmpty()) {
            return datasource.getConnectionUrl();
        }

        DataSourceConnector connector = connectorRegistry.getConnector(datasource.getType());
        return connector.buildConnectionUrl(datasource);
    }

    /**
     * 测试连接
     *
     * @param datasource 数据源实体
     * @return 连接是否成功
     */
    public boolean testConnection(DataSource datasource) {
        DataSourceConnector connector = connectorRegistry.getConnector(datasource.getType());
        return connector.testConnection(datasource);
    }
}

