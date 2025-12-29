package com.example.datamodel.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.datamodel.typehandler.JsonbTypeHandler;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 元类实体（MetaClass）
 * 定义"类"这个概念，是平台的基础建模语言
 *
 * @author DataModel Team
 */
@Data
@TableName("meta_classes")
public class MetaClass {

    /**
     * 元类ID
     * 平台内置的元类使用固定ID（如'META_CLASS'）
     * 用户扩展的元类使用UUID
     */
    @TableId
    private String id;

    /**
     * 元类代码（唯一）
     */
    private String code;

    /**
     * 元类名称
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
     * 定义该元模型需要哪些元数据字段
     */
    @TableField(typeHandler = JsonbTypeHandler.class)
    private String metadataSchema;

    /**
     * 是否为平台内置元模型
     * true: 平台内置，不建议修改
     * false: 用户扩展，可以由管理员创建/修改
     */
    private Boolean isBuiltin;

    /**
     * 版本号
     */
    private String version;

    /**
     * 创建者
     * 平台内置的为 'SYSTEM'
     * 用户扩展的为创建者的用户名
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

