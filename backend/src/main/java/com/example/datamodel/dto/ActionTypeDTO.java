package com.example.datamodel.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Map;
import java.util.UUID;

/**
 * 操作类型DTO
 *
 * @author DataModel Team
 */
@Data
public class ActionTypeDTO {

    /**
     * 操作类型名称（唯一）
     */
    @NotBlank(message = "操作类型名称不能为空")
    @Size(max = 255, message = "操作类型名称长度不能超过255个字符")
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
     * 目标对象类型ID
     */
    @NotNull(message = "目标对象类型ID不能为空")
    private UUID targetObjectTypeId;

    /**
     * 输入参数Schema
     */
    private Map<String, Object> inputSchema;

    /**
     * 输出参数Schema
     */
    private Map<String, Object> outputSchema;

    /**
     * 是否需要审批
     */
    private Boolean requiresApproval;

    /**
     * 处理函数名称
     */
    private String handlerFunction;

    /**
     * 元数据
     */
    private Map<String, Object> metadata;

    /**
     * 所属业务域ID（可选）
     */
    private UUID domainId;

    /**
     * 元操作类型ID（指向元模型层，可选，默认为'META_ACTION_TYPE'）
     */
    private String metaActionTypeId;
}
