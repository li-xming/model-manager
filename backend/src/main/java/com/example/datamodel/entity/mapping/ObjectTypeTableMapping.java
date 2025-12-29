package com.example.datamodel.entity.mapping;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.datamodel.typehandler.JsonbTypeHandler;
import com.example.datamodel.utils.UUIDUtils;
import lombok.Data;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 对象类型-主题表映射实体
 * 
 * 用于建立 ObjectType（逻辑层）与 SubjectTable（物理层）的映射关系
 * 
 * @author DataModel Team
 */
@Data
@TableName("object_type_table_mapping")
public class ObjectTypeTableMapping {

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
     * 对象类型ID（逻辑层）
     */
    private UUID objectTypeId;

    /**
     * 主题表ID（物理层，引用 dome-etl 的 integrate_subject_table.id）
     */
    private Long subjectTableId;

    /**
     * 主题集ID（冗余字段，便于查询）
     */
    private Long subjectSetId;

    /**
     * 映射类型：ONE_TO_ONE（一对一）、ONE_TO_MANY（一对多）
     */
    private String mappingType;

    /**
     * 同步方向：FORWARD（正向）、REVERSE（反向）、BIDIRECTIONAL（双向）
     */
    private String syncDirection;

    /**
     * 是否启用同步
     */
    private Boolean syncEnabled;

    /**
     * 是否自动创建物理表
     */
    private Boolean autoCreateTable;

    /**
     * 表名生成模式（如：dwd_{object_type_name}）
     */
    private String tableNamePattern;

    /**
     * 目标数据源ID
     */
    private Long datasourceId;

    /**
     * 元数据（JSON格式）
     */
    @com.baomidou.mybatisplus.annotation.TableField(typeHandler = JsonbTypeHandler.class)
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

