package com.example.datamodel.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.datamodel.typehandler.JsonbTypeHandler;
import com.example.datamodel.utils.UUIDUtils;
import lombok.Data;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 数据源实体
 *
 * @author DataModel Team
 */
@Data
@TableName("datasources")
public class DataSource {

    @TableId(type = IdType.ASSIGN_UUID)
    @Setter(lombok.AccessLevel.NONE)
    private UUID id;

    /**
     * 自定义setter，处理MyBatis-Plus ASSIGN_UUID生成的字符串格式UUID
     */
    public void setId(Object idValue) {
        this.id = UUIDUtils.parseUUID(idValue);
    }

    /**
     * 数据源名称
     */
    private String name;

    /**
     * 数据源代码（唯一标识）
     */
    private String code;

    /**
     * 数据源类型：MYSQL, POSTGRESQL, ORACLE, SQL_SERVER, MONGODB等
     */
    private String type;

    /**
     * 描述
     */
    private String description;

    /**
     * 主机地址
     */
    private String host;

    /**
     * 端口号
     */
    private Integer port;

    /**
     * 数据库名称（可选）
     */
    private String databaseName;

    /**
     * Schema名称（可选，PostgreSQL/Oracle使用）
     */
    private String schemaName;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码（加密存储）
     */
    private String password;

    /**
     * 完整的连接URL（可选，如果不提供则自动构建）
     */
    private String connectionUrl;

    /**
     * 最大连接数
     */
    private Integer maxConnections;

    /**
     * 最小连接数
     */
    private Integer minConnections;

    /**
     * 连接超时时间（秒）
     */
    private Integer connectionTimeout;

    /**
     * 是否启用
     */
    private Boolean enabled;

    /**
     * 状态：ACTIVE, INACTIVE, ERROR
     */
    private String status;

    /**
     * 最后一次连接测试时间
     */
    private LocalDateTime lastTestTime;

    /**
     * 最后一次连接测试结果：SUCCESS, FAILED
     */
    private String lastTestResult;

    /**
     * 最后一次连接测试消息
     */
    private String lastTestMessage;

    /**
     * 扩展元数据（JSON格式字符串，对应JSONB列）
     */
    @TableField(typeHandler = JsonbTypeHandler.class)
    private String metadata;

    /**
     * 所属业务域ID（可选）
     */
    private UUID domainId;

    /**
     * 创建人
     */
    private String createdBy;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新人
     */
    private String updatedBy;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}

