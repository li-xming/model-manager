package com.example.datamodel.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.datamodel.dto.BatchCreatePropertiesFromTableDTO;
import com.example.datamodel.dto.PropertyDTO;
import com.example.datamodel.entity.Property;
import com.example.datamodel.vo.BatchCreatePropertiesResult;

import java.util.List;
import java.util.UUID;

/**
 * 属性服务接口
 *
 * @author DataModel Team
 */
public interface PropertyService extends IService<Property> {

    /**
     * 创建属性
     *
     * @param objectTypeId 对象类型ID
     * @param dto 属性DTO
     * @return 属性实体
     */
    Property createProperty(UUID objectTypeId, PropertyDTO dto);

    /**
     * 更新属性
     *
     * @param id 属性ID
     * @param dto 属性DTO
     * @return 属性实体
     */
    Property updateProperty(UUID id, PropertyDTO dto);

    /**
     * 根据对象类型ID查询所有属性
     *
     * @param objectTypeId 对象类型ID
     * @return 属性列表
     */
    List<Property> getByObjectTypeId(UUID objectTypeId);

    /**
     * 删除属性
     *
     * @param id 属性ID
     */
    void deleteProperty(UUID id);

    /**
     * 从关联的数据源表批量创建属性
     *
     * @param objectTypeId 对象类型ID
     * @param dto 批量创建DTO
     * @return 批量创建结果
     */
    BatchCreatePropertiesResult batchCreatePropertiesFromTable(UUID objectTypeId, BatchCreatePropertiesFromTableDTO dto);
}
