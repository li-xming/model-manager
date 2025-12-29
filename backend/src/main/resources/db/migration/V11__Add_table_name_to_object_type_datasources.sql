-- 为 object_type_datasources 表添加 table_name 和 schema_name 字段
-- 用于支持对象类型关联数据源下的具体表

-- 1. 添加字段
ALTER TABLE object_type_datasources 
ADD COLUMN IF NOT EXISTS table_name VARCHAR(255),
ADD COLUMN IF NOT EXISTS schema_name VARCHAR(255);

-- 2. 添加注释
COMMENT ON COLUMN object_type_datasources.table_name IS '关联的表名（必填）';
COMMENT ON COLUMN object_type_datasources.schema_name IS 'Schema名称（可选，PostgreSQL/Oracle使用）';

-- 3. 删除旧的唯一约束
ALTER TABLE object_type_datasources 
DROP CONSTRAINT IF EXISTS object_type_datasources_object_type_id_datasource_id_key;

-- 4. 添加新的唯一约束（允许一个对象类型关联同一数据源的不同表）
CREATE UNIQUE INDEX IF NOT EXISTS object_type_datasources_object_type_id_datasource_id_schema_name_table_name_key 
ON object_type_datasources(object_type_id, datasource_id, COALESCE(schema_name, ''), table_name);

-- 5. 添加索引以便查询
CREATE INDEX IF NOT EXISTS idx_otd_table_name ON object_type_datasources(datasource_id, COALESCE(schema_name, ''), table_name);

-- 6. 数据迁移说明
-- 注意：如果表中已有数据，需要手动为每条记录指定 table_name
-- 建议的处理方式：
-- 1. 对于已有记录，可以根据对象类型的 name 字段作为默认表名
-- 2. 或者清空现有关联记录，让用户重新关联（推荐）
-- 
-- 示例SQL（如果需要使用对象类型名称作为默认表名）：
-- UPDATE object_type_datasources otd
-- SET table_name = LOWER(ot.name)
-- FROM object_types ot
-- WHERE otd.object_type_id = ot.id 
--   AND otd.table_name IS NULL;

-- 7. 添加NOT NULL约束（在数据迁移完成后执行）
-- 注意：在数据迁移完成之前，暂时允许 table_name 为 NULL
-- 数据迁移完成后，可以执行以下SQL添加NOT NULL约束：
-- ALTER TABLE object_type_datasources ALTER COLUMN table_name SET NOT NULL;

