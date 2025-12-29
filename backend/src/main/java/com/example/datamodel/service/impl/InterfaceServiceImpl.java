package com.example.datamodel.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.datamodel.dto.InterfaceDTO;
import com.example.datamodel.dto.InterfaceImplementationDTO;
import com.example.datamodel.entity.Interface;
import com.example.datamodel.entity.InterfaceImplementation;
import com.example.datamodel.entity.ObjectType;
import com.example.datamodel.entity.Property;
import com.example.datamodel.exception.BusinessException;
import com.example.datamodel.mapper.InterfaceImplementationMapper;
import com.example.datamodel.mapper.InterfaceMapper;
import com.example.datamodel.service.InterfaceService;
import com.example.datamodel.service.ObjectTypeService;
import com.example.datamodel.service.PropertyService;
import com.example.datamodel.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 接口服务实现类
 *
 * @author DataModel Team
 */
@Slf4j
@Service
public class InterfaceServiceImpl extends ServiceImpl<InterfaceMapper, Interface> implements InterfaceService {

    @Autowired
    private InterfaceImplementationMapper interfaceImplementationMapper;

    @Autowired
    @Lazy
    private ObjectTypeService objectTypeService;

    @Autowired
    @Lazy
    private PropertyService propertyService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Interface createInterface(InterfaceDTO dto) {
        // 检查名称是否已存在
        Interface existing = baseMapper.selectByName(dto.getName());
        if (existing != null) {
            throw new BusinessException("接口名称已存在：" + dto.getName());
        }

        Interface interfaceEntity = new Interface();
        BeanUtils.copyProperties(dto, interfaceEntity);
        interfaceEntity.setRequiredProperties(dto.getRequiredProperties() != null ? JsonUtils.toJsonString(dto.getRequiredProperties()) : "{}");
        interfaceEntity.setMetadata(dto.getMetadata() != null ? JsonUtils.toJsonString(dto.getMetadata()) : "{}");
        interfaceEntity.setCreatedAt(LocalDateTime.now());
        interfaceEntity.setUpdatedAt(LocalDateTime.now());

        save(interfaceEntity);
        log.info("创建接口成功：{}", interfaceEntity.getName());
        return interfaceEntity;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Interface updateInterface(UUID id, InterfaceDTO dto) {
        Interface interfaceEntity = getById(id);
        if (interfaceEntity == null) {
            throw new BusinessException("接口不存在：" + id);
        }

        // 如果名称改变，检查新名称是否已被使用
        if (!interfaceEntity.getName().equals(dto.getName())) {
            Interface existing = baseMapper.selectByName(dto.getName());
            if (existing != null && !existing.getId().equals(id)) {
                throw new BusinessException("接口名称已被使用：" + dto.getName());
            }
        }

        interfaceEntity.setName(dto.getName());
        interfaceEntity.setDisplayName(dto.getDisplayName());
        interfaceEntity.setDescription(dto.getDescription());
        interfaceEntity.setMethod(dto.getMethod());
        interfaceEntity.setPath(dto.getPath());
        interfaceEntity.setActionTypeId(dto.getActionTypeId() != null ? dto.getActionTypeId() : interfaceEntity.getActionTypeId());
        interfaceEntity.setRequiredProperties(dto.getRequiredProperties() != null ? JsonUtils.toJsonString(dto.getRequiredProperties()) : interfaceEntity.getRequiredProperties());
        interfaceEntity.setDomainId(dto.getDomainId() != null ? dto.getDomainId() : interfaceEntity.getDomainId());
        interfaceEntity.setMetadata(dto.getMetadata() != null ? JsonUtils.toJsonString(dto.getMetadata()) : interfaceEntity.getMetadata());
        interfaceEntity.setUpdatedAt(LocalDateTime.now());

        updateById(interfaceEntity);
        log.info("更新接口成功：{}", interfaceEntity.getName());
        return interfaceEntity;
    }

    @Override
    public Interface getByName(String name) {
        return baseMapper.selectByName(name);
    }

    @Override
    public Interface getByMethodAndPath(String method, String path) {
        if (method == null || path == null) {
            return null;
        }
        return baseMapper.selectByMethodAndPath(method.toUpperCase(), path);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void implementInterface(InterfaceImplementationDTO dto) {
        // 验证接口是否存在
        Interface interfaceEntity = getById(dto.getInterfaceId());
        if (interfaceEntity == null) {
            throw new BusinessException("接口不存在：" + dto.getInterfaceId());
        }

        // 验证对象类型是否存在
        ObjectType objectType = objectTypeService.getById(dto.getObjectTypeId());
        if (objectType == null) {
            throw new BusinessException("对象类型不存在：" + dto.getObjectTypeId());
        }

        // 验证对象类型是否满足接口要求
        Map<String, Object> requiredProperties = JsonUtils.parseObject(interfaceEntity.getRequiredProperties());
        List<Property> objectTypeProperties = propertyService.getByObjectTypeId(dto.getObjectTypeId());

        if (requiredProperties != null && !requiredProperties.isEmpty()) {
            for (String requiredPropName : requiredProperties.keySet()) {
                boolean hasProperty = objectTypeProperties.stream()
                        .anyMatch(p -> p.getName().equals(requiredPropName));
                if (!hasProperty) {
                    throw new BusinessException("对象类型缺少接口要求的属性：" + requiredPropName);
                }
            }
        }

        // 检查是否已实现
        InterfaceImplementation existing = interfaceImplementationMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<InterfaceImplementation>()
                        .eq(InterfaceImplementation::getInterfaceId, dto.getInterfaceId())
                        .eq(InterfaceImplementation::getObjectTypeId, dto.getObjectTypeId())
        );

        if (existing == null) {
            InterfaceImplementation implementation = new InterfaceImplementation();
            implementation.setInterfaceId(dto.getInterfaceId());
            implementation.setObjectTypeId(dto.getObjectTypeId());
            implementation.setCreatedAt(LocalDateTime.now());
            interfaceImplementationMapper.insert(implementation);
            log.info("对象类型 {} 实现接口 {} 成功", objectType.getName(), interfaceEntity.getName());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeImplementation(UUID interfaceId, UUID objectTypeId) {
        interfaceImplementationMapper.delete(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<InterfaceImplementation>()
                        .eq(InterfaceImplementation::getInterfaceId, interfaceId)
                        .eq(InterfaceImplementation::getObjectTypeId, objectTypeId)
        );
        log.info("取消接口实现：{} - {}", interfaceId, objectTypeId);
    }

    @Override
    public List<UUID> getObjectTypeIdsByInterfaceId(UUID interfaceId) {
        return interfaceImplementationMapper.selectObjectTypeIdsByInterfaceId(interfaceId);
    }

    @Override
    public List<UUID> getInterfaceIdsByObjectTypeId(UUID objectTypeId) {
        return interfaceImplementationMapper.selectInterfaceIdsByObjectTypeId(objectTypeId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteInterface(UUID id) {
        Interface interfaceEntity = getById(id);
        if (interfaceEntity == null) {
            throw new BusinessException("接口不存在：" + id);
        }

        // 删除所有实现关系
        interfaceImplementationMapper.delete(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<InterfaceImplementation>()
                        .eq(InterfaceImplementation::getInterfaceId, id)
        );

        removeById(id);
        log.info("删除接口成功：{}", interfaceEntity.getName());
    }

    @Override
    public com.baomidou.mybatisplus.core.metadata.IPage<Interface> page(com.baomidou.mybatisplus.extension.plugins.pagination.Page<Interface> page, java.util.UUID domainId) {
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Interface> wrapper = new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        if (domainId != null) {
            wrapper.eq(Interface::getDomainId, domainId);
        }
        return page(page, wrapper);
    }
}

