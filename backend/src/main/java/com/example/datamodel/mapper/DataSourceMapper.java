package com.example.datamodel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.datamodel.entity.DataSource;
import org.apache.ibatis.annotations.Mapper;

/**
 * 数据源Mapper接口
 *
 * @author DataModel Team
 */
@Mapper
public interface DataSourceMapper extends BaseMapper<DataSource> {
}

