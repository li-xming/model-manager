package com.example.datamodel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.datamodel.dto.ObjectTypeDTO;
import com.example.datamodel.entity.ObjectType;
import com.example.datamodel.exception.BusinessException;
import com.example.datamodel.mapper.ObjectTypeMapper;
import com.example.datamodel.service.ObjectTypeService;
import com.example.datamodel.service.PropertyService;
import com.example.datamodel.service.LinkTypeService;
import com.example.datamodel.service.ActionTypeService;
import com.example.datamodel.service.InterfaceService;
import com.example.datamodel.service.MetaModelService;
import com.example.datamodel.core.DynamicTableManager;
import com.example.datamodel.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 对象类型服务实现类
 *
 * @author DataModel Team
 */
@Slf4j
@Service
public class ObjectTypeServiceImpl extends ServiceImpl<ObjectTypeMapper, ObjectType> implements ObjectTypeService {

    @Autowired
    @Lazy
    private PropertyService propertyService;

    @Autowired
    @Lazy
    private LinkTypeService linkTypeService;

    @Autowired
    @Lazy
    private ActionTypeService actionTypeService;

    @Autowired
    @Lazy
    private InterfaceService interfaceService;

    @Autowired
    @Lazy
    private DynamicTableManager dynamicTableManager;

    @Autowired
    @Lazy
    private MetaModelService metaModelService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ObjectType createObjectType(ObjectTypeDTO dto) {
        // 检查名称是否已存在
        if (baseMapper.countByName(dto.getName(), null) > 0) {
            throw new BusinessException("对象类型名称已存在：" + dto.getName());
        }

        // 验证元类ID是否存在（如果提供了metaClassId）
        String metaClassId = dto.getMetaClassId() != null ? dto.getMetaClassId() : "META_CLASS";
        com.example.datamodel.entity.MetaClass metaClass = metaModelService.getMetaClassById(metaClassId);
        if (metaClass == null) {
            throw new BusinessException("无效的元类ID：" + metaClassId);
        }

        ObjectType objectType = new ObjectType();
        BeanUtils.copyProperties(dto, objectType);
        objectType.setMetaClassId(metaClassId);  // 设置元类ID
        objectType.setPrimaryKey(dto.getPrimaryKey() != null ? dto.getPrimaryKey() : "id");
        objectType.setMetadata(dto.getMetadata() != null ? JsonUtils.toJsonString(dto.getMetadata()) : "{}");
        objectType.setCreatedAt(LocalDateTime.now());
        objectType.setUpdatedAt(LocalDateTime.now());

        save(objectType);
        log.info("创建对象类型成功：{}", objectType.getName());
        
        // 注意：创建实例表应该在添加属性后调用，这里暂时不自动创建
        // 实例表将在第一次创建属性时或创建实例时自动创建
        
        return objectType;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ObjectType updateObjectType(UUID id, ObjectTypeDTO dto) {
        ObjectType objectType = getById(id);
        if (objectType == null) {
            throw new BusinessException("对象类型不存在：" + id);
        }

        // 检查名称是否已被其他对象类型使用
        if (baseMapper.countByName(dto.getName(), id) > 0) {
            throw new BusinessException("对象类型名称已被使用：" + dto.getName());
        }

        objectType.setName(dto.getName());
        objectType.setDisplayName(dto.getDisplayName());
        objectType.setDescription(dto.getDescription());
        objectType.setPrimaryKey(dto.getPrimaryKey() != null ? dto.getPrimaryKey() : objectType.getPrimaryKey());
        objectType.setDomainId(dto.getDomainId() != null ? dto.getDomainId() : objectType.getDomainId());
        objectType.setMetadata(dto.getMetadata() != null ? JsonUtils.toJsonString(dto.getMetadata()) : objectType.getMetadata());
        objectType.setUpdatedAt(LocalDateTime.now());

        updateById(objectType);
        log.info("更新对象类型成功：{}", objectType.getName());
        return objectType;
    }

    @Override
    public ObjectType getByName(String name) {
        return baseMapper.selectByName(name);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteObjectType(UUID id) {
        ObjectType objectType = getById(id);
        if (objectType == null) {
            throw new BusinessException("对象类型不存在：" + id);
        }

        // 检查是否有属性关联
        List<com.example.datamodel.entity.Property> properties = propertyService.getByObjectTypeId(id);
        if (properties != null && !properties.isEmpty()) {
            throw new BusinessException(
                String.format("无法删除对象类型 '%s'，存在 %d 个关联属性，请先删除相关属性", 
                    objectType.getDisplayName(), properties.size()));
        }

        // 检查是否有链接类型关联（作为源或目标）
        List<com.example.datamodel.entity.LinkType> linkTypes = linkTypeService.getByObjectTypeId(id);
        if (linkTypes != null && !linkTypes.isEmpty()) {
            throw new BusinessException(
                String.format("无法删除对象类型 '%s'，存在 %d 个关联链接类型，请先删除相关链接类型", 
                    objectType.getDisplayName(), linkTypes.size()));
        }

        // 检查是否有操作类型关联
        List<com.example.datamodel.entity.ActionType> actionTypes = actionTypeService.getByTargetObjectTypeId(id);
        if (actionTypes != null && !actionTypes.isEmpty()) {
            throw new BusinessException(
                String.format("无法删除对象类型 '%s'，存在 %d 个关联操作类型，请先删除相关操作类型", 
                    objectType.getDisplayName(), actionTypes.size()));
        }

        // 检查是否有接口实现关联
        List<UUID> interfaceIds = interfaceService.getInterfaceIdsByObjectTypeId(id);
        if (interfaceIds != null && !interfaceIds.isEmpty()) {
            throw new BusinessException(
                String.format("无法删除对象类型 '%s'，实现了 %d 个接口，请先取消接口实现关系", 
                    objectType.getDisplayName(), interfaceIds.size()));
        }

        // 如果存在实例表，尝试删除（表可能不存在，忽略错误）
        try {
            if (dynamicTableManager.tableExists(objectType)) {
                dynamicTableManager.dropInstanceTable(objectType);
            }
        } catch (Exception e) {
            log.warn("删除实例表失败（可能表不存在）：{}", objectType.getName(), e);
        }

        removeById(id);
        log.info("删除对象类型成功：{}", objectType.getName());
    }

    @Override
    public IPage<ObjectType> page(Page<ObjectType> page, UUID domainId) {
        LambdaQueryWrapper<ObjectType> wrapper = new LambdaQueryWrapper<>();
        if (domainId != null) {
            wrapper.eq(ObjectType::getDomainId, domainId);
        }
        // 按最后修改时间倒序排序
        wrapper.orderByDesc(ObjectType::getUpdatedAt);
        return page(page, wrapper);
    }
}

