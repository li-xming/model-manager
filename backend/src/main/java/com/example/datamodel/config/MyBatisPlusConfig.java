package com.example.datamodel.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.example.datamodel.typehandler.UUIDTypeHandler;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.UUID;

/**
 * MyBatis Plus配置
 *
 * @author DataModel Team
 */
@Configuration
public class MyBatisPlusConfig {

    /**
     * 分页插件
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.POSTGRE_SQL));
        return interceptor;
    }

    /**
     * UUID类型处理器注册器
     * 在应用启动完成后注册UUID类型处理器
     */
    @Bean
    public ApplicationListener<ContextRefreshedEvent> uuidTypeHandlerRegistrar(SqlSessionFactory sqlSessionFactory) {
        return event -> {
            TypeHandlerRegistry typeHandlerRegistry = sqlSessionFactory.getConfiguration().getTypeHandlerRegistry();
            UUIDTypeHandler uuidTypeHandler = new UUIDTypeHandler();
            // 注册UUID类型处理器，支持多种JDBC类型
            typeHandlerRegistry.register(UUID.class, JdbcType.OTHER, uuidTypeHandler);
            typeHandlerRegistry.register(UUID.class, JdbcType.VARCHAR, uuidTypeHandler);
            typeHandlerRegistry.register(UUID.class, JdbcType.CHAR, uuidTypeHandler);
            // 注册默认的UUID类型处理器（当jdbcType为null时使用）
            typeHandlerRegistry.register(UUID.class, uuidTypeHandler);
        };
    }
}
