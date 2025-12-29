package com.example.datamodel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.datamodel.entity.ActionType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.UUID;

/**
 * 操作类型Mapper
 *
 * @author DataModel Team
 */
@Mapper
public interface ActionTypeMapper extends BaseMapper<ActionType> {

    /**
     * 根据目标对象类型ID查询操作类型列表
     *
     * @param targetObjectTypeId 目标对象类型ID
     * @return 操作类型列表
     */
    List<ActionType> selectByTargetObjectTypeId(@Param("targetObjectTypeId") UUID targetObjectTypeId);
}

