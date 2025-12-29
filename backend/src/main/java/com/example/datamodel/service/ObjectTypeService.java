package com.example.datamodel.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.datamodel.dto.ObjectTypeDTO;
import com.example.datamodel.entity.ObjectType;

import java.util.UUID;

/**
 * 对象类型服务接口
 *
 * @author DataModel Team
 */
public interface ObjectTypeService extends IService<ObjectType> {

    /**
     * 创建对象类型
     *
     * @param dto 对象类型DTO
     * @return 对象类型实体
     */
    ObjectType createObjectType(ObjectTypeDTO dto);

    /**
     * 更新对象类型
     *
     * @param id 对象类型ID
     * @param dto 对象类型DTO
     * @return 对象类型实体
     */
    ObjectType updateObjectType(UUID id, ObjectTypeDTO dto);

    /**
     * 根据名称查询对象类型
     *
     * @param name 对象类型名称
     * @return 对象类型实体
     */
    ObjectType getByName(String name);

    /**
     * 删除对象类型
     *
     * @param id 对象类型ID
     */
    void deleteObjectType(UUID id);

    /**
     * 分页查询对象类型列表（支持按业务域过滤）
     *
     * @param page 分页参数
     * @param domainId 业务域ID（可选）
     * @return 分页结果
     */
    IPage<ObjectType> page(Page<ObjectType> page, UUID domainId);
}

