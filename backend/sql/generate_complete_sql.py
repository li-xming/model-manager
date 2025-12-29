#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
根据PlantUML文件生成完整的数据模型SQL脚本
"""

import re
import sys
from pathlib import Path

# 数据类型映射
DATA_TYPE_MAP = {
    'string': 'STRING',
    'integer': 'INTEGER',
    'long': 'DECIMAL',
    'date': 'DATE',
    'boolean': 'BOOLEAN',
}

# 字段类型映射
FIELD_TYPE_MAP = {
    'string': 'VARCHAR',
    'integer': 'INTEGER',
    'long': 'DECIMAL',
    'date': 'DATE',
    'boolean': 'BOOLEAN',
}

def parse_puml_file(puml_path):
    """解析PlantUML文件，提取实体和属性"""
    with open(puml_path, 'r', encoding='utf-8') as f:
        content = f.read()
    
    entities = {}
    
    # 匹配实体定义
    entity_pattern = r'entity\s+(\w+)\s+as\s+"([^"]+)"\s*\{([^}]+)\}'
    matches = re.finditer(entity_pattern, content, re.DOTALL)
    
    for match in matches:
        entity_code = match.group(1)
        entity_name = match.group(2)
        entity_body = match.group(3)
        
        properties = []
        
        # 解析属性（按行处理，保留原始行）
        lines = entity_body.split('\n')
        current_comment = ''
        
        for i, line in enumerate(lines):
            original_line = line
            line = line.strip()
            
            # 处理注释行
            if line.startswith("'"):
                current_comment = line.lstrip("'").strip()
                continue
            
            if not line or line.startswith('--'):
                continue
            
            # 判断是否为主键
            is_primary = '* ' in line
            if is_primary:
                line = line.replace('* ', '').strip()
            
            # 匹配属性定义: propertyName : type
            prop_match = re.match(r'([a-zA-Z0-9_]+)\s*:\s*(\w+)', line)
            if prop_match:
                prop_name = prop_match.group(1)
                prop_type = prop_match.group(2).lower()
                
                # 使用当前注释或属性名作为显示名称
                display_name = current_comment if current_comment else prop_name
                current_comment = ''  # 使用后清空
                
                properties.append({
                    'code': prop_name,
                    'name': display_name,
                    'type': prop_type,
                    'required': is_primary
                })
        
        entities[entity_code] = {
            'name': entity_name,
            'code': entity_code.upper(),
            'properties': properties,
        }
    
    return entities

def generate_property_sql(entity_code, object_type_id_var, properties, start_sort=1):
    """生成属性插入SQL"""
    sql_lines = []
    entity_display = entity_code.replace('_', ' ').title()
    sql_lines.append(f"    -- {entity_display} 属性 - {len(properties)}个属性")
    sql_lines.append(f"    INSERT INTO datamodel_property (object_type_id, property_code, property_name, data_type, field_name, field_type, required, sort_order, create_time, update_time, status, is_deleted) VALUES")
    
    values = []
    for idx, prop in enumerate(properties, start=start_sort):
        data_type = DATA_TYPE_MAP.get(prop['type'], 'STRING')
        field_type = FIELD_TYPE_MAP.get(prop['type'], 'VARCHAR')
        field_name = camel_to_snake(prop['code'])
        required = 'TRUE' if prop['required'] else 'FALSE'
        
        # 转义单引号
        prop_name = prop['name'].replace("'", "''")
        
        value = f"    ({object_type_id_var}, '{prop['code']}', '{prop_name}', '{data_type}', '{field_name}', '{field_type}', {required}, {idx}, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0)"
        values.append(value)
    
    sql_lines.append(',\n'.join(values) + ';')
    return '\n'.join(sql_lines)

def camel_to_snake(name):
    """将驼峰命名转换为下划线命名"""
    s1 = re.sub('(.)([A-Z][a-z]+)', r'\1_\2', name)
    return re.sub('([a-z0-9])([A-Z])', r'\1_\2', s1).lower()

def main():
    # 获取PlantUML文件路径
    puml_file = Path(__file__).parent.parent / '省中心联网收费业务实体关系图.puml'
    
    if not puml_file.exists():
        print(f"错误：找不到文件 {puml_file}")
        sys.exit(1)
    
    # 解析PlantUML文件
    entities = parse_puml_file(puml_file)
    
    # 生成属性SQL
    output_file = Path(__file__).parent / '省中心联网收费业务数据模型-属性补充.sql'
    
    with open(output_file, 'w', encoding='utf-8') as f:
        f.write("-- ============================================\n")
        f.write("-- 省中心联网收费业务数据模型 - 完整属性SQL\n")
        f.write("-- 根据PlantUML实体关系图生成\n")
        f.write("-- 注意：此文件需要在主SQL脚本执行后运行\n")
        f.write("-- ============================================\n\n")
        
        f.write("DO $$\n")
        f.write("DECLARE\n")
        f.write("    v_domain_id BIGINT;\n")
        for entity_code in entities.keys():
            var_name = f"v_{entity_code.lower()}_type_id"
            f.write(f"    {var_name} BIGINT;\n")
        f.write("BEGIN\n")
        f.write("    -- 获取业务域ID和对象类型ID\n")
        f.write("    SELECT id INTO v_domain_id FROM datamodel_business_domain WHERE domain_code = 'TOLL_COLLECTION' LIMIT 1;\n\n")
        
        # 对象类型代码映射（PlantUML中的实体名 -> SQL中的object_type_code）
        code_mapping = {
            'User': 'USER',
            'Vehicle': 'VEHICLE',
            'Medium': 'MEDIUM',
            'TollRoad': 'TOLL_ROAD',
            'SectionOwner': 'SECTION_OWNER',
            'Section': 'SECTION',
            'TollStation': 'TOLL_STATION',
            'TollPlaza': 'TOLL_PLAZA',
            'TollGantry': 'TOLL_GANTRY',
            'TollInterval': 'TOLL_INTERVAL',
            'TollLane': 'TOLL_LANE',
            'Transaction': 'TRANSACTION',
            'Path': 'PATH',
            'PathDetail': 'PATH_DETAIL',
            'RestorePath': 'RESTORE_PATH',
            'RestorePathDetail': 'RESTORE_PATH_DETAIL',
            'SplitDetail': 'SPLIT_DETAIL',
        }
        
        for entity_code in entities.keys():
            var_name = f"v_{entity_code.lower()}_type_id"
            object_type_code = code_mapping.get(entity_code, entity_code.upper())
            f.write(f"    SELECT id INTO {var_name} FROM datamodel_object_type WHERE object_type_code = '{object_type_code}' AND domain_id = v_domain_id LIMIT 1;\n")
        
        f.write("\n")
        
        # 生成每个实体的属性SQL
        sort_counter = 1
        for entity_code, entity in entities.items():
            var_name = f"v_{entity_code.lower()}_type_id"
            prop_sql = generate_property_sql(entity_code, var_name, entity['properties'], sort_counter)
            f.write(prop_sql)
            f.write("\n\n")
            sort_counter += len(entity['properties'])
        
        f.write("END $$;\n")
    
    print(f"成功生成属性SQL文件: {output_file}")
    print(f"共处理 {len(entities)} 个实体")

if __name__ == '__main__':
    main()

