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
 * 操作类型实体
 *
 * @author DataModel Team
 */
@Data
@TableName("action_types")
public class ActionType {

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
     * 操作类型名称（唯一）
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
     * 元操作类型ID（指向元模型层）
     * 默认为'META_ACTION_TYPE'（平台内置的元操作类型）
     */
    private String metaActionTypeId;

    /**
     * 目标对象类型ID
     */
    private UUID targetObjectTypeId;

    /**
     * 输入参数Schema（JSON格式）
     */
    @TableField(typeHandler = JsonbTypeHandler.class)
    private String inputSchema;

    /**
     * 输出参数Schema（JSON格式）
     */
    @TableField(typeHandler = JsonbTypeHandler.class)
    private String outputSchema;

    /**
     * 是否需要审批
     */
    private Boolean requiresApproval;

    /**
     * 处理函数名称
     */
    private String handlerFunction;

    /**
     * 所属业务域ID
     */
    private UUID domainId;

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
}

