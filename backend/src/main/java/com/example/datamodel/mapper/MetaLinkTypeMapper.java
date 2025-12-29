package com.example.datamodel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.datamodel.entity.MetaLinkType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 元关系类型Mapper接口
 *
 * @author DataModel Team
 */
@Mapper
public interface MetaLinkTypeMapper extends BaseMapper<MetaLinkType> {

    /**
     * 查询所有平台内置的元关系类型
     *
     * @return 平台内置的元关系类型列表
     */
    @Select("SELECT * FROM meta_link_types WHERE is_builtin = TRUE ORDER BY code")
    List<MetaLinkType> selectBuiltinMetaLinkTypes();

    /**
     * 查询所有用户扩展的元关系类型
     *
     * @return 用户扩展的元关系类型列表
     */
    @Select("SELECT * FROM meta_link_types WHERE is_builtin = FALSE ORDER BY created_at DESC")
    List<MetaLinkType> selectUserDefinedMetaLinkTypes();

    /**
     * 根据代码查询元关系类型
     *
     * @param code 元关系类型代码
     * @return 元关系类型实体
     */
    @Select("SELECT * FROM meta_link_types WHERE code = #{code}")
    MetaLinkType selectByCode(String code);
}

