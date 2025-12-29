package com.example.datamodel.controller.v1;

import com.example.datamodel.dto.ObjectTypeDTO;
import com.example.datamodel.dto.PropertyDTO;
import com.example.datamodel.entity.ObjectType;
import com.example.datamodel.entity.Property;
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

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 属性Controller测试
 *
 * @author DataModel Team
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class PropertyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ObjectTypeService objectTypeService;

    @Autowired
    private PropertyService propertyService;

    private ObjectType testObjectType;

    @BeforeEach
    void setUp() {
        // 创建测试用的对象类型
        ObjectTypeDTO objectTypeDTO = new ObjectTypeDTO();
        objectTypeDTO.setName("TestObject");
        objectTypeDTO.setDisplayName("测试对象");
        testObjectType = objectTypeService.createObjectType(objectTypeDTO);
    }

    @Test
    void testCreateProperty() throws Exception {
        PropertyDTO dto = new PropertyDTO();
        dto.setName("name");
        dto.setDataType("STRING");
        dto.setDescription("名称属性");
        dto.setRequired(true);

        mockMvc.perform(post("/v1/object-types/{objectTypeId}/properties", testObjectType.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.name").value("name"))
                .andExpect(jsonPath("$.data.dataType").value("STRING"));
    }

    @Test
    void testGetPropertyById() throws Exception {
        // 先创建一个属性
        PropertyDTO dto = new PropertyDTO();
        dto.setName("email");
        dto.setDataType("STRING");
        Property created = propertyService.createProperty(testObjectType.getId(), dto);

        mockMvc.perform(get("/v1/object-types/{objectTypeId}/properties/{id}",
                        testObjectType.getId(), created.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(created.getId().toString()))
                .andExpect(jsonPath("$.data.name").value("email"));
    }

    @Test
    void testGetPropertiesByObjectTypeId() throws Exception {
        // 创建几个属性
        for (int i = 1; i <= 3; i++) {
            PropertyDTO dto = new PropertyDTO();
            dto.setName("property" + i);
            dto.setDataType("STRING");
            propertyService.createProperty(testObjectType.getId(), dto);
        }

        mockMvc.perform(get("/v1/object-types/{objectTypeId}/properties", testObjectType.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(3));
    }

    @Test
    void testUpdateProperty() throws Exception {
        // 先创建一个属性
        PropertyDTO dto = new PropertyDTO();
        dto.setName("original");
        dto.setDataType("STRING");
        Property created = propertyService.createProperty(testObjectType.getId(), dto);

        // 更新属性
        PropertyDTO updateDto = new PropertyDTO();
        updateDto.setName("updated");
        updateDto.setDataType("INTEGER");
        updateDto.setDescription("更新后的描述");

        mockMvc.perform(put("/v1/object-types/{objectTypeId}/properties/{id}",
                        testObjectType.getId(), created.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.name").value("updated"))
                .andExpect(jsonPath("$.data.dataType").value("INTEGER"));
    }

    @Test
    void testDeleteProperty() throws Exception {
        // 先创建一个属性
        PropertyDTO dto = new PropertyDTO();
        dto.setName("toDelete");
        dto.setDataType("STRING");
        Property created = propertyService.createProperty(testObjectType.getId(), dto);

        mockMvc.perform(delete("/v1/object-types/{objectTypeId}/properties/{id}",
                        testObjectType.getId(), created.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        // 验证已删除
        Property deleted = propertyService.getById(created.getId());
        assert deleted == null;
    }

    @Test
    void testCreatePropertyWithDuplicateName() throws Exception {
        // 先创建一个属性
        PropertyDTO dto1 = new PropertyDTO();
        dto1.setName("unique");
        dto1.setDataType("STRING");
        propertyService.createProperty(testObjectType.getId(), dto1);

        // 尝试创建同名属性
        PropertyDTO dto2 = new PropertyDTO();
        dto2.setName("unique");
        dto2.setDataType("INTEGER");

        mockMvc.perform(post("/v1/object-types/{objectTypeId}/properties", testObjectType.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto2)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }
}

