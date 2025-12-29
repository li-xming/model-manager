-- 为接口增加运行时相关字段：HTTP 方法、URL 路径、绑定的操作类型
ALTER TABLE interfaces
    ADD COLUMN IF NOT EXISTS method VARCHAR(16),
    ADD COLUMN IF NOT EXISTS path VARCHAR(255),
    ADD COLUMN IF NOT EXISTS action_type_id UUID NULL REFERENCES action_types(id);

-- 为接口增加 (method, path) 唯一约束，避免重复暴露相同的 HTTP 接口
CREATE UNIQUE INDEX IF NOT EXISTS uk_interfaces_method_path
    ON interfaces(method, path);

-- 为绑定的操作类型增加索引，便于按操作类型反查接口
CREATE INDEX IF NOT EXISTS idx_interfaces_action_type
    ON interfaces(action_type_id);


