package com.example.datamodel.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.datamodel.core.datasource.TableColumnInfo;
import com.example.datamodel.dto.BatchCreatePropertiesFromTableDTO;
import com.example.datamodel.dto.PropertyDTO;
import com.example.datamodel.entity.DataSource;
import com.example.datamodel.entity.ObjectType;
import com.example.datamodel.entity.Property;
import com.example.datamodel.exception.BusinessException;
import com.example.datamodel.mapper.PropertyMapper;
import com.example.datamodel.core.DynamicTableManager;
import com.example.datamodel.service.DataSourceService;
import com.example.datamodel.service.ObjectTypeService;
import com.example.datamodel.service.PropertyService;
import com.example.datamodel.service.MetaModelService;
import com.example.datamodel.utils.DataTypeMapper;
import com.example.datamodel.utils.JsonUtils;
import com.example.datamodel.vo.BatchCreatePropertiesResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 属性服务实现类
 *
 * @author DataModel Team
 */
@Slf4j
@Service
public class PropertyServiceImpl extends ServiceImpl<PropertyMapper, Property> implements PropertyService {

    @Autowired
    @Lazy
    private ObjectTypeService objectTypeService;

    @Autowired
    private DynamicTableManager dynamicTableManager;

    @Autowired
    @Lazy
    private MetaModelService metaModelService;

