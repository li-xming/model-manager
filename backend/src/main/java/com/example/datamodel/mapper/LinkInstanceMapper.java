package com.example.datamodel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.datamodel.entity.LinkInstance;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.UUID;

/**
 * 链接实例Mapper
 *
 * @author DataModel Team
 */
@Mapper
public interface LinkInstanceMapper extends BaseMapper<LinkInstance> {

    /**
     * 根据链接类型ID和源实例ID查询目标实例列表
     *
     * @param linkTypeId 链接类型ID
     * @param sourceInstanceId 源实例ID
     * @return 链接实例列表
     */
    List<LinkInstance> selectByLinkTypeAndSource(@Param("linkTypeId") UUID linkTypeId,
                                                   @Param("sourceInstanceId") UUID sourceInstanceId);

    /**
     * 根据链接类型ID和目标实例ID查询源实例列表
     *
     * @param linkTypeId 链接类型ID
     * @param targetInstanceId 目标实例ID
     * @return 链接实例列表
     */
    List<LinkInstance> selectByLinkTypeAndTarget(@Param("linkTypeId") UUID linkTypeId,
                                                   @Param("targetInstanceId") UUID targetInstanceId);

    /**
     * 根据实例ID查询所有关联的链接实例
     *
     * @param instanceId 实例ID
     * @return 链接实例列表
     */
    List<LinkInstance> selectByInstanceId(@Param("instanceId") UUID instanceId);

    /**
     * 根据链接类型ID查询所有链接实例
     *
     * @param linkTypeId 链接类型ID
     * @return 链接实例列表
     */
    List<LinkInstance> selectByLinkTypeId(@Param("linkTypeId") UUID linkTypeId);
}

