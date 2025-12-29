package com.example.datamodel.controller.v1;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.datamodel.dto.FunctionDTO;
import com.example.datamodel.entity.Function;
import com.example.datamodel.service.FunctionService;
import com.example.datamodel.vo.ResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * 函数控制器
 *
 * @author DataModel Team
 */
@Slf4j
@Tag(name = "函数管理", description = "函数相关接口")
@RestController
@RequestMapping("/v1/functions")
public class FunctionController {

    @Autowired
    private FunctionService functionService;

    @Operation(summary = "创建函数")
    @PostMapping
    public ResponseVO<Function> create(@Validated @RequestBody FunctionDTO dto) {
        Function function = functionService.createFunction(dto);
        return ResponseVO.success(function);
    }

    @Operation(summary = "更新函数")
    @PutMapping("/{id}")
    public ResponseVO<Function> update(@PathVariable UUID id,
                                       @Validated @RequestBody FunctionDTO dto) {
        Function function = functionService.updateFunction(id, dto);
        return ResponseVO.success(function);
    }

    @Operation(summary = "根据ID查询函数")
    @GetMapping("/{id}")
    public ResponseVO<Function> getById(@PathVariable UUID id) {
        Function function = functionService.getById(id);
        if (function == null) {
            return ResponseVO.error(404, "函数不存在");
        }
        return ResponseVO.success(function);
    }

    @Operation(summary = "根据名称查询函数")
    @GetMapping("/name/{name}")
    public ResponseVO<Function> getByName(@PathVariable String name) {
        Function function = functionService.getByName(name);
        if (function == null) {
            return ResponseVO.error(404, "函数不存在");
        }
        return ResponseVO.success(function);
    }

    @Operation(summary = "分页查询函数列表")
    @GetMapping
    public ResponseVO<IPage<Function>> list(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) UUID domainId) {
        Page<Function> page = new Page<>(current, size);
        IPage<Function> result = functionService.page(page, domainId);
        return ResponseVO.success(result);
    }

    @Operation(summary = "删除函数")
    @DeleteMapping("/{id}")
    public ResponseVO<?> delete(@PathVariable UUID id) {
        functionService.deleteFunction(id);
        return ResponseVO.success();
    }
}

