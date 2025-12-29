package com.example.datamodel.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Map;
import java.util.UUID;

/**
 * 链接类型DTO
 *
 * @author DataModel Team
 */
@Data
public class LinkTypeDTO {

    /**
     * 链接类型名称（唯一）
     */
    @NotBlank(message = "链接类型名称不能为空")
    @Size(max = 255, message = "链接类型名称长度不能超过255个字符")
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
     * 源对象类型ID
     */
    @NotNull(message = "源对象类型ID不能为空")
    private UUID sourceObjectTypeId;

    /**
     * 目标对象类型ID
     */
    @NotNull(message = "目标对象类型ID不能为空")
    private UUID targetObjectTypeId;

    /**
     * 关系基数：1:1, 1:N, N:1, N:M
     */
    @NotBlank(message = "关系基数不能为空")
    private String cardinality;

    /**
     * 是否双向关系
     */
    private Boolean bidirectional;

    /**
     * 元数据
     */
    private Map<String, Object> metadata;

    /**
     * 所属业务域ID（可选）
     */
    private UUID domainId;

    /**
     * 元关系类型ID（指向元模型层，可选，默认为'META_LINK_TYPE'）
     */
    private String metaLinkTypeId;
}

