package com.example.datamodel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.datamodel.entity.Interface;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


/**
 * 接口Mapper
 *
 * @author DataModel Team
 */
@Mapper
public interface InterfaceMapper extends BaseMapper<Interface> {

    /**
     * 根据名称查询接口
     *
     * @param name 接口名称
     * @return 接口
     */
    Interface selectByName(@Param("name") String name);

    /**
     * 根据HTTP方法和URL路径查询接口
     *
     * @param method HTTP方法
     * @param path   URL路径
     * @return 接口
     */
    Interface selectByMethodAndPath(@Param("method") String method, @Param("path") String path);
}
