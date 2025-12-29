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
 * 属性实体
 *
 * @author DataModel Team
 */
@Data
@TableName("properties")
public class Property {

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
     * 对象类型ID
     */
    private UUID objectTypeId;

    /**
     * 元属性ID（指向元模型层）
     * 默认为'META_PROPERTY'（平台内置的元属性）
     */
    private String metaPropertyId;

    /**
     * 属性名称
     */
    private String name;

    /**
     * 数据类型
     */
    private String dataType;

    /**
     * 描述
     */
    private String description;

    /**
     * 是否必需
     */
    private Boolean required;

    /**
     * 默认值
     */
    private String defaultValue;

    /**
     * 约束条件（JSON格式）
     */
    @TableField(typeHandler = JsonbTypeHandler.class)
    private String constraints;

    /**
     * 元数据（JSON格式）
     */
    @TableField(typeHandler = JsonbTypeHandler.class)
    private String metadata;

    /**
     * 排序顺序
     */
    private Integer sortOrder;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}

