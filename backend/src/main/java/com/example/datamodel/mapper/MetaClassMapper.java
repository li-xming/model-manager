package com.example.datamodel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.datamodel.entity.MetaClass;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 元类Mapper接口
 *
 * @author DataModel Team
 */
@Mapper
public interface MetaClassMapper extends BaseMapper<MetaClass> {

    /**
     * 查询所有平台内置的元类
     *
     * @return 平台内置的元类列表
     */
    @Select("SELECT * FROM meta_classes WHERE is_builtin = TRUE ORDER BY code")
    List<MetaClass> selectBuiltinMetaClasses();

    /**
     * 查询所有用户扩展的元类
     *
     * @return 用户扩展的元类列表
     */
    @Select("SELECT * FROM meta_classes WHERE is_builtin = FALSE ORDER BY created_at DESC")
    List<MetaClass> selectUserDefinedMetaClasses();

    /**
     * 根据代码查询元类
     *
     * @param code 元类代码
     * @return 元类实体
     */
    @Select("SELECT * FROM meta_classes WHERE code = #{code}")
    MetaClass selectByCode(String code);
}

