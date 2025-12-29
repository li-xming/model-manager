package com.example.datamodel.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Map;
import java.util.UUID;

/**
 * 对象类型DTO
 *
 * @author DataModel Team
 */
@Data
public class ObjectTypeDTO {

    /**
     * 对象类型名称（唯一）
     */
    @NotBlank(message = "对象类型名称不能为空")
    @Size(max = 255, message = "对象类型名称长度不能超过255个字符")
    private String name;

    /**
     * 显示名称
     */
    @NotBlank(message = "显示名称不能为空")
    @Size(max = 255, message = "显示名称长度不能超过255个字符")
    private String displayName;

    /**
     * 描述
     */
    private String description;

    /**
     * 主键字段名
     */
    private String primaryKey;

    /**
     * 元数据
     */
    private Map<String, Object> metadata;

    /**
     * 所属业务域ID（可选）
     */
    private UUID domainId;

    /**
     * 元类ID（指向元模型层，可选，默认为'META_CLASS'）
     */
    private String metaClassId;
}

