package com.example.datamodel.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.datamodel.dto.InterfaceDTO;
import com.example.datamodel.dto.InterfaceImplementationDTO;
import com.example.datamodel.entity.Interface;

import java.util.List;
import java.util.UUID;

/**
 * 接口服务接口
 *
 * @author DataModel Team
 */
public interface InterfaceService extends IService<Interface> {

    /**
     * 创建接口
     *
     * @param dto 接口DTO
     * @return 接口实体
     */
    Interface createInterface(InterfaceDTO dto);

    /**
     * 更新接口
     *
     * @param id 接口ID
     * @param dto 接口DTO
     * @return 接口实体
     */
    Interface updateInterface(UUID id, InterfaceDTO dto);

    /**
     * 根据名称查询接口
     *
     * @param name 接口名称
     * @return 接口实体
     */
    Interface getByName(String name);

    /**
     * 根据HTTP方法和URL路径查询接口
     *
     * @param method HTTP方法
     * @param path   URL路径
     * @return 接口实体
     */
    Interface getByMethodAndPath(String method, String path);

    /**
     * 让对象类型实现接口
     *
     * @param dto 接口实现DTO
     */
    void implementInterface(InterfaceImplementationDTO dto);

    /**
     * 取消对象类型对接口的实现
     *
     * @param interfaceId 接口ID
     * @param objectTypeId 对象类型ID
     */
    void removeImplementation(UUID interfaceId, UUID objectTypeId);

    /**
     * 根据接口ID查询实现该接口的对象类型ID列表
     *
     * @param interfaceId 接口ID
     * @return 对象类型ID列表
     */
    List<UUID> getObjectTypeIdsByInterfaceId(UUID interfaceId);

    /**
     * 根据对象类型ID查询该对象类型实现的接口ID列表
     *
     * @param objectTypeId 对象类型ID
     * @return 接口ID列表
     */
    List<UUID> getInterfaceIdsByObjectTypeId(UUID objectTypeId);

    /**
     * 删除接口
     *
     * @param id 接口ID
     */
    void deleteInterface(UUID id);

    /**
     * 分页查询接口列表（支持按业务域过滤）
     *
     * @param page 分页参数
     * @param domainId 业务域ID（可选）
     * @return 分页结果
     */
    IPage<Interface> page(Page<Interface> page, UUID domainId);
}

