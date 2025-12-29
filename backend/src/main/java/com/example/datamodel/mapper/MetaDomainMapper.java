package com.example.datamodel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.datamodel.entity.MetaDomain;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 元域Mapper接口
 *
 * @author DataModel Team
 */
@Mapper
public interface MetaDomainMapper extends BaseMapper<MetaDomain> {

    /**
     * 查询所有平台内置的元域
     *
     * @return 平台内置的元域列表
     */
    @Select("SELECT * FROM meta_domains WHERE is_builtin = TRUE ORDER BY code")
    List<MetaDomain> selectBuiltinMetaDomains();

    /**
     * 查询所有用户扩展的元域
     *
     * @return 用户扩展的元域列表
     */
    @Select("SELECT * FROM meta_domains WHERE is_builtin = FALSE ORDER BY created_at DESC")
    List<MetaDomain> selectUserDefinedMetaDomains();

    /**
     * 根据代码查询元域
     *
     * @param code 元域代码
     * @return 元域实体
     */
    @Select("SELECT * FROM meta_domains WHERE code = #{code}")
    MetaDomain selectByCode(String code);
}

