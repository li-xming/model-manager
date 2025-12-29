package com.example.datamodel.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.UUID;

/**
 * 接口实现DTO
 *
 * @author DataModel Team
 */
@Data
public class InterfaceImplementationDTO {

    /**
     * 接口ID
     */
    @NotNull(message = "接口ID不能为空")
    private UUID interfaceId;

    /**
     * 对象类型ID
     */
    @NotNull(message = "对象类型ID不能为空")
    private UUID objectTypeId;
}

