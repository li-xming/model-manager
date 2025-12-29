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
 * 链接类型实体
 *
 * @author DataModel Team
 */
@Data
@TableName("link_types")
public class LinkType {

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
     * 链接类型名称（唯一）
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
     * 源对象类型ID
     */
    private UUID sourceObjectTypeId;

    /**
     * 目标对象类型ID
     */
    private UUID targetObjectTypeId;

    /**
     * 关系基数：1:1, 1:N, N:1, N:M
     */
    private String cardinality;

    /**
     * 是否双向关系
     */
    private Boolean bidirectional;

    /**
     * 所属业务域ID
     */
    private UUID domainId;

    /**
     * 元关系类型ID（指向元模型层）
     * 默认为'META_LINK_TYPE'（平台内置的元关系类型）
     */
    private String metaLinkTypeId;

    /**
     * 元数据（JSON格式）
     */
    @TableField(typeHandler = JsonbTypeHandler.class)
    private String metadata;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    /**
     * 创建人
     */
    private String createdBy;

    /**
     * 更新人
     */
    private String updatedBy;
}

