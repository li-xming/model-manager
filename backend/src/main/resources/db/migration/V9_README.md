# V9 实例表迁移脚本说明

## 迁移内容

将现有实例表从旧命名规则 `instances_{name}` 迁移到新命名规则 `instances_{uuid}`，并添加 `class_id` 字段。

## 迁移步骤

### 1. 表重命名
- 旧表名：`instances_{object_type_name}`（例如：`instances_user`）
- 新表名：`instances_{object_type_uuid}`（例如：`instances_550e8400_e29b_41d4_a716_446655440000`）

### 2. 添加 class_id 字段
- 字段名：`class_id`
- 类型：`UUID NOT NULL`
- 默认值：对象类型的ID
- 索引：为 `class_id` 字段创建索引

### 3. 数据验证
- 验证所有记录的 `class_id` 是否正确
- 自动修复不正确的 `class_id` 值

## 执行前准备

### 1. 备份数据库
```bash
pg_dump -U username -d database_name > backup_before_v9_migration.sql
```

### 2. 检查现有实例表
```sql
-- 查看所有实例表
SELECT table_name 
FROM information_schema.tables 
WHERE table_schema = 'public' 
AND table_name LIKE 'instances_%'
ORDER BY table_name;

-- 查看每个表的记录数
DO $$
DECLARE
    rec RECORD;
    count_val BIGINT;
BEGIN
    FOR rec IN 
        SELECT table_name 
        FROM information_schema.tables 
        WHERE table_schema = 'public' 
        AND table_name LIKE 'instances_%'
    LOOP
        EXECUTE format('SELECT COUNT(*) FROM %I', rec.table_name) INTO count_val;
        RAISE NOTICE '表 % 有 % 条记录', rec.table_name, count_val;
    END LOOP;
END $$;
```

### 3. 验证对象类型数据
```sql
-- 查看所有对象类型
SELECT id, name, display_name 
FROM object_types 
ORDER BY name;

-- 检查是否有重名的对象类型（理论上不应该有）
SELECT name, COUNT(*) as count
FROM object_types
GROUP BY name
HAVING COUNT(*) > 1;
```

## 执行迁移

### 方式1：使用Flyway自动执行
如果使用Flyway管理数据库迁移，只需启动应用程序，Flyway会自动执行此迁移脚本。

### 方式2：手动执行
```bash
psql -U username -d database_name -f V9__Migrate_instance_tables_to_uuid_naming.sql
```

## 迁移后验证

### 1. 检查表命名
```sql
-- 所有实例表应该使用UUID命名
SELECT table_name 
FROM information_schema.tables 
WHERE table_schema = 'public' 
AND table_name LIKE 'instances_%'
ORDER BY table_name;
```

### 2. 检查 class_id 字段
```sql
-- 检查每个表的 class_id 字段
DO $$
DECLARE
    rec RECORD;
    table_name TEXT;
    class_id_val UUID;
    count_val BIGINT;
BEGIN
    FOR rec IN 
        SELECT id, name FROM object_types
    LOOP
        table_name := 'instances_' || replace(rec.id::text, '-', '_');
        class_id_val := rec.id;
        
        IF EXISTS (
            SELECT FROM information_schema.tables 
            WHERE table_schema = 'public' 
            AND table_name = table_name
        ) THEN
            -- 检查 class_id 字段是否存在
            IF EXISTS (
                SELECT FROM information_schema.columns 
                WHERE table_schema = 'public' 
                AND table_name = table_name 
                AND column_name = 'class_id'
            ) THEN
                -- 检查 class_id 是否正确
                EXECUTE format('SELECT COUNT(*) FROM %I WHERE class_id != %L',
                    table_name, class_id_val) INTO count_val;
                
                IF count_val = 0 THEN
                    RAISE NOTICE '✓ 表 % 的 class_id 全部正确', table_name;
                ELSE
                    RAISE WARNING '✗ 表 % 有 % 条记录的 class_id 不正确', table_name, count_val;
                END IF;
            ELSE
                RAISE WARNING '✗ 表 % 缺少 class_id 字段', table_name;
            END IF;
        END IF;
    END LOOP;
END $$;
```

