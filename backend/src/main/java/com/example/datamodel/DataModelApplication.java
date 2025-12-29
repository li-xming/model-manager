package com.example.datamodel;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 数据模型管理平台启动类
 *
 * @author DataModel Team
 */
@SpringBootApplication
@MapperScan("com.example.datamodel.mapper")
public class DataModelApplication {

    public static void main(String[] args) {
        SpringApplication.run(DataModelApplication.class, args);
    }
}

