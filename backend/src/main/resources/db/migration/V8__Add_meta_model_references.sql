-- 数据模型管理平台 - 为模型层表添加元模型引用字段
-- 版本：V8
-- 描述：为模型层表添加meta_xxx_id字段，建立与元模型层的关联

-- ==========================================
-- 为业务域表添加元域引用
-- ==========================================
ALTER TABLE business_domains
    ADD COLUMN IF NOT EXISTS meta_domain_id VARCHAR(50) DEFAULT 'META_DOMAIN' REFERENCES meta_domains(id);

-- 创建索引
CREATE INDEX IF NOT EXISTS idx_business_domains_meta_domain ON business_domains(meta_domain_id);

-- 更新现有数据：将现有的业务域都关联到默认的元域
UPDATE business_domains SET meta_domain_id = 'META_DOMAIN' WHERE meta_domain_id IS NULL;

-- ==========================================
-- 为对象类型表添加元类引用
-- ==========================================
ALTER TABLE object_types
    ADD COLUMN IF NOT EXISTS meta_class_id VARCHAR(50) DEFAULT 'META_CLASS' REFERENCES meta_classes(id);

-- 创建索引
CREATE INDEX IF NOT EXISTS idx_object_types_meta_class ON object_types(meta_class_id);

-- 更新现有数据：将现有的对象类型都关联到默认的元类
UPDATE object_types SET meta_class_id = 'META_CLASS' WHERE meta_class_id IS NULL;

-- ==========================================
-- 为属性表添加元属性引用
-- ==========================================
ALTER TABLE properties
    ADD COLUMN IF NOT EXISTS meta_property_id VARCHAR(50) DEFAULT 'META_PROPERTY' REFERENCES meta_properties(id);

-- 创建索引
CREATE INDEX IF NOT EXISTS idx_properties_meta_property ON properties(meta_property_id);

-- 更新现有数据：将现有的属性都关联到默认的元属性
UPDATE properties SET meta_property_id = 'META_PROPERTY' WHERE meta_property_id IS NULL;

-- ==========================================
-- 为链接类型表添加元关系类型引用
-- ==========================================
ALTER TABLE link_types
    ADD COLUMN IF NOT EXISTS meta_link_type_id VARCHAR(50) DEFAULT 'META_LINK_TYPE' REFERENCES meta_link_types(id);

-- 创建索引
CREATE INDEX IF NOT EXISTS idx_link_types_meta_link_type ON link_types(meta_link_type_id);

-- 更新现有数据：将现有的链接类型都关联到默认的元关系类型
UPDATE link_types SET meta_link_type_id = 'META_LINK_TYPE' WHERE meta_link_type_id IS NULL;

-- ==========================================
-- 为操作类型表添加元操作类型引用
-- ==========================================
ALTER TABLE action_types
    ADD COLUMN IF NOT EXISTS meta_action_type_id VARCHAR(50) DEFAULT 'META_ACTION_TYPE' REFERENCES meta_action_types(id);

-- 创建索引
CREATE INDEX IF NOT EXISTS idx_action_types_meta_action_type ON action_types(meta_action_type_id);

-- 更新现有数据：将现有的操作类型都关联到默认的元操作类型
UPDATE action_types SET meta_action_type_id = 'META_ACTION_TYPE' WHERE meta_action_type_id IS NULL;

