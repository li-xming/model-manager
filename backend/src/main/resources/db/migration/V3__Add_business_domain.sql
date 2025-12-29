-- 业务域表，用于对对象类型、链接类型、操作类型、接口、函数等进行业务分组
CREATE TABLE IF NOT EXISTS business_domains (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code VARCHAR(100) UNIQUE NOT NULL,
    name VARCHAR(255) NOT NULL,
    display_name VARCHAR(255),
    description TEXT,
    metadata JSONB DEFAULT '{}',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 为对象类型增加业务域关联
ALTER TABLE object_types
    ADD COLUMN IF NOT EXISTS domain_id UUID NULL REFERENCES business_domains(id);

CREATE INDEX IF NOT EXISTS idx_object_types_domain ON object_types(domain_id);

-- 为链接类型增加业务域关联
ALTER TABLE link_types
    ADD COLUMN IF NOT EXISTS domain_id UUID NULL REFERENCES business_domains(id);

CREATE INDEX IF NOT EXISTS idx_link_types_domain ON link_types(domain_id);

-- 为操作类型增加业务域关联
ALTER TABLE action_types
    ADD COLUMN IF NOT EXISTS domain_id UUID NULL REFERENCES business_domains(id);

CREATE INDEX IF NOT EXISTS idx_action_types_domain ON action_types(domain_id);

-- 为接口增加业务域关联
ALTER TABLE interfaces
    ADD COLUMN IF NOT EXISTS domain_id UUID NULL REFERENCES business_domains(id);

CREATE INDEX IF NOT EXISTS idx_interfaces_domain ON interfaces(domain_id);

-- 为函数增加业务域关联
ALTER TABLE functions
    ADD COLUMN IF NOT EXISTS domain_id UUID NULL REFERENCES business_domains(id);

CREATE INDEX IF NOT EXISTS idx_functions_domain ON functions(domain_id);


