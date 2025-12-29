package com.example.datamodel.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Map;

/**
 * 属性DTO
 *
 * @author DataModel Team
 */
@Data
public class PropertyDTO {

    /**
     * 元属性ID（指向元模型层，可选，默认为'META_PROPERTY'）
     */
    private String metaPropertyId;

    /**
     * 属性名称
     */
    @NotBlank(message = "属性名称不能为空")
    @Size(max = 255, message = "属性名称长度不能超过255个字符")
    private String name;

    /**
     * 数据类型
     */
    @NotBlank(message = "数据类型不能为空")
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
     * 约束条件
     */
    private Map<String, Object> constraints;

    /**
     * 元数据
     */
    private Map<String, Object> metadata;

    /**
     * 排序顺序
     */
    private Integer sortOrder;
}

