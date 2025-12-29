package com.example.datamodel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.datamodel.entity.ObjectTypeDataSource;
import org.apache.ibatis.annotations.Mapper;

/**
 * 对象类型-数据源关联Mapper接口
 *
 * @author DataModel Team
 */
@Mapper
public interface ObjectTypeDataSourceMapper extends BaseMapper<ObjectTypeDataSource> {
}

