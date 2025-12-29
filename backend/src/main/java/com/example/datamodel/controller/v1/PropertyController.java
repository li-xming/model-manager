package com.example.datamodel.controller.v1;

import com.example.datamodel.dto.BatchCreatePropertiesFromTableDTO;
import com.example.datamodel.dto.PropertyDTO;
import com.example.datamodel.entity.Property;
import com.example.datamodel.service.PropertyService;
import com.example.datamodel.vo.BatchCreatePropertiesResult;
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
 * 属性控制器
 *
 * @author DataModel Team
 */
@Slf4j
@Tag(name = "属性管理", description = "属性相关接口")
@RestController
@RequestMapping("/v1/object-types/{objectTypeId}/properties")
public class PropertyController {

    @Autowired
    private PropertyService propertyService;

    @Operation(summary = "创建属性")
    @PostMapping
    public ResponseVO<Property> create(@PathVariable UUID objectTypeId,
                                       @Validated @RequestBody PropertyDTO dto) {
        Property property = propertyService.createProperty(objectTypeId, dto);
        return ResponseVO.success(property);
    }

    @Operation(summary = "更新属性")
    @PutMapping("/{id}")
    public ResponseVO<Property> update(@PathVariable UUID id,
                                       @Validated @RequestBody PropertyDTO dto) {
        Property property = propertyService.updateProperty(id, dto);
        return ResponseVO.success(property);
    }

    @Operation(summary = "根据ID查询属性")
    @GetMapping("/{id}")
    public ResponseVO<Property> getById(@PathVariable UUID id) {
        Property property = propertyService.getById(id);
        if (property == null) {
            return ResponseVO.error(404, "属性不存在");
        }
        return ResponseVO.success(property);
    }

    @Operation(summary = "根据对象类型ID查询所有属性")
    @GetMapping
    public ResponseVO<List<Property>> getByObjectTypeId(@PathVariable UUID objectTypeId) {
        List<Property> properties = propertyService.getByObjectTypeId(objectTypeId);
        return ResponseVO.success(properties);
    }

    @Operation(summary = "删除属性")
    @DeleteMapping("/{id}")
    public ResponseVO<?> delete(@PathVariable UUID id) {
        propertyService.deleteProperty(id);
        return ResponseVO.success();
    }

    @Operation(summary = "从关联的数据源表批量创建属性")
    @PostMapping("/batch-from-table")
    public ResponseVO<BatchCreatePropertiesResult> batchCreateFromTable(
            @PathVariable UUID objectTypeId,
            @Validated @RequestBody BatchCreatePropertiesFromTableDTO dto) {
        BatchCreatePropertiesResult result = propertyService.batchCreatePropertiesFromTable(objectTypeId, dto);
        return ResponseVO.success(result);
    }
}
