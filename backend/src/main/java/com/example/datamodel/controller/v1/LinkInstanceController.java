package com.example.datamodel.controller.v1;

import com.example.datamodel.dto.LinkInstanceDTO;
import com.example.datamodel.entity.LinkInstance;
import com.example.datamodel.service.LinkInstanceService;
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
 * 链接实例控制器
 *
 * @author DataModel Team
 */
@Slf4j
@Tag(name = "链接实例管理", description = "链接实例相关接口")
@RestController
@RequestMapping("/v1/links")
public class LinkInstanceController {

    @Autowired
    private LinkInstanceService linkInstanceService;

    @Operation(summary = "创建链接实例")
    @PostMapping
    public ResponseVO<LinkInstance> create(@Validated @RequestBody LinkInstanceDTO dto) {
        LinkInstance linkInstance = linkInstanceService.createLinkInstance(dto);
        return ResponseVO.success(linkInstance);
    }

    @Operation(summary = "根据ID查询链接实例")
    @GetMapping("/{id}")
    public ResponseVO<LinkInstance> getById(@PathVariable UUID id) {
        LinkInstance linkInstance = linkInstanceService.getById(id);
        if (linkInstance == null) {
            return ResponseVO.error(404, "链接实例不存在");
        }
        return ResponseVO.success(linkInstance);
    }

    @Operation(summary = "根据链接类型和源实例查询目标实例")
    @GetMapping("/link-type/{linkTypeId}/source/{sourceInstanceId}")
    public ResponseVO<List<LinkInstance>> getByLinkTypeAndSource(
            @PathVariable UUID linkTypeId,
            @PathVariable UUID sourceInstanceId) {
        List<LinkInstance> linkInstances = linkInstanceService.getByLinkTypeAndSource(linkTypeId, sourceInstanceId);
        return ResponseVO.success(linkInstances);
    }

    @Operation(summary = "根据链接类型和目标实例查询源实例")
    @GetMapping("/link-type/{linkTypeId}/target/{targetInstanceId}")
    public ResponseVO<List<LinkInstance>> getByLinkTypeAndTarget(
            @PathVariable UUID linkTypeId,
            @PathVariable UUID targetInstanceId) {
        List<LinkInstance> linkInstances = linkInstanceService.getByLinkTypeAndTarget(linkTypeId, targetInstanceId);
        return ResponseVO.success(linkInstances);
    }

    @Operation(summary = "根据实例ID查询所有关联的链接实例")
    @GetMapping("/instance/{instanceId}")
    public ResponseVO<List<LinkInstance>> getByInstanceId(@PathVariable UUID instanceId) {
        List<LinkInstance> linkInstances = linkInstanceService.getByInstanceId(instanceId);
        return ResponseVO.success(linkInstances);
    }

    @Operation(summary = "根据链接类型ID查询所有链接实例")
    @GetMapping("/link-type/{linkTypeId}")
    public ResponseVO<List<LinkInstance>> getByLinkTypeId(@PathVariable UUID linkTypeId) {
        List<LinkInstance> linkInstances = linkInstanceService.getByLinkTypeId(linkTypeId);
        return ResponseVO.success(linkInstances);
    }

    @Operation(summary = "删除链接实例")
    @DeleteMapping("/{id}")
    public ResponseVO<?> delete(@PathVariable UUID id) {
        linkInstanceService.deleteLinkInstance(id);
        return ResponseVO.success();
    }
}

