package com.example.datamodel.controller.v1;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.datamodel.dto.InstanceDTO;
import com.example.datamodel.service.InstanceService;
import com.example.datamodel.vo.ResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 实例控制器
 *
 * @author DataModel Team
 */
@Slf4j
@Tag(name = "实例管理", description = "实例相关接口")
@RestController
@RequestMapping("/v1/instances/{objectType}")
public class InstanceController {

    @Autowired
    private InstanceService instanceService;

    @Operation(summary = "创建实例")
    @PostMapping
    public ResponseVO<Map<String, Object>> create(@PathVariable String objectType,
                                                   @Validated @RequestBody InstanceDTO dto) {
        log.debug("创建实例请求 - 对象类型: {}, DTO: {}", objectType, dto);
        // 如果properties为null，尝试从请求体中直接获取属性
        if (dto.getProperties() == null || dto.getProperties().isEmpty()) {
            log.warn("DTO的properties为空，可能需要检查请求格式");
        }
        Map<String, Object> instance = instanceService.createInstance(objectType, dto);
        return ResponseVO.success(instance);
    }

    @Operation(summary = "更新实例")
    @PutMapping("/{id}")
    public ResponseVO<Map<String, Object>> update(@PathVariable String objectType,
                                                   @PathVariable UUID id,
                                                   @Validated @RequestBody InstanceDTO dto) {
        Map<String, Object> instance = instanceService.updateInstance(objectType, id, dto);
        return ResponseVO.success(instance);
    }

    @Operation(summary = "根据ID查询实例")
    @GetMapping("/{id}")
    public ResponseVO<Map<String, Object>> getById(@PathVariable String objectType,
                                                    @PathVariable UUID id) {
        Map<String, Object> instance = instanceService.getInstance(objectType, id);
        if (instance == null) {
            return ResponseVO.error(404, "实例不存在");
        }
        return ResponseVO.success(instance);
    }

    @Operation(summary = "分页查询实例列表")
    @GetMapping
    public ResponseVO<IPage<Map<String, Object>>> list(
            @PathVariable String objectType,
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) Map<String, Object> filters) {
        IPage<Map<String, Object>> page = instanceService.listInstances(objectType, current, size, filters);
        return ResponseVO.success(page);
    }

    @Operation(summary = "删除实例")
    @DeleteMapping("/{id}")
    public ResponseVO<?> delete(@PathVariable String objectType,
                                @PathVariable UUID id) {
        instanceService.deleteInstance(objectType, id);
        return ResponseVO.success();
    }

    @Operation(summary = "批量删除实例")
    @DeleteMapping("/batch")
    public ResponseVO<?> batchDelete(@PathVariable String objectType,
                                     @RequestBody List<UUID> instanceIds) {
        instanceService.batchDeleteInstances(objectType, instanceIds);
        return ResponseVO.success();
    }
}

