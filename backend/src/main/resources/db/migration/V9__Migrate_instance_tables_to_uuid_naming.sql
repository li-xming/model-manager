-- 数据模型管理平台 - 实例表迁移到UUID命名规则
-- 版本：V9
-- 描述：将现有实例表从 instances_{name} 迁移到 instances_{uuid}，并添加 class_id 字段

-- ==========================================
-- 步骤1：为现有实例表添加 class_id 字段（如果表存在）
-- ==========================================
DO $$
DECLARE
    rec RECORD;
    v_table_name_old TEXT;
    v_table_name_new TEXT;
    v_class_id_val UUID;
    v_count_val BIGINT;
BEGIN
    -- 遍历所有对象类型
    FOR rec IN 
        SELECT id, name FROM object_types
    LOOP
        v_class_id_val := rec.id;
        v_table_name_old := 'instances_' || lower(rec.name);
        v_table_name_new := 'instances_' || replace(v_class_id_val::text, '-', '_');
        
        -- 检查旧表是否存在
        IF EXISTS (
            SELECT FROM information_schema.tables t
            WHERE t.table_schema = 'public' 
            AND t.table_name = v_table_name_old
        ) THEN
            -- 如果旧表存在且新表不存在，则重命名
            IF NOT EXISTS (
                SELECT FROM information_schema.tables t
                WHERE t.table_schema = 'public' 
                AND t.table_name = v_table_name_new
            ) THEN
                -- 重命名表
                EXECUTE format('ALTER TABLE %I RENAME TO %I', v_table_name_old, v_table_name_new);
                RAISE NOTICE '表重命名成功: % -> %', v_table_name_old, v_table_name_new;
            ELSE
                -- 新表已存在，记录警告
                RAISE WARNING '新表已存在，跳过重命名: % (旧表: %)', v_table_name_new, v_table_name_old;
            END IF;
            
            -- 为新表添加 class_id 字段（如果不存在）
            IF NOT EXISTS (
                SELECT FROM information_schema.columns c
                WHERE c.table_schema = 'public' 
                AND c.table_name = v_table_name_new 
                AND c.column_name = 'class_id'
            ) THEN
                -- 添加 class_id 字段，设置为 NOT NULL，默认值为对象类型ID
                EXECUTE format('ALTER TABLE %I ADD COLUMN class_id UUID NOT NULL DEFAULT %L', 
                    v_table_name_new, v_class_id_val);
                RAISE NOTICE '添加 class_id 字段成功: %', v_table_name_new;
                
                -- 更新现有记录的 class_id（虽然已经有默认值，但显式更新更安全）
                EXECUTE format('UPDATE %I SET class_id = %L WHERE class_id IS NULL', 
                    v_table_name_new, v_class_id_val);
                
                -- 创建索引
                EXECUTE format('CREATE INDEX IF NOT EXISTS idx_%s_class_id ON %I(class_id)',
                    replace(v_class_id_val::text, '-', '_'), v_table_name_new);
                RAISE NOTICE '创建 class_id 索引成功: %', v_table_name_new;
            ELSE
                RAISE NOTICE 'class_id 字段已存在，跳过: %', v_table_name_new;
            END IF;
            
            -- 验证数据：检查是否有 class_id 不正确的记录
            EXECUTE format('SELECT COUNT(*) FROM %I WHERE class_id != %L',
                v_table_name_new, v_class_id_val) INTO v_count_val;
            IF v_count_val > 0 THEN
                RAISE WARNING '表 % 中有 % 条记录的 class_id 不正确', v_table_name_new, v_count_val;
                -- 修复不正确的 class_id
                EXECUTE format('UPDATE %I SET class_id = %L WHERE class_id != %L',
                    v_table_name_new, v_class_id_val, v_class_id_val);
                RAISE NOTICE '已修复 % 条记录的 class_id: %', v_count_val, v_table_name_new;
            END IF;
            
        ELSE
            -- 旧表不存在，检查新表是否存在（可能是已经迁移过的）
            IF EXISTS (
                SELECT FROM information_schema.tables t
                WHERE t.table_schema = 'public' 
                AND t.table_name = v_table_name_new
            ) THEN
                RAISE NOTICE '表已使用新命名规则: %', v_table_name_new;
                
                -- 确保新表有 class_id 字段
                IF NOT EXISTS (
                    SELECT FROM information_schema.columns c
                    WHERE c.table_schema = 'public' 
                    AND c.table_name = v_table_name_new 
                    AND c.column_name = 'class_id'
                ) THEN
                    EXECUTE format('ALTER TABLE %I ADD COLUMN class_id UUID NOT NULL DEFAULT %L', 
                        v_table_name_new, v_class_id_val);
                    EXECUTE format('UPDATE %I SET class_id = %L WHERE class_id IS NULL', 
                        v_table_name_new, v_class_id_val);
                    EXECUTE format('CREATE INDEX IF NOT EXISTS idx_%s_class_id ON %I(class_id)',
                        replace(v_class_id_val::text, '-', '_'), v_table_name_new);
                    RAISE NOTICE '为新表添加 class_id 字段和索引: %', v_table_name_new;
                END IF;
            END IF;
        END IF;
    END LOOP;
