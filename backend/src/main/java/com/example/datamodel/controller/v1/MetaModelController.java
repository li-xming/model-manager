package com.example.datamodel.controller.v1;

import com.example.datamodel.dto.MetaClassDTO;
import com.example.datamodel.entity.MetaActionType;
import com.example.datamodel.entity.MetaClass;
import com.example.datamodel.entity.MetaDomain;
import com.example.datamodel.entity.MetaLinkType;
import com.example.datamodel.entity.MetaProperty;
import com.example.datamodel.service.MetaModelService;
import com.example.datamodel.vo.ResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 元模型控制器
 * 管理平台内置和用户扩展的元模型
 *
 * @author DataModel Team
 */
@Slf4j
@Tag(name = "元模型管理", description = "元模型相关接口")
@RestController
@RequestMapping("/v1/meta-models")
public class MetaModelController {

    @Autowired
    private MetaModelService metaModelService;

    // ==================== MetaClass ====================

    @Operation(summary = "获取所有元类（包括平台内置和用户扩展）")
    @GetMapping("/meta-classes")
    public ResponseVO<List<MetaClass>> getAllMetaClasses() {
        List<MetaClass> metaClasses = metaModelService.getAllMetaClasses();
        return ResponseVO.success(metaClasses);
    }

    @Operation(summary = "获取平台内置的元类")
    @GetMapping("/meta-classes/builtin")
    public ResponseVO<List<MetaClass>> getBuiltinMetaClasses() {
        List<MetaClass> metaClasses = metaModelService.getBuiltinMetaClasses();
        return ResponseVO.success(metaClasses);
    }

    @Operation(summary = "获取用户扩展的元类")
    @GetMapping("/meta-classes/custom")
    public ResponseVO<List<MetaClass>> getUserDefinedMetaClasses() {
        List<MetaClass> metaClasses = metaModelService.getUserDefinedMetaClasses();
        return ResponseVO.success(metaClasses);
    }

    @Operation(summary = "根据ID获取元类")
    @GetMapping("/meta-classes/{id}")
    public ResponseVO<MetaClass> getMetaClassById(@PathVariable String id) {
        MetaClass metaClass = metaModelService.getMetaClassById(id);
        if (metaClass == null) {
            return ResponseVO.error(404, "元类不存在");
        }
        return ResponseVO.success(metaClass);
    }

    @Operation(summary = "创建自定义元类（仅系统管理员）")
    @PostMapping("/meta-classes")
    public ResponseVO<MetaClass> createCustomMetaClass(@Validated @RequestBody MetaClassDTO dto) {
        // TODO: 添加权限检查 @PreAuthorize("hasRole('ADMIN')")
        // 临时使用固定值，后续需要从安全上下文获取当前用户
        String createdBy = "admin";
        MetaClass metaClass = metaModelService.createCustomMetaClass(dto, createdBy);
        return ResponseVO.success(metaClass);
    }

    @Operation(summary = "更新元类（仅允许更新用户扩展的元模型）")
    @PutMapping("/meta-classes/{id}")
    public ResponseVO<MetaClass> updateMetaClass(@PathVariable String id,
                                                   @Validated @RequestBody MetaClassDTO dto) {
        // TODO: 添加权限检查 @PreAuthorize("hasRole('ADMIN')")
        MetaClass metaClass = metaModelService.updateMetaClass(id, dto);
        return ResponseVO.success(metaClass);
    }

    @Operation(summary = "删除元类（仅允许删除用户扩展的元模型）")
    @DeleteMapping("/meta-classes/{id}")
    public ResponseVO<Void> deleteMetaClass(@PathVariable String id) {
        // TODO: 添加权限检查 @PreAuthorize("hasRole('ADMIN')")
        metaModelService.deleteMetaClass(id);
        return ResponseVO.success();
    }

    // ==================== MetaProperty ====================

    @Operation(summary = "获取所有元属性")
    @GetMapping("/meta-properties")
    public ResponseVO<List<MetaProperty>> getAllMetaProperties() {
        List<MetaProperty> metaProperties = metaModelService.getAllMetaProperties();
        return ResponseVO.success(metaProperties);
    }

    @Operation(summary = "获取平台内置的元属性")
    @GetMapping("/meta-properties/builtin")
    public ResponseVO<List<MetaProperty>> getBuiltinMetaProperties() {
        List<MetaProperty> metaProperties = metaModelService.getBuiltinMetaProperties();
        return ResponseVO.success(metaProperties);
    }

    @Operation(summary = "获取用户扩展的元属性")
    @GetMapping("/meta-properties/custom")
    public ResponseVO<List<MetaProperty>> getUserDefinedMetaProperties() {
        List<MetaProperty> metaProperties = metaModelService.getUserDefinedMetaProperties();
        return ResponseVO.success(metaProperties);
    }

