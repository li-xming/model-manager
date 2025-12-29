-- 数据模型管理平台 - 元模型层表结构
-- 版本：V6
-- 描述：创建元模型层表，包括meta_classes, meta_properties, meta_link_types, meta_action_types, meta_domains

-- ==========================================
-- 第一层：元模型层（Meta-Model Layer）
-- ==========================================

-- 元类（MetaClass）：定义"类"这个概念
CREATE TABLE IF NOT EXISTS meta_classes (
    id VARCHAR(50) PRIMARY KEY,  -- 固定ID（平台内置，如'META_CLASS'）或UUID（用户扩展）
    code VARCHAR(100) UNIQUE NOT NULL,
    name VARCHAR(255) NOT NULL,
    display_name VARCHAR(255),
    description TEXT,
    metadata_schema JSONB,  -- 定义该元模型需要哪些元数据字段
    is_builtin BOOLEAN DEFAULT FALSE,  -- 是否为平台内置元模型
    version VARCHAR(50) DEFAULT '1.0.0',  -- 版本号
    created_by VARCHAR(255),  -- 创建者（内置的为 'SYSTEM'）
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 元属性（MetaProperty）：定义"属性"这个概念
CREATE TABLE IF NOT EXISTS meta_properties (
    id VARCHAR(50) PRIMARY KEY,  -- 固定ID（平台内置）或UUID（用户扩展）
    code VARCHAR(100) UNIQUE NOT NULL,
    name VARCHAR(255) NOT NULL,
    display_name VARCHAR(255),
    description TEXT,
    metadata_schema JSONB,
    is_builtin BOOLEAN DEFAULT FALSE,
    version VARCHAR(50) DEFAULT '1.0.0',
    created_by VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 元关系类型（MetaLinkType）：定义"关系"这个概念
CREATE TABLE IF NOT EXISTS meta_link_types (
    id VARCHAR(50) PRIMARY KEY,
    code VARCHAR(100) UNIQUE NOT NULL,
    name VARCHAR(255) NOT NULL,
    display_name VARCHAR(255),
    description TEXT,
    metadata_schema JSONB,
    is_builtin BOOLEAN DEFAULT FALSE,
    version VARCHAR(50) DEFAULT '1.0.0',
    created_by VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 元操作类型（MetaActionType）：定义"操作"这个概念
CREATE TABLE IF NOT EXISTS meta_action_types (
    id VARCHAR(50) PRIMARY KEY,
    code VARCHAR(100) UNIQUE NOT NULL,
    name VARCHAR(255) NOT NULL,
    display_name VARCHAR(255),
    description TEXT,
    metadata_schema JSONB,
    is_builtin BOOLEAN DEFAULT FALSE,
    version VARCHAR(50) DEFAULT '1.0.0',
    created_by VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 元域（MetaDomain）：定义"域"这个概念
CREATE TABLE IF NOT EXISTS meta_domains (
    id VARCHAR(50) PRIMARY KEY,
    code VARCHAR(100) UNIQUE NOT NULL,
    name VARCHAR(255) NOT NULL,
    display_name VARCHAR(255),
    description TEXT,
    metadata_schema JSONB,
    is_builtin BOOLEAN DEFAULT FALSE,
    version VARCHAR(50) DEFAULT '1.0.0',
    created_by VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 创建索引
CREATE INDEX IF NOT EXISTS idx_meta_classes_code ON meta_classes(code);
CREATE INDEX IF NOT EXISTS idx_meta_classes_builtin ON meta_classes(is_builtin);
CREATE INDEX IF NOT EXISTS idx_meta_properties_code ON meta_properties(code);
CREATE INDEX IF NOT EXISTS idx_meta_properties_builtin ON meta_properties(is_builtin);
CREATE INDEX IF NOT EXISTS idx_meta_link_types_code ON meta_link_types(code);
CREATE INDEX IF NOT EXISTS idx_meta_link_types_builtin ON meta_link_types(is_builtin);
CREATE INDEX IF NOT EXISTS idx_meta_action_types_code ON meta_action_types(code);
CREATE INDEX IF NOT EXISTS idx_meta_action_types_builtin ON meta_action_types(is_builtin);
CREATE INDEX IF NOT EXISTS idx_meta_domains_code ON meta_domains(code);
CREATE INDEX IF NOT EXISTS idx_meta_domains_builtin ON meta_domains(is_builtin);