END $$;

-- ==========================================
-- 步骤2：验证迁移结果
-- ==========================================
DO $$
DECLARE
    rec RECORD;
    v_table_name TEXT;
    v_count_val BIGINT;
    v_class_id_count BIGINT;
    v_total_tables INTEGER := 0;
    v_migrated_tables INTEGER := 0;
    v_error_tables INTEGER := 0;
BEGIN
    RAISE NOTICE '开始验证迁移结果...';
    
    FOR rec IN 
        SELECT id, name FROM object_types
    LOOP
        v_table_name := 'instances_' || replace(rec.id::text, '-', '_');
        
        -- 检查表是否存在
        IF EXISTS (
            SELECT FROM information_schema.tables t
            WHERE t.table_schema = 'public' 
            AND t.table_name = v_table_name
        ) THEN
            v_total_tables := v_total_tables + 1;
            
            -- 检查是否有 class_id 字段
            IF EXISTS (
                SELECT FROM information_schema.columns c
                WHERE c.table_schema = 'public' 
                AND c.table_name = v_table_name 
                AND c.column_name = 'class_id'
            ) THEN
                -- 检查 class_id 是否正确
                EXECUTE format('SELECT COUNT(*) FROM %I WHERE class_id != %L',
                    v_table_name, rec.id) INTO v_count_val;
                
                IF v_count_val = 0 THEN
                    -- 检查记录数
                    EXECUTE format('SELECT COUNT(*) FROM %I', v_table_name) INTO v_class_id_count;
                    v_migrated_tables := v_migrated_tables + 1;
                    RAISE NOTICE '✓ 表 % 迁移成功，包含 % 条记录', v_table_name, v_class_id_count;
                ELSE
                    v_error_tables := v_error_tables + 1;
                    RAISE WARNING '✗ 表 % 有 % 条记录的 class_id 不正确', v_table_name, v_count_val;
                END IF;
            ELSE
                v_error_tables := v_error_tables + 1;
                RAISE WARNING '✗ 表 % 缺少 class_id 字段', v_table_name;
            END IF;
        END IF;
    END LOOP;
    
    RAISE NOTICE '迁移验证完成: 总表数=%，成功=%，失败=%', v_total_tables, v_migrated_tables, v_error_tables;
END $$;

-- ==========================================
-- 步骤3：清理旧表（可选，谨慎使用）
-- ==========================================
-- 注意：此步骤会删除所有使用旧命名规则的表
-- 建议在执行此步骤前，先备份数据库并验证迁移结果
-- 如果迁移验证全部成功，可以取消注释以下代码来清理旧表

/*
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
        
        -- 如果旧表存在且新表也存在且新表有数据，则删除旧表
        IF EXISTS (
            SELECT FROM information_schema.tables 
            WHERE table_schema = 'public' 
            AND table_name = table_name_old
        ) AND EXISTS (
            SELECT FROM information_schema.tables 
            WHERE table_schema = 'public' 
            AND table_name = table_name_new
        ) THEN
            -- 检查新表是否有数据
            EXECUTE format('SELECT COUNT(*) FROM %I', table_name_new) INTO count_val;
            IF count_val > 0 THEN
                EXECUTE format('DROP TABLE IF EXISTS %I', table_name_old);
                RAISE NOTICE '已删除旧表: %', table_name_old;
            END IF;
        END IF;
    END LOOP;
END $$;
*/

