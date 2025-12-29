package com.example.datamodel.service.impl;

import com.example.datamodel.dto.MetaClassDTO;
import com.example.datamodel.entity.MetaActionType;
import com.example.datamodel.entity.MetaClass;
import com.example.datamodel.entity.MetaDomain;
import com.example.datamodel.entity.MetaLinkType;
import com.example.datamodel.entity.MetaProperty;
import com.example.datamodel.exception.BusinessException;
import com.example.datamodel.mapper.MetaActionTypeMapper;
import com.example.datamodel.mapper.MetaClassMapper;
import com.example.datamodel.mapper.MetaDomainMapper;
import com.example.datamodel.mapper.MetaLinkTypeMapper;
import com.example.datamodel.mapper.MetaPropertyMapper;
import com.example.datamodel.service.MetaModelService;
import com.example.datamodel.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 元模型服务实现类
 * 支持分级管理：平台内置元模型（只读），用户扩展元模型（可CRUD）
 *
 * @author DataModel Team
 */
@Slf4j
@Service
public class MetaModelServiceImpl implements MetaModelService {

    @Autowired
    private MetaClassMapper metaClassMapper;

    @Autowired
    private MetaPropertyMapper metaPropertyMapper;

    @Autowired
    private MetaLinkTypeMapper metaLinkTypeMapper;

    @Autowired
    private MetaActionTypeMapper metaActionTypeMapper;

    @Autowired
    private MetaDomainMapper metaDomainMapper;

    // ==================== MetaClass ====================

    @Override
    public List<MetaClass> getAllMetaClasses() {
        return metaClassMapper.selectList(null);
    }

    @Override
    public List<MetaClass> getBuiltinMetaClasses() {
        return metaClassMapper.selectBuiltinMetaClasses();
    }

    @Override
    public List<MetaClass> getUserDefinedMetaClasses() {
        return metaClassMapper.selectUserDefinedMetaClasses();
    }

    @Override
    public MetaClass getMetaClassById(String id) {
        return metaClassMapper.selectById(id);
    }

