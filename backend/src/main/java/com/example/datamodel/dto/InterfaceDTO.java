package com.example.datamodel.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Map;
import java.util.UUID;

/**
 * 接口DTO
 *
 * @author DataModel Team
 */
@Data
public class InterfaceDTO {

    /**
     * 接口名称（唯一）
     */
    @NotBlank(message = "接口名称不能为空")
    @Size(max = 255, message = "接口名称长度不能超过255个字符")
    private String name;

    /**
     * 显示名称
     */
    @NotBlank(message = "显示名称不能为空")
    @Size(max = 255, message = "显示名称长度不能超过255个字符")
    private String displayName;

    /**
     * HTTP 方法，例如 GET/POST/PUT/DELETE
     */
    @NotBlank(message = "HTTP方法不能为空")
    @Size(max = 16, message = "HTTP方法长度不能超过16个字符")
    private String method;

    /**
     * 对外暴露的 URL 路径，例如 /api/toll/charge
     */
    @NotBlank(message = "URL路径不能为空")
    @Size(max = 255, message = "URL路径长度不能超过255个字符")
    private String path;

    /**
     * 描述
     */
    private String description;

    /**
     * 必需属性
     */
    private Map<String, Object> requiredProperties;

    /**
     * 元数据
     */
    private Map<String, Object> metadata;

    /**
     * 所属业务域ID（可选）
     */
    private UUID domainId;

    /**
     * 绑定的操作类型ID（可选）
     */
    private UUID actionTypeId;
}

