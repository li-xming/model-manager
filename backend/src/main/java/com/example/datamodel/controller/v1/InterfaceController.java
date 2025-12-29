package com.example.datamodel.controller.v1;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.datamodel.dto.InterfaceDTO;
import com.example.datamodel.dto.InterfaceImplementationDTO;
import com.example.datamodel.entity.Interface;
import com.example.datamodel.service.InterfaceService;
import com.example.datamodel.vo.ResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * 接口控制器
 *
 * @author DataModel Team
 */
@Slf4j
@Tag(name = "接口管理", description = "接口相关接口")
@RestController
@RequestMapping("/v1/interfaces")
public class InterfaceController {

    @Autowired
    private InterfaceService interfaceService;

    @Operation(summary = "创建接口")
    @PostMapping
    public ResponseVO<Interface> create(@Validated @RequestBody InterfaceDTO dto) {
        Interface interfaceEntity = interfaceService.createInterface(dto);
        return ResponseVO.success(interfaceEntity);
    }

    @Operation(summary = "更新接口")
    @PutMapping("/{id}")
    public ResponseVO<Interface> update(@PathVariable UUID id,
                                        @Validated @RequestBody InterfaceDTO dto) {
        Interface interfaceEntity = interfaceService.updateInterface(id, dto);
        return ResponseVO.success(interfaceEntity);
    }

    @Operation(summary = "根据ID查询接口")
    @GetMapping("/{id}")
    public ResponseVO<Interface> getById(@PathVariable UUID id) {
        Interface interfaceEntity = interfaceService.getById(id);
        if (interfaceEntity == null) {
            return ResponseVO.error(404, "接口不存在");
        }
        return ResponseVO.success(interfaceEntity);
    }

    @Operation(summary = "根据名称查询接口")
    @GetMapping("/name/{name}")
    public ResponseVO<Interface> getByName(@PathVariable String name) {
        Interface interfaceEntity = interfaceService.getByName(name);
        if (interfaceEntity == null) {
            return ResponseVO.error(404, "接口不存在");
        }
        return ResponseVO.success(interfaceEntity);
    }

    @Operation(summary = "分页查询接口列表")
    @GetMapping
    public ResponseVO<IPage<Interface>> list(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) UUID domainId) {
        Page<Interface> page = new Page<>(current, size);
        IPage<Interface> result = interfaceService.page(page, domainId);
        return ResponseVO.success(result);
    }

    @Operation(summary = "让对象类型实现接口")
    @PostMapping("/implementations")
    public ResponseVO<?> implementInterface(@Validated @RequestBody InterfaceImplementationDTO dto) {
        interfaceService.implementInterface(dto);
        return ResponseVO.success();
    }

    @Operation(summary = "取消对象类型对接口的实现")
    @DeleteMapping("/{interfaceId}/implementations/{objectTypeId}")
    public ResponseVO<?> removeImplementation(@PathVariable UUID interfaceId,
                                              @PathVariable UUID objectTypeId) {
        interfaceService.removeImplementation(interfaceId, objectTypeId);
        return ResponseVO.success();
    }

    @Operation(summary = "根据接口ID查询实现该接口的对象类型ID列表")
    @GetMapping("/{interfaceId}/object-types")
    public ResponseVO<List<UUID>> getObjectTypeIds(@PathVariable UUID interfaceId) {
        List<UUID> objectTypeIds = interfaceService.getObjectTypeIdsByInterfaceId(interfaceId);
        return ResponseVO.success(objectTypeIds);
    }

    @Operation(summary = "根据对象类型ID查询该对象类型实现的接口ID列表")
    @GetMapping("/object-type/{objectTypeId}")
    public ResponseVO<List<UUID>> getInterfaceIds(@PathVariable UUID objectTypeId) {
        List<UUID> interfaceIds = interfaceService.getInterfaceIdsByObjectTypeId(objectTypeId);
        return ResponseVO.success(interfaceIds);
    }

    @Operation(summary = "删除接口")
    @DeleteMapping("/{id}")
    public ResponseVO<?> delete(@PathVariable UUID id) {
        interfaceService.deleteInterface(id);
        return ResponseVO.success();
    }
}

