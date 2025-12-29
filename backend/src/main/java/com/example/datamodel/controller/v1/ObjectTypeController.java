package com.example.datamodel.controller.v1;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.datamodel.dto.ObjectTypeDTO;
import com.example.datamodel.entity.ObjectType;
import com.example.datamodel.service.ObjectTypeService;
import com.example.datamodel.vo.ResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * 对象类型控制器
 *
 * @author DataModel Team
 */
@Slf4j
@Tag(name = "对象类型管理", description = "对象类型相关接口")
@RestController
@RequestMapping("/v1/object-types")
public class ObjectTypeController {

    @Autowired
    private ObjectTypeService objectTypeService;

    @Operation(summary = "创建对象类型")
    @PostMapping
    public ResponseVO<ObjectType> create(@Validated @RequestBody ObjectTypeDTO dto) {
        ObjectType objectType = objectTypeService.createObjectType(dto);
        return ResponseVO.success(objectType);
    }

    @Operation(summary = "更新对象类型")
    @PutMapping("/{id}")
    public ResponseVO<ObjectType> update(@PathVariable UUID id, @Validated @RequestBody ObjectTypeDTO dto) {
        ObjectType objectType = objectTypeService.updateObjectType(id, dto);
        return ResponseVO.success(objectType);
    }

    @Operation(summary = "根据ID查询对象类型")
    @GetMapping("/{id}")
    public ResponseVO<ObjectType> getById(@PathVariable UUID id) {
        ObjectType objectType = objectTypeService.getById(id);
        if (objectType == null) {
            return ResponseVO.error(404, "对象类型不存在");
        }
        return ResponseVO.success(objectType);
    }

    @Operation(summary = "根据名称查询对象类型")
    @GetMapping("/name/{name}")
    public ResponseVO<ObjectType> getByName(@PathVariable String name) {
        ObjectType objectType = objectTypeService.getByName(name);
        if (objectType == null) {
            return ResponseVO.error(404, "对象类型不存在");
        }
        return ResponseVO.success(objectType);
    }

    @Operation(summary = "分页查询对象类型列表")
    @GetMapping
    public ResponseVO<IPage<ObjectType>> list(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) UUID domainId) {
        Page<ObjectType> page = new Page<>(current, size);
        IPage<ObjectType> result = objectTypeService.page(page, domainId);
        return ResponseVO.success(result);
    }

    @Operation(summary = "删除对象类型")
    @DeleteMapping("/{id}")
    public ResponseVO<?> delete(@PathVariable UUID id) {
        objectTypeService.deleteObjectType(id);
        return ResponseVO.success();
    }
}

