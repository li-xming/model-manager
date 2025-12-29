package com.example.datamodel.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.datamodel.dto.InstanceDTO;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 实例服务接口
 *
 * @author DataModel Team
 */
public interface InstanceService {

    /**
     * 创建实例
     *
     * @param objectTypeName 对象类型名称
     * @param dto 实例DTO
     * @return 实例数据
     */
    Map<String, Object> createInstance(String objectTypeName, InstanceDTO dto);

    /**
     * 更新实例
     *
     * @param objectTypeName 对象类型名称
     * @param instanceId 实例ID
     * @param dto 实例DTO
     * @return 实例数据
     */
    Map<String, Object> updateInstance(String objectTypeName, UUID instanceId, InstanceDTO dto);

    /**
     * 根据ID查询实例
     *
     * @param objectTypeName 对象类型名称
     * @param instanceId 实例ID
     * @return 实例数据
     */
    Map<String, Object> getInstance(String objectTypeName, UUID instanceId);

    /**
     * 分页查询实例列表
     *
     * @param objectTypeName 对象类型名称
     * @param current 当前页
     * @param size 每页大小
     * @param filters 过滤条件
     * @return 分页结果
     */
    IPage<Map<String, Object>> listInstances(String objectTypeName, Long current, Long size, Map<String, Object> filters);

    /**
     * 删除实例
     *
     * @param objectTypeName 对象类型名称
     * @param instanceId 实例ID
     */
    void deleteInstance(String objectTypeName, UUID instanceId);

    /**
     * 批量删除实例
     *
     * @param objectTypeName 对象类型名称
     * @param instanceIds 实例ID列表
     */
    void batchDeleteInstances(String objectTypeName, List<UUID> instanceIds);
}

