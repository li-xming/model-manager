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
 * 链接实例实体
 *
 * @author DataModel Team
 */
@Data
@TableName("link_instances")
public class LinkInstance {

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
     * 链接类型ID
     */
    private UUID linkTypeId;

    /**
     * 源实例ID
     */
    private UUID sourceInstanceId;

    /**
     * 目标实例ID
     */
    private UUID targetInstanceId;

    /**
     * 链接属性（JSON格式）
     */
    @TableField(typeHandler = JsonbTypeHandler.class)
    private String properties;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 创建人
     */
    private String createdBy;
}

