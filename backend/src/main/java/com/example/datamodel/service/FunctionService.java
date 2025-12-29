package com.example.datamodel.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.datamodel.dto.FunctionDTO;
import com.example.datamodel.entity.Function;

import java.util.UUID;

/**
 * 函数服务接口
 *
 * @author DataModel Team
 */
public interface FunctionService extends IService<Function> {

    /**
     * 创建函数
     *
     * @param dto 函数DTO
     * @return 函数实体
     */
    Function createFunction(FunctionDTO dto);

    /**
     * 更新函数
     *
     * @param id 函数ID
     * @param dto 函数DTO
     * @return 函数实体
     */
    Function updateFunction(UUID id, FunctionDTO dto);

    /**
     * 根据名称查询函数
     *
     * @param name 函数名称
     * @return 函数实体
     */
    Function getByName(String name);

    /**
     * 删除函数
     *
     * @param id 函数ID
     */
    void deleteFunction(UUID id);

    /**
     * 分页查询函数列表（支持按业务域过滤）
     *
     * @param page 分页参数
     * @param domainId 业务域ID（可选）
     * @return 分页结果
     */
    IPage<Function> page(Page<Function> page, UUID domainId);
}