    @Autowired
    private DataSourceService dataSourceService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Property createProperty(UUID objectTypeId, PropertyDTO dto) {
        // 验证对象类型是否存在
        ObjectType objectType = objectTypeService.getById(objectTypeId);
        if (objectType == null) {
            throw new BusinessException("对象类型不存在：" + objectTypeId);
        }

        // 验证元属性ID是否存在（如果提供了metaPropertyId）
        String metaPropertyId = dto.getMetaPropertyId() != null ? dto.getMetaPropertyId() : "META_PROPERTY";
        com.example.datamodel.entity.MetaProperty metaProperty = metaModelService.getMetaPropertyById(metaPropertyId);
        if (metaProperty == null) {
            throw new BusinessException("无效的元属性ID：" + metaPropertyId);
        }

        // 检查属性名称是否已存在
        List<Property> existingProperties = baseMapper.selectByObjectTypeId(objectTypeId);
        boolean nameExists = existingProperties.stream()
                .anyMatch(p -> p.getName().equals(dto.getName()));
        if (nameExists) {
            throw new BusinessException("属性名称已存在：" + dto.getName());
        }

        Property property = new Property();
        BeanUtils.copyProperties(Objects.requireNonNull(dto, "PropertyDTO不能为null"), property);
        property.setObjectTypeId(objectTypeId);
        property.setMetaPropertyId(metaPropertyId);  // 设置元属性ID
        property.setRequired(dto.getRequired() != null ? dto.getRequired() : false);
        property.setConstraints(dto.getConstraints() != null ? JsonUtils.toJsonString(dto.getConstraints()) : "{}");
        property.setMetadata(dto.getMetadata() != null ? JsonUtils.toJsonString(dto.getMetadata()) : "{}");
        property.setSortOrder(dto.getSortOrder() != null ? dto.getSortOrder() : 0);
        property.setCreatedAt(LocalDateTime.now());
        property.setUpdatedAt(LocalDateTime.now());

        save(property);
        log.info("创建属性成功：{} - {}", objectType.getName(), property.getName());
        
        // 检查并创建实例表
        if (!dynamicTableManager.tableExists(objectType)) {
            List<Property> allProperties = baseMapper.selectByObjectTypeId(objectTypeId);
            dynamicTableManager.createInstanceTable(objectType, allProperties);
        } else {
            // 如果表已存在，添加新字段
            dynamicTableManager.addColumn(objectType, property);
        }
        
        return property;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Property updateProperty(UUID id, PropertyDTO dto) {
        Property property = getById(id);
        if (property == null) {
            throw new BusinessException("属性不存在：" + id);
        }

        // 如果属性名称改变，检查新名称是否已存在
        if (!property.getName().equals(dto.getName())) {
            List<Property> existingProperties = baseMapper.selectByObjectTypeId(property.getObjectTypeId());
            boolean nameExists = existingProperties.stream()
                    .anyMatch(p -> p.getName().equals(dto.getName()) && !p.getId().equals(id));
            if (nameExists) {
                throw new BusinessException("属性名称已存在：" + dto.getName());
            }
        }

        property.setName(dto.getName());
        property.setDataType(dto.getDataType());
        property.setDescription(dto.getDescription());
        property.setRequired(dto.getRequired() != null ? dto.getRequired() : property.getRequired());
        property.setDefaultValue(dto.getDefaultValue());
        property.setConstraints(dto.getConstraints() != null ? JsonUtils.toJsonString(dto.getConstraints()) : property.getConstraints());
        property.setMetadata(dto.getMetadata() != null ? JsonUtils.toJsonString(dto.getMetadata()) : property.getMetadata());
        property.setSortOrder(dto.getSortOrder() != null ? dto.getSortOrder() : property.getSortOrder());
        property.setUpdatedAt(LocalDateTime.now());

        updateById(property);
        log.info("更新属性成功：{}", property.getName());
        return property;
    }

    @Override
    public List<Property> getByObjectTypeId(UUID objectTypeId) {
        return baseMapper.selectByObjectTypeId(objectTypeId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteProperty(UUID id) {
        Property property = getById(id);
        if (property == null) {
            throw new BusinessException("属性不存在：" + id);
        }

        // 检查是否有实例使用了该属性，如果有需要处理数据迁移
        ObjectType objectType = objectTypeService.getById(property.getObjectTypeId());
        if (objectType != null) {
            // 检查实例表是否存在且该列有数据
            if (dynamicTableManager.tableExists(objectType)) {
                boolean hasData = dynamicTableManager.hasColumnData(objectType, property.getName());
                if (hasData) {
                    throw new BusinessException(
                        String.format("无法删除属性 '%s'，该属性在对象类型 '%s' 的实例中存在数据。" +
                            "请先清理或迁移相关实例数据，然后再删除属性。", 
                            property.getName(), objectType.getDisplayName()));
                }
            }
            // 删除数据库列（如果存在）
            try {
                dynamicTableManager.dropColumn(objectType, property.getName());
            } catch (Exception e) {
                log.warn("删除字段失败（可能列不存在）：{} - {}", objectType.getName(), property.getName(), e);
            }
        }

        removeById(id);
        log.info("删除属性成功：{}", property.getName());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BatchCreatePropertiesResult batchCreatePropertiesFromTable(UUID objectTypeId, BatchCreatePropertiesFromTableDTO dto) {
        // 验证对象类型是否存在
        ObjectType objectType = objectTypeService.getById(objectTypeId);
        if (objectType == null) {
            throw new BusinessException("对象类型不存在：" + objectTypeId);
        }

        // 验证数据源ID格式
        UUID datasourceId;
        try {
            datasourceId = UUID.fromString(dto.getDatasourceId());
        } catch (IllegalArgumentException e) {
            throw new BusinessException("无效的数据源ID格式：" + dto.getDatasourceId());
        }

        // 验证数据源是否存在
        DataSource datasource = dataSourceService.getById(datasourceId);
        if (datasource == null) {
            throw new BusinessException("数据源不存在：" + dto.getDatasourceId());
        }

        // 获取表字段信息
        List<TableColumnInfo> columns = dataSourceService.getTableColumns(
            datasourceId,
            dto.getSchemaName(),
            dto.getTableName()
        );

        if (columns == null || columns.isEmpty()) {
            throw new BusinessException("表不存在或无字段：" + dto.getTableName());
        }

        // 系统保留字段，这些字段不应该作为属性创建
        Set<String> reservedFields = new HashSet<>(Arrays.asList(
            "id", "class_id", "datasource_id", "created_at", "updated_at", "created_by", "updated_by"
        ));

        // 过滤字段：只处理指定的字段名，并排除系统保留字段
        List<TableColumnInfo> filteredColumns = columns.stream()
            .filter(col -> dto.getColumnNames() != null && dto.getColumnNames().contains(col.getColumnName()))
            .filter(col -> !reservedFields.contains(col.getColumnName().toLowerCase()))
            .collect(Collectors.toList());

        if (filteredColumns.isEmpty()) {
            throw new BusinessException("没有可同步的字段（已过滤系统保留字段）");
        }

        // 获取现有属性名称集合
        List<Property> existingProperties = baseMapper.selectByObjectTypeId(objectTypeId);
        Set<String> existingPropertyNames = existingProperties.stream()
            .map(Property::getName)
            .collect(Collectors.toSet());

        // 准备结果
        BatchCreatePropertiesResult result = new BatchCreatePropertiesResult();
        List<Property> createdProperties = new ArrayList<>();
        List<String> skippedColumns = new ArrayList<>();
        List<BatchCreatePropertiesResult.ColumnError> failedColumns = new ArrayList<>();

        // 批量创建属性
        for (TableColumnInfo column : filteredColumns) {
            String columnName = column.getColumnName();

            // 检查是否跳过已存在的属性
            if (dto.getSkipExisting() && existingPropertyNames.contains(columnName)) {
                skippedColumns.add(columnName);
                continue;
            }

            try {
                // 将表字段转换为属性
                PropertyDTO propertyDTO = convertColumnToPropertyDTO(column, datasource.getType());
                
                // 创建属性
                Property property = createProperty(objectTypeId, propertyDTO);
                createdProperties.add(property);
                
            } catch (Exception e) {
                log.error("从字段创建属性失败：{}", columnName, e);
                BatchCreatePropertiesResult.ColumnError error = new BatchCreatePropertiesResult.ColumnError();
                error.setColumnName(columnName);
                error.setErrorMessage(e.getMessage());
                failedColumns.add(error);
            }
        }

        // 设置结果
        result.setCreated(createdProperties.size());
        result.setSkipped(skippedColumns.size());
        result.setFailed(failedColumns.size());
        result.setProperties(createdProperties);
        result.setSkippedColumns(skippedColumns);
        result.setFailedColumns(failedColumns);

        log.info("批量创建属性完成：对象类型={}, 创建={}, 跳过={}, 失败={}",
            objectType.getName(), result.getCreated(), result.getSkipped(), result.getFailed());

        return result;
    }

    /**
     * 将表字段转换为属性DTO
     */
    private PropertyDTO convertColumnToPropertyDTO(TableColumnInfo column, String datasourceType) {
        PropertyDTO dto = new PropertyDTO();
        
        // 字段名作为属性名
        dto.setName(column.getColumnName());
        
        // 映射数据类型
        String propertyDataType = DataTypeMapper.mapToPropertyType(column.getDataType(), datasourceType);
        dto.setDataType(propertyDataType);
        
        // 是否必需：不可空或主键则为必填
        boolean required = (column.getNullable() != null && !column.getNullable()) || 
                          (column.getPrimaryKey() != null && column.getPrimaryKey());
        dto.setRequired(required);
        
        // 字段注释作为描述
        dto.setDescription(column.getComment());
        
        // 默认值
        dto.setDefaultValue(column.getDefaultValue());
        
        // 排序顺序（默认0）
        dto.setSortOrder(0);
        
        return dto;
    }
}
