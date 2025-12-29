package com.example.datamodel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.datamodel.dto.FunctionDTO;
import com.example.datamodel.entity.Function;
import com.example.datamodel.exception.BusinessException;
import com.example.datamodel.mapper.FunctionMapper;
import com.example.datamodel.entity.ActionType;
import com.example.datamodel.mapper.ActionTypeMapper;
import com.example.datamodel.service.FunctionService;
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
 * 函数服务实现类
 *
 * @author DataModel Team
 */
@Slf4j
@Service
public class FunctionServiceImpl extends ServiceImpl<FunctionMapper, Function> implements FunctionService {

    @Autowired
    private ActionTypeMapper actionTypeMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Function createFunction(FunctionDTO dto) {
        // 检查名称是否已存在
        Function existing = lambdaQuery()
                .eq(Function::getName, dto.getName())
                .one();
        if (existing != null) {
            throw new BusinessException("函数名称已存在：" + dto.getName());
        }

        Function function = new Function();
        BeanUtils.copyProperties(dto, function);
        function.setInputSchema(dto.getInputSchema() != null ? JsonUtils.toJsonString(dto.getInputSchema()) : "{}");
        function.setVersion(dto.getVersion() != null ? dto.getVersion() : "1.0.0");
        function.setMetadata(dto.getMetadata() != null ? JsonUtils.toJsonString(dto.getMetadata()) : "{}");
        function.setCreatedAt(LocalDateTime.now());
        function.setUpdatedAt(LocalDateTime.now());

        save(function);
        log.info("创建函数成功：{}", function.getName());
        return function;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Function updateFunction(UUID id, FunctionDTO dto) {
        Function function = getById(id);
        if (function == null) {
            throw new BusinessException("函数不存在：" + id);
        }

        // 如果名称改变，检查新名称是否已被使用
        if (!function.getName().equals(dto.getName())) {
            Function existing = lambdaQuery()
                    .eq(Function::getName, dto.getName())
                    .ne(Function::getId, id)
                    .one();
            if (existing != null) {
                throw new BusinessException("函数名称已被使用：" + dto.getName());
            }
        }

        function.setName(dto.getName());
        function.setDisplayName(dto.getDisplayName());
        function.setDescription(dto.getDescription());
        function.setCode(dto.getCode());
        function.setInputSchema(dto.getInputSchema() != null ? JsonUtils.toJsonString(dto.getInputSchema()) : function.getInputSchema());
        function.setReturnType(dto.getReturnType());
        function.setVersion(dto.getVersion() != null ? dto.getVersion() : function.getVersion());
        function.setMetadata(dto.getMetadata() != null ? JsonUtils.toJsonString(dto.getMetadata()) : function.getMetadata());
        function.setUpdatedAt(LocalDateTime.now());

        updateById(function);
        log.info("更新函数成功：{}", function.getName());
        return function;
    }

    @Override
    public Function getByName(String name) {
        return lambdaQuery()
                .eq(Function::getName, name)
                .one();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteFunction(UUID id) {
        Function function = getById(id);
        if (function == null) {
            throw new BusinessException("函数不存在：" + id);
        }

        // 检查是否有操作类型使用了该函数
        LambdaQueryWrapper<ActionType> wrapper = Wrappers.<ActionType>lambdaQuery()
            .eq(ActionType::getHandlerFunction, function.getName());
        List<ActionType> actionTypes = actionTypeMapper.selectList(wrapper);
        if (actionTypes != null && !actionTypes.isEmpty()) {
            throw new BusinessException(
                String.format("无法删除函数 '%s'，存在 %d 个操作类型正在使用该函数，请先删除或修改相关操作类型", 
                    function.getDisplayName() != null ? function.getDisplayName() : function.getName(), 
                    actionTypes.size()));
        }

        removeById(id);
        log.info("删除函数成功：{}", function.getName());
    }

    @Override
    public com.baomidou.mybatisplus.core.metadata.IPage<Function> page(com.baomidou.mybatisplus.extension.plugins.pagination.Page<Function> page, java.util.UUID domainId) {
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Function> wrapper = new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        if (domainId != null) {
            wrapper.eq(Function::getDomainId, domainId);
        }
        return page(page, wrapper);
    }
}

