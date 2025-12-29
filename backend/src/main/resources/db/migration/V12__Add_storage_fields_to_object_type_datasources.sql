-- V12: 添加存储库字段到 object_type_datasources 表
-- 说明：添加 is_storage 字段，用于标识数据源是否作为对象实例的存储库

-- 添加 is_storage 字段
ALTER TABLE object_type_datasources 
ADD COLUMN IF NOT EXISTS is_storage BOOLEAN DEFAULT FALSE;

-- 添加注释
COMMENT ON COLUMN object_type_datasources.is_storage IS '是否作为对象实例的存储库。true表示该数据源用于存储对象实例，表名由系统生成；false表示仅用于数据映射等其他用途，需要指定table_name';

-- 添加索引（用于快速查询存储库）
CREATE INDEX IF NOT EXISTS idx_otd_is_storage ON object_type_datasources(object_type_id, is_storage);

-- 更新现有数据：将所有现有关联标记为"非存储库"（is_storage = false）
-- 因为现有的关联都是有指定table_name的，属于数据映射用途
UPDATE object_type_datasources 
SET is_storage = FALSE 
WHERE is_storage IS NULL;

