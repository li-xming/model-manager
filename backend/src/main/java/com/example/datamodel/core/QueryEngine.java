package com.example.datamodel.core;

import com.example.datamodel.entity.LinkInstance;
import com.example.datamodel.entity.LinkType;
import com.example.datamodel.entity.ObjectType;
import com.example.datamodel.service.LinkInstanceService;
import com.example.datamodel.service.LinkTypeService;
import com.example.datamodel.service.ObjectTypeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 查询引擎
 * 提供关系查询和路径查找功能
 * 分为两类查询：
 * 1. 对象类型关系查询（模型级别）：查询对象类型之间的链接类型关系
 * 2. 实例关系查询（数据级别）：查询实例之间的链接实例关系
 *
 * @author DataModel Team
 */
@Slf4j
@Component
public class QueryEngine {

    @Autowired
    private LinkInstanceService linkInstanceService;

    @Autowired
    private LinkTypeService linkTypeService;

    @Autowired
    private ObjectTypeService objectTypeService;

    // ==================== 对象类型关系查询（模型级别） ====================

    /**
     * 查询对象类型的所有链接类型
     * 返回该对象类型作为源或目标的所有链接类型
     *
     * @param objectTypeName 对象类型名称
     * @return 链接类型列表
     */
    public List<LinkType> findLinkTypesByObjectType(String objectTypeName) {
        ObjectType objectType = objectTypeService.getByName(objectTypeName);
        if (objectType == null) {
            throw new RuntimeException("对象类型不存在：" + objectTypeName);
        }
        return linkTypeService.getByObjectTypeId(objectType.getId());
    }

    /**
     * 查询对象类型之间的关系路径
     * 查询从一个对象类型到另一个对象类型可以通过哪些链接类型连接
     *
     * @param sourceObjectTypeName 源对象类型名称
     * @param targetObjectTypeName 目标对象类型名称
     * @param maxDepth 最大深度
     * @return 路径列表（每个路径是一个LinkType列表）
     */
    public List<List<LinkType>> findObjectTypePaths(String sourceObjectTypeName, String targetObjectTypeName, int maxDepth) {
        ObjectType sourceType = objectTypeService.getByName(sourceObjectTypeName);
        ObjectType targetType = objectTypeService.getByName(targetObjectTypeName);
        
        if (sourceType == null) {
            throw new RuntimeException("源对象类型不存在：" + sourceObjectTypeName);
        }
        if (targetType == null) {
            throw new RuntimeException("目标对象类型不存在：" + targetObjectTypeName);
        }

        List<List<LinkType>> paths = new ArrayList<>();
        Set<UUID> visited = new HashSet<>();
        List<LinkType> currentPath = new ArrayList<>();

        dfsFindObjectTypePath(sourceType.getId(), targetType.getId(), visited, currentPath, paths, maxDepth, 0);

        return paths;
    }

    /**
     * 深度优先搜索查找对象类型路径
     */
    private void dfsFindObjectTypePath(UUID currentTypeId, UUID targetTypeId, Set<UUID> visited,
                                      List<LinkType> currentPath, List<List<LinkType>> paths,
                                      int maxDepth, int currentDepth) {
        if (currentDepth > maxDepth) {
            return;
        }

        if (currentTypeId.equals(targetTypeId)) {
            paths.add(new ArrayList<>(currentPath));
            return;
        }

        visited.add(currentTypeId);

        // 查找所有以当前对象类型为源或目标的链接类型
        List<LinkType> linkTypes = linkTypeService.getByObjectTypeId(currentTypeId);

        for (LinkType linkType : linkTypes) {
            UUID nextTypeId = null;
            boolean isSource = linkType.getSourceObjectTypeId().equals(currentTypeId);
            boolean isTarget = linkType.getTargetObjectTypeId().equals(currentTypeId);

            // 模型级对象类型关系查询视为无向图：只要当前类型参与该链接，就可到达另一端类型
            if (isSource) {
                nextTypeId = linkType.getTargetObjectTypeId();
            } else if (isTarget) {
                nextTypeId = linkType.getSourceObjectTypeId();
            }

            if (nextTypeId != null && !visited.contains(nextTypeId)) {
                currentPath.add(linkType);
                dfsFindObjectTypePath(nextTypeId, targetTypeId, visited, currentPath, paths, maxDepth, currentDepth + 1);
                currentPath.remove(currentPath.size() - 1);
            }
        }

        visited.remove(currentTypeId);
    }

    /**
     * 查询对象类型的可达对象类型
     * 查询某个对象类型可以通过链接类型连接到哪些其他对象类型
     *
     * @param objectTypeName 对象类型名称
     * @param depth 查询深度
     * @return 可达的对象类型列表
     */
    public List<ObjectType> findReachableObjectTypes(String objectTypeName, int depth) {
        ObjectType objectType = objectTypeService.getByName(objectTypeName);
        if (objectType == null) {
            throw new RuntimeException("对象类型不存在：" + objectTypeName);
        }

        Set<UUID> result = new HashSet<>();
        Set<UUID> currentLevel = new HashSet<>();
        currentLevel.add(objectType.getId());
        result.add(objectType.getId());

        for (int i = 0; i < depth; i++) {
            Set<UUID> nextLevel = new HashSet<>();
            for (UUID typeId : currentLevel) {
                List<LinkType> linkTypes = linkTypeService.getByObjectTypeId(typeId);
                for (LinkType linkType : linkTypes) {
                    UUID nextTypeId = null;
                    boolean isSource = linkType.getSourceObjectTypeId().equals(typeId);
                    boolean isTarget = linkType.getTargetObjectTypeId().equals(typeId);

                    // 模型级对象类型关系查询视为无向图：只要当前类型参与该链接，就可到达另一端类型
                    if (isSource) {
                        nextTypeId = linkType.getTargetObjectTypeId();
                    } else if (isTarget) {
                        nextTypeId = linkType.getSourceObjectTypeId();
                    }

                    if (nextTypeId != null && !result.contains(nextTypeId)) {
                        nextLevel.add(nextTypeId);
                        result.add(nextTypeId);
                    }
                }
            }
            currentLevel = nextLevel;
            if (currentLevel.isEmpty()) {
                break;
            }
        }

        result.remove(objectType.getId()); // 移除自身

        return result.stream()
                .map(id -> objectTypeService.getById(id))
                .filter(Objects::nonNull)
                .collect(java.util.stream.Collectors.toList());
    }

