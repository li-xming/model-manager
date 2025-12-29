package com.example.datamodel.controller.v1;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.datamodel.entity.BusinessDomain;
import com.example.datamodel.service.BusinessDomainService;
import com.example.datamodel.vo.ResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 业务域控制器
 *
 * 提供业务域的基础增删改查能力
 *
 * @author DataModel Team
 */
@Slf4j
@Tag(name = "业务域管理", description = "业务域相关接口")
@RestController
@RequestMapping("/v1/domains")
public class BusinessDomainController {

    @Autowired
    private BusinessDomainService businessDomainService;

    @Operation(summary = "创建业务域")
    @PostMapping
    public ResponseVO<BusinessDomain> create(@RequestBody BusinessDomain domain) {
        domain.setId(null);
        domain.setCreatedAt(LocalDateTime.now());
        domain.setUpdatedAt(LocalDateTime.now());
        businessDomainService.save(domain);
        return ResponseVO.success(domain);
    }

    @Operation(summary = "更新业务域")
    @PutMapping("/{id}")
    public ResponseVO<BusinessDomain> update(@PathVariable UUID id, @RequestBody BusinessDomain domain) {
        BusinessDomain existing = businessDomainService.getById(id);
        if (existing == null) {
            return ResponseVO.error(404, "业务域不存在");
        }
        domain.setId(id);
        domain.setCreatedAt(existing.getCreatedAt());
        domain.setUpdatedAt(LocalDateTime.now());
        businessDomainService.updateById(domain);
        return ResponseVO.success(domain);
    }

    @Operation(summary = "根据ID查询业务域")
    @GetMapping("/{id}")
    public ResponseVO<BusinessDomain> getById(@PathVariable UUID id) {
        BusinessDomain domain = businessDomainService.getById(id);
        if (domain == null) {
            return ResponseVO.error(404, "业务域不存在");
        }
        return ResponseVO.success(domain);
    }

    @Operation(summary = "分页查询业务域列表")
    @GetMapping
    public ResponseVO<IPage<BusinessDomain>> list(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size) {
        Page<BusinessDomain> page = new Page<>(current, size);
        IPage<BusinessDomain> result = businessDomainService.page(page);
        return ResponseVO.success(result);
    }

    @Operation(summary = "删除业务域")
    @DeleteMapping("/{id}")
    public ResponseVO<?> delete(@PathVariable UUID id) {
        businessDomainService.removeById(id);
        return ResponseVO.success();
    }
}


