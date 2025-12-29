-- 数据源管理功能数据库迁移脚本
-- 版本：V10
-- 说明：添加数据源管理相关表结构

-- 1. 创建数据源表
CREATE TABLE IF NOT EXISTS datasources (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    code VARCHAR(100) UNIQUE NOT NULL,
    type VARCHAR(50) NOT NULL,
    description TEXT,
    
    -- 连接信息
    host VARCHAR(255) NOT NULL,
    port INTEGER NOT NULL,
    database_name VARCHAR(255),
    schema_name VARCHAR(255),
    username VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    connection_url TEXT,
    
    -- 连接池配置
    max_connections INTEGER DEFAULT 10,
    min_connections INTEGER DEFAULT 2,
    connection_timeout INTEGER DEFAULT 30,
    
    -- 状态信息
    enabled BOOLEAN DEFAULT TRUE,
    status VARCHAR(50) DEFAULT 'ACTIVE',
    last_test_time TIMESTAMP,
    last_test_result VARCHAR(50),
    last_test_message TEXT,
    
    -- 元数据
    metadata JSONB DEFAULT '{}',
    domain_id UUID REFERENCES business_domains(id) ON DELETE SET NULL,
    
    -- 审计字段
    created_by VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(255),
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 索引
CREATE INDEX IF NOT EXISTS idx_datasources_code ON datasources(code);
CREATE INDEX IF NOT EXISTS idx_datasources_type ON datasources(type);
CREATE INDEX IF NOT EXISTS idx_datasources_enabled ON datasources(enabled);
CREATE INDEX IF NOT EXISTS idx_datasources_domain_id ON datasources(domain_id);

-- 2. 创建对象类型-数据源关联表
CREATE TABLE IF NOT EXISTS object_type_datasources (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    object_type_id UUID NOT NULL REFERENCES object_types(id) ON DELETE CASCADE,
    datasource_id UUID NOT NULL REFERENCES datasources(id) ON DELETE CASCADE,
    
    -- 关联信息
    is_default BOOLEAN DEFAULT FALSE,
    priority INTEGER DEFAULT 0,
    description TEXT,
    
    -- 元数据
    metadata JSONB DEFAULT '{}',
    
    -- 审计字段
    created_by VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- 唯一约束：一个对象类型不能重复关联同一个数据源
    UNIQUE(object_type_id, datasource_id)
);

-- 索引
CREATE INDEX IF NOT EXISTS idx_otd_object_type_id ON object_type_datasources(object_type_id);
CREATE INDEX IF NOT EXISTS idx_otd_datasource_id ON object_type_datasources(datasource_id);
CREATE INDEX IF NOT EXISTS idx_otd_is_default ON object_type_datasources(object_type_id, is_default);

-- 3. 为所有现有的实例表添加 datasource_id 字段（如果需要的话）
-- 注意：由于实例表是动态创建的，这里只是提供一个迁移脚本的示例
-- 实际使用时，DynamicTableManager会在创建新表时自动添加此字段

-- 如果需要为现有实例表添加datasource_id字段，可以使用以下PL/pgSQL脚本：
DO $$
DECLARE
    rec RECORD;
    v_table_name TEXT;
BEGIN
    -- 遍历所有instances_开头的表
    FOR rec IN 
        SELECT table_name 
        FROM information_schema.tables 
        WHERE table_schema = 'public' 
        AND table_name LIKE 'instances_%'
    LOOP
        v_table_name := rec.table_name;
        
        -- 检查表中是否已有datasource_id字段
        IF NOT EXISTS (
            SELECT 1 
            FROM information_schema.columns 
            WHERE table_schema = 'public' 
            AND table_name = v_table_name 
            AND column_name = 'datasource_id'
        ) THEN
            -- 添加datasource_id字段（允许为NULL，因为现有数据可能没有数据源）
            EXECUTE format('ALTER TABLE %I ADD COLUMN datasource_id UUID REFERENCES datasources(id)', v_table_name);
            
            -- 创建索引
            EXECUTE format('CREATE INDEX IF NOT EXISTS idx_%s_datasource_id ON %I(datasource_id)', 
                replace(v_table_name, 'instances_', ''), v_table_name);
            
            RAISE NOTICE '为表 % 添加 datasource_id 字段成功', v_table_name;
        ELSE
            RAISE NOTICE '表 % 已存在 datasource_id 字段', v_table_name;
        END IF;
    END LOOP;
END $$;

