package com.example.datamodel.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Map;
import java.util.UUID;

/**
 * 函数DTO
 *
 * @author DataModel Team
 */
@Data
public class FunctionDTO {

    /**
     * 函数名称（唯一）
     */
    @NotBlank(message = "函数名称不能为空")
    @Size(max = 255, message = "函数名称长度不能超过255个字符")
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
     * 函数代码
     */
    @NotBlank(message = "函数代码不能为空")
    private String code;

    /**
     * 输入参数Schema
     */
    private Map<String, Object> inputSchema;

    /**
     * 返回类型
     */
    @NotBlank(message = "返回类型不能为空")
    private String returnType;

    /**
     * 版本号
     */
    private String version;

    /**
     * 元数据
     */
    private Map<String, Object> metadata;

    /**
     * 所属业务域ID（可选）
     */
    private UUID domainId;
}

