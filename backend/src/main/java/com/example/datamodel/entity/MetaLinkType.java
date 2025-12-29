package com.example.datamodel.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.datamodel.typehandler.JsonbTypeHandler;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 元关系类型实体（MetaLinkType）
 * 定义"关系"这个概念，是平台的基础建模语言
 *
 * @author DataModel Team
 */
@Data
@TableName("meta_link_types")
public class MetaLinkType {

    /**
     * 元关系类型ID
     */
    @TableId
    private String id;

    /**
     * 元关系类型代码（唯一）
     */
    private String code;

    /**
     * 元关系类型名称
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

