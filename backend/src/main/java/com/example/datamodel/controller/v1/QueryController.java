package com.example.datamodel.controller.v1;

import com.example.datamodel.core.QueryEngine;
import com.example.datamodel.entity.LinkInstance;
import com.example.datamodel.service.InstanceService;
import com.example.datamodel.vo.ResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 查询控制器
 *
 * @author DataModel Team
 */
@Slf4j
@Tag(name = "查询引擎", description = "查询和关系查询相关接口")
@RestController
@RequestMapping("/v1/query")
public class QueryController {

    @Autowired
    private QueryEngine queryEngine;

    @Autowired
    private InstanceService instanceService;

    @Operation(summary = "查询路径")
    @GetMapping("/path")
    public ResponseVO<List<List<LinkInstance>>> findPath(
            @RequestParam String sourceObjectType,
            @RequestParam UUID sourceInstanceId,
            @RequestParam String targetObjectType,
            @RequestParam UUID targetInstanceId,
            @RequestParam(defaultValue = "5") int maxDepth) {
        List<List<LinkInstance>> paths = queryEngine.findPaths(
                sourceObjectType, sourceInstanceId,
                targetObjectType, targetInstanceId,
                maxDepth);
        return ResponseVO.success(paths);
    }

    @Operation(summary = "查询邻居节点")
    @GetMapping("/neighbors")
    public ResponseVO<List<Map<String, Object>>> findNeighbors(
            @RequestParam UUID instanceId,
            @RequestParam(required = false) String linkTypeName,
            @RequestParam(required = false) String objectType) {
        List<UUID> neighborIds = queryEngine.findNeighbors(instanceId, linkTypeName);

        // 如果指定了对象类型，返回完整的实例信息
        if (objectType != null && !objectType.isEmpty()) {
            List<Map<String, Object>> neighbors = neighborIds.stream()
                    .map(id -> instanceService.getInstance(objectType, id))
                    .filter(instance -> instance != null)
                    .collect(Collectors.toList());
            return ResponseVO.success(neighbors);
        } else {
            // 否则只返回ID列表
            List<Map<String, Object>> result = neighborIds.stream()
                    .map(id -> {
                        Map<String, Object> item = new java.util.HashMap<>();
                        item.put("id", id);
                        return item;
                    })
                    .collect(Collectors.toList());
            return ResponseVO.success(result);
        }
    }

    @Operation(summary = "查询指定深度的所有关联实例")
    @GetMapping("/related")
    public ResponseVO<List<UUID>> findRelated(
            @RequestParam UUID instanceId,
            @RequestParam(defaultValue = "2") int depth) {
        List<UUID> relatedIds = queryEngine.findRelatedInstances(instanceId, depth);
        return ResponseVO.success(relatedIds);
    }

    // ==================== 对象类型关系查询（模型级别） ====================

    @Operation(summary = "查询对象类型的所有链接类型")
    @GetMapping("/object-type/link-types")
    public ResponseVO<List<com.example.datamodel.entity.LinkType>> findLinkTypesByObjectType(
            @RequestParam String objectTypeName) {
        List<com.example.datamodel.entity.LinkType> linkTypes = queryEngine.findLinkTypesByObjectType(objectTypeName);
        return ResponseVO.success(linkTypes);
    }

    @Operation(summary = "查询对象类型之间的关系路径")
    @GetMapping("/object-type/path")
    public ResponseVO<List<List<com.example.datamodel.entity.LinkType>>> findObjectTypePath(
            @RequestParam String sourceObjectTypeName,
            @RequestParam String targetObjectTypeName,
            @RequestParam(defaultValue = "5") int maxDepth) {
        List<List<com.example.datamodel.entity.LinkType>> paths = queryEngine.findObjectTypePaths(
                sourceObjectTypeName, targetObjectTypeName, maxDepth);
        return ResponseVO.success(paths);
    }

    @Operation(summary = "查询对象类型的可达对象类型")
    @GetMapping("/object-type/reachable")
    public ResponseVO<List<com.example.datamodel.entity.ObjectType>> findReachableObjectTypes(
            @RequestParam String objectTypeName,
            @RequestParam(defaultValue = "2") int depth) {
        List<com.example.datamodel.entity.ObjectType> types = queryEngine.findReachableObjectTypes(objectTypeName, depth);
        return ResponseVO.success(types);
    }
}

