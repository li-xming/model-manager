package com.example.datamodel.controller.v1;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.datamodel.dto.LinkTypeDTO;
import com.example.datamodel.entity.LinkType;
import com.example.datamodel.service.LinkTypeService;
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
 * 链接类型控制器
 *
 * @author DataModel Team
 */
@Slf4j
@Tag(name = "链接类型管理", description = "链接类型相关接口")
@RestController
@RequestMapping("/v1/link-types")
public class LinkTypeController {

    @Autowired
    private LinkTypeService linkTypeService;

    @Operation(summary = "创建链接类型")
    @PostMapping
    public ResponseVO<LinkType> create(@Validated @RequestBody LinkTypeDTO dto) {
        LinkType linkType = linkTypeService.createLinkType(dto);
        return ResponseVO.success(linkType);
    }

    @Operation(summary = "更新链接类型")
    @PutMapping("/{id}")
    public ResponseVO<LinkType> update(@PathVariable UUID id, @Validated @RequestBody LinkTypeDTO dto) {
        LinkType linkType = linkTypeService.updateLinkType(id, dto);
        return ResponseVO.success(linkType);
    }

    @Operation(summary = "根据ID查询链接类型")
    @GetMapping("/{id}")
    public ResponseVO<LinkType> getById(@PathVariable UUID id) {
        LinkType linkType = linkTypeService.getById(id);
        if (linkType == null) {
            return ResponseVO.error(404, "链接类型不存在");
        }
        return ResponseVO.success(linkType);
    }

    @Operation(summary = "根据名称查询链接类型")
    @GetMapping("/name/{name}")
    public ResponseVO<LinkType> getByName(@PathVariable String name) {
        LinkType linkType = linkTypeService.getByName(name);
        if (linkType == null) {
            return ResponseVO.error(404, "链接类型不存在");
        }
        return ResponseVO.success(linkType);
    }

    @Operation(summary = "根据对象类型ID查询相关链接类型")
    @GetMapping("/object-type/{objectTypeId}")
    public ResponseVO<List<LinkType>> getByObjectTypeId(@PathVariable UUID objectTypeId) {
        List<LinkType> linkTypes = linkTypeService.getByObjectTypeId(objectTypeId);
        return ResponseVO.success(linkTypes);
    }

    @Operation(summary = "分页查询链接类型列表")
    @GetMapping
    public ResponseVO<IPage<LinkType>> list(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) UUID domainId) {
        Page<LinkType> page = new Page<>(current, size);
        IPage<LinkType> result = linkTypeService.page(page, domainId);
        return ResponseVO.success(result);
    }

    @Operation(summary = "删除链接类型")
    @DeleteMapping("/{id}")
    public ResponseVO<?> delete(@PathVariable UUID id) {
        linkTypeService.deleteLinkType(id);
        return ResponseVO.success();
    }
}

