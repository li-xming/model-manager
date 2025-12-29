package com.example.datamodel.controller.v1;

import com.example.datamodel.dto.LinkTypeDTO;
import com.example.datamodel.dto.ObjectTypeDTO;
import com.example.datamodel.entity.LinkType;
import com.example.datamodel.entity.ObjectType;
import com.example.datamodel.service.LinkTypeService;
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
 * 链接类型Controller测试
 *
 * @author DataModel Team
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class LinkTypeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ObjectTypeService objectTypeService;

    @Autowired
    private LinkTypeService linkTypeService;

    private ObjectType sourceObjectType;
    private ObjectType targetObjectType;

    @BeforeEach
    void setUp() {
        // 创建源对象类型
        ObjectTypeDTO sourceDto = new ObjectTypeDTO();
        sourceDto.setName("Customer");
        sourceDto.setDisplayName("客户");
        sourceObjectType = objectTypeService.createObjectType(sourceDto);

        // 创建目标对象类型
        ObjectTypeDTO targetDto = new ObjectTypeDTO();
        targetDto.setName("Order");
        targetDto.setDisplayName("订单");
        targetObjectType = objectTypeService.createObjectType(targetDto);
    }

    @Test
    void testCreateLinkType() throws Exception {
        LinkTypeDTO dto = new LinkTypeDTO();
        dto.setName("places_order");
        dto.setDisplayName("下单");
        dto.setSourceObjectTypeId(sourceObjectType.getId());
        dto.setTargetObjectTypeId(targetObjectType.getId());
        dto.setCardinality("1:N");

        mockMvc.perform(post("/v1/link-types")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.name").value("places_order"))
                .andExpect(jsonPath("$.data.cardinality").value("1:N"));
    }

    @Test
    void testGetLinkTypeById() throws Exception {
        // 先创建一个链接类型
        LinkTypeDTO dto = new LinkTypeDTO();
        dto.setName("owns");
        dto.setDisplayName("拥有");
        dto.setSourceObjectTypeId(sourceObjectType.getId());
        dto.setTargetObjectTypeId(targetObjectType.getId());
        dto.setCardinality("1:1");
        LinkType created = linkTypeService.createLinkType(dto);

        mockMvc.perform(get("/v1/link-types/{id}", created.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(created.getId().toString()))
                .andExpect(jsonPath("$.data.name").value("owns"));
    }

    @Test
    void testGetLinkTypeByName() throws Exception {
        // 先创建一个链接类型
        LinkTypeDTO dto = new LinkTypeDTO();
        dto.setName("belongs_to");
        dto.setDisplayName("属于");
        dto.setSourceObjectTypeId(sourceObjectType.getId());
        dto.setTargetObjectTypeId(targetObjectType.getId());
        dto.setCardinality("N:1");
        linkTypeService.createLinkType(dto);

        mockMvc.perform(get("/v1/link-types/name/{name}", "belongs_to"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.name").value("belongs_to"));
    }

    @Test
    void testGetLinkTypesByObjectTypeId() throws Exception {
        // 创建几个链接类型
        for (int i = 1; i <= 2; i++) {
            LinkTypeDTO dto = new LinkTypeDTO();
            dto.setName("link" + i);
            dto.setDisplayName("链接" + i);
            dto.setSourceObjectTypeId(sourceObjectType.getId());
            dto.setTargetObjectTypeId(targetObjectType.getId());
            dto.setCardinality("1:N");
            linkTypeService.createLinkType(dto);
        }

        mockMvc.perform(get("/v1/link-types/object-type/{objectTypeId}", sourceObjectType.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2));
    }

    @Test
    void testUpdateLinkType() throws Exception {
        // 先创建一个链接类型
        LinkTypeDTO dto = new LinkTypeDTO();
        dto.setName("original_link");
        dto.setDisplayName("原始链接");
        dto.setSourceObjectTypeId(sourceObjectType.getId());
        dto.setTargetObjectTypeId(targetObjectType.getId());
        dto.setCardinality("1:1");
        LinkType created = linkTypeService.createLinkType(dto);

        // 更新链接类型
        LinkTypeDTO updateDto = new LinkTypeDTO();
        updateDto.setName("updated_link");
        updateDto.setDisplayName("已更新链接");
        updateDto.setSourceObjectTypeId(sourceObjectType.getId());
        updateDto.setTargetObjectTypeId(targetObjectType.getId());
        updateDto.setCardinality("1:N");
        updateDto.setBidirectional(true);

        mockMvc.perform(put("/v1/link-types/{id}", created.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.displayName").value("已更新链接"))
                .andExpect(jsonPath("$.data.cardinality").value("1:N"));
    }

    @Test
    void testDeleteLinkType() throws Exception {
        // 先创建一个链接类型
        LinkTypeDTO dto = new LinkTypeDTO();
        dto.setName("to_delete");
        dto.setDisplayName("待删除");
        dto.setSourceObjectTypeId(sourceObjectType.getId());
        dto.setTargetObjectTypeId(targetObjectType.getId());
        dto.setCardinality("1:N");
        LinkType created = linkTypeService.createLinkType(dto);

        mockMvc.perform(delete("/v1/link-types/{id}", created.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        // 验证已删除
        mockMvc.perform(get("/v1/link-types/{id}", created.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(404));
    }

    @Test
    void testCreateLinkTypeWithInvalidObjectType() throws Exception {
        LinkTypeDTO dto = new LinkTypeDTO();
        dto.setName("invalid_link");
        dto.setDisplayName("无效链接");
        dto.setSourceObjectTypeId(UUID.randomUUID()); // 不存在的ID
        dto.setTargetObjectTypeId(targetObjectType.getId());
        dto.setCardinality("1:N");

        mockMvc.perform(post("/v1/link-types")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }
}

