package com.example.datamodel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.datamodel.core.datasource.DataSourceConnectionManager;
import com.example.datamodel.core.datasource.DataSourceConnector;
import com.example.datamodel.core.datasource.DataSourceConnectorRegistry;
import com.example.datamodel.core.datasource.TableColumnInfo;
import com.example.datamodel.entity.DataSource;
import com.example.datamodel.exception.BusinessException;
import com.example.datamodel.mapper.DataSourceMapper;
import com.example.datamodel.service.DataSourceService;
import com.example.datamodel.utils.PasswordUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 数据源服务实现类
 *
 * @author DataModel Team
 */
@Slf4j
@Service
public class DataSourceServiceImpl extends ServiceImpl<DataSourceMapper, DataSource> implements DataSourceService {

    @Autowired
    private DataSourceConnectionManager connectionManager;

    @Autowired
    private DataSourceConnectorRegistry connectorRegistry;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DataSource createDataSource(DataSource datasource) {
        // 检查代码是否已存在
        LambdaQueryWrapper<DataSource> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DataSource::getCode, datasource.getCode());
        if (count(wrapper) > 0) {
            throw new BusinessException("数据源代码已存在：" + datasource.getCode());
        }

        // 加密密码
        if (datasource.getPassword() != null && !datasource.getPassword().isEmpty()) {
            String encryptedPassword = PasswordUtils.encrypt(datasource.getPassword());
            datasource.setPassword(PasswordUtils.addEncryptionPrefix(encryptedPassword));
        }

        // 设置默认值
        if (datasource.getEnabled() == null) {
            datasource.setEnabled(true);
        }
        if (datasource.getStatus() == null) {
            datasource.setStatus("ACTIVE");
        }
        if (datasource.getMaxConnections() == null) {
            datasource.setMaxConnections(10);
        }
        if (datasource.getMinConnections() == null) {
            datasource.setMinConnections(2);
        }
        if (datasource.getConnectionTimeout() == null) {
            datasource.setConnectionTimeout(30);
        }
        if (datasource.getMetadata() == null) {
            datasource.setMetadata("{}");
        }

        datasource.setCreatedAt(LocalDateTime.now());
        datasource.setUpdatedAt(LocalDateTime.now());

        save(datasource);
        log.info("创建数据源成功：{}", datasource.getCode());
        return datasource;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DataSource updateDataSource(UUID id, DataSource datasource) {
        DataSource existing = getById(id);
        if (existing == null) {
            throw new BusinessException("数据源不存在：" + id);
        }

        // 检查代码是否已被其他数据源使用
        if (datasource.getCode() != null && !datasource.getCode().equals(existing.getCode())) {
            LambdaQueryWrapper<DataSource> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(DataSource::getCode, datasource.getCode());
            wrapper.ne(DataSource::getId, id);
            if (count(wrapper) > 0) {
                throw new BusinessException("数据源代码已被使用：" + datasource.getCode());
            }
            existing.setCode(datasource.getCode());
        }

        // 更新字段
        if (datasource.getName() != null) {
            existing.setName(datasource.getName());
        }
        if (datasource.getType() != null) {
            existing.setType(datasource.getType());
        }
        if (datasource.getDescription() != null) {
            existing.setDescription(datasource.getDescription());
        }
        if (datasource.getHost() != null) {
            existing.setHost(datasource.getHost());
        }
        if (datasource.getPort() != null) {
            existing.setPort(datasource.getPort());
        }
        if (datasource.getDatabaseName() != null) {
            existing.setDatabaseName(datasource.getDatabaseName());
        }
        if (datasource.getSchemaName() != null) {
            existing.setSchemaName(datasource.getSchemaName());
        }
        if (datasource.getUsername() != null) {
            existing.setUsername(datasource.getUsername());
        }
        // 密码：如果提供了新密码，则加密并更新；否则保留原密码
        if (datasource.getPassword() != null && !datasource.getPassword().isEmpty()) {
            // 如果密码不以ENC:开头，说明是明文，需要加密
            if (!PasswordUtils.isEncrypted(datasource.getPassword())) {
                String encryptedPassword = PasswordUtils.encrypt(datasource.getPassword());
                existing.setPassword(PasswordUtils.addEncryptionPrefix(encryptedPassword));
            } else {
                existing.setPassword(datasource.getPassword());
            }
        }
        if (datasource.getConnectionUrl() != null) {
            existing.setConnectionUrl(datasource.getConnectionUrl());
        }
        if (datasource.getMaxConnections() != null) {
            existing.setMaxConnections(datasource.getMaxConnections());
        }
        if (datasource.getMinConnections() != null) {
            existing.setMinConnections(datasource.getMinConnections());
        }
        if (datasource.getConnectionTimeout() != null) {
            existing.setConnectionTimeout(datasource.getConnectionTimeout());
        }
        if (datasource.getEnabled() != null) {
            existing.setEnabled(datasource.getEnabled());
        }
        if (datasource.getDomainId() != null) {
            existing.setDomainId(datasource.getDomainId());
        }
        if (datasource.getMetadata() != null) {
            existing.setMetadata(datasource.getMetadata());
        }

        existing.setUpdatedAt(LocalDateTime.now());

        updateById(existing);
        log.info("更新数据源成功：{}", existing.getCode());
        return existing;
    }

