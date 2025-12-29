package com.example.datamodel.controller.v1;

import com.example.datamodel.dto.ObjectTypeDTO;
import com.example.datamodel.entity.ObjectType;
import com.example.datamodel.service.ObjectTypeService;
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
 * 对象类型Controller测试
 *
 * @author DataModel Team
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ObjectTypeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ObjectTypeService objectTypeService;

    private ObjectType testObjectType;

    @BeforeEach
    void setUp() {
        // 清理测试数据
        objectTypeService.list().forEach(obj -> {
            try {
                objectTypeService.deleteObjectType(obj.getId());
            } catch (Exception e) {
                // 忽略删除错误
            }
        });
    }

    @Test
    void testCreateObjectType() throws Exception {
        ObjectTypeDTO dto = new ObjectTypeDTO();
        dto.setName("Customer");
        dto.setDisplayName("客户");
        dto.setDescription("客户实体");
        dto.setPrimaryKey("id");

        mockMvc.perform(post("/v1/object-types")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.name").value("Customer"))
                .andExpect(jsonPath("$.data.displayName").value("客户"));
    }

    @Test
    void testCreateObjectTypeWithDuplicateName() throws Exception {
        // 先创建一个对象类型
        ObjectTypeDTO dto1 = new ObjectTypeDTO();
        dto1.setName("Customer");
        dto1.setDisplayName("客户");
        objectTypeService.createObjectType(dto1);

        // 尝试创建同名的对象类型
        ObjectTypeDTO dto2 = new ObjectTypeDTO();
        dto2.setName("Customer");
        dto2.setDisplayName("客户2");

        mockMvc.perform(post("/v1/object-types")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto2)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }

    @Test
    void testGetObjectTypeById() throws Exception {
        // 先创建一个对象类型
        ObjectTypeDTO dto = new ObjectTypeDTO();
        dto.setName("Order");
        dto.setDisplayName("订单");
        ObjectType created = objectTypeService.createObjectType(dto);

        mockMvc.perform(get("/v1/object-types/{id}", created.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(created.getId().toString()))
                .andExpect(jsonPath("$.data.name").value("Order"));
    }

    @Test
    void testGetObjectTypeByName() throws Exception {
        // 先创建一个对象类型
        ObjectTypeDTO dto = new ObjectTypeDTO();
        dto.setName("Product");
        dto.setDisplayName("产品");
        objectTypeService.createObjectType(dto);

        mockMvc.perform(get("/v1/object-types/name/{name}", "Product"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.name").value("Product"));
    }

    @Test
    void testListObjectTypes() throws Exception {
        // 创建几个测试对象类型
        for (int i = 1; i <= 3; i++) {
            ObjectTypeDTO dto = new ObjectTypeDTO();
            dto.setName("TestType" + i);
            dto.setDisplayName("测试类型" + i);
            objectTypeService.createObjectType(dto);
        }

        mockMvc.perform(get("/v1/object-types")
                        .param("current", "1")
                        .param("size", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.records").isArray())
                .andExpect(jsonPath("$.data.total").value(3));
    }

    @Test
    void testUpdateObjectType() throws Exception {
        // 先创建一个对象类型
        ObjectTypeDTO dto = new ObjectTypeDTO();
        dto.setName("Original");
        dto.setDisplayName("原始");
        ObjectType created = objectTypeService.createObjectType(dto);

        // 更新对象类型
        ObjectTypeDTO updateDto = new ObjectTypeDTO();
        updateDto.setName("Updated");
        updateDto.setDisplayName("已更新");
        updateDto.setDescription("更新后的描述");

        mockMvc.perform(put("/v1/object-types/{id}", created.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.displayName").value("已更新"));
    }

    @Test
    void testDeleteObjectType() throws Exception {
        // 先创建一个对象类型
        ObjectTypeDTO dto = new ObjectTypeDTO();
        dto.setName("ToDelete");
        dto.setDisplayName("待删除");
        ObjectType created = objectTypeService.createObjectType(dto);

        mockMvc.perform(delete("/v1/object-types/{id}", created.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        // 验证已删除
        mockMvc.perform(get("/v1/object-types/{id}", created.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(404));
    }

    @Test
    void testCreateObjectTypeWithInvalidData() throws Exception {
        // 测试必填字段验证
        ObjectTypeDTO dto = new ObjectTypeDTO();
        // name和displayName都为空，应该返回400错误

        mockMvc.perform(post("/v1/object-types")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400));
    }
}

