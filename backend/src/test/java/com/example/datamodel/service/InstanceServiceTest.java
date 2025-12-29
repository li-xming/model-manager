package com.example.datamodel.service;

import com.example.datamodel.dto.InstanceDTO;
import com.example.datamodel.dto.ObjectTypeDTO;
import com.example.datamodel.dto.PropertyDTO;
import com.example.datamodel.entity.ObjectType;
import com.example.datamodel.entity.Property;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 实例服务测试
 *
 * @author DataModel Team
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class InstanceServiceTest {

    @Autowired
    private InstanceService instanceService;

    @Autowired
    private ObjectTypeService objectTypeService;

    @Autowired
    private PropertyService propertyService;

    private String objectTypeName = "TestProduct";

    @BeforeEach
    void setUp() {
        // 创建测试用的对象类型
        ObjectTypeDTO objectTypeDTO = new ObjectTypeDTO();
        objectTypeDTO.setName(objectTypeName);
        objectTypeDTO.setDisplayName("测试产品");
        ObjectType objectType = objectTypeService.createObjectType(objectTypeDTO);

        // 创建属性
        PropertyDTO prop1 = new PropertyDTO();
        prop1.setName("name");
        prop1.setDataType("STRING");
        prop1.setRequired(true);
        propertyService.createProperty(objectType.getId(), prop1);

        PropertyDTO prop2 = new PropertyDTO();
        prop2.setName("price");
        prop2.setDataType("FLOAT");
        propertyService.createProperty(objectType.getId(), prop2);

        PropertyDTO prop3 = new PropertyDTO();
        prop3.setName("stock");
        prop3.setDataType("INTEGER");
        prop3.setDefaultValue("0");
        propertyService.createProperty(objectType.getId(), prop3);
    }

    @Test
    void testCreateInstance() {
        InstanceDTO dto = new InstanceDTO();
        Map<String, Object> properties = new HashMap<>();
        properties.put("name", "测试产品1");
        properties.put("price", 99.99);
        properties.put("stock", 100);
        dto.setProperties(properties);

        Map<String, Object> instance = instanceService.createInstance(objectTypeName, dto);

        assertNotNull(instance);
        assertNotNull(instance.get("id"));
        assertEquals("测试产品1", instance.get("name"));
        assertEquals(99.99, instance.get("price"));
    }

    @Test
    void testGetInstance() {
        // 先创建一个实例
        InstanceDTO dto = new InstanceDTO();
        Map<String, Object> properties = new HashMap<>();
        properties.put("name", "测试产品2");
        properties.put("price", 199.99);
        Map<String, Object> created = instanceService.createInstance(objectTypeName, dto);

        UUID instanceId = UUID.fromString(created.get("id").toString());
        Map<String, Object> retrieved = instanceService.getInstance(objectTypeName, instanceId);

        assertNotNull(retrieved);
        assertEquals(created.get("id"), retrieved.get("id"));
        assertEquals("测试产品2", retrieved.get("name"));
    }

    @Test
    void testUpdateInstance() {
        // 先创建一个实例
        InstanceDTO createDto = new InstanceDTO();
        Map<String, Object> properties = new HashMap<>();
        properties.put("name", "原始名称");
        properties.put("price", 50.0);
        Map<String, Object> created = instanceService.createInstance(objectTypeName, createDto);

        // 更新实例
        UUID instanceId = UUID.fromString(created.get("id").toString());
        InstanceDTO updateDto = new InstanceDTO();
        Map<String, Object> updateProperties = new HashMap<>();
        updateProperties.put("price", 75.0);
        updateDto.setProperties(updateProperties);

        Map<String, Object> updated = instanceService.updateInstance(objectTypeName, instanceId, updateDto);

        assertNotNull(updated);
        assertEquals("原始名称", updated.get("name")); // 未更新的字段保持不变
        assertEquals(75.0, updated.get("price")); // 更新的字段
    }

    @Test
    void testListInstances() {
        // 创建几个实例
        for (int i = 1; i <= 5; i++) {
            InstanceDTO dto = new InstanceDTO();
            Map<String, Object> properties = new HashMap<>();
            properties.put("name", "产品" + i);
            properties.put("price", 10.0 * i);
            dto.setProperties(properties);
            instanceService.createInstance(objectTypeName, dto);
        }

        IPage<Map<String, Object>> page = instanceService.listInstances(objectTypeName, 1L, 10L, null);

        assertNotNull(page);
        assertTrue(page.getTotal() >= 5);
        assertEquals(5, page.getRecords().size());
    }

    @Test
    void testListInstancesWithFilters() {
        // 创建几个实例
        InstanceDTO dto1 = new InstanceDTO();
        Map<String, Object> props1 = new HashMap<>();
        props1.put("name", "高价格产品");
        props1.put("price", 1000.0);
        dto1.setProperties(props1);
        instanceService.createInstance(objectTypeName, dto1);

        InstanceDTO dto2 = new InstanceDTO();
        Map<String, Object> props2 = new HashMap<>();
        props2.put("name", "低价格产品");
        props2.put("price", 10.0);
        dto2.setProperties(props2);
        instanceService.createInstance(objectTypeName, dto2);

        // 使用过滤器查询
        Map<String, Object> filters = new HashMap<>();
        filters.put("price", 1000.0);
        IPage<Map<String, Object>> page = instanceService.listInstances(objectTypeName, 1L, 10L, filters);

        assertNotNull(page);
        assertTrue(page.getTotal() >= 1);
        // 验证查询结果
        boolean found = page.getRecords().stream()
                .anyMatch(instance -> instance.get("price").equals(1000.0));
        assertTrue(found);
    }

    @Test
    void testDeleteInstance() {
        // 先创建一个实例
        InstanceDTO dto = new InstanceDTO();
        Map<String, Object> properties = new HashMap<>();
        properties.put("name", "待删除产品");
        properties.put("price", 50.0);
        Map<String, Object> created = instanceService.createInstance(objectTypeName, dto);

        UUID instanceId = UUID.fromString(created.get("id").toString());
        instanceService.deleteInstance(objectTypeName, instanceId);

        // 验证已删除
        Map<String, Object> deleted = instanceService.getInstance(objectTypeName, instanceId);
        assertNull(deleted);
    }

    @Test
    void testBatchDeleteInstances() {
        // 创建几个实例
        java.util.List<UUID> idsToDelete = new java.util.ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            InstanceDTO dto = new InstanceDTO();
            Map<String, Object> properties = new HashMap<>();
            properties.put("name", "批量删除产品" + i);
            properties.put("price", 10.0 * i);
            Map<String, Object> created = instanceService.createInstance(objectTypeName, dto);
            idsToDelete.add(UUID.fromString(created.get("id").toString()));
        }

        instanceService.batchDeleteInstances(objectTypeName, idsToDelete);

        // 验证都已删除
        for (UUID id : idsToDelete) {
            Map<String, Object> deleted = instanceService.getInstance(objectTypeName, id);
            assertNull(deleted);
        }
    }

    @Test
    void testCreateInstanceWithRequiredField() {
        InstanceDTO dto = new InstanceDTO();
        Map<String, Object> properties = new HashMap<>();
        // 缺少必需的name字段
        properties.put("price", 99.99);
        dto.setProperties(properties);

        assertThrows(Exception.class, () -> {
            instanceService.createInstance(objectTypeName, dto);
        });
    }

    @Test
    void testCreateInstanceWithDefaultValue() {
        InstanceDTO dto = new InstanceDTO();
        Map<String, Object> properties = new HashMap<>();
        properties.put("name", "使用默认值的产品");
        properties.put("price", 99.99);
        // stock使用默认值
        dto.setProperties(properties);

        Map<String, Object> instance = instanceService.createInstance(objectTypeName, dto);

        assertNotNull(instance);
        // stock应该有默认值
        assertNotNull(instance.get("stock"));
    }
}

