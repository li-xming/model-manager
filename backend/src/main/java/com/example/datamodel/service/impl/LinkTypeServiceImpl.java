package com.example.datamodel.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.datamodel.dto.LinkTypeDTO;
import com.example.datamodel.entity.LinkType;
import com.example.datamodel.entity.ObjectType;
import com.example.datamodel.exception.BusinessException;
import com.example.datamodel.mapper.LinkTypeMapper;
import com.example.datamodel.service.LinkInstanceService;
import com.example.datamodel.service.LinkTypeService;
import com.example.datamodel.service.ObjectTypeService;
import com.example.datamodel.service.MetaModelService;
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
 * 链接类型服务实现类
 *
 * @author DataModel Team
 */
@Slf4j
@Service
public class LinkTypeServiceImpl extends ServiceImpl<LinkTypeMapper, LinkType> implements LinkTypeService {

    @Autowired
    @Lazy
    private ObjectTypeService objectTypeService;

    @Autowired
    @Lazy
    private LinkInstanceService linkInstanceService;

    @Autowired
    @Lazy
    private MetaModelService metaModelService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LinkType createLinkType(LinkTypeDTO dto) {
        // 检查名称是否已存在
        LinkType existing = baseMapper.selectByName(dto.getName());
        if (existing != null) {
            throw new BusinessException("链接类型名称已存在：" + dto.getName());
        }

        // 验证源对象类型和目标对象类型是否存在
        ObjectType sourceType = objectTypeService.getById(dto.getSourceObjectTypeId());
        if (sourceType == null) {
            throw new BusinessException("源对象类型不存在：" + dto.getSourceObjectTypeId());
        }

        ObjectType targetType = objectTypeService.getById(dto.getTargetObjectTypeId());
        if (targetType == null) {
            throw new BusinessException("目标对象类型不存在：" + dto.getTargetObjectTypeId());
        }

        // 验证元关系类型ID是否存在（如果提供了metaLinkTypeId）
        String metaLinkTypeId = dto.getMetaLinkTypeId() != null ? dto.getMetaLinkTypeId() : "META_LINK_TYPE";
        com.example.datamodel.entity.MetaLinkType metaLinkType = metaModelService.getMetaLinkTypeById(metaLinkTypeId);
        if (metaLinkType == null) {
            throw new BusinessException("无效的元关系类型ID：" + metaLinkTypeId);
        }

        LinkType linkType = new LinkType();
        BeanUtils.copyProperties(dto, linkType);
        linkType.setMetaLinkTypeId(metaLinkTypeId);  // 设置元关系类型ID
        linkType.setBidirectional(dto.getBidirectional() != null ? dto.getBidirectional() : false);
        linkType.setMetadata(dto.getMetadata() != null ? JsonUtils.toJsonString(dto.getMetadata()) : "{}");
        linkType.setCreatedAt(LocalDateTime.now());
        linkType.setUpdatedAt(LocalDateTime.now());

        save(linkType);
        log.info("创建链接类型成功：{}", linkType.getName());
        return linkType;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LinkType updateLinkType(UUID id, LinkTypeDTO dto) {
        LinkType linkType = getById(id);
        if (linkType == null) {
            throw new BusinessException("链接类型不存在：" + id);
        }

        // 如果名称改变，检查新名称是否已被使用
        if (!linkType.getName().equals(dto.getName())) {
            LinkType existing = baseMapper.selectByName(dto.getName());
            if (existing != null && !existing.getId().equals(id)) {
                throw new BusinessException("链接类型名称已被使用：" + dto.getName());
            }
        }

        // 验证源对象类型和目标对象类型是否存在
        ObjectType sourceType = objectTypeService.getById(dto.getSourceObjectTypeId());
        if (sourceType == null) {
            throw new BusinessException("源对象类型不存在：" + dto.getSourceObjectTypeId());
        }

        ObjectType targetType = objectTypeService.getById(dto.getTargetObjectTypeId());
        if (targetType == null) {
            throw new BusinessException("目标对象类型不存在：" + dto.getTargetObjectTypeId());
        }

        linkType.setName(dto.getName());
        linkType.setDisplayName(dto.getDisplayName());
        linkType.setDescription(dto.getDescription());
        linkType.setSourceObjectTypeId(dto.getSourceObjectTypeId());
        linkType.setTargetObjectTypeId(dto.getTargetObjectTypeId());
        linkType.setCardinality(dto.getCardinality());
        linkType.setBidirectional(dto.getBidirectional() != null ? dto.getBidirectional() : linkType.getBidirectional());
        linkType.setDomainId(dto.getDomainId() != null ? dto.getDomainId() : linkType.getDomainId());
        linkType.setMetadata(dto.getMetadata() != null ? JsonUtils.toJsonString(dto.getMetadata()) : linkType.getMetadata());
        linkType.setUpdatedAt(LocalDateTime.now());

        updateById(linkType);
        log.info("更新链接类型成功：{}", linkType.getName());
        return linkType;
    }

    @Override
    public LinkType getByName(String name) {
        return baseMapper.selectByName(name);
    }

    @Override
    public List<LinkType> getByObjectTypeId(UUID objectTypeId) {
        return baseMapper.selectByObjectTypeId(objectTypeId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteLinkType(UUID id) {
        LinkType linkType = getById(id);
        if (linkType == null) {
            throw new BusinessException("链接类型不存在：" + id);
        }

        // 检查是否有链接实例关联数据
        List<com.example.datamodel.entity.LinkInstance> linkInstances = linkInstanceService.lambdaQuery()
                .eq(com.example.datamodel.entity.LinkInstance::getLinkTypeId, id)
                .list();
        if (linkInstances != null && !linkInstances.isEmpty()) {
            throw new BusinessException(
                String.format("无法删除链接类型 '%s'，存在 %d 个链接实例正在使用该类型，请先删除相关链接实例", 
                    linkType.getDisplayName(), linkInstances.size()));
        }

        removeById(id);
        log.info("删除链接类型成功：{}", linkType.getName());
    }

    @Override
    public com.baomidou.mybatisplus.core.metadata.IPage<LinkType> page(com.baomidou.mybatisplus.extension.plugins.pagination.Page<LinkType> page, java.util.UUID domainId) {
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<LinkType> wrapper = new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        if (domainId != null) {
            wrapper.eq(LinkType::getDomainId, domainId);
        }
        return page(page, wrapper);
    }
}

