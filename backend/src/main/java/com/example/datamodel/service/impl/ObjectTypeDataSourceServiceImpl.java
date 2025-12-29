package com.example.datamodel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.datamodel.dto.ObjectTypeDataSourceTable;
import com.example.datamodel.entity.DataSource;
import com.example.datamodel.entity.ObjectTypeDataSource;
import com.example.datamodel.exception.BusinessException;
import com.example.datamodel.mapper.DataSourceMapper;
import com.example.datamodel.mapper.ObjectTypeDataSourceMapper;
import com.example.datamodel.service.ObjectTypeDataSourceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 对象类型-数据源关联服务实现类
 *
 * @author DataModel Team
 */
@Slf4j
@Service
public class ObjectTypeDataSourceServiceImpl extends ServiceImpl<ObjectTypeDataSourceMapper, ObjectTypeDataSource>
        implements ObjectTypeDataSourceService {

    @Autowired
    private DataSourceMapper dataSourceMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ObjectTypeDataSource addDataSourceTableToObjectType(UUID objectTypeId, UUID datasourceId,
                                                                String tableName, String schemaName,
                                                                Boolean isStorage, Boolean isDefault, Integer priority) {
        // 验证：如果 is_storage = false，表名不能为空
        boolean isStorageValue = isStorage != null && isStorage;
        if (!isStorageValue && (tableName == null || tableName.trim().isEmpty())) {
            throw new BusinessException("非存储库模式时，表名不能为空");
        }
        
        // 如果 is_storage = true，表名可以为空（将由系统生成）
        // 如果 is_storage = false，表名必须指定

        // 检查数据源是否存在
        DataSource datasource = dataSourceMapper.selectById(datasourceId);
        if (datasource == null) {
            throw new BusinessException("数据源不存在：" + datasourceId);
        }

        // 检查关联是否已存在
        // 如果是存储库模式（is_storage=true），同一个对象类型只能有一个存储库关联同一数据源
        // 如果是非存储库模式（is_storage=false），需要检查表名是否重复
        LambdaQueryWrapper<ObjectTypeDataSource> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ObjectTypeDataSource::getObjectTypeId, objectTypeId)
               .eq(ObjectTypeDataSource::getDatasourceId, datasourceId);
        
        if (isStorageValue) {
            // 存储库模式：检查是否已有该数据源的存储库关联
            wrapper.eq(ObjectTypeDataSource::getIsStorage, true);
        } else {
            // 非存储库模式：检查表名是否重复
            wrapper.eq(ObjectTypeDataSource::getTableName, tableName);
            if (schemaName != null && !schemaName.trim().isEmpty()) {
                wrapper.eq(ObjectTypeDataSource::getSchemaName, schemaName);
            } else {
                wrapper.and(w -> w.isNull(ObjectTypeDataSource::getSchemaName)
                               .or()
                               .eq(ObjectTypeDataSource::getSchemaName, ""));
            }
        }
        
        ObjectTypeDataSource existing = getOne(wrapper);
        if (existing != null) {
            if (isStorageValue) {
                throw new BusinessException("该数据源已作为存储库关联到该对象类型");
            } else {
                throw new BusinessException("该数据源表已关联到该对象类型");
            }
        }

        // 如果设置为默认数据源，需要先取消其他默认数据源
        if (isDefault != null && isDefault) {
            LambdaQueryWrapper<ObjectTypeDataSource> defaultWrapper = new LambdaQueryWrapper<>();
            defaultWrapper.eq(ObjectTypeDataSource::getObjectTypeId, objectTypeId)
                         .eq(ObjectTypeDataSource::getIsDefault, true);
            List<ObjectTypeDataSource> defaultDatasources = list(defaultWrapper);
            for (ObjectTypeDataSource ds : defaultDatasources) {
                ds.setIsDefault(false);
                updateById(ds);
            }
        }

        // 创建关联
        ObjectTypeDataSource mapping = new ObjectTypeDataSource();
        mapping.setObjectTypeId(objectTypeId);
        mapping.setDatasourceId(datasourceId);
        mapping.setTableName(isStorageValue ? null : tableName); // 存储库模式时表名为空，由系统生成
        mapping.setSchemaName(schemaName);
        mapping.setIsStorage(isStorageValue);
        mapping.setIsDefault(isDefault != null ? isDefault : false);
        mapping.setPriority(priority != null ? priority : 0);
        mapping.setCreatedAt(LocalDateTime.now());
        mapping.setUpdatedAt(LocalDateTime.now());

        save(mapping);
        log.info("为对象类型 {} 添加数据源表关联成功，isStorage={}, tableName={}", objectTypeId, isStorageValue, tableName);
        return mapping;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeDataSourceTableById(UUID id) {
        ObjectTypeDataSource mapping = getById(id);
        if (mapping == null) {
            throw new BusinessException("关联不存在：" + id);
        }
        removeById(id);
        log.info("移除对象类型 {} 的数据源表关联 {} 成功", mapping.getObjectTypeId(), id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setDefaultDataSourceTable(UUID objectTypeId, UUID id) {
        // 检查关联是否存在
        ObjectTypeDataSource mapping = getById(id);
        if (mapping == null) {
            throw new BusinessException("关联不存在：" + id);
        }
        if (!mapping.getObjectTypeId().equals(objectTypeId)) {
            throw new BusinessException("关联不属于该对象类型");
        }

        // 取消其他默认数据源
        LambdaQueryWrapper<ObjectTypeDataSource> defaultWrapper = new LambdaQueryWrapper<>();
        defaultWrapper.eq(ObjectTypeDataSource::getObjectTypeId, objectTypeId)
                     .eq(ObjectTypeDataSource::getIsDefault, true);
        List<ObjectTypeDataSource> defaultDatasources = list(defaultWrapper);
        for (ObjectTypeDataSource ds : defaultDatasources) {
            if (!ds.getId().equals(mapping.getId())) {
                ds.setIsDefault(false);
                updateById(ds);
            }
        }

        // 设置当前数据源为默认
        mapping.setIsDefault(true);
        mapping.setUpdatedAt(LocalDateTime.now());
        updateById(mapping);
        log.info("设置对象类型 {} 的默认数据源表为 {} 成功", objectTypeId, id);
    }

    @Override
    public List<ObjectTypeDataSourceTable> getDataSourceTablesByObjectTypeId(UUID objectTypeId) {
        LambdaQueryWrapper<ObjectTypeDataSource> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ObjectTypeDataSource::getObjectTypeId, objectTypeId)
               .orderByDesc(ObjectTypeDataSource::getPriority)
               .orderByDesc(ObjectTypeDataSource::getIsDefault);
        
        List<ObjectTypeDataSource> mappings = list(wrapper);
        
        // 转换为DTO列表
        return mappings.stream()
            .map(mapping -> {
                ObjectTypeDataSourceTable dto = new ObjectTypeDataSourceTable();
                dto.setId(mapping.getId());
                dto.setObjectTypeId(mapping.getObjectTypeId());
                dto.setTableName(mapping.getTableName());
                dto.setSchemaName(mapping.getSchemaName());
                dto.setIsStorage(mapping.getIsStorage());
                dto.setIsDefault(mapping.getIsDefault());
                dto.setPriority(mapping.getPriority());
                dto.setDescription(mapping.getDescription());
                
                // 获取数据源信息（隐藏密码）
                DataSource ds = dataSourceMapper.selectById(mapping.getDatasourceId());
                if (ds != null) {
                    ds.setPassword(null);
                    dto.setDatasource(ds);
                }
                
                return dto;
            })
            .filter(dto -> dto.getDatasource() != null)
            .collect(Collectors.toList());
    }

    @Override
    public ObjectTypeDataSourceTable getDefaultDataSourceTable(UUID objectTypeId) {
        LambdaQueryWrapper<ObjectTypeDataSource> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ObjectTypeDataSource::getObjectTypeId, objectTypeId)
               .eq(ObjectTypeDataSource::getIsDefault, true)
               .last("LIMIT 1");
        
        ObjectTypeDataSource mapping = getOne(wrapper);
        if (mapping == null) {
            return null;
        }

        ObjectTypeDataSourceTable dto = new ObjectTypeDataSourceTable();
        dto.setId(mapping.getId());
        dto.setObjectTypeId(mapping.getObjectTypeId());
        dto.setTableName(mapping.getTableName());
        dto.setSchemaName(mapping.getSchemaName());
        dto.setIsDefault(mapping.getIsDefault());
        dto.setPriority(mapping.getPriority());
        dto.setDescription(mapping.getDescription());
        
        DataSource datasource = dataSourceMapper.selectById(mapping.getDatasourceId());
        if (datasource != null) {
            datasource.setPassword(null);
            dto.setDatasource(datasource);
        }
        
        return dto;
    }

    @Override
    public List<ObjectTypeDataSourceTable> getStorageDatasourcesByObjectTypeId(UUID objectTypeId) {
        LambdaQueryWrapper<ObjectTypeDataSource> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ObjectTypeDataSource::getObjectTypeId, objectTypeId)
               .eq(ObjectTypeDataSource::getIsStorage, true)
               .orderByDesc(ObjectTypeDataSource::getPriority)
               .orderByDesc(ObjectTypeDataSource::getIsDefault);
        
        List<ObjectTypeDataSource> mappings = list(wrapper);
        
        // 转换为DTO列表
        return mappings.stream()
            .map(mapping -> {
                ObjectTypeDataSourceTable dto = new ObjectTypeDataSourceTable();
                dto.setId(mapping.getId());
                dto.setObjectTypeId(mapping.getObjectTypeId());
                dto.setTableName(mapping.getTableName()); // 存储库模式时可能为null
                dto.setSchemaName(mapping.getSchemaName());
                dto.setIsStorage(mapping.getIsStorage());
                dto.setIsDefault(mapping.getIsDefault());
                dto.setPriority(mapping.getPriority());
                dto.setDescription(mapping.getDescription());
                
                // 获取数据源信息（隐藏密码）
                DataSource ds = dataSourceMapper.selectById(mapping.getDatasourceId());
                if (ds != null) {
                    ds.setPassword(null);
                    dto.setDatasource(ds);
                }
                
                return dto;
            })
            .filter(dto -> dto.getDatasource() != null)
            .collect(Collectors.toList());
    }

    @Override
    public ObjectTypeDataSourceTable getDefaultStorageDatasource(UUID objectTypeId) {
        LambdaQueryWrapper<ObjectTypeDataSource> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ObjectTypeDataSource::getObjectTypeId, objectTypeId)
               .eq(ObjectTypeDataSource::getIsStorage, true)
               .eq(ObjectTypeDataSource::getIsDefault, true)
               .last("LIMIT 1");
        
        ObjectTypeDataSource mapping = getOne(wrapper);
        if (mapping == null) {
            return null;
        }

        ObjectTypeDataSourceTable dto = new ObjectTypeDataSourceTable();
        dto.setId(mapping.getId());
        dto.setObjectTypeId(mapping.getObjectTypeId());
        dto.setTableName(mapping.getTableName()); // 存储库模式时可能为null
        dto.setSchemaName(mapping.getSchemaName());
        dto.setIsStorage(mapping.getIsStorage());
        dto.setIsDefault(mapping.getIsDefault());
        dto.setPriority(mapping.getPriority());
        dto.setDescription(mapping.getDescription());
        
        DataSource datasource = dataSourceMapper.selectById(mapping.getDatasourceId());
        if (datasource != null) {
            datasource.setPassword(null);
            dto.setDatasource(datasource);
        }
        
        return dto;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Deprecated
    public ObjectTypeDataSource addDataSourceToObjectType(UUID objectTypeId, UUID datasourceId,
                                                           Boolean isDefault, Integer priority) {
        // 旧方法已废弃，要求使用新方法并指定tableName
        throw new BusinessException("该方法已废弃，请使用 addDataSourceTableToObjectType 方法，并指定 tableName");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Deprecated
    public void removeDataSourceFromObjectType(UUID objectTypeId, UUID datasourceId) {
        // 兼容旧方法：查找第一个匹配的关联并删除
        LambdaQueryWrapper<ObjectTypeDataSource> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ObjectTypeDataSource::getObjectTypeId, objectTypeId)
               .eq(ObjectTypeDataSource::getDatasourceId, datasourceId)
               .last("LIMIT 1");
        
        ObjectTypeDataSource mapping = getOne(wrapper);
        if (mapping == null) {
            throw new BusinessException("该数据源未关联到该对象类型");
        }

        removeById(mapping.getId());
        log.info("移除对象类型 {} 的数据源 {} 成功", objectTypeId, datasourceId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setDefaultDataSource(UUID objectTypeId, UUID datasourceId) {
        // 检查关联是否存在
        LambdaQueryWrapper<ObjectTypeDataSource> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ObjectTypeDataSource::getObjectTypeId, objectTypeId)
               .eq(ObjectTypeDataSource::getDatasourceId, datasourceId);
        ObjectTypeDataSource mapping = getOne(wrapper);
        if (mapping == null) {
            throw new BusinessException("该数据源未关联到该对象类型");
        }

        // 取消其他默认数据源
        LambdaQueryWrapper<ObjectTypeDataSource> defaultWrapper = new LambdaQueryWrapper<>();
        defaultWrapper.eq(ObjectTypeDataSource::getObjectTypeId, objectTypeId)
                     .eq(ObjectTypeDataSource::getIsDefault, true);
        List<ObjectTypeDataSource> defaultDatasources = list(defaultWrapper);
        for (ObjectTypeDataSource ds : defaultDatasources) {
            if (!ds.getId().equals(mapping.getId())) {
                ds.setIsDefault(false);
                updateById(ds);
            }
        }

        // 设置当前数据源为默认
        mapping.setIsDefault(true);
        mapping.setUpdatedAt(LocalDateTime.now());
        updateById(mapping);
        log.info("设置对象类型 {} 的默认数据源为 {} 成功", objectTypeId, datasourceId);
    }

    @Override
    public List<DataSource> getDataSourcesByObjectTypeId(UUID objectTypeId) {
        LambdaQueryWrapper<ObjectTypeDataSource> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ObjectTypeDataSource::getObjectTypeId, objectTypeId)
               .orderByDesc(ObjectTypeDataSource::getPriority)
               .orderByDesc(ObjectTypeDataSource::getIsDefault);
        
        List<ObjectTypeDataSource> mappings = list(wrapper);
        
        // 获取数据源列表
        List<DataSource> datasources = mappings.stream()
            .map(mapping -> {
                DataSource ds = dataSourceMapper.selectById(mapping.getDatasourceId());
                if (ds != null) {
                    // 隐藏密码
                    ds.setPassword(null);
                }
                return ds;
            })
            .filter(ds -> ds != null)
            .collect(Collectors.toList());
        
        return datasources;
    }

    @Override
    public DataSource getDefaultDataSource(UUID objectTypeId) {
        LambdaQueryWrapper<ObjectTypeDataSource> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ObjectTypeDataSource::getObjectTypeId, objectTypeId)
               .eq(ObjectTypeDataSource::getIsDefault, true)
               .last("LIMIT 1");
        
        ObjectTypeDataSource mapping = getOne(wrapper);
        if (mapping == null) {
            return null;
        }

        DataSource datasource = dataSourceMapper.selectById(mapping.getDatasourceId());
        if (datasource != null) {
            // 隐藏密码
            datasource.setPassword(null);
        }
        return datasource;
    }

    /**
     * 检查数据源是否被对象类型使用
     *
     * @param datasourceId 数据源ID
     * @return 使用该数据源的对象类型数量
     */
    public long countObjectTypesByDatasourceId(UUID datasourceId) {
        LambdaQueryWrapper<ObjectTypeDataSource> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ObjectTypeDataSource::getDatasourceId, datasourceId);
        return count(wrapper);
    }
}

