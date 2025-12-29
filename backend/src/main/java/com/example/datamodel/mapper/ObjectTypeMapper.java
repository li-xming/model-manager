package com.example.datamodel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.datamodel.entity.ObjectType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.UUID;

/**
 * 对象类型Mapper
 *
 * @author DataModel Team
 */
@Mapper
public interface ObjectTypeMapper extends BaseMapper<ObjectType> {

    /**
     * 根据名称查询对象类型
     *
     * @param name 对象类型名称
     * @return 对象类型
     */
    ObjectType selectByName(@Param("name") String name);

    /**
     * 检查名称是否存在
     *
     * @param name 对象类型名称
     * @param excludeId 排除的ID（用于更新时检查）
     * @return 数量
     */
    int countByName(@Param("name") String name, @Param("excludeId") UUID excludeId);
}