    // ==================== 实例关系查询（数据级别） ====================

    /**
     * 查询路径
     * 从一个实例到另一个实例的路径
     *
     * @param sourceObjectType 源对象类型
     * @param sourceInstanceId 源实例ID
     * @param targetObjectType 目标对象类型
     * @param targetInstanceId 目标实例ID
     * @param maxDepth 最大深度
     * @return 路径列表（每个路径是一个LinkInstance列表）
     */
    public List<List<LinkInstance>> findPaths(String sourceObjectType, UUID sourceInstanceId,
                                              String targetObjectType, UUID targetInstanceId,
                                              int maxDepth) {
        List<List<LinkInstance>> paths = new ArrayList<>();
        Set<UUID> visited = new HashSet<>();
        List<LinkInstance> currentPath = new ArrayList<>();

        dfsFindPath(sourceInstanceId, targetInstanceId, visited, currentPath, paths, maxDepth, 0);

        return paths;
    }

    /**
     * 深度优先搜索查找路径
     */
    private void dfsFindPath(UUID currentId, UUID targetId, Set<UUID> visited,
                             List<LinkInstance> currentPath, List<List<LinkInstance>> paths,
                             int maxDepth, int currentDepth) {
        if (currentDepth > maxDepth) {
            return;
        }

        if (currentId.equals(targetId)) {
            paths.add(new ArrayList<>(currentPath));
            return;
        }

        visited.add(currentId);

        // 查找所有关联的链接实例
        List<LinkInstance> linkInstances = linkInstanceService.getByInstanceId(currentId);

        for (LinkInstance linkInstance : linkInstances) {
            UUID nextId = linkInstance.getTargetInstanceId().equals(currentId) ?
                    linkInstance.getSourceInstanceId() : linkInstance.getTargetInstanceId();

            if (!visited.contains(nextId)) {
                currentPath.add(linkInstance);
                dfsFindPath(nextId, targetId, visited, currentPath, paths, maxDepth, currentDepth + 1);
                currentPath.remove(currentPath.size() - 1);
            }
        }

        visited.remove(currentId);
    }

    /**
     * 查询邻居节点
     * 获取与指定实例直接关联的所有实例
     *
     * @param instanceId 实例ID
     * @param linkTypeName 链接类型名称（可选，如果指定则只查询该类型的链接）
     * @return 邻居实例ID列表
     */
    public List<UUID> findNeighbors(UUID instanceId, String linkTypeName) {
        List<LinkInstance> linkInstances;

        if (linkTypeName != null && !linkTypeName.isEmpty()) {
            LinkType linkType = linkTypeService.getByName(linkTypeName);
            if (linkType == null) {
                throw new RuntimeException("链接类型不存在：" + linkTypeName);
            }
            // 获取作为源实例的链接
            List<LinkInstance> sourceLinks = linkInstanceService.getByLinkTypeAndSource(linkType.getId(), instanceId);
            // 获取作为目标实例的链接
            List<LinkInstance> targetLinks = linkInstanceService.getByLinkTypeAndTarget(linkType.getId(), instanceId);
            linkInstances = new ArrayList<>(sourceLinks);
            linkInstances.addAll(targetLinks);
        } else {
            linkInstances = linkInstanceService.getByInstanceId(instanceId);
        }

        Set<UUID> neighborIds = new HashSet<>();
        for (LinkInstance linkInstance : linkInstances) {
            if (linkInstance.getSourceInstanceId().equals(instanceId)) {
                neighborIds.add(linkInstance.getTargetInstanceId());
            } else {
                neighborIds.add(linkInstance.getSourceInstanceId());
            }
        }

        return new ArrayList<>(neighborIds);
    }

    /**
     * 查询指定深度的所有关联实例
     *
     * @param instanceId 实例ID
     * @param depth 深度
     * @return 实例ID列表
     */
    public List<UUID> findRelatedInstances(UUID instanceId, int depth) {
        Set<UUID> result = new HashSet<>();
        Set<UUID> currentLevel = new HashSet<>();
        currentLevel.add(instanceId);
        result.add(instanceId);

        for (int i = 0; i < depth; i++) {
            Set<UUID> nextLevel = new HashSet<>();
            for (UUID id : currentLevel) {
                List<UUID> neighbors = findNeighbors(id, null);
                for (UUID neighborId : neighbors) {
                    if (!result.contains(neighborId)) {
                        nextLevel.add(neighborId);
                        result.add(neighborId);
                    }
                }
            }
            currentLevel = nextLevel;
            if (currentLevel.isEmpty()) {
                break;
            }
        }

        result.remove(instanceId); // 移除自身
        return new ArrayList<>(result);
    }
}

