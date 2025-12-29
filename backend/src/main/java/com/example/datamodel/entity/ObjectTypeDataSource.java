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
 * 对象类型-数据源关联实体
 *
 * @author DataModel Team
 */
@Data
@TableName("object_type_datasources")
public class ObjectTypeDataSource {

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
     * 数据源ID
     */
    private UUID datasourceId;

    /**
     * 表名（is_storage=false时必填，is_storage=true时可为空，表名由系统生成）
     */
    private String tableName;

    /**
     * Schema名称（可选，PostgreSQL/Oracle使用）
     */
    private String schemaName;

    /**
     * 是否作为对象实例的存储库
     * true：该数据源用于存储对象实例，表名由系统生成
     * false：该数据源仅用于数据映射等其他用途，需要指定table_name
     */
    private Boolean isStorage;

    /**
     * 是否为默认数据源
     */
    private Boolean isDefault;

    /**
     * 优先级（数字越大优先级越高，用于排序）
     */
    private Integer priority;

    /**
     * 关联说明
     */
    private String description;

    /**
     * 扩展元数据（JSON格式字符串，对应JSONB列）
     */
    @TableField(typeHandler = JsonbTypeHandler.class)
    private String metadata;

    /**
     * 创建人
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

