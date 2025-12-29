package com.example.datamodel.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Map;
import java.util.UUID;

/**
 * 链接实例DTO
 *
 * @author DataModel Team
 */
@Data
public class LinkInstanceDTO {

    /**
     * 链接类型ID
     */
    @NotNull(message = "链接类型ID不能为空")
    private UUID linkTypeId;

    /**
     * 源实例ID
     */
    @NotNull(message = "源实例ID不能为空")
    private UUID sourceInstanceId;

    /**
     * 目标实例ID
     */
    @NotNull(message = "目标实例ID不能为空")
    private UUID targetInstanceId;

    /**
     * 链接属性（JSON格式）
     */
    private Map<String, Object> properties;
}

