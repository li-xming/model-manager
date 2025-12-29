package com.example.datamodel.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.datamodel.utils.UUIDUtils;
import lombok.Data;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 接口实现关系实体
 *
 * @author DataModel Team
 */
@Data
@TableName("interface_implementations")
public class InterfaceImplementation {

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
     * 接口ID
     */
    private UUID interfaceId;

    /**
     * 对象类型ID
     */
    private UUID objectTypeId;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}

