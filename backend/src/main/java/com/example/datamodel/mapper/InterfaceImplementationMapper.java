package com.example.datamodel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.datamodel.entity.InterfaceImplementation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.UUID;

/**
 * 接口实现关系Mapper
 *
 * @author DataModel Team
 */
@Mapper
public interface InterfaceImplementationMapper extends BaseMapper<InterfaceImplementation> {

    /**
     * 根据接口ID查询实现该接口的对象类型ID列表
     *
     * @param interfaceId 接口ID
     * @return 对象类型ID列表
     */
    List<UUID> selectObjectTypeIdsByInterfaceId(@Param("interfaceId") UUID interfaceId);

    /**
     * 根据对象类型ID查询该对象类型实现的接口ID列表
     *
     * @param objectTypeId 对象类型ID
     * @return 接口ID列表
     */
    List<UUID> selectInterfaceIdsByObjectTypeId(@Param("objectTypeId") UUID objectTypeId);
}

