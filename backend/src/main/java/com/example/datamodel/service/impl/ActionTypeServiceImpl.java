package com.example.datamodel.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.datamodel.dto.ActionTypeDTO;
import com.example.datamodel.entity.ActionType;
import com.example.datamodel.entity.ObjectType;
import com.example.datamodel.exception.BusinessException;
import com.example.datamodel.mapper.ActionTypeMapper;
import com.example.datamodel.service.ActionTypeService;
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
 * 操作类型服务实现类
 *
 * @author DataModel Team
 */
@Slf4j
@Service
public class ActionTypeServiceImpl extends ServiceImpl<ActionTypeMapper, ActionType> implements ActionTypeService {

    @Autowired
    @Lazy
    private ObjectTypeService objectTypeService;

    @Autowired
    @Lazy
    private MetaModelService metaModelService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ActionType createActionType(ActionTypeDTO dto) {
        // 检查名称是否已存在
        ActionType existing = lambdaQuery()
                .eq(ActionType::getName, dto.getName())
                .one();
        if (existing != null) {
            throw new BusinessException("操作类型名称已存在：" + dto.getName());
        }

        // 验证目标对象类型是否存在
        ObjectType targetType = objectTypeService.getById(dto.getTargetObjectTypeId());
        if (targetType == null) {
            throw new BusinessException("目标对象类型不存在：" + dto.getTargetObjectTypeId());
        }

        // 验证元操作类型ID是否存在（如果提供了metaActionTypeId）
        String metaActionTypeId = dto.getMetaActionTypeId() != null ? dto.getMetaActionTypeId() : "META_ACTION_TYPE";
        com.example.datamodel.entity.MetaActionType metaActionType = metaModelService.getMetaActionTypeById(metaActionTypeId);
        if (metaActionType == null) {
            throw new BusinessException("无效的元操作类型ID：" + metaActionTypeId);
        }

        ActionType actionType = new ActionType();
        BeanUtils.copyProperties(dto, actionType);
        actionType.setMetaActionTypeId(metaActionTypeId);  // 设置元操作类型ID
        actionType.setInputSchema(dto.getInputSchema() != null ? JsonUtils.toJsonString(dto.getInputSchema()) : "{}");
        actionType.setOutputSchema(dto.getOutputSchema() != null ? JsonUtils.toJsonString(dto.getOutputSchema()) : "{}");
        actionType.setRequiresApproval(dto.getRequiresApproval() != null ? dto.getRequiresApproval() : false);
        actionType.setMetadata(dto.getMetadata() != null ? JsonUtils.toJsonString(dto.getMetadata()) : "{}");
        actionType.setCreatedAt(LocalDateTime.now());
        actionType.setUpdatedAt(LocalDateTime.now());

        save(actionType);
        log.info("创建操作类型成功：{}", actionType.getName());
        return actionType;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ActionType updateActionType(UUID id, ActionTypeDTO dto) {
        ActionType actionType = getById(id);
        if (actionType == null) {
            throw new BusinessException("操作类型不存在：" + id);
        }

        // 如果名称改变，检查新名称是否已被使用
        if (!actionType.getName().equals(dto.getName())) {
            ActionType existing = lambdaQuery()
                    .eq(ActionType::getName, dto.getName())
                    .ne(ActionType::getId, id)
                    .one();
            if (existing != null) {
                throw new BusinessException("操作类型名称已被使用：" + dto.getName());
            }
        }

        // 验证目标对象类型是否存在
        ObjectType targetType = objectTypeService.getById(dto.getTargetObjectTypeId());
        if (targetType == null) {
            throw new BusinessException("目标对象类型不存在：" + dto.getTargetObjectTypeId());
        }

        actionType.setName(dto.getName());
        actionType.setDisplayName(dto.getDisplayName());
        actionType.setDescription(dto.getDescription());
        actionType.setTargetObjectTypeId(dto.getTargetObjectTypeId());
        actionType.setInputSchema(dto.getInputSchema() != null ? JsonUtils.toJsonString(dto.getInputSchema()) : actionType.getInputSchema());
        actionType.setOutputSchema(dto.getOutputSchema() != null ? JsonUtils.toJsonString(dto.getOutputSchema()) : actionType.getOutputSchema());
        actionType.setRequiresApproval(dto.getRequiresApproval() != null ? dto.getRequiresApproval() : actionType.getRequiresApproval());
        actionType.setHandlerFunction(dto.getHandlerFunction());
        actionType.setDomainId(dto.getDomainId() != null ? dto.getDomainId() : actionType.getDomainId());
        actionType.setMetadata(dto.getMetadata() != null ? JsonUtils.toJsonString(dto.getMetadata()) : actionType.getMetadata());
        actionType.setUpdatedAt(LocalDateTime.now());

        updateById(actionType);
        log.info("更新操作类型成功：{}", actionType.getName());
        return actionType;
    }

    @Override
    public List<ActionType> getByTargetObjectTypeId(UUID targetObjectTypeId) {
        return baseMapper.selectByTargetObjectTypeId(targetObjectTypeId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteActionType(UUID id) {
        ActionType actionType = getById(id);
        if (actionType == null) {
            throw new BusinessException("操作类型不存在：" + id);
        }

        removeById(id);
        log.info("删除操作类型成功：{}", actionType.getName());
    }

    @Override
    public com.baomidou.mybatisplus.core.metadata.IPage<ActionType> page(com.baomidou.mybatisplus.extension.plugins.pagination.Page<ActionType> page, java.util.UUID domainId) {
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<ActionType> wrapper = new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        if (domainId != null) {
            wrapper.eq(ActionType::getDomainId, domainId);
        }
        return page(page, wrapper);
    }
}

