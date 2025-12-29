package com.example.datamodel.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.datamodel.dto.LinkInstanceDTO;
import com.example.datamodel.entity.LinkInstance;

import java.util.List;
import java.util.UUID;

/**
 * 链接实例服务接口
 *
 * @author DataModel Team
 */
public interface LinkInstanceService extends IService<LinkInstance> {

    /**
     * 创建链接实例
     *
     * @param dto 链接实例DTO
     * @return 链接实例实体
     */
    LinkInstance createLinkInstance(LinkInstanceDTO dto);

    /**
     * 根据链接类型ID和源实例ID查询目标实例列表
     *
     * @param linkTypeId 链接类型ID
     * @param sourceInstanceId 源实例ID
     * @return 链接实例列表
     */
    List<LinkInstance> getByLinkTypeAndSource(UUID linkTypeId, UUID sourceInstanceId);

    /**
     * 根据链接类型ID和目标实例ID查询源实例列表
     *
     * @param linkTypeId 链接类型ID
     * @param targetInstanceId 目标实例ID
     * @return 链接实例列表
     */
    List<LinkInstance> getByLinkTypeAndTarget(UUID linkTypeId, UUID targetInstanceId);

    /**
     * 根据实例ID查询所有关联的链接实例
     *
     * @param instanceId 实例ID
     * @return 链接实例列表
     */
    List<LinkInstance> getByInstanceId(UUID instanceId);

    /**
     * 根据链接类型ID查询所有链接实例
     *
     * @param linkTypeId 链接类型ID
     * @return 链接实例列表
     */
    List<LinkInstance> getByLinkTypeId(UUID linkTypeId);

    /**
     * 删除链接实例
     *
     * @param id 链接实例ID
     */
    void deleteLinkInstance(UUID id);
}

