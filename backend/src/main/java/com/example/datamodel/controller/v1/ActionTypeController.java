package com.example.datamodel.controller.v1;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.datamodel.dto.ActionTypeDTO;
import com.example.datamodel.entity.ActionType;
import com.example.datamodel.service.ActionTypeService;
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
 * 操作类型控制器
 *
 * @author DataModel Team
 */
@Slf4j
@Tag(name = "操作类型管理", description = "操作类型相关接口")
@RestController
@RequestMapping("/v1/action-types")
public class ActionTypeController {

    @Autowired
    private ActionTypeService actionTypeService;

    @Operation(summary = "创建操作类型")
    @PostMapping
    public ResponseVO<ActionType> create(@Validated @RequestBody ActionTypeDTO dto) {
        ActionType actionType = actionTypeService.createActionType(dto);
        return ResponseVO.success(actionType);
    }

    @Operation(summary = "更新操作类型")
    @PutMapping("/{id}")
    public ResponseVO<ActionType> update(@PathVariable UUID id,
                                         @Validated @RequestBody ActionTypeDTO dto) {
        ActionType actionType = actionTypeService.updateActionType(id, dto);
        return ResponseVO.success(actionType);
    }

    @Operation(summary = "根据ID查询操作类型")
    @GetMapping("/{id}")
    public ResponseVO<ActionType> getById(@PathVariable UUID id) {
        ActionType actionType = actionTypeService.getById(id);
        if (actionType == null) {
            return ResponseVO.error(404, "操作类型不存在");
        }
        return ResponseVO.success(actionType);
    }

    @Operation(summary = "分页查询操作类型列表")
    @GetMapping
    public ResponseVO<IPage<ActionType>> list(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) UUID domainId) {
        Page<ActionType> page = new Page<>(current, size);
        IPage<ActionType> result = actionTypeService.page(page, domainId);
        return ResponseVO.success(result);
    }

    @Operation(summary = "根据目标对象类型ID查询操作类型列表")
    @GetMapping("/target-object-type/{targetObjectTypeId}")
    public ResponseVO<List<ActionType>> getByTargetObjectTypeId(@PathVariable UUID targetObjectTypeId) {
        List<ActionType> actionTypes = actionTypeService.getByTargetObjectTypeId(targetObjectTypeId);
        return ResponseVO.success(actionTypes);
    }

    @Operation(summary = "删除操作类型")
    @DeleteMapping("/{id}")
    public ResponseVO<?> delete(@PathVariable UUID id) {
        actionTypeService.deleteActionType(id);
        return ResponseVO.success();
    }
}

