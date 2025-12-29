package com.example.datamodel.controller.v1;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.datamodel.core.datasource.TableColumnInfo;
import com.example.datamodel.entity.DataSource;
import com.example.datamodel.exception.BusinessException;
import com.example.datamodel.service.DataSourceService;
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
 * 数据源管理控制器
 *
 * @author DataModel Team
 */
@Slf4j
@RestController
@RequestMapping("/v1/datasources")
@Tag(name = "数据源管理", description = "数据源相关接口")
public class DataSourceController {

    @Autowired
    private DataSourceService dataSourceService;

    @PostMapping
    @Operation(summary = "创建数据源")
    public ResponseVO<DataSource> createDataSource(@RequestBody DataSource datasource) {
        try {
            DataSource created = dataSourceService.createDataSource(datasource);
            return ResponseVO.success(created);
        } catch (BusinessException e) {
            return ResponseVO.error(e.getMessage());
        } catch (Exception e) {
            log.error("创建数据源失败", e);
            return ResponseVO.error("创建数据源失败：" + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新数据源")
    public ResponseVO<DataSource> updateDataSource(
            @Parameter(description = "数据源ID") @PathVariable UUID id,
            @RequestBody DataSource datasource) {
        try {
            DataSource updated = dataSourceService.updateDataSource(id, datasource);
            return ResponseVO.success(updated);
        } catch (BusinessException e) {
            return ResponseVO.error(e.getMessage());
        } catch (Exception e) {
            log.error("更新数据源失败", e);
            return ResponseVO.error("更新数据源失败：" + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除数据源")
    public ResponseVO<Void> deleteDataSource(@Parameter(description = "数据源ID") @PathVariable UUID id) {
        try {
            // TODO: 检查是否被对象类型使用
            dataSourceService.removeById(id);
            return ResponseVO.success(null);
        } catch (BusinessException e) {
            return ResponseVO.error(e.getMessage());
        } catch (Exception e) {
            log.error("删除数据源失败", e);
            return ResponseVO.error("删除数据源失败：" + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询数据源")
    public ResponseVO<DataSource> getDataSourceById(@Parameter(description = "数据源ID") @PathVariable UUID id) {
        try {
            DataSource datasource = dataSourceService.getDataSourceById(id);
            if (datasource == null) {
                return ResponseVO.error("数据源不存在");
            }
            return ResponseVO.success(datasource);
        } catch (Exception e) {
            log.error("查询数据源失败", e);
            return ResponseVO.error("查询数据源失败：" + e.getMessage());
        }
    }

    @GetMapping
    @Operation(summary = "分页查询数据源列表")
    public ResponseVO<IPage<DataSource>> listDataSources(
            @Parameter(description = "数据源类型") @RequestParam(required = false) String type,
            @Parameter(description = "业务域ID") @RequestParam(required = false) UUID domainId,
            @Parameter(description = "当前页") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size) {
        try {
            Page<DataSource> page = new Page<>(current, size);
            IPage<DataSource> result = dataSourceService.listDataSources(page, type, domainId);
            return ResponseVO.success(result);
        } catch (Exception e) {
            log.error("查询数据源列表失败", e);
            return ResponseVO.error("查询数据源列表失败：" + e.getMessage());
        }
    }

    @PostMapping("/{id}/test")
    @Operation(summary = "测试数据源连接")
    public ResponseVO<Boolean> testConnection(@Parameter(description = "数据源ID") @PathVariable UUID id) {
        try {
            boolean success = dataSourceService.testConnection(id);
            return ResponseVO.success(success);
        } catch (BusinessException e) {
            return ResponseVO.error(e.getMessage());
        } catch (Exception e) {
            log.error("测试数据源连接失败", e);
            return ResponseVO.error("测试数据源连接失败：" + e.getMessage());
        }
    }

    @PutMapping("/{id}/enabled")
    @Operation(summary = "启用/禁用数据源")
    public ResponseVO<Void> setEnabled(
            @Parameter(description = "数据源ID") @PathVariable UUID id,
            @Parameter(description = "是否启用") @RequestParam Boolean enabled) {
        try {
            dataSourceService.setEnabled(id, enabled);
            return ResponseVO.success(null);
        } catch (BusinessException e) {
            return ResponseVO.error(e.getMessage());
        } catch (Exception e) {
            log.error("设置数据源启用状态失败", e);
            return ResponseVO.error("设置数据源启用状态失败：" + e.getMessage());
        }
    }

    @GetMapping("/{id}/tables")
    @Operation(summary = "获取数据源下的表列表")
    public ResponseVO<List<String>> getTableList(
            @Parameter(description = "数据源ID") @PathVariable UUID id,
            @Parameter(description = "Schema名称（可选）") @RequestParam(required = false) String schemaName) {
        try {
            List<String> tables = dataSourceService.getTableList(id, schemaName);
            return ResponseVO.success(tables);
        } catch (BusinessException e) {
            return ResponseVO.error(e.getMessage());
        } catch (Exception e) {
            log.error("获取表列表失败", e);
            return ResponseVO.error("获取表列表失败：" + e.getMessage());
        }
    }

    @GetMapping("/{id}/tables/{tableName}/columns")
    @Operation(summary = "获取表的字段信息")
    public ResponseVO<List<TableColumnInfo>> getTableColumns(
            @Parameter(description = "数据源ID") @PathVariable UUID id,
            @Parameter(description = "Schema名称（可选）") @RequestParam(required = false) String schemaName,
            @Parameter(description = "表名") @PathVariable String tableName) {
        try {
            List<TableColumnInfo> columns = dataSourceService.getTableColumns(id, schemaName, tableName);
            return ResponseVO.success(columns);
        } catch (BusinessException e) {
            return ResponseVO.error(e.getMessage());
        } catch (Exception e) {
            log.error("获取表字段信息失败", e);
            return ResponseVO.error("获取表字段信息失败：" + e.getMessage());
        }
    }

    @GetMapping("/{id}/schemas")
    @Operation(summary = "获取数据源的Schema列表（如果支持）")
    public ResponseVO<List<String>> getSchemaList(
            @Parameter(description = "数据源ID") @PathVariable UUID id) {
        try {
            List<String> schemas = dataSourceService.getSchemaList(id);
            return ResponseVO.success(schemas);
        } catch (BusinessException e) {
            return ResponseVO.error(e.getMessage());
        } catch (Exception e) {
            log.error("获取Schema列表失败", e);
            return ResponseVO.error("获取Schema列表失败：" + e.getMessage());
        }
    }
}

