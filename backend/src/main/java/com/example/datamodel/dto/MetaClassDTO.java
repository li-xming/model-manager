package com.example.datamodel.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 元类DTO
 *
 * @author DataModel Team
 */
@Data
public class MetaClassDTO {

    /**
     * 元类代码（唯一）
     */
    @NotBlank(message = "元类代码不能为空")
    @Size(max = 100, message = "元类代码长度不能超过100")
    private String code;

    /**
     * 元类名称
     */
    @NotBlank(message = "元类名称不能为空")
    @Size(max = 255, message = "元类名称长度不能超过255")
    private String name;

    /**
     * 显示名称
     */
    @Size(max = 255, message = "显示名称长度不能超过255")
    private String displayName;

    /**
     * 描述
     */
    private String description;

    /**
     * 元数据Schema（JSON格式）
     * 定义该元模型需要哪些元数据字段
     */
    private Object metadataSchema;
}