    @Override
    public MetaClass getMetaClassByCode(String code) {
        return metaClassMapper.selectByCode(code);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MetaClass createCustomMetaClass(MetaClassDTO dto, String createdBy) {
        // 检查代码是否已存在
        MetaClass existing = metaClassMapper.selectByCode(dto.getCode());
        if (existing != null) {
            throw new BusinessException("元类代码已存在：" + dto.getCode());
        }

        // 创建用户扩展的元类
        MetaClass metaClass = new MetaClass();
        BeanUtils.copyProperties(dto, metaClass);
        
        // 生成UUID作为ID（用户扩展的元类使用UUID）
        metaClass.setId(UUID.randomUUID().toString());
        metaClass.setIsBuiltin(false);
        metaClass.setVersion("1.0.0");
        metaClass.setCreatedBy(createdBy);
        metaClass.setMetadataSchema(dto.getMetadataSchema() != null ? JsonUtils.toJsonString(dto.getMetadataSchema()) : "{}");
        metaClass.setCreatedAt(LocalDateTime.now());
        metaClass.setUpdatedAt(LocalDateTime.now());

        metaClassMapper.insert(metaClass);
        log.info("创建自定义元类成功：{}, 创建者：{}", metaClass.getCode(), createdBy);
        return metaClass;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MetaClass updateMetaClass(String id, MetaClassDTO dto) {
        MetaClass metaClass = metaClassMapper.selectById(id);
        if (metaClass == null) {
            throw new BusinessException("元类不存在");
        }

        // 不允许修改平台内置的元模型
        if (Boolean.TRUE.equals(metaClass.getIsBuiltin())) {
            throw new BusinessException("不允许修改平台内置的元模型");
        }

        // 如果修改了code，检查新code是否已存在
        if (!metaClass.getCode().equals(dto.getCode())) {
            MetaClass existing = metaClassMapper.selectByCode(dto.getCode());
            if (existing != null && !existing.getId().equals(id)) {
                throw new BusinessException("元类代码已存在：" + dto.getCode());
            }
        }

        // 更新字段（不允许修改code和metadata_schema，因为可能影响已创建的模型）
        metaClass.setName(dto.getName());
        metaClass.setDisplayName(dto.getDisplayName());
        metaClass.setDescription(dto.getDescription());
        metaClass.setUpdatedAt(LocalDateTime.now());

        metaClassMapper.updateById(metaClass);
        log.info("更新元类成功：{}", metaClass.getCode());
        return metaClass;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteMetaClass(String id) {
        MetaClass metaClass = metaClassMapper.selectById(id);
        if (metaClass == null) {
            throw new BusinessException("元类不存在");
        }

        // 不允许删除平台内置的元模型
        if (Boolean.TRUE.equals(metaClass.getIsBuiltin())) {
            throw new BusinessException("不允许删除平台内置的元模型");
        }

        // TODO: 检查是否有模型使用此元模型
        // long count = classMapper.countByMetaClassId(id);
        // if (count > 0) {
        //     throw new BusinessException("该元模型正在被使用，无法删除");
        // }

        metaClassMapper.deleteById(id);
        log.info("删除元类成功：{}", metaClass.getCode());
    }

    // ==================== MetaProperty ====================

    @Override
    public List<MetaProperty> getAllMetaProperties() {
        return metaPropertyMapper.selectList(null);
    }

    @Override
    public List<MetaProperty> getBuiltinMetaProperties() {
        return metaPropertyMapper.selectBuiltinMetaProperties();
    }

    @Override
    public List<MetaProperty> getUserDefinedMetaProperties() {
        return metaPropertyMapper.selectUserDefinedMetaProperties();
    }

    @Override
    public MetaProperty getMetaPropertyById(String id) {
        return metaPropertyMapper.selectById(id);
    }

    @Override
    public MetaProperty getMetaPropertyByCode(String code) {
        return metaPropertyMapper.selectByCode(code);
    }

    // ==================== MetaLinkType ====================

    @Override
    public List<MetaLinkType> getAllMetaLinkTypes() {
        return metaLinkTypeMapper.selectList(null);
    }

    @Override
    public List<MetaLinkType> getBuiltinMetaLinkTypes() {
        return metaLinkTypeMapper.selectBuiltinMetaLinkTypes();
    }

    @Override
    public List<MetaLinkType> getUserDefinedMetaLinkTypes() {
        return metaLinkTypeMapper.selectUserDefinedMetaLinkTypes();
    }

    @Override
    public MetaLinkType getMetaLinkTypeById(String id) {
        return metaLinkTypeMapper.selectById(id);
    }

    @Override
    public MetaLinkType getMetaLinkTypeByCode(String code) {
        return metaLinkTypeMapper.selectByCode(code);
    }

    // ==================== MetaActionType ====================

    @Override
    public List<MetaActionType> getAllMetaActionTypes() {
        return metaActionTypeMapper.selectList(null);
    }

    @Override
    public List<MetaActionType> getBuiltinMetaActionTypes() {
        return metaActionTypeMapper.selectBuiltinMetaActionTypes();
    }

    @Override
    public List<MetaActionType> getUserDefinedMetaActionTypes() {
        return metaActionTypeMapper.selectUserDefinedMetaActionTypes();
    }

    @Override
    public MetaActionType getMetaActionTypeById(String id) {
        return metaActionTypeMapper.selectById(id);
    }

    @Override
    public MetaActionType getMetaActionTypeByCode(String code) {
        return metaActionTypeMapper.selectByCode(code);
    }

    // ==================== MetaDomain ====================

    @Override
    public List<MetaDomain> getAllMetaDomains() {
        return metaDomainMapper.selectList(null);
    }

    @Override
    public List<MetaDomain> getBuiltinMetaDomains() {
        return metaDomainMapper.selectBuiltinMetaDomains();
    }

    @Override
    public List<MetaDomain> getUserDefinedMetaDomains() {
        return metaDomainMapper.selectUserDefinedMetaDomains();
    }

    @Override
    public MetaDomain getMetaDomainById(String id) {
        return metaDomainMapper.selectById(id);
    }

    @Override
    public MetaDomain getMetaDomainByCode(String code) {
        return metaDomainMapper.selectByCode(code);
    }
}

