package com.example.datamodel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.datamodel.entity.MetaActionType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 元操作类型Mapper接口
 *
 * @author DataModel Team
 */
@Mapper
public interface MetaActionTypeMapper extends BaseMapper<MetaActionType> {

    /**
     * 查询所有平台内置的元操作类型
     *
     * @return 平台内置的元操作类型列表
     */
    @Select("SELECT * FROM meta_action_types WHERE is_builtin = TRUE ORDER BY code")
    List<MetaActionType> selectBuiltinMetaActionTypes();

    /**
     * 查询所有用户扩展的元操作类型
     *
     * @return 用户扩展的元操作类型列表
     */
    @Select("SELECT * FROM meta_action_types WHERE is_builtin = FALSE ORDER BY created_at DESC")
    List<MetaActionType> selectUserDefinedMetaActionTypes();

    /**
     * 根据代码查询元操作类型
     *
     * @param code 元操作类型代码
     * @return 元操作类型实体
     */
    @Select("SELECT * FROM meta_action_types WHERE code = #{code}")
    MetaActionType selectByCode(String code);
}

