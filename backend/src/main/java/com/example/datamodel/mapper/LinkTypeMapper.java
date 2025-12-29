package com.example.datamodel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.datamodel.entity.LinkType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.UUID;

/**
 * 链接类型Mapper
 *
 * @author DataModel Team
 */
@Mapper
public interface LinkTypeMapper extends BaseMapper<LinkType> {

    /**
     * 根据名称查询链接类型
     *
     * @param name 链接类型名称
     * @return 链接类型
     */
    LinkType selectByName(@Param("name") String name);

    /**
     * 根据对象类型ID查询相关链接类型
     *
     * @param objectTypeId 对象类型ID
     * @return 链接类型列表
     */
    List<LinkType> selectByObjectTypeId(@Param("objectTypeId") UUID objectTypeId);
}

