package com.example.datamodel.controller.v1;

import com.example.datamodel.dto.InstanceDTO;
import com.example.datamodel.dto.ObjectTypeDTO;
import com.example.datamodel.dto.PropertyDTO;
import com.example.datamodel.entity.ObjectType;
import com.example.datamodel.service.InstanceService;
import com.example.datamodel.service.ObjectTypeService;
import com.example.datamodel.service.PropertyService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 实例Controller测试
 *
 * @author DataModel Team
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class InstanceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ObjectTypeService objectTypeService;

    @Autowired
    private PropertyService propertyService;

    @Autowired
    private InstanceService instanceService;

    private String objectTypeName = "TestProduct";

    @BeforeEach
    void setUp() {
        // 创建测试用的对象类型
        try {
            ObjectType existing = objectTypeService.getByName(objectTypeName);
            if (existing != null) {
                objectTypeService.deleteObjectType(existing.getId());
            }
        } catch (Exception e) {
            // 忽略错误
        }

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
    }

    @Test
    void testCreateInstance() throws Exception {
        InstanceDTO dto = new InstanceDTO();
        Map<String, Object> properties = new HashMap<>();
        properties.put("name", "测试产品1");
        properties.put("price", 99.99);
        dto.setProperties(properties);

        mockMvc.perform(post("/v1/instances/{objectType}", objectTypeName)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.name").value("测试产品1"))
                .andExpect(jsonPath("$.data.price").value(99.99));
    }

    @Test
    void testGetInstance() throws Exception {
        // 先创建一个实例
        InstanceDTO dto = new InstanceDTO();
        Map<String, Object> properties = new HashMap<>();
        properties.put("name", "测试产品2");
        properties.put("price", 199.99);
        Map<String, Object> created = instanceService.createInstance(objectTypeName, dto);
        UUID instanceId = UUID.fromString(created.get("id").toString());

        mockMvc.perform(get("/v1/instances/{objectType}/{id}", objectTypeName, instanceId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(instanceId.toString()))
                .andExpect(jsonPath("$.data.name").value("测试产品2"));
    }

    @Test
    void testListInstances() throws Exception {
        // 创建几个实例
        for (int i = 1; i <= 3; i++) {
            InstanceDTO dto = new InstanceDTO();
            Map<String, Object> properties = new HashMap<>();
            properties.put("name", "产品" + i);
            properties.put("price", 10.0 * i);
            dto.setProperties(properties);
            instanceService.createInstance(objectTypeName, dto);
        }

        mockMvc.perform(get("/v1/instances/{objectType}", objectTypeName)
                        .param("current", "1")
                        .param("size", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.records").isArray())
                .andExpect(jsonPath("$.data.total").value(3));
    }

    @Test
    void testUpdateInstance() throws Exception {
        // 先创建一个实例
        InstanceDTO createDto = new InstanceDTO();
        Map<String, Object> properties = new HashMap<>();
        properties.put("name", "原始名称");
        properties.put("price", 50.0);
        Map<String, Object> created = instanceService.createInstance(objectTypeName, createDto);

        UUID instanceId = UUID.fromString(created.get("id").toString());

        // 更新实例
        InstanceDTO updateDto = new InstanceDTO();
        Map<String, Object> updateProperties = new HashMap<>();
        updateProperties.put("price", 75.0);
        updateDto.setProperties(updateProperties);

        mockMvc.perform(put("/v1/instances/{objectType}/{id}", objectTypeName, instanceId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.price").value(75.0));
    }

    @Test
    void testDeleteInstance() throws Exception {
        // 先创建一个实例
        InstanceDTO dto = new InstanceDTO();
        Map<String, Object> properties = new HashMap<>();
        properties.put("name", "待删除产品");
        properties.put("price", 50.0);
        Map<String, Object> created = instanceService.createInstance(objectTypeName, dto);

        UUID instanceId = UUID.fromString(created.get("id").toString());

        mockMvc.perform(delete("/v1/instances/{objectType}/{id}", objectTypeName, instanceId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        // 验证已删除
        mockMvc.perform(get("/v1/instances/{objectType}/{id}", objectTypeName, instanceId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(404));
    }
}

