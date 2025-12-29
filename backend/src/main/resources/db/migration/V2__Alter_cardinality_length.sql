-- 修改cardinality字段长度，支持ONE_TO_MANY, MANY_TO_ONE等值
ALTER TABLE link_types ALTER COLUMN cardinality TYPE VARCHAR(20);

