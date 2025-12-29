package com.example.datamodel.controller.v1;

import com.example.datamodel.dto.ObjectTypeDataSourceTable;
import com.example.datamodel.entity.DataSource;
import com.example.datamodel.entity.ObjectTypeDataSource;
import com.example.datamodel.exception.BusinessException;
import com.example.datamodel.service.ObjectTypeDataSourceService;
import com.example.datamodel.vo.ResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * 对象类型-数据源关联管理控制器
 *
 * @author DataModel Team
 */
@Slf4j
@RestController
@RequestMapping("/v1/object-types/{objectTypeId}/datasources")
@Tag(name = "对象类型-数据源关联管理", description = "对象类型与数据源关联相关接口")
public class ObjectTypeDataSourceController {

    @Autowired
    private ObjectTypeDataSourceService objectTypeDataSourceService;

    @PostMapping
    @Operation(summary = "为对象类型添加数据源表关联")
    public ResponseVO<ObjectTypeDataSource> addDataSourceTable(
            @Parameter(description = "对象类型ID") @PathVariable UUID objectTypeId,
            @Parameter(description = "数据源ID") @RequestParam UUID datasourceId,
            @Parameter(description = "表名（is_storage=false时必填，is_storage=true时可为空）") @RequestParam(required = false) String tableName,
            @Parameter(description = "Schema名称（可选）") @RequestParam(required = false) String schemaName,
            @Parameter(description = "是否作为对象实例的存储库") @RequestParam(required = false, defaultValue = "false") Boolean isStorage,
            @Parameter(description = "是否为默认数据源") @RequestParam(required = false) Boolean isDefault,
            @Parameter(description = "优先级") @RequestParam(required = false) Integer priority) {
        try {
            ObjectTypeDataSource mapping = objectTypeDataSourceService.addDataSourceTableToObjectType(
                    objectTypeId, datasourceId, tableName, schemaName, isStorage, isDefault, priority);
            return ResponseVO.success(mapping);
        } catch (BusinessException e) {
            return ResponseVO.error(e.getMessage());
        } catch (Exception e) {
            log.error("添加数据源表关联失败", e);
            return ResponseVO.error("添加数据源表关联失败：" + e.getMessage());
        }
    }

    @PostMapping("/deprecated")
    @Operation(summary = "为对象类型添加数据源（已废弃）", deprecated = true)
    @Deprecated
    public ResponseVO<ObjectTypeDataSource> addDataSource(
            @Parameter(description = "对象类型ID") @PathVariable UUID objectTypeId,
            @Parameter(description = "数据源ID") @RequestParam UUID datasourceId,
            @Parameter(description = "是否为默认数据源") @RequestParam(required = false) Boolean isDefault,
            @Parameter(description = "优先级") @RequestParam(required = false) Integer priority) {
        return ResponseVO.error("该接口已废弃，请使用 POST /v1/object-types/{objectTypeId}/datasources，并指定 tableName 参数");
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "移除对象类型的数据源表关联（通过关联ID）")
    public ResponseVO<Void> removeDataSourceTable(
            @Parameter(description = "对象类型ID") @PathVariable UUID objectTypeId,
            @Parameter(description = "关联ID") @PathVariable UUID id) {
        try {
            objectTypeDataSourceService.removeDataSourceTableById(id);
            return ResponseVO.success(null);
        } catch (BusinessException e) {
            return ResponseVO.error(e.getMessage());
        } catch (Exception e) {
            log.error("移除数据源表关联失败", e);
            return ResponseVO.error("移除数据源表关联失败：" + e.getMessage());
        }
    }

    @PutMapping("/{id}/default")
    @Operation(summary = "设置默认数据源表")
    public ResponseVO<Void> setDefaultDataSourceTable(
            @Parameter(description = "对象类型ID") @PathVariable UUID objectTypeId,
            @Parameter(description = "关联ID") @PathVariable UUID id) {
        try {
            objectTypeDataSourceService.setDefaultDataSourceTable(objectTypeId, id);
            return ResponseVO.success(null);
        } catch (BusinessException e) {
            return ResponseVO.error(e.getMessage());
        } catch (Exception e) {
            log.error("设置默认数据源表失败", e);
            return ResponseVO.error("设置默认数据源表失败：" + e.getMessage());
        }
    }

    @GetMapping("/tables")
    @Operation(summary = "获取对象类型关联的所有数据源表")
    public ResponseVO<List<ObjectTypeDataSourceTable>> getDataSourceTables(
            @Parameter(description = "对象类型ID") @PathVariable UUID objectTypeId) {
        try {
            List<ObjectTypeDataSourceTable> tables = objectTypeDataSourceService.getDataSourceTablesByObjectTypeId(objectTypeId);
            return ResponseVO.success(tables);
        } catch (Exception e) {
            log.error("查询数据源表列表失败", e);
            return ResponseVO.error("查询数据源表列表失败：" + e.getMessage());
        }
    }

    @GetMapping("/tables/default")
    @Operation(summary = "获取对象类型的默认数据源表")
    public ResponseVO<ObjectTypeDataSourceTable> getDefaultDataSourceTable(
            @Parameter(description = "对象类型ID") @PathVariable UUID objectTypeId) {
        try {
            ObjectTypeDataSourceTable table = objectTypeDataSourceService.getDefaultDataSourceTable(objectTypeId);
            if (table == null) {
                return ResponseVO.error("未设置默认数据源表");
            }
            return ResponseVO.success(table);
        } catch (Exception e) {
            log.error("查询默认数据源表失败", e);
            return ResponseVO.error("查询默认数据源表失败：" + e.getMessage());
        }
    }

    @GetMapping
    @Operation(summary = "获取对象类型关联的所有数据源（已废弃）", deprecated = true)
    @Deprecated
    public ResponseVO<List<DataSource>> getDataSources(
            @Parameter(description = "对象类型ID") @PathVariable UUID objectTypeId) {
        return ResponseVO.error("该接口已废弃，请使用 GET /v1/object-types/{objectTypeId}/datasources/tables");
    }

    @GetMapping("/default")
    @Operation(summary = "获取对象类型的默认数据源（已废弃）", deprecated = true)
    @Deprecated
    public ResponseVO<DataSource> getDefaultDataSource(
            @Parameter(description = "对象类型ID") @PathVariable UUID objectTypeId) {
        return ResponseVO.error("该接口已废弃，请使用 GET /v1/object-types/{objectTypeId}/datasources/tables/default");
    }
}


