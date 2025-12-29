-- 数据模型管理平台数据库初始化脚本

-- 对象类型表
CREATE TABLE IF NOT EXISTS object_types (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) UNIQUE NOT NULL,
    display_name VARCHAR(255) NOT NULL,
    description TEXT,
    primary_key VARCHAR(255) DEFAULT 'id',
    metadata JSONB DEFAULT '{}',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

CREATE INDEX IF NOT EXISTS idx_object_types_name ON object_types(name);

-- 属性定义表
CREATE TABLE IF NOT EXISTS properties (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    object_type_id UUID NOT NULL REFERENCES object_types(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    data_type VARCHAR(50) NOT NULL,
    description TEXT,
    required BOOLEAN DEFAULT FALSE,
    default_value TEXT,
    constraints JSONB DEFAULT '{}',
    metadata JSONB DEFAULT '{}',
    sort_order INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(object_type_id, name)
);

CREATE INDEX IF NOT EXISTS idx_properties_object_type ON properties(object_type_id);

-- 链接类型表
CREATE TABLE IF NOT EXISTS link_types (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) UNIQUE NOT NULL,
    display_name VARCHAR(255) NOT NULL,
    description TEXT,
    source_object_type_id UUID NOT NULL REFERENCES object_types(id),
    target_object_type_id UUID NOT NULL REFERENCES object_types(id),
    cardinality VARCHAR(20) NOT NULL,
    bidirectional BOOLEAN DEFAULT FALSE,
    metadata JSONB DEFAULT '{}',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

CREATE INDEX IF NOT EXISTS idx_link_types_source ON link_types(source_object_type_id);
CREATE INDEX IF NOT EXISTS idx_link_types_target ON link_types(target_object_type_id);

-- 操作类型表
CREATE TABLE IF NOT EXISTS action_types (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) UNIQUE NOT NULL,
    display_name VARCHAR(255) NOT NULL,
    description TEXT,
    target_object_type_id UUID NOT NULL REFERENCES object_types(id),
    input_schema JSONB DEFAULT '{}',
    output_schema JSONB DEFAULT '{}',
    requires_approval BOOLEAN DEFAULT FALSE,
    handler_function VARCHAR(255),
    metadata JSONB DEFAULT '{}',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255)
);

-- 接口表
CREATE TABLE IF NOT EXISTS interfaces (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) UNIQUE NOT NULL,
    display_name VARCHAR(255) NOT NULL,
    description TEXT,
    required_properties JSONB DEFAULT '{}',
    metadata JSONB DEFAULT '{}',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 接口实现关系表
CREATE TABLE IF NOT EXISTS interface_implementations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    interface_id UUID NOT NULL REFERENCES interfaces(id) ON DELETE CASCADE,
    object_type_id UUID NOT NULL REFERENCES object_types(id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(interface_id, object_type_id)
);

-- 函数表
CREATE TABLE IF NOT EXISTS functions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) UNIQUE NOT NULL,
    display_name VARCHAR(255) NOT NULL,
    description TEXT,
    code TEXT NOT NULL,
    input_schema JSONB DEFAULT '{}',
    return_type VARCHAR(50) NOT NULL,
    version VARCHAR(50) DEFAULT '1.0.0',
    metadata JSONB DEFAULT '{}',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 链接实例表
CREATE TABLE IF NOT EXISTS link_instances (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    link_type_id UUID NOT NULL REFERENCES link_types(id),
    source_instance_id UUID NOT NULL,
    target_instance_id UUID NOT NULL,
    properties JSONB DEFAULT '{}',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255)
);

CREATE INDEX IF NOT EXISTS idx_link_instances_source ON link_instances(link_type_id, source_instance_id);
CREATE INDEX IF NOT EXISTS idx_link_instances_target ON link_instances(link_type_id, target_instance_id);

