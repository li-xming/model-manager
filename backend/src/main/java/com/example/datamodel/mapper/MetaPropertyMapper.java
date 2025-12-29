package com.example.datamodel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.datamodel.entity.MetaProperty;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 元属性Mapper接口
 *
 * @author DataModel Team
 */
@Mapper
public interface MetaPropertyMapper extends BaseMapper<MetaProperty> {

    /**
     * 查询所有平台内置的元属性
     *
     * @return 平台内置的元属性列表
     */
    @Select("SELECT * FROM meta_properties WHERE is_builtin = TRUE ORDER BY code")
    List<MetaProperty> selectBuiltinMetaProperties();

    /**
     * 查询所有用户扩展的元属性
     *
     * @return 用户扩展的元属性列表
     */
    @Select("SELECT * FROM meta_properties WHERE is_builtin = FALSE ORDER BY created_at DESC")
    List<MetaProperty> selectUserDefinedMetaProperties();

    /**
     * 根据代码查询元属性
     *
     * @param code 元属性代码
     * @return 元属性实体
     */
    @Select("SELECT * FROM meta_properties WHERE code = #{code}")
    MetaProperty selectByCode(String code);
}

