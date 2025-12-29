package com.example.datamodel.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.datamodel.typehandler.JsonbTypeHandler;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 元域实体（MetaDomain）
 * 定义"域"这个概念，是平台的基础建模语言
 *
 * @author DataModel Team
 */
@Data
@TableName("meta_domains")
public class MetaDomain {

    /**
     * 元域ID
     */
    @TableId
    private String id;

    /**
     * 元域代码（唯一）
     */
    private String code;

    /**
     * 元域名称
     */
    private String name;

    /**
     * 显示名称
     */
    private String displayName;

    /**
     * 描述
     */
    private String description;

    /**
     * 元数据Schema（JSON格式）
     */
    @TableField(typeHandler = JsonbTypeHandler.class)
    private String metadataSchema;

    /**
     * 是否为平台内置元模型
     */
    private Boolean isBuiltin;

    /**
     * 版本号
     */
    private String version;

    /**
     * 创建者
     */
    private String createdBy;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}

