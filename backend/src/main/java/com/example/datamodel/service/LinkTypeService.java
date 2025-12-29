package com.example.datamodel.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.datamodel.dto.LinkTypeDTO;
import com.example.datamodel.entity.LinkType;

import java.util.List;
import java.util.UUID;

/**
 * 链接类型服务接口
 *
 * @author DataModel Team
 */
public interface LinkTypeService extends IService<LinkType> {

    /**
     * 创建链接类型
     *
     * @param dto 链接类型DTO
     * @return 链接类型实体
     */
    LinkType createLinkType(LinkTypeDTO dto);

    /**
     * 更新链接类型
     *
     * @param id 链接类型ID
     * @param dto 链接类型DTO
     * @return 链接类型实体
     */
    LinkType updateLinkType(UUID id, LinkTypeDTO dto);

    /**
     * 根据名称查询链接类型
     *
     * @param name 链接类型名称
     * @return 链接类型实体
     */
    LinkType getByName(String name);

    /**
     * 根据对象类型ID查询相关链接类型
     *
     * @param objectTypeId 对象类型ID
     * @return 链接类型列表
     */
    List<LinkType> getByObjectTypeId(UUID objectTypeId);

    /**
     * 删除链接类型
     *
     * @param id 链接类型ID
     */
    void deleteLinkType(UUID id);

    /**
     * 分页查询链接类型列表（支持按业务域过滤）
     *
     * @param page 分页参数
     * @param domainId 业务域ID（可选）
     * @return 分页结果
     */
    IPage<LinkType> page(Page<LinkType> page, UUID domainId);
}

