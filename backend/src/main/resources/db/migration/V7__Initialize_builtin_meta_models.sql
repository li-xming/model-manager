-- 数据模型管理平台 - 初始化平台内置元模型数据
-- 版本：V7
-- 描述：插入平台内置的元模型数据（is_builtin=true）

-- 1. 元类（MetaClass）：定义"类"这个概念
INSERT INTO meta_classes (id, code, name, display_name, description, metadata_schema, is_builtin, version, created_by, created_at, updated_at) VALUES
('META_CLASS', 'CLASS', '类', '类', '表示一个业务对象类型的元模型', 
 '{"required_fields": ["code", "name"], "optional_fields": ["display_name", "description", "primary_key"]}'::jsonb, 
 TRUE, '1.0.0', 'SYSTEM', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;

-- 2. 元属性（MetaProperty）：定义"属性"这个概念
INSERT INTO meta_properties (id, code, name, display_name, description, metadata_schema, is_builtin, version, created_by, created_at, updated_at) VALUES
('META_PROPERTY', 'PROPERTY', '属性', '属性', '表示一个类的属性的元模型', 
 '{"required_fields": ["code", "name", "data_type"], "optional_fields": ["display_name", "description", "required", "default_value"]}'::jsonb, 
 TRUE, '1.0.0', 'SYSTEM', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;

-- 3. 元关系类型（MetaLinkType）：定义"关系"这个概念
INSERT INTO meta_link_types (id, code, name, display_name, description, metadata_schema, is_builtin, version, created_by, created_at, updated_at) VALUES
('META_LINK_TYPE', 'LINK_TYPE', '关系类型', '关系类型', '表示两个类之间的关系的元模型', 
 '{"required_fields": ["code", "name", "source_class_id", "target_class_id"], "optional_fields": ["display_name", "description", "cardinality", "bidirectional"]}'::jsonb, 
 TRUE, '1.0.0', 'SYSTEM', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;

-- 4. 元操作类型（MetaActionType）：定义"操作"这个概念
INSERT INTO meta_action_types (id, code, name, display_name, description, metadata_schema, is_builtin, version, created_by, created_at, updated_at) VALUES
('META_ACTION_TYPE', 'ACTION_TYPE', '操作类型', '操作类型', '表示对类的实例执行的操作的元模型', 
 '{"required_fields": ["code", "name", "target_class_id"], "optional_fields": ["display_name", "description", "input_schema", "output_schema"]}'::jsonb, 
 TRUE, '1.0.0', 'SYSTEM', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;

-- 5. 元域（MetaDomain）：定义"域"这个概念
INSERT INTO meta_domains (id, code, name, display_name, description, metadata_schema, is_builtin, version, created_by, created_at, updated_at) VALUES
('META_DOMAIN', 'DOMAIN', '域', '域', '表示一个业务域的元模型', 
 '{"required_fields": ["code", "name"], "optional_fields": ["display_name", "description"]}'::jsonb, 
 TRUE, '1.0.0', 'SYSTEM', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;