### 3. 检查索引
```sql
-- 检查 class_id 索引
SELECT 
    t.table_name,
    i.indexname,
    i.indexdef
FROM information_schema.tables t
LEFT JOIN pg_indexes i ON i.tablename = t.table_name AND i.indexname LIKE '%class_id%'
WHERE t.table_schema = 'public' 
AND t.table_name LIKE 'instances_%'
ORDER BY t.table_name;
```

### 4. 数据完整性检查
```sql
-- 检查每个表的记录数
DO $$
DECLARE
    rec RECORD;
    table_name TEXT;
    count_val BIGINT;
BEGIN
    FOR rec IN 
        SELECT id, name FROM object_types
    LOOP
        table_name := 'instances_' || replace(rec.id::text, '-', '_');
        
        IF EXISTS (
            SELECT FROM information_schema.tables 
            WHERE table_schema = 'public' 
            AND table_name = table_name
        ) THEN
            EXECUTE format('SELECT COUNT(*) FROM %I', table_name) INTO count_val;
            RAISE NOTICE '表 % (%): % 条记录', table_name, rec.name, count_val;
        END IF;
    END LOOP;
END $$;
```

## 回滚方案

如果迁移失败需要回滚，可以执行以下步骤：

### 1. 恢复数据库备份
```bash
psql -U username -d database_name < backup_before_v9_migration.sql
```

### 2. 手动回滚（如果备份不可用）

```sql
-- 将表名从新命名规则改回旧命名规则
DO $$
DECLARE
    rec RECORD;
    table_name_old TEXT;
    table_name_new TEXT;
BEGIN
    FOR rec IN 
        SELECT id, name FROM object_types
    LOOP
        table_name_old := 'instances_' || lower(rec.name);
        table_name_new := 'instances_' || replace(rec.id::text, '-', '_');
        
        -- 如果新表存在且旧表不存在，则重命名回去
        IF EXISTS (
            SELECT FROM information_schema.tables 
            WHERE table_schema = 'public' 
            AND table_name = table_name_new
        ) AND NOT EXISTS (
            SELECT FROM information_schema.tables 
            WHERE table_schema = 'public' 
            AND table_name = table_name_old
        ) THEN
            EXECUTE format('ALTER TABLE %I RENAME TO %I', table_name_new, table_name_old);
            RAISE NOTICE '回滚表重命名: % -> %', table_name_new, table_name_old;
        END IF;
        
        -- 删除 class_id 字段（如果存在）
        IF EXISTS (
            SELECT FROM information_schema.columns 
            WHERE table_schema = 'public' 
            AND table_name = table_name_old 
            AND column_name = 'class_id'
        ) THEN
            EXECUTE format('ALTER TABLE %I DROP COLUMN IF EXISTS class_id', table_name_old);
            RAISE NOTICE '删除 class_id 字段: %', table_name_old;
        END IF;
    END LOOP;
END $$;
```

## 注意事项

1. **备份数据**：执行迁移前必须备份数据库
2. **测试环境验证**：建议先在测试环境验证迁移脚本
3. **停机时间**：迁移过程中实例表会被锁定，建议在低峰期执行
4. **监控日志**：迁移过程中注意观察日志输出，确认没有错误
5. **清理旧表**：迁移验证成功后，可以执行脚本中的清理步骤删除旧表（已注释）

## 常见问题

### Q1: 如果对象类型名称包含特殊字符怎么办？
A: 对象类型名称会被转换为小写，特殊字符会在表名中保留。如果表名中包含PostgreSQL不允许的字符，迁移可能会失败。建议在执行前检查对象类型名称。

### Q2: 如果新表名已存在怎么办？
A: 脚本会检测新表是否已存在，如果存在会跳过重命名并记录警告。这种情况下，脚本会继续为现有表添加 `class_id` 字段。

### Q3: 迁移过程中如果出错怎么办？
A: 由于使用了事务（DO块），单个对象的迁移失败不会影响其他对象的迁移。如果迁移失败，可以查看日志找出失败的对象，手动修复后重新执行脚本。

### Q4: 如何验证迁移是否成功？
A: 执行脚本末尾的验证代码块，会输出详细的验证结果，包括总表数、成功数、失败数。

