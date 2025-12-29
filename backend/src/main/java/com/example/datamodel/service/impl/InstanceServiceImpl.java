package com.example.datamodel.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.datamodel.core.DynamicTableManager;
import com.example.datamodel.core.Validator;
import com.example.datamodel.core.datasource.DataSourceConnectionManager;
import com.example.datamodel.dto.InstanceDTO;
import com.example.datamodel.entity.DataSource;
import com.example.datamodel.entity.ObjectType;
import com.example.datamodel.entity.Property;
import com.example.datamodel.exception.BusinessException;
import com.example.datamodel.service.InstanceService;
import com.example.datamodel.service.ObjectTypeService;
import com.example.datamodel.service.PropertyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.datamodel.utils.UUIDUtils;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 实例服务实现类
 *
 * @author DataModel Team
 */
@Slf4j
@Service
public class InstanceServiceImpl implements InstanceService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ObjectTypeService objectTypeService;

    @Autowired
    private PropertyService propertyService;

    @Autowired
    private DynamicTableManager dynamicTableManager;

    @Autowired
    private Validator validator;

    @Autowired
    private com.example.datamodel.service.ObjectTypeDataSourceService objectTypeDataSourceService;

    @Autowired
    private DataSourceConnectionManager dataSourceConnectionManager;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> createInstance(String objectTypeName, InstanceDTO dto) {
        // 获取对象类型
        ObjectType objectType = objectTypeService.getByName(objectTypeName);
        if (objectType == null) {
            throw new BusinessException("对象类型不存在：" + objectTypeName);
        }

        // 获取属性定义
        List<Property> properties = propertyService.getByObjectTypeId(objectType.getId());

        // 验证实例数据
        validator.validateInstance(dto.getProperties(), properties);

        // 确定存储位置：优先使用存储库，否则使用平台存储
        com.example.datamodel.dto.ObjectTypeDataSourceTable storageDatasource = null;
        UUID storageDatasourceId = dto.getStorageDatasourceId();
        
        if (storageDatasourceId != null) {
            // 如果指定了存储库ID，查找该存储库
            List<com.example.datamodel.dto.ObjectTypeDataSourceTable> storages = 
                objectTypeDataSourceService.getStorageDatasourcesByObjectTypeId(objectType.getId());
            storageDatasource = storages.stream()
                .filter(s -> s.getId().equals(storageDatasourceId))
                .findFirst()
                .orElse(null);
            if (storageDatasource == null) {
                throw new BusinessException("指定的存储库不存在或不属于该对象类型：" + storageDatasourceId);
            }
        } else {
            // 如果没有指定存储库，尝试获取默认存储库
            try {
                storageDatasource = objectTypeDataSourceService.getDefaultStorageDatasource(objectType.getId());
            } catch (Exception e) {
                log.warn("获取默认存储库失败: {}", e.getMessage());
            }
        }

        // 确定存储位置并创建表（如果需要）
        boolean useStorageDatasource = storageDatasource != null && storageDatasource.getIsStorage() != null && storageDatasource.getIsStorage();
        String tableName = dynamicTableManager.generateInstanceTableName(objectType);
        
        Map<String, Object> result;
        if (useStorageDatasource && storageDatasource != null) {
            // 使用存储库存储
            com.example.datamodel.entity.DataSource datasource = storageDatasource.getDatasource();
            if (datasource == null) {
                throw new BusinessException("存储库数据源信息不存在");
            }
            
            // 确保表在存储库中存在
            try (Connection connection = dataSourceConnectionManager.getConnection(datasource)) {
                if (!dynamicTableManager.tableExistsInDatasource(connection, datasource, tableName)) {
                    dynamicTableManager.createInstanceTableInDatasource(datasource, tableName, objectType, properties);
                }
            } catch (java.sql.SQLException e) {
                throw new BusinessException("连接存储库失败：" + e.getMessage());
            }
            
            // 在存储库中创建实例
            result = createInstanceInDatasource(datasource, tableName, objectType, properties, dto);
        } else {
            // 使用平台存储
            if (!dynamicTableManager.tableExists(objectType)) {
                dynamicTableManager.createInstanceTable(objectType, properties);
            }
            
            // 在平台数据库中创建实例
            result = createInstanceInPlatform(objectType, tableName, properties, dto);
        }
        
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> updateInstance(String objectTypeName, UUID instanceId, InstanceDTO dto) {
        ObjectType objectType = objectTypeService.getByName(objectTypeName);
        if (objectType == null) {
            throw new BusinessException("对象类型不存在：" + objectTypeName);
        }

        // 获取属性定义
        List<Property> properties = propertyService.getByObjectTypeId(objectType.getId());
        
        // 验证实例数据
        if (dto.getProperties() != null) {
            validator.validateInstance(dto.getProperties(), properties);
        }

        // 确定实例的存储位置
        com.example.datamodel.dto.ObjectTypeDataSourceTable storage = determineInstanceStorage(objectType, instanceId);
        String tableName = dynamicTableManager.generateInstanceTableName(objectType);

        if (storage != null && storage.getIsStorage() != null && storage.getIsStorage()) {
            // 在存储库中更新实例
            return updateInstanceInDatasource(storage.getDatasource(), tableName, instanceId, properties, dto);
        } else {
            // 在平台存储中更新实例
            return updateInstanceInPlatform(objectType, tableName, instanceId, properties, dto);
        }
    }

    @Override
    public Map<String, Object> getInstance(String objectTypeName, UUID instanceId) {
        ObjectType objectType = objectTypeService.getByName(objectTypeName);
        if (objectType == null) {
            throw new BusinessException("对象类型不存在：" + objectTypeName);
        }

        String tableName = dynamicTableManager.generateInstanceTableName(objectType);
        List<Property> properties = propertyService.getByObjectTypeId(objectType.getId());

        // 首先尝试从平台存储查询（为了获取 datasource_id）
        Map<String, Object> instance = getInstanceFromPlatform(objectType, tableName, instanceId);
        if (instance != null) {
            // 检查是否有 datasource_id，如果有则从存储库查询
            Object datasourceIdObj = instance.get("datasource_id");
            if (datasourceIdObj != null) {
                UUID datasourceId = UUIDUtils.parseUUID(datasourceIdObj);
                // 查找对应的存储库
                com.example.datamodel.dto.ObjectTypeDataSourceTable storage = findStorageByDatasourceId(objectType.getId(), datasourceId);
                if (storage != null && storage.getIsStorage() != null && storage.getIsStorage()) {
                    // 从存储库查询
                    return getInstanceFromDatasource(storage.getDatasource(), tableName, instanceId, properties);
                }
            }
            // 从平台存储返回
            instance.remove("class_id");
            return instance;
        }

        // 如果平台存储中不存在，尝试从所有存储库中查找
        List<com.example.datamodel.dto.ObjectTypeDataSourceTable> storages = 
            objectTypeDataSourceService.getStorageDatasourcesByObjectTypeId(objectType.getId());
        for (com.example.datamodel.dto.ObjectTypeDataSourceTable storage : storages) {
            if (storage.getIsStorage() != null && storage.getIsStorage()) {
                Map<String, Object> result = getInstanceFromDatasource(storage.getDatasource(), tableName, instanceId, properties);
                if (result != null) {
                    return result;
                }
            }
        }

        return null;
    }

    @Override
    public IPage<Map<String, Object>> listInstances(String objectTypeName, Long current, Long size, Map<String, Object> filters) {
        ObjectType objectType = objectTypeService.getByName(objectTypeName);
        if (objectType == null) {
            throw new BusinessException("对象类型不存在：" + objectTypeName);
        }

        // 获取属性定义
        List<Property> properties = propertyService.getByObjectTypeId(objectType.getId());
        String tableName = dynamicTableManager.generateInstanceTableName(objectType);

        // 收集所有实例（平台存储 + 所有存储库）
        List<Map<String, Object>> allRecords = new ArrayList<>();

        // 1. 从平台存储查询
        List<Map<String, Object>> platformRecords = listInstancesFromPlatform(objectType, tableName, properties, filters);
        allRecords.addAll(platformRecords);

        // 2. 从所有存储库查询
        List<com.example.datamodel.dto.ObjectTypeDataSourceTable> storages = 
            objectTypeDataSourceService.getStorageDatasourcesByObjectTypeId(objectType.getId());
        for (com.example.datamodel.dto.ObjectTypeDataSourceTable storage : storages) {
            if (storage.getIsStorage() != null && storage.getIsStorage()) {
                try {
                    List<Map<String, Object>> datasourceRecords = listInstancesFromDatasource(
                        storage.getDatasource(), tableName, properties, filters);
                    allRecords.addAll(datasourceRecords);
                } catch (Exception e) {
                    log.warn("从存储库 {} 查询实例列表失败：{}", storage.getDatasource().getName(), e.getMessage());
                    // 继续查询其他存储库
                }
            }
        }

        // 按创建时间排序（降序）
        allRecords.sort((a, b) -> {
            Object timeA = a.get("created_at");
            Object timeB = b.get("created_at");
            if (timeA == null && timeB == null) return 0;
            if (timeA == null) return 1;
            if (timeB == null) return -1;
            // 简单比较，实际应该转换为LocalDateTime比较
            return timeB.toString().compareTo(timeA.toString());
        });

        // 分页处理
        long total = allRecords.size();
        int fromIndex = (int) ((current - 1) * size);
        int toIndex = (int) Math.min(fromIndex + size, total);
        List<Map<String, Object>> pagedRecords = fromIndex < total ? allRecords.subList(fromIndex, toIndex) : new ArrayList<>();

        // 移除class_id字段
        for (Map<String, Object> record : pagedRecords) {
            record.remove("class_id");
        }

        Page<Map<String, Object>> page = new Page<>(current, size, total);
        page.setRecords(pagedRecords);
        return page;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteInstance(String objectTypeName, UUID instanceId) {
        ObjectType objectType = objectTypeService.getByName(objectTypeName);
        if (objectType == null) {
            throw new BusinessException("对象类型不存在：" + objectTypeName);
        }

        // 确定实例的存储位置
        com.example.datamodel.dto.ObjectTypeDataSourceTable storage = determineInstanceStorage(objectType, instanceId);
        String tableName = dynamicTableManager.generateInstanceTableName(objectType);

        if (storage != null && storage.getIsStorage() != null && storage.getIsStorage()) {
            // 从存储库中删除实例
            deleteInstanceFromDatasource(storage.getDatasource(), tableName, instanceId);
        } else {
            // 从平台存储中删除实例
            deleteInstanceFromPlatform(objectType, tableName, instanceId);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDeleteInstances(String objectTypeName, List<UUID> instanceIds) {
        ObjectType objectType = objectTypeService.getByName(objectTypeName);
        if (objectType == null) {
            throw new BusinessException("对象类型不存在：" + objectTypeName);
        }

        if (instanceIds == null || instanceIds.isEmpty()) {
            return;
        }

        // 按存储位置分组删除
        Map<com.example.datamodel.dto.ObjectTypeDataSourceTable, List<UUID>> storageGroups = new HashMap<>();
        List<UUID> platformIds = new ArrayList<>();

        // 确定每个实例的存储位置
        for (UUID instanceId : instanceIds) {
            com.example.datamodel.dto.ObjectTypeDataSourceTable storage = determineInstanceStorage(objectType, instanceId);
            if (storage != null && storage.getIsStorage() != null && storage.getIsStorage()) {
                storageGroups.computeIfAbsent(storage, k -> new ArrayList<>()).add(instanceId);
            } else {
                platformIds.add(instanceId);
            }
        }

        // 从平台存储删除
        if (!platformIds.isEmpty()) {
            batchDeleteInstancesFromPlatform(objectType, platformIds);
        }

        // 从各个存储库删除
        String tableName = dynamicTableManager.generateInstanceTableName(objectType);
        for (Map.Entry<com.example.datamodel.dto.ObjectTypeDataSourceTable, List<UUID>> entry : storageGroups.entrySet()) {
            batchDeleteInstancesFromDatasource(entry.getKey().getDatasource(), tableName, entry.getValue());
        }

        log.info("批量删除实例成功：{} - {} 条", objectTypeName, instanceIds.size());
    }

    /**
     * 在平台数据库中创建实例
     */
    private Map<String, Object> createInstanceInPlatform(ObjectType objectType, String tableName,
                                                          List<Property> properties, InstanceDTO dto) {
        // 构建插入SQL
        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO ").append(tableName).append(" (id, class_id, created_at, updated_at");
        
        List<Object> params = new ArrayList<>();
        UUID id = UUID.randomUUID();
        params.add(id);
        params.add(objectType.getId());  // class_id
        params.add(LocalDateTime.now());
        params.add(LocalDateTime.now());

        // 添加属性字段
        for (Property property : properties) {
            Object value = dto.getProperties() != null ? dto.getProperties().get(property.getName()) : null;
            if (value != null) {
                sql.append(", ").append(property.getName().toLowerCase());
                params.add(value);
            }
        }
        
        sql.append(") VALUES (");
        for (int i = 0; i < params.size(); i++) {
            if (i > 0) {
                sql.append(", ");
            }
            sql.append("?");
        }
        sql.append(")");

        jdbcTemplate.update(sql.toString(), params.toArray());
        log.info("在平台数据库中创建实例成功：{} - {}", objectType.getName(), id);

        // 返回创建的实例
        return getInstance(objectType.getName(), id);
    }

    /**
     * 在数据源中创建实例
     */
    private Map<String, Object> createInstanceInDatasource(DataSource datasource, String tableName,
                                                            ObjectType objectType, List<Property> properties,
                                                            InstanceDTO dto) {
        UUID id = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        
        try (Connection connection = dataSourceConnectionManager.getConnection(datasource)) {
            // 构建插入SQL（注意：不同数据库的SQL语法可能不同）
            StringBuilder sql = new StringBuilder();
            sql.append("INSERT INTO ").append(escapeTableName(datasource.getType(), tableName))
               .append(" (id, class_id, created_at, updated_at");
            
            List<Object> params = new ArrayList<>();
            params.add(id.toString());  // 使用字符串格式的UUID
            params.add(objectType.getId().toString());
            params.add(now);
            params.add(now);

            // 添加属性字段
            for (Property property : properties) {
                Object value = dto.getProperties() != null ? dto.getProperties().get(property.getName()) : null;
                if (value != null) {
                    sql.append(", ").append(escapeTableName(datasource.getType(), property.getName().toLowerCase()));
                    params.add(value);
                }
            }
            
            sql.append(") VALUES (");
            for (int i = 0; i < params.size(); i++) {
                if (i > 0) {
                    sql.append(", ");
                }
                sql.append("?");
            }
            sql.append(")");

            try (PreparedStatement stmt = connection.prepareStatement(sql.toString())) {
                for (int i = 0; i < params.size(); i++) {
                    stmt.setObject(i + 1, params.get(i));
                }
                stmt.executeUpdate();
                log.info("在数据源 {} 中创建实例成功：{} - {}", datasource.getName(), objectType.getName(), id);
            }
            
            // 返回创建的实例
            Map<String, Object> instance = new HashMap<>();
            instance.put("id", id.toString());
            instance.put("class_id", objectType.getId().toString());
            instance.put("created_at", now);
            instance.put("updated_at", now);
            if (dto.getProperties() != null) {
                instance.putAll(dto.getProperties());
            }
            return instance;
        } catch (Exception e) {
            log.error("在数据源中创建实例失败", e);
            throw new BusinessException("在数据源中创建实例失败：" + e.getMessage());
        }
    }

    /**
     * 从平台存储查询实例
     */
    private Map<String, Object> getInstanceFromPlatform(ObjectType objectType, String tableName, UUID instanceId) {
        // 确保表存在，如果不存在则创建（空表）
        if (!dynamicTableManager.tableExists(objectType)) {
            dynamicTableManager.createInstanceTable(objectType, propertyService.getByObjectTypeId(objectType.getId()));
            return null; // 表刚创建，返回null
        }

        String sql = "SELECT * FROM " + tableName + " WHERE id = ?";
        try {
            return jdbcTemplate.queryForMap(sql, instanceId);
        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            return null;
        }
    }

    /**
     * 从数据源查询实例
     */
    private Map<String, Object> getInstanceFromDatasource(DataSource datasource, String tableName, UUID instanceId, List<Property> properties) {
        try (Connection connection = dataSourceConnectionManager.getConnection(datasource)) {
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT * FROM ").append(escapeTableName(datasource.getType(), tableName))
               .append(" WHERE id = ?");

            try (PreparedStatement stmt = connection.prepareStatement(sql.toString())) {
                stmt.setString(1, instanceId.toString());
                
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        Map<String, Object> result = new HashMap<>();
                        ResultSetMetaData metaData = rs.getMetaData();
                        int columnCount = metaData.getColumnCount();
                        
                        for (int i = 1; i <= columnCount; i++) {
                            String columnName = metaData.getColumnName(i).toLowerCase();
                            Object value = rs.getObject(i);
                            result.put(columnName, value);
                        }
                        
                        // 移除class_id字段
                        result.remove("class_id");
                        return result;
                    }
                }
            }
        } catch (Exception e) {
            log.error("从数据源查询实例失败：{}", datasource.getName(), e);
            // 查询失败时返回null，不抛出异常（可能是实例不存在或连接失败）
            return null;
        }
        return null;
    }

    /**
     * 根据数据源ID查找存储库
     */
    private com.example.datamodel.dto.ObjectTypeDataSourceTable findStorageByDatasourceId(UUID objectTypeId, UUID datasourceId) {
        List<com.example.datamodel.dto.ObjectTypeDataSourceTable> storages = 
            objectTypeDataSourceService.getStorageDatasourcesByObjectTypeId(objectTypeId);
        return storages.stream()
            .filter(s -> s.getDatasource() != null && s.getDatasource().getId().equals(datasourceId))
            .findFirst()
            .orElse(null);
    }

    /**
     * 确定实例的存储位置
     * 
     * @param objectType 对象类型
     * @param instanceId 实例ID
     * @return 存储库信息，如果存储在平台则返回null
     */
    private com.example.datamodel.dto.ObjectTypeDataSourceTable determineInstanceStorage(ObjectType objectType, UUID instanceId) {
        String tableName = dynamicTableManager.generateInstanceTableName(objectType);
        
        // 先从平台存储查询，获取 datasource_id
        Map<String, Object> instance = getInstanceFromPlatform(objectType, tableName, instanceId);
        if (instance != null) {
            Object datasourceIdObj = instance.get("datasource_id");
            if (datasourceIdObj != null) {
                UUID datasourceId = UUIDUtils.parseUUID(datasourceIdObj);
                return findStorageByDatasourceId(objectType.getId(), datasourceId);
            }
        }
        
        // 如果平台存储中不存在，尝试从所有存储库中查找
        List<com.example.datamodel.dto.ObjectTypeDataSourceTable> storages = 
            objectTypeDataSourceService.getStorageDatasourcesByObjectTypeId(objectType.getId());
        for (com.example.datamodel.dto.ObjectTypeDataSourceTable storage : storages) {
            if (storage.getIsStorage() != null && storage.getIsStorage()) {
                List<Property> properties = propertyService.getByObjectTypeId(objectType.getId());
                Map<String, Object> result = getInstanceFromDatasource(storage.getDatasource(), tableName, instanceId, properties);
                if (result != null) {
                    return storage;
                }
            }
        }
        
        return null;
    }

    /**
     * 在平台存储中更新实例
     */
    private Map<String, Object> updateInstanceInPlatform(ObjectType objectType, String tableName, UUID instanceId, 
                                                          List<Property> properties, InstanceDTO dto) {
        // 确保表存在
        if (!dynamicTableManager.tableExists(objectType)) {
            dynamicTableManager.createInstanceTable(objectType, properties);
            throw new BusinessException("实例不存在，表刚被创建");
        }

        // 构建更新SQL
        StringBuilder sql = new StringBuilder();
        sql.append("UPDATE ").append(tableName).append(" SET updated_at = ?");
        
        List<Object> params = new ArrayList<>();
        params.add(LocalDateTime.now());

        // 如果指定了存储库ID，添加到UPDATE语句中
        if (dto.getStorageDatasourceId() != null) {
            // 查找存储库，获取数据源ID
            // 需要通过 objectTypeId 和 storageDatasourceId 查找存储库
            List<com.example.datamodel.dto.ObjectTypeDataSourceTable> storages = 
                objectTypeDataSourceService.getStorageDatasourcesByObjectTypeId(objectType.getId());
            com.example.datamodel.dto.ObjectTypeDataSourceTable storage = storages.stream()
                .filter(s -> s.getId().equals(dto.getStorageDatasourceId()))
                .findFirst()
                .orElse(null);
            if (storage != null && storage.getDatasource() != null) {
                sql.append(", datasource_id = ?");
                params.add(storage.getDatasource().getId());
            }
        }

        // 添加属性字段更新
        if (dto.getProperties() != null) {
            for (Property property : properties) {
                if (dto.getProperties().containsKey(property.getName())) {
                    sql.append(", ").append(property.getName().toLowerCase()).append(" = ?");
                    params.add(dto.getProperties().get(property.getName()));
                }
            }
        }
        
        sql.append(" WHERE id = ?");
        params.add(instanceId);

        int updated = jdbcTemplate.update(sql.toString(), params.toArray());
        if (updated == 0) {
            throw new BusinessException("实例不存在：" + instanceId);
        }

        log.info("在平台存储中更新实例成功：{} - {}", objectType.getName(), instanceId);
        return getInstance(objectType.getName(), instanceId);
    }

    /**
     * 在数据源中更新实例
     */
    private Map<String, Object> updateInstanceInDatasource(DataSource datasource, String tableName, UUID instanceId,
                                                            List<Property> properties, InstanceDTO dto) {
        try (Connection connection = dataSourceConnectionManager.getConnection(datasource)) {
            // 构建更新SQL
            StringBuilder sql = new StringBuilder();
            sql.append("UPDATE ").append(escapeTableName(datasource.getType(), tableName))
               .append(" SET updated_at = ?");
            
            List<Object> params = new ArrayList<>();
            params.add(LocalDateTime.now());

            // 添加属性字段更新
            if (dto.getProperties() != null) {
                for (Property property : properties) {
                    if (dto.getProperties().containsKey(property.getName())) {
                        sql.append(", ").append(escapeTableName(datasource.getType(), property.getName().toLowerCase())).append(" = ?");
                        params.add(dto.getProperties().get(property.getName()));
                    }
                }
            }
            
            sql.append(" WHERE id = ?");
            params.add(instanceId.toString());

            try (PreparedStatement stmt = connection.prepareStatement(sql.toString())) {
                for (int i = 0; i < params.size(); i++) {
                    stmt.setObject(i + 1, params.get(i));
                }
                int updated = stmt.executeUpdate();
                if (updated == 0) {
                    throw new BusinessException("实例不存在：" + instanceId);
                }
                log.info("在数据源 {} 中更新实例成功：{}", datasource.getName(), instanceId);
            }
            
            // 返回更新后的实例
            return getInstanceFromDatasource(datasource, tableName, instanceId, properties);
        } catch (Exception e) {
            log.error("在数据源中更新实例失败", e);
            throw new BusinessException("在数据源中更新实例失败：" + e.getMessage());
        }
    }

    /**
     * 从平台存储查询实例列表
     */
    private List<Map<String, Object>> listInstancesFromPlatform(ObjectType objectType, String tableName, 
                                                                 List<Property> properties, Map<String, Object> filters) {
        // 确保表存在，如果不存在则创建（空表）
        if (!dynamicTableManager.tableExists(objectType)) {
            dynamicTableManager.createInstanceTable(objectType, properties);
            return new ArrayList<>(); // 表刚创建，返回空列表
        }

        // 构建查询SQL
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * FROM ").append(tableName).append(" WHERE 1=1");
        
        List<Object> params = new ArrayList<>();
        
        // 添加过滤条件
        if (filters != null && !filters.isEmpty()) {
            for (Property property : properties) {
                if (filters.containsKey(property.getName())) {
                    sql.append(" AND ").append(property.getName().toLowerCase()).append(" = ?");
                    params.add(filters.get(property.getName()));
                }
            }
        }

        sql.append(" ORDER BY created_at DESC");

        List<Map<String, Object>> records = jdbcTemplate.queryForList(sql.toString(), params.toArray());
        return records;
    }

    /**
     * 从数据源查询实例列表
     */
    private List<Map<String, Object>> listInstancesFromDatasource(DataSource datasource, String tableName,
                                                                    List<Property> properties, Map<String, Object> filters) {
        List<Map<String, Object>> records = new ArrayList<>();
        
        try (Connection connection = dataSourceConnectionManager.getConnection(datasource)) {
            // 构建查询SQL
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT * FROM ").append(escapeTableName(datasource.getType(), tableName)).append(" WHERE 1=1");
            
            List<Object> params = new ArrayList<>();
            
            // 添加过滤条件
            if (filters != null && !filters.isEmpty()) {
                for (Property property : properties) {
                    if (filters.containsKey(property.getName())) {
                        sql.append(" AND ").append(escapeTableName(datasource.getType(), property.getName().toLowerCase())).append(" = ?");
                        params.add(filters.get(property.getName()));
                    }
                }
            }

            sql.append(" ORDER BY created_at DESC");

            try (PreparedStatement stmt = connection.prepareStatement(sql.toString())) {
                for (int i = 0; i < params.size(); i++) {
                    stmt.setObject(i + 1, params.get(i));
                }
                
                try (ResultSet rs = stmt.executeQuery()) {
                    ResultSetMetaData metaData = rs.getMetaData();
                    int columnCount = metaData.getColumnCount();
                    
                    while (rs.next()) {
                        Map<String, Object> record = new HashMap<>();
                        for (int i = 1; i <= columnCount; i++) {
                            String columnName = metaData.getColumnName(i).toLowerCase();
                            Object value = rs.getObject(i);
                            record.put(columnName, value);
                        }
                        records.add(record);
                    }
                }
            }
        } catch (Exception e) {
            log.error("从数据源查询实例列表失败：{}", datasource.getName(), e);
            throw new BusinessException("从数据源查询实例列表失败：" + e.getMessage());
        }
        
        return records;
    }

    /**
     * 从平台存储中删除实例
     */
    private void deleteInstanceFromPlatform(ObjectType objectType, String tableName, UUID instanceId) {
        // 如果表不存在，直接返回（没有数据可删除）
        if (!dynamicTableManager.tableExists(objectType)) {
            log.warn("实例表不存在，无需删除：{}", objectType.getName());
            return;
        }

        String sql = "DELETE FROM " + tableName + " WHERE id = ?";
        int deleted = jdbcTemplate.update(sql, instanceId);
        if (deleted == 0) {
            throw new BusinessException("实例不存在：" + instanceId);
        }

        log.info("从平台存储中删除实例成功：{} - {}", objectType.getName(), instanceId);
    }

    /**
     * 从数据源中删除实例
     */
    private void deleteInstanceFromDatasource(DataSource datasource, String tableName, UUID instanceId) {
        try (Connection connection = dataSourceConnectionManager.getConnection(datasource)) {
            StringBuilder sql = new StringBuilder();
            sql.append("DELETE FROM ").append(escapeTableName(datasource.getType(), tableName))
               .append(" WHERE id = ?");

            try (PreparedStatement stmt = connection.prepareStatement(sql.toString())) {
                stmt.setString(1, instanceId.toString());
                int deleted = stmt.executeUpdate();
                if (deleted == 0) {
                    throw new BusinessException("实例不存在：" + instanceId);
                }
                log.info("从数据源 {} 中删除实例成功：{}", datasource.getName(), instanceId);
            }
        } catch (Exception e) {
            log.error("从数据源中删除实例失败", e);
            throw new BusinessException("从数据源中删除实例失败：" + e.getMessage());
        }
    }

    /**
     * 从平台存储批量删除实例
     */
    private void batchDeleteInstancesFromPlatform(ObjectType objectType, List<UUID> instanceIds) {
        String tableName = dynamicTableManager.generateInstanceTableName(objectType);
        // 如果表不存在，直接返回（没有数据可删除）
        if (!dynamicTableManager.tableExists(objectType)) {
            log.warn("实例表不存在，无需删除：{}", objectType.getName());
            return;
        }

        String placeholders = String.join(",", Collections.nCopies(instanceIds.size(), "?"));
        String sql = "DELETE FROM " + tableName + " WHERE id IN (" + placeholders + ")";
        jdbcTemplate.update(sql, instanceIds.toArray());
        log.info("从平台存储批量删除实例成功：{} - {} 条", objectType.getName(), instanceIds.size());
    }

    /**
     * 从数据源批量删除实例
     */
    private void batchDeleteInstancesFromDatasource(DataSource datasource, String tableName, List<UUID> instanceIds) {
        try (Connection connection = dataSourceConnectionManager.getConnection(datasource)) {
            String placeholders = String.join(",", Collections.nCopies(instanceIds.size(), "?"));
            StringBuilder sql = new StringBuilder();
            sql.append("DELETE FROM ").append(escapeTableName(datasource.getType(), tableName))
               .append(" WHERE id IN (").append(placeholders).append(")");

            try (PreparedStatement stmt = connection.prepareStatement(sql.toString())) {
                for (int i = 0; i < instanceIds.size(); i++) {
                    stmt.setString(i + 1, instanceIds.get(i).toString());
                }
                int deleted = stmt.executeUpdate();
                log.info("从数据源 {} 批量删除实例成功：{} 条", datasource.getName(), deleted);
            }
        } catch (Exception e) {
            log.error("从数据源批量删除实例失败", e);
            throw new BusinessException("从数据源批量删除实例失败：" + e.getMessage());
        }
    }

    /**
     * 转义表名/字段名（根据数据库类型）
     */
    private String escapeTableName(String datasourceType, String name) {
        if ("MYSQL".equalsIgnoreCase(datasourceType) || "MARIADB".equalsIgnoreCase(datasourceType)) {
            return "`" + name + "`";
        } else if ("POSTGRESQL".equalsIgnoreCase(datasourceType)) {
            return "\"" + name + "\"";
        } else if ("SQL_SERVER".equalsIgnoreCase(datasourceType) || "SQLSERVER".equalsIgnoreCase(datasourceType)) {
            return "[" + name + "]";
        } else if ("ORACLE".equalsIgnoreCase(datasourceType)) {
            return "\"" + name.toUpperCase() + "\"";
        } else {
            return name;
        }
    }
}


