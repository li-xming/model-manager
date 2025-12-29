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
 * 接口实体
 *
 * @author DataModel Team
 */
@Data
@TableName("interfaces")
public class Interface {

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
     * 接口名称（唯一）
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
     * HTTP 方法，例如 GET/POST/PUT/DELETE
     */
    private String method;

    /**
     * 对外暴露的 URL 路径，例如 /api/toll/charge
     */
    private String path;

    /**
     * 绑定的操作类型ID（可选）
     */
    private UUID actionTypeId;

    /**
     * 所属业务域ID
     */
    private UUID domainId;

    /**
     * 必需属性（JSON格式）
     */
    @TableField(typeHandler = JsonbTypeHandler.class)
    private String requiredProperties;

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
}

