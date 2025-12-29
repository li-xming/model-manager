package com.example.datamodel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.datamodel.entity.Property;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.UUID;

/**
 * 属性Mapper
 *
 * @author DataModel Team
 */
@Mapper
public interface PropertyMapper extends BaseMapper<Property> {

    /**
     * 根据对象类型ID查询所有属性
     *
     * @param objectTypeId 对象类型ID
     * @return 属性列表
     */
    List<Property> selectByObjectTypeId(UUID objectTypeId);
}

