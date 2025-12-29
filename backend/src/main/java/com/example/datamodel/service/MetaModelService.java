package com.example.datamodel.service;

import com.example.datamodel.entity.MetaActionType;
import com.example.datamodel.entity.MetaClass;
import com.example.datamodel.entity.MetaDomain;
import com.example.datamodel.entity.MetaLinkType;
import com.example.datamodel.entity.MetaProperty;

import java.util.List;

/**
 * 元模型服务接口
 * 管理平台内置和用户扩展的元模型
 *
 * @author DataModel Team
 */
public interface MetaModelService {

    // ==================== MetaClass ====================

    /**
     * 获取所有元类（包括平台内置和用户扩展）
     *
     * @return 元类列表
     */
    List<MetaClass> getAllMetaClasses();

    /**
     * 获取平台内置的元类
     *
     * @return 平台内置的元类列表
     */
    List<MetaClass> getBuiltinMetaClasses();

    /**
     * 获取用户扩展的元类
     *
     * @return 用户扩展的元类列表
     */
    List<MetaClass> getUserDefinedMetaClasses();

    /**
     * 根据ID获取元类
     *
     * @param id 元类ID
     * @return 元类实体
     */
    MetaClass getMetaClassById(String id);

    /**
     * 根据代码获取元类
     *
     * @param code 元类代码
     * @return 元类实体
     */
    MetaClass getMetaClassByCode(String code);

    /**
     * 创建自定义元类（仅系统管理员）
     *
     * @param dto 元类DTO
     * @param createdBy 创建者
     * @return 元类实体
     */
    MetaClass createCustomMetaClass(com.example.datamodel.dto.MetaClassDTO dto, String createdBy);

    /**
     * 更新元类（仅允许更新用户扩展的元模型）
     *
     * @param id 元类ID
     * @param dto 元类DTO
     * @return 元类实体
     */
    MetaClass updateMetaClass(String id, com.example.datamodel.dto.MetaClassDTO dto);

    /**
     * 删除元类（仅允许删除用户扩展的元模型，且需检查是否有模型使用）
     *
     * @param id 元类ID
     */
    void deleteMetaClass(String id);

    // ==================== MetaProperty ====================

    /**
     * 获取所有元属性
     *
     * @return 元属性列表
     */
    List<MetaProperty> getAllMetaProperties();

    /**
     * 获取平台内置的元属性
     *
     * @return 平台内置的元属性列表
     */
    List<MetaProperty> getBuiltinMetaProperties();

    /**
     * 获取用户扩展的元属性
     *
     * @return 用户扩展的元属性列表
     */
    List<MetaProperty> getUserDefinedMetaProperties();

    /**
     * 根据ID获取元属性
     *
     * @param id 元属性ID
     * @return 元属性实体
     */
    MetaProperty getMetaPropertyById(String id);

    /**
     * 根据代码获取元属性
     *
     * @param code 元属性代码
     * @return 元属性实体
     */
    MetaProperty getMetaPropertyByCode(String code);

    // ==================== MetaLinkType ====================

    /**
     * 获取所有元关系类型
     *
     * @return 元关系类型列表
     */
    List<MetaLinkType> getAllMetaLinkTypes();

    /**
     * 获取平台内置的元关系类型
     *
     * @return 平台内置的元关系类型列表
     */
    List<MetaLinkType> getBuiltinMetaLinkTypes();

    /**
     * 获取用户扩展的元关系类型
     *
     * @return 用户扩展的元关系类型列表
     */
    List<MetaLinkType> getUserDefinedMetaLinkTypes();

    /**
     * 根据ID获取元关系类型
     *
     * @param id 元关系类型ID
     * @return 元关系类型实体
     */
    MetaLinkType getMetaLinkTypeById(String id);

    /**
     * 根据代码获取元关系类型
     *
     * @param code 元关系类型代码
     * @return 元关系类型实体
     */
    MetaLinkType getMetaLinkTypeByCode(String code);

    // ==================== MetaActionType ====================

    /**
     * 获取所有元操作类型
     *
     * @return 元操作类型列表
     */
    List<MetaActionType> getAllMetaActionTypes();

    /**
     * 获取平台内置的元操作类型
     *
     * @return 平台内置的元操作类型列表
     */
    List<MetaActionType> getBuiltinMetaActionTypes();

    /**
     * 获取用户扩展的元操作类型
     *
     * @return 用户扩展的元操作类型列表
     */
    List<MetaActionType> getUserDefinedMetaActionTypes();

    /**
     * 根据ID获取元操作类型
     *
     * @param id 元操作类型ID
     * @return 元操作类型实体
     */
    MetaActionType getMetaActionTypeById(String id);

    /**
     * 根据代码获取元操作类型
     *
     * @param code 元操作类型代码
     * @return 元操作类型实体
     */
    MetaActionType getMetaActionTypeByCode(String code);

    // ==================== MetaDomain ====================

    /**
     * 获取所有元域
     *
     * @return 元域列表
     */
    List<MetaDomain> getAllMetaDomains();

    /**
     * 获取平台内置的元域
     *
     * @return 平台内置的元域列表
     */
    List<MetaDomain> getBuiltinMetaDomains();

    /**
     * 获取用户扩展的元域
     *
     * @return 用户扩展的元域列表
     */
    List<MetaDomain> getUserDefinedMetaDomains();

    /**
     * 根据ID获取元域
     *
     * @param id 元域ID
     * @return 元域实体
     */
    MetaDomain getMetaDomainById(String id);

    /**
     * 根据代码获取元域
     *
     * @param code 元域代码
     * @return 元域实体
     */
    MetaDomain getMetaDomainByCode(String code);
}

