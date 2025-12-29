package com.example.datamodel.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.datamodel.dto.LinkInstanceDTO;
import com.example.datamodel.entity.LinkInstance;
import com.example.datamodel.entity.LinkType;
import com.example.datamodel.exception.BusinessException;
import com.example.datamodel.mapper.LinkInstanceMapper;
import com.example.datamodel.entity.ObjectType;
import com.example.datamodel.service.InstanceService;
import com.example.datamodel.service.LinkInstanceService;
import com.example.datamodel.service.LinkTypeService;
import com.example.datamodel.service.ObjectTypeService;
import com.example.datamodel.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 链接实例服务实现类
 *
 * @author DataModel Team
 */
@Slf4j
@Service
public class LinkInstanceServiceImpl extends ServiceImpl<LinkInstanceMapper, LinkInstance> implements LinkInstanceService {

    @Autowired
    private LinkTypeService linkTypeService;

    @Autowired
    private ObjectTypeService objectTypeService;

    @Autowired
    private InstanceService instanceService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LinkInstance createLinkInstance(LinkInstanceDTO dto) {
        // 验证链接类型是否存在
        LinkType linkType = linkTypeService.getById(dto.getLinkTypeId());
        if (linkType == null) {
            throw new BusinessException("链接类型不存在：" + dto.getLinkTypeId());
        }

        // 验证源实例和目标实例是否存在
        // 获取源对象类型和目标对象类型
        ObjectType sourceObjectType = objectTypeService.getById(linkType.getSourceObjectTypeId());
        ObjectType targetObjectType = objectTypeService.getById(linkType.getTargetObjectTypeId());
        
        if (sourceObjectType == null) {
            throw new BusinessException("源对象类型不存在：" + linkType.getSourceObjectTypeId());
        }
        if (targetObjectType == null) {
            throw new BusinessException("目标对象类型不存在：" + linkType.getTargetObjectTypeId());
        }

        // 验证源实例是否存在
        Map<String, Object> sourceInstance = instanceService.getInstance(
            sourceObjectType.getName(), dto.getSourceInstanceId());
        if (sourceInstance == null) {
            throw new BusinessException(
                String.format("源实例不存在：%s (ID: %s)", 
                    sourceObjectType.getName(), dto.getSourceInstanceId()));
        }

        // 验证目标实例是否存在
        Map<String, Object> targetInstance = instanceService.getInstance(
            targetObjectType.getName(), dto.getTargetInstanceId());
        if (targetInstance == null) {
            throw new BusinessException(
                String.format("目标实例不存在：%s (ID: %s)", 
                    targetObjectType.getName(), dto.getTargetInstanceId()));
        }

        LinkInstance linkInstance = new LinkInstance();
        linkInstance.setLinkTypeId(dto.getLinkTypeId());
        linkInstance.setSourceInstanceId(dto.getSourceInstanceId());
        linkInstance.setTargetInstanceId(dto.getTargetInstanceId());
        linkInstance.setProperties(dto.getProperties() != null ? JsonUtils.toJsonString(dto.getProperties()) : "{}");
        linkInstance.setCreatedAt(LocalDateTime.now());

        save(linkInstance);
        log.info("创建链接实例成功：{} - {} -> {}", 
                linkType.getName(), dto.getSourceInstanceId(), dto.getTargetInstanceId());
        return linkInstance;
    }

    @Override
    public List<LinkInstance> getByLinkTypeAndSource(UUID linkTypeId, UUID sourceInstanceId) {
        return baseMapper.selectByLinkTypeAndSource(linkTypeId, sourceInstanceId);
    }

    @Override
    public List<LinkInstance> getByLinkTypeAndTarget(UUID linkTypeId, UUID targetInstanceId) {
        return baseMapper.selectByLinkTypeAndTarget(linkTypeId, targetInstanceId);
    }

    @Override
    public List<LinkInstance> getByInstanceId(UUID instanceId) {
        return baseMapper.selectByInstanceId(instanceId);
    }

    @Override
    public List<LinkInstance> getByLinkTypeId(UUID linkTypeId) {
        return baseMapper.selectByLinkTypeId(linkTypeId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteLinkInstance(UUID id) {
        LinkInstance linkInstance = getById(id);
        if (linkInstance == null) {
            throw new BusinessException("链接实例不存在：" + id);
        }

        removeById(id);
        log.info("删除链接实例成功：{}", id);
    }
}