    @Override
    public DataSource getDataSourceById(UUID id) {
        DataSource datasource = getById(id);
        if (datasource == null) {
            return null;
        }
        // 隐藏密码
        datasource.setPassword(null);
        return datasource;
    }

    @Override
    public DataSource getDataSourceByCode(String code) {
        LambdaQueryWrapper<DataSource> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DataSource::getCode, code);
        DataSource datasource = getOne(wrapper);
        if (datasource == null) {
            return null;
        }
        // 隐藏密码
        datasource.setPassword(null);
        return datasource;
    }

    @Override
    public IPage<DataSource> listDataSources(Page<DataSource> page, String type, UUID domainId) {
        LambdaQueryWrapper<DataSource> wrapper = new LambdaQueryWrapper<>();
        if (type != null && !type.isEmpty()) {
            wrapper.eq(DataSource::getType, type);
        }
        if (domainId != null) {
            wrapper.eq(DataSource::getDomainId, domainId);
        }
        wrapper.orderByDesc(DataSource::getCreatedAt);
        
        IPage<DataSource> result = page(page, wrapper);
        // 隐藏密码
        result.getRecords().forEach(ds -> ds.setPassword(null));
        return result;
    }

    @Override
    public boolean testConnection(UUID id) {
        DataSource datasource = getById(id);
        if (datasource == null) {
            throw new BusinessException("数据源不存在：" + id);
        }

        // 解密密码用于测试连接
        String password = datasource.getPassword();
        if (password != null && PasswordUtils.isEncrypted(password)) {
            String encryptedPassword = PasswordUtils.removeEncryptionPrefix(password);
            password = PasswordUtils.decrypt(encryptedPassword);
        }
        
        // 创建临时数据源对象用于测试（不保存到数据库）
        DataSource testDatasource = new DataSource();
        testDatasource.setType(datasource.getType());
        testDatasource.setHost(datasource.getHost());
        testDatasource.setPort(datasource.getPort());
        testDatasource.setDatabaseName(datasource.getDatabaseName());
        testDatasource.setSchemaName(datasource.getSchemaName());
        testDatasource.setUsername(datasource.getUsername());
        testDatasource.setPassword(password);
        testDatasource.setConnectionUrl(datasource.getConnectionUrl());

        try {
            long startTime = System.currentTimeMillis();
            boolean success = connectionManager.testConnection(testDatasource);
            long responseTime = System.currentTimeMillis() - startTime;

            // 更新测试结果
            datasource.setLastTestTime(LocalDateTime.now());
            datasource.setLastTestResult(success ? "SUCCESS" : "FAILED");
            datasource.setLastTestMessage(success ? "连接成功（响应时间：" + responseTime + "ms）" : "连接失败");
            if (!success) {
                datasource.setStatus("ERROR");
            } else if ("ERROR".equals(datasource.getStatus())) {
                datasource.setStatus("ACTIVE");
            }
            datasource.setUpdatedAt(LocalDateTime.now());
            updateById(datasource);

            log.info("数据源连接测试 {}：{}（响应时间：{}ms）", success ? "成功" : "失败", datasource.getCode(), responseTime);
            return success;
        } catch (Exception e) {
            datasource.setLastTestTime(LocalDateTime.now());
            datasource.setLastTestResult("FAILED");
            datasource.setLastTestMessage("连接失败：" + e.getMessage());
            datasource.setStatus("ERROR");
            datasource.setUpdatedAt(LocalDateTime.now());
            updateById(datasource);

            log.error("数据源连接测试失败：{}，错误：{}", datasource.getCode(), e.getMessage());
            return false;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setEnabled(UUID id, Boolean enabled) {
        DataSource datasource = getById(id);
        if (datasource == null) {
            throw new BusinessException("数据源不存在：" + id);
        }
        datasource.setEnabled(enabled);
        datasource.setUpdatedAt(LocalDateTime.now());
        updateById(datasource);
        log.info("设置数据源启用状态：{} -> {}", datasource.getCode(), enabled);
    }

    @Override
    public String buildConnectionUrl(DataSource datasource) {
        return connectionManager.buildConnectionUrl(datasource);
    }

    /**
     * 获取数据源（包含密码，用于内部使用）
     *
     * @param id 数据源ID
     * @return 数据源实体（包含密码）
     */
    public DataSource getDataSourceWithPassword(UUID id) {
        return getById(id);
    }

    @Override
    public List<String> getTableList(UUID datasourceId, String schemaName) {
        DataSource datasource = getDataSourceWithPassword(datasourceId);
        if (datasource == null) {
            throw new BusinessException("数据源不存在：" + datasourceId);
        }

        DataSourceConnector connector = connectorRegistry.getConnector(datasource.getType());
        if (connector == null) {
            throw new BusinessException("不支持的数据源类型：" + datasource.getType());
        }

        try (Connection connection = connector.createConnection(datasource)) {
            return connector.getTableList(connection, schemaName);
        } catch (Exception e) {
            log.error("获取表列表失败：{}", e.getMessage(), e);
            throw new BusinessException("获取表列表失败：" + e.getMessage());
        }
    }

    @Override
    public List<TableColumnInfo> getTableColumns(UUID datasourceId, String schemaName, String tableName) {
        DataSource datasource = getDataSourceWithPassword(datasourceId);
        if (datasource == null) {
            throw new BusinessException("数据源不存在：" + datasourceId);
        }

        DataSourceConnector connector = connectorRegistry.getConnector(datasource.getType());
        if (connector == null) {
            throw new BusinessException("不支持的数据源类型：" + datasource.getType());
        }

        try (Connection connection = connector.createConnection(datasource)) {
            return connector.getTableColumns(connection, schemaName, tableName);
        } catch (Exception e) {
            log.error("获取表字段信息失败：{}", e.getMessage(), e);
            throw new BusinessException("获取表字段信息失败：" + e.getMessage());
        }
    }

    @Override
    public List<String> getSchemaList(UUID datasourceId) {
        DataSource datasource = getDataSourceWithPassword(datasourceId);
        if (datasource == null) {
            throw new BusinessException("数据源不存在：" + datasourceId);
        }

        DataSourceConnector connector = connectorRegistry.getConnector(datasource.getType());
        if (connector == null) {
            throw new BusinessException("不支持的数据源类型：" + datasource.getType());
        }

        if (!connector.requiresSchema()) {
            return new java.util.ArrayList<>(); // 不支持Schema的数据源返回空列表
        }

        try (Connection connection = connector.createConnection(datasource)) {
            return connector.getSchemaList(connection);
        } catch (Exception e) {
            log.error("获取Schema列表失败：{}", e.getMessage(), e);
            throw new BusinessException("获取Schema列表失败：" + e.getMessage());
        }
    }
}

