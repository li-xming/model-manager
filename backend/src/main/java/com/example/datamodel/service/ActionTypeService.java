package com.example.datamodel.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.datamodel.dto.ActionTypeDTO;
import com.example.datamodel.entity.ActionType;

import java.util.List;
import java.util.UUID;

/**
 * 操作类型服务接口
 *
 * @author DataModel Team
 */
public interface ActionTypeService extends IService<ActionType> {

    /**
     * 创建操作类型
     *
     * @param dto 操作类型DTO
     * @return 操作类型实体
     */
    ActionType createActionType(ActionTypeDTO dto);

    /**
     * 更新操作类型
     *
     * @param id 操作类型ID
     * @param dto 操作类型DTO
     * @return 操作类型实体
     */
    ActionType updateActionType(UUID id, ActionTypeDTO dto);

    /**
     * 根据目标对象类型ID查询操作类型列表
     *
     * @param targetObjectTypeId 目标对象类型ID
     * @return 操作类型列表
     */
    List<ActionType> getByTargetObjectTypeId(UUID targetObjectTypeId);

    /**
     * 删除操作类型
     *
     * @param id 操作类型ID
     */
    void deleteActionType(UUID id);

    /**
     * 分页查询操作类型列表（支持按业务域过滤）
     *
     * @param page 分页参数
     * @param domainId 业务域ID（可选）
     * @return 分页结果
     */
    IPage<ActionType> page(Page<ActionType> page, UUID domainId);
}