    @Operation(summary = "根据ID获取元属性")
    @GetMapping("/meta-properties/{id}")
    public ResponseVO<MetaProperty> getMetaPropertyById(@PathVariable String id) {
        MetaProperty metaProperty = metaModelService.getMetaPropertyById(id);
        if (metaProperty == null) {
            return ResponseVO.error(404, "元属性不存在");
        }
        return ResponseVO.success(metaProperty);
    }

    // ==================== MetaLinkType ====================

    @Operation(summary = "获取所有元关系类型")
    @GetMapping("/meta-link-types")
    public ResponseVO<List<MetaLinkType>> getAllMetaLinkTypes() {
        List<MetaLinkType> metaLinkTypes = metaModelService.getAllMetaLinkTypes();
        return ResponseVO.success(metaLinkTypes);
    }

    @Operation(summary = "获取平台内置的元关系类型")
    @GetMapping("/meta-link-types/builtin")
    public ResponseVO<List<MetaLinkType>> getBuiltinMetaLinkTypes() {
        List<MetaLinkType> metaLinkTypes = metaModelService.getBuiltinMetaLinkTypes();
        return ResponseVO.success(metaLinkTypes);
    }

    @Operation(summary = "获取用户扩展的元关系类型")
    @GetMapping("/meta-link-types/custom")
    public ResponseVO<List<MetaLinkType>> getUserDefinedMetaLinkTypes() {
        List<MetaLinkType> metaLinkTypes = metaModelService.getUserDefinedMetaLinkTypes();
        return ResponseVO.success(metaLinkTypes);
    }

    @Operation(summary = "根据ID获取元关系类型")
    @GetMapping("/meta-link-types/{id}")
    public ResponseVO<MetaLinkType> getMetaLinkTypeById(@PathVariable String id) {
        MetaLinkType metaLinkType = metaModelService.getMetaLinkTypeById(id);
        if (metaLinkType == null) {
            return ResponseVO.error(404, "元关系类型不存在");
        }
        return ResponseVO.success(metaLinkType);
    }

    // ==================== MetaActionType ====================

    @Operation(summary = "获取所有元操作类型")
    @GetMapping("/meta-action-types")
    public ResponseVO<List<MetaActionType>> getAllMetaActionTypes() {
        List<MetaActionType> metaActionTypes = metaModelService.getAllMetaActionTypes();
        return ResponseVO.success(metaActionTypes);
    }

    @Operation(summary = "获取平台内置的元操作类型")
    @GetMapping("/meta-action-types/builtin")
    public ResponseVO<List<MetaActionType>> getBuiltinMetaActionTypes() {
        List<MetaActionType> metaActionTypes = metaModelService.getBuiltinMetaActionTypes();
        return ResponseVO.success(metaActionTypes);
    }

    @Operation(summary = "获取用户扩展的元操作类型")
    @GetMapping("/meta-action-types/custom")
    public ResponseVO<List<MetaActionType>> getUserDefinedMetaActionTypes() {
        List<MetaActionType> metaActionTypes = metaModelService.getUserDefinedMetaActionTypes();
        return ResponseVO.success(metaActionTypes);
    }

    @Operation(summary = "根据ID获取元操作类型")
    @GetMapping("/meta-action-types/{id}")
    public ResponseVO<MetaActionType> getMetaActionTypeById(@PathVariable String id) {
        MetaActionType metaActionType = metaModelService.getMetaActionTypeById(id);
        if (metaActionType == null) {
            return ResponseVO.error(404, "元操作类型不存在");
        }
        return ResponseVO.success(metaActionType);
    }

    // ==================== MetaDomain ====================

    @Operation(summary = "获取所有元域")
    @GetMapping("/meta-domains")
    public ResponseVO<List<MetaDomain>> getAllMetaDomains() {
        List<MetaDomain> metaDomains = metaModelService.getAllMetaDomains();
        return ResponseVO.success(metaDomains);
    }

    @Operation(summary = "获取平台内置的元域")
    @GetMapping("/meta-domains/builtin")
    public ResponseVO<List<MetaDomain>> getBuiltinMetaDomains() {
        List<MetaDomain> metaDomains = metaModelService.getBuiltinMetaDomains();
        return ResponseVO.success(metaDomains);
    }

    @Operation(summary = "获取用户扩展的元域")
    @GetMapping("/meta-domains/custom")
    public ResponseVO<List<MetaDomain>> getUserDefinedMetaDomains() {
        List<MetaDomain> metaDomains = metaModelService.getUserDefinedMetaDomains();
        return ResponseVO.success(metaDomains);
    }

    @Operation(summary = "根据ID获取元域")
    @GetMapping("/meta-domains/{id}")
    public ResponseVO<MetaDomain> getMetaDomainById(@PathVariable String id) {
        MetaDomain metaDomain = metaModelService.getMetaDomainById(id);
        if (metaDomain == null) {
            return ResponseVO.error(404, "元域不存在");
        }
        return ResponseVO.success(metaDomain);
    }
}

