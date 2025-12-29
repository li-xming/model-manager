-- ============================================
-- 省中心联网收费业务数据模型SQL脚本（完整版）
-- 根据PlantUML实体关系图生成
-- 适用于：数据模型管理平台（当前项目）
-- 包含所有17个实体和19个链接类型的完整定义
-- ============================================

-- 注意：此脚本适用于当前数据模型管理平台
-- 表结构：business_domains, object_types, properties, link_types
-- 使用 UUID 作为主键类型

DO $$
DECLARE
    v_domain_id UUID;
    v_user_type_id UUID;
    v_vehicle_type_id UUID;
    v_medium_type_id UUID;
    v_tollroad_type_id UUID;
    v_sectionowner_type_id UUID;
    v_section_type_id UUID;
    v_tollstation_type_id UUID;
    v_tollplaza_type_id UUID;
    v_tollgantry_type_id UUID;
    v_tollinterval_type_id UUID;
    v_tolllane_type_id UUID;
    v_transaction_type_id UUID;
    v_path_type_id UUID;
    v_pathdetail_type_id UUID;
    v_restorepath_type_id UUID;
    v_restorepathdetail_type_id UUID;
    v_splitdetail_type_id UUID;
BEGIN
    -- ============================================
    -- 1. 创建业务域
    -- ============================================
    INSERT INTO business_domains (
        code, name, display_name, description,
        created_at, updated_at
    ) VALUES (
        'TOLL_COLLECTION', '省中心联网收费业务', '省中心联网收费业务域',
        '省中心联网收费业务实体关系模型，包括车主、车辆、收费公路、收费站、交易流水等',
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
    ) RETURNING id INTO v_domain_id;

    -- ============================================
    -- 2. 创建对象类型（17个实体）
    -- ============================================
    
    -- 2.1 车主 (User)
    INSERT INTO object_types (
        domain_id, name, display_name, description, primary_key,
        created_at, updated_at
    ) VALUES (
        v_domain_id, 'User', '车主', '机动车所有人信息，包括自然人和法人车主', 'id',
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
    ) RETURNING id INTO v_user_type_id;

    -- 2.2 车辆 (Vehicle)
    INSERT INTO object_types (
        domain_id, name, display_name, description, primary_key,
        created_at, updated_at
    ) VALUES (
        v_domain_id, 'Vehicle', '车辆', '车辆信息', 'id',
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
    ) RETURNING id INTO v_vehicle_type_id;

    -- 2.3 通行介质 (Medium)
    INSERT INTO object_types (
        domain_id, name, display_name, description, primary_key,
        created_at, updated_at
    ) VALUES (
        v_domain_id, 'Medium', '通行介质', '通行介质信息，如OBU卡等', 'id',
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
    ) RETURNING id INTO v_medium_type_id;

    -- 2.4 收费公路 (TollRoad)
    INSERT INTO object_types (
        domain_id, name, display_name, description, primary_key,
        created_at, updated_at
    ) VALUES (
        v_domain_id, 'TollRoad', '收费公路', '收费公路信息', 'id',
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
    ) RETURNING id INTO v_tollroad_type_id;

    -- 2.5 路段业主 (SectionOwner)
    INSERT INTO object_types (
        domain_id, name, display_name, description, primary_key,
        created_at, updated_at
    ) VALUES (
        v_domain_id, 'SectionOwner', '路段业主', '路段业主信息', 'id',
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
    ) RETURNING id INTO v_sectionowner_type_id;

    -- 2.6 收费路段 (Section)
    INSERT INTO object_types (
        domain_id, name, display_name, description, primary_key,
        created_at, updated_at
    ) VALUES (
        v_domain_id, 'Section', '收费路段', '收费路段信息', 'id',
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
    ) RETURNING id INTO v_section_type_id;

    -- 2.7 收费站 (TollStation)
    INSERT INTO object_types (
        domain_id, name, display_name, description, primary_key,
        created_at, updated_at
    ) VALUES (
        v_domain_id, 'TollStation', '收费站', '收费站信息', 'id',
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
    ) RETURNING id INTO v_tollstation_type_id;

    -- 2.8 收费广场 (TollPlaza)
    INSERT INTO object_types (
        domain_id, name, display_name, description, primary_key,
        created_at, updated_at
    ) VALUES (
        v_domain_id, 'TollPlaza', '收费广场', '收费广场信息', 'id',
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
    ) RETURNING id INTO v_tollplaza_type_id;

    -- 2.9 收费门架 (TollGantry)
    INSERT INTO object_types (
        domain_id, name, display_name, description, primary_key,
        created_at, updated_at
    ) VALUES (
        v_domain_id, 'TollGantry', '收费门架', '收费门架信息', 'id',
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
    ) RETURNING id INTO v_tollgantry_type_id;

    -- 2.10 收费单元 (TollInterval)
    INSERT INTO object_types (
        domain_id, name, display_name, description, primary_key,
        created_at, updated_at
    ) VALUES (
        v_domain_id, 'TollInterval', '收费单元', '收费单元信息', 'id',
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
    ) RETURNING id INTO v_tollinterval_type_id;

    -- 2.11 收费车道 (TollLane)
    INSERT INTO object_types (
        domain_id, name, display_name, description, primary_key,
        created_at, updated_at
    ) VALUES (
        v_domain_id, 'TollLane', '收费车道', '收费车道信息', 'id',
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
    ) RETURNING id INTO v_tolllane_type_id;

    -- 2.12 交易流水 (Transaction)
    INSERT INTO object_types (
        domain_id, name, display_name, description, primary_key,
        created_at, updated_at
    ) VALUES (
        v_domain_id, 'Transaction', '交易流水', '交易流水信息', 'id',
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
    ) RETURNING id INTO v_transaction_type_id;

    -- 2.13 车辆通行路径 (Path)
    INSERT INTO object_types (
        domain_id, name, display_name, description, primary_key,
        created_at, updated_at
    ) VALUES (
        v_domain_id, 'Path', '车辆通行路径', '车辆通行路径信息', 'id',
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
    ) RETURNING id INTO v_path_type_id;

    -- 2.14 车辆通行路径明细 (PathDetail)
    INSERT INTO object_types (
        domain_id, name, display_name, description, primary_key,
        created_at, updated_at
    ) VALUES (
        v_domain_id, 'PathDetail', '车辆通行路径明细', '车辆通行路径明细信息', 'id',
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
    ) RETURNING id INTO v_pathdetail_type_id;

    -- 2.15 车辆通行拟合路径 (RestorePath)
    INSERT INTO object_types (
        domain_id, name, display_name, description, primary_key,
        created_at, updated_at
    ) VALUES (
        v_domain_id, 'RestorePath', '车辆通行拟合路径', '车辆通行拟合路径信息', 'id',
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
    ) RETURNING id INTO v_restorepath_type_id;

    -- 2.16 车辆通行拟合路径明细 (RestorePathDetail)
    INSERT INTO object_types (
        domain_id, name, display_name, description, primary_key,
        created_at, updated_at
    ) VALUES (
        v_domain_id, 'RestorePathDetail', '车辆通行拟合路径明细', '车辆通行拟合路径明细信息', 'id',
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
    ) RETURNING id INTO v_restorepathdetail_type_id;

    -- 2.17 拆分明细 (SplitDetail)
    INSERT INTO object_types (
        domain_id, name, display_name, description, primary_key,
        created_at, updated_at
    ) VALUES (
        v_domain_id, 'SplitDetail', '拆分明细', '拆分明细信息', 'passId',
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
    ) RETURNING id INTO v_splitdetail_type_id;

    -- ============================================
    -- 3. 创建属性
    -- 注意：由于属性太多，这里仅包含前几个实体的属性作为示例
    -- 完整属性请参考属性补充SQL文件
    -- ============================================
    
    -- 3.1 车主 (User) 属性 - 12个属性
    INSERT INTO properties (object_type_id, name, data_type, description, required, sort_order, created_at, updated_at) VALUES
    (v_user_type_id, 'id', 'STRING', '车主编号', TRUE, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (v_user_type_id, 'ownerName', 'STRING', '机动车所有人姓名', FALSE, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (v_user_type_id, 'ownerIdType', 'STRING', '机动车所有人证件类型', FALSE, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (v_user_type_id, 'ownerIdNum', 'STRING', '机动车所有人证件号', FALSE, 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (v_user_type_id, 'registeredType', 'INTEGER', '录入方式，1-线上，2-线下', FALSE, 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (v_user_type_id, 'channelId', 'STRING', '录入渠道编号', FALSE, 6, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (v_user_type_id, 'registeredTime', 'STRING', '录入时间', FALSE, 7, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (v_user_type_id, 'ownerTel', 'STRING', '机动车所有人联系电话', FALSE, 8, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (v_user_type_id, 'address', 'STRING', '机动车所有人地址', FALSE, 9, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (v_user_type_id, 'contact', 'STRING', '指定联系人姓名，法人客户独有', FALSE, 10, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (v_user_type_id, 'transportIdNum', 'STRING', '道路运输证编号，法人客户独有', FALSE, 11, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (v_user_type_id, 'licenseIdNum', 'STRING', '经营许可证编号，法人客户独有', FALSE, 12, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

    -- 3.2 车辆 (Vehicle) 属性 - 30个属性（示例，完整版本见属性补充SQL）
    -- 这里仅插入前几个属性作为示例
    INSERT INTO properties (object_type_id, name, data_type, description, required, sort_order, created_at, updated_at) VALUES
    (v_vehicle_type_id, 'id', 'STRING', '车辆编号，由车牌号码+间隔符+车牌颜色组成', TRUE, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (v_vehicle_type_id, 'plateNum', 'STRING', '车牌号码', FALSE, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (v_vehicle_type_id, 'plateColor', 'INTEGER', '车牌颜色，2位数字', FALSE, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

    -- 3.3 通行介质 (Medium) 属性 - 8个属性（示例）
    INSERT INTO properties (object_type_id, name, data_type, description, required, sort_order, created_at, updated_at) VALUES
    (v_medium_type_id, 'id', 'STRING', '通行介质编号', TRUE, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (v_medium_type_id, 'type', 'STRING', '通行介质类型', FALSE, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (v_medium_type_id, 'channelId', 'STRING', '渠道编号', FALSE, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

    -- 注意：完整属性定义请参考属性补充SQL文件

    -- ============================================
    -- 4. 创建链接类型（19个关系）
    -- ============================================

    -- 4.1 User 1--n Vehicle (拥有)
    INSERT INTO link_types (
        domain_id, name, display_name, description,
        source_object_type_id, target_object_type_id, cardinality, bidirectional,
        created_at, updated_at
    ) VALUES (
        v_domain_id, 'USER_OWNS_VEHICLE', '拥有', '车主拥有车辆',
        v_user_type_id, v_vehicle_type_id, '1:N', FALSE,
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
    );

    -- 4.2 Vehicle 1--1n Medium (持有)
    INSERT INTO link_types (
        domain_id, name, display_name, description,
        source_object_type_id, target_object_type_id, cardinality, bidirectional,
        created_at, updated_at
    ) VALUES (
        v_domain_id, 'VEHICLE_HOLDS_MEDIUM', '持有', '车辆持有通行介质',
        v_vehicle_type_id, v_medium_type_id, '1:1', FALSE,
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
    );

    -- 4.3 Vehicle 1--n Transaction (关联)
    INSERT INTO link_types (
        domain_id, name, display_name, description,
        source_object_type_id, target_object_type_id, cardinality, bidirectional,
        created_at, updated_at
    ) VALUES (
        v_domain_id, 'VEHICLE_RELATES_TRANSACTION', '关联', '车辆关联交易流水',
        v_vehicle_type_id, v_transaction_type_id, '1:N', FALSE,
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
    );

    -- 4.4 TollRoad 1--n Section (包含)
    INSERT INTO link_types (
        domain_id, name, display_name, description,
        source_object_type_id, target_object_type_id, cardinality, bidirectional,
        created_at, updated_at
    ) VALUES (
        v_domain_id, 'TOLL_ROAD_CONTAINS_SECTION', '包含', '收费公路包含收费路段',
        v_tollroad_type_id, v_section_type_id, '1:N', FALSE,
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
    );

    -- 4.5 SectionOwner 1--n Section (管理)
    INSERT INTO link_types (
        domain_id, name, display_name, description,
        source_object_type_id, target_object_type_id, cardinality, bidirectional,
        created_at, updated_at
    ) VALUES (
        v_domain_id, 'SECTION_OWNER_MANAGES_SECTION', '管理', '路段业主管理收费路段',
        v_sectionowner_type_id, v_section_type_id, '1:N', FALSE,
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
    );

    -- 4.6 Section 1--n TollStation (包含)
    INSERT INTO link_types (
        domain_id, name, display_name, description,
        source_object_type_id, target_object_type_id, cardinality, bidirectional,
        created_at, updated_at
    ) VALUES (
        v_domain_id, 'SECTION_CONTAINS_TOLL_STATION', '包含', '收费路段包含收费站',
        v_section_type_id, v_tollstation_type_id, '1:N', FALSE,
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
    );

    -- 4.7 Section 1--n TollInterval (包含)
    INSERT INTO link_types (
        domain_id, name, display_name, description,
        source_object_type_id, target_object_type_id, cardinality, bidirectional,
        created_at, updated_at
    ) VALUES (
        v_domain_id, 'SECTION_CONTAINS_TOLL_INTERVAL', '包含', '收费路段包含收费单元',
        v_section_type_id, v_tollinterval_type_id, '1:N', FALSE,
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
    );

    -- 4.8 TollStation 1--n TollPlaza (包含)
    INSERT INTO link_types (
        domain_id, name, display_name, description,
        source_object_type_id, target_object_type_id, cardinality, bidirectional,
        created_at, updated_at
    ) VALUES (
        v_domain_id, 'TOLL_STATION_CONTAINS_TOLL_PLAZA', '包含', '收费站包含收费广场',
        v_tollstation_type_id, v_tollplaza_type_id, '1:N', FALSE,
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
    );

    -- 4.9 TollPlaza 1--n TollLane (包含)
    INSERT INTO link_types (
        domain_id, name, display_name, description,
        source_object_type_id, target_object_type_id, cardinality, bidirectional,
        created_at, updated_at
    ) VALUES (
        v_domain_id, 'TOLL_PLAZA_CONTAINS_TOLL_LANE', '包含', '收费广场包含收费车道',
        v_tollplaza_type_id, v_tolllane_type_id, '1:N', FALSE,
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
    );

    -- 4.10 TollGantry 1--n TollInterval (所在收费单元是)
    INSERT INTO link_types (
        domain_id, name, display_name, description,
        source_object_type_id, target_object_type_id, cardinality, bidirectional,
        created_at, updated_at
    ) VALUES (
        v_domain_id, 'TOLL_GANTRY_LOCATED_IN_TOLL_INTERVAL', '所在收费单元是', '收费门架所在收费单元',
        v_tollgantry_type_id, v_tollinterval_type_id, '1:N', FALSE,
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
    );

    -- 4.11 TollGantry 1--n TollInterval (代收)
    INSERT INTO link_types (
        domain_id, name, display_name, description,
        source_object_type_id, target_object_type_id, cardinality, bidirectional,
        created_at, updated_at
    ) VALUES (
        v_domain_id, 'TOLL_GANTRY_AGENCY_TOLL_INTERVAL', '代收', '收费门架代收收费单元',
        v_tollgantry_type_id, v_tollinterval_type_id, '1:N', FALSE,
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
    );

    -- 4.12 TollLane 1--n Transaction (生成)
    INSERT INTO link_types (
        domain_id, name, display_name, description,
        source_object_type_id, target_object_type_id, cardinality, bidirectional,
        created_at, updated_at
    ) VALUES (
        v_domain_id, 'TOLL_LANE_GENERATES_TRANSACTION', '生成', '收费车道生成交易流水',
        v_tolllane_type_id, v_transaction_type_id, '1:N', FALSE,
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
    );

    -- 4.13 TollGantry 1--n Transaction (生成)
    INSERT INTO link_types (
        domain_id, name, display_name, description,
        source_object_type_id, target_object_type_id, cardinality, bidirectional,
        created_at, updated_at
    ) VALUES (
        v_domain_id, 'TOLL_GANTRY_GENERATES_TRANSACTION', '生成', '收费门架生成交易流水',
        v_tollgantry_type_id, v_transaction_type_id, '1:N', FALSE,
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
    );

    -- 4.14 Transaction n--1 Path (汇聚为)
    INSERT INTO link_types (
        domain_id, name, display_name, description,
        source_object_type_id, target_object_type_id, cardinality, bidirectional,
        created_at, updated_at
    ) VALUES (
        v_domain_id, 'TRANSACTION_AGGREGATES_TO_PATH', '汇聚为', '交易流水汇聚为车辆通行路径',
        v_transaction_type_id, v_path_type_id, 'N:1', FALSE,
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
    );

    -- 4.15 Path 1--n PathDetail (持有)
    INSERT INTO link_types (
        domain_id, name, display_name, description,
        source_object_type_id, target_object_type_id, cardinality, bidirectional,
        created_at, updated_at
    ) VALUES (
        v_domain_id, 'PATH_HOLDS_PATH_DETAIL', '持有', '车辆通行路径持有路径明细',
        v_path_type_id, v_pathdetail_type_id, '1:N', FALSE,
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
    );

    -- 4.16 Path 1--1 RestorePath (拟合为)
    INSERT INTO link_types (
        domain_id, name, display_name, description,
        source_object_type_id, target_object_type_id, cardinality, bidirectional,
        created_at, updated_at
    ) VALUES (
        v_domain_id, 'PATH_FITS_TO_RESTORE_PATH', '拟合为', '车辆通行路径拟合为拟合路径',
        v_path_type_id, v_restorepath_type_id, '1:1', FALSE,
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
    );

    -- 4.17 RestorePath 1--n RestorePathDetail (持有)
    INSERT INTO link_types (
        domain_id, name, display_name, description,
        source_object_type_id, target_object_type_id, cardinality, bidirectional,
        created_at, updated_at
    ) VALUES (
        v_domain_id, 'RESTORE_PATH_HOLDS_RESTORE_PATH_DETAIL', '持有', '拟合路径持有拟合路径明细',
        v_restorepath_type_id, v_restorepathdetail_type_id, '1:N', FALSE,
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
    );

    -- 4.18 RestorePath 1--n SplitDetail (拆分为)
    INSERT INTO link_types (
        domain_id, name, display_name, description,
        source_object_type_id, target_object_type_id, cardinality, bidirectional,
        created_at, updated_at
    ) VALUES (
        v_domain_id, 'RESTORE_PATH_SPLITS_TO_SPLIT_DETAIL', '拆分为', '拟合路径拆分为拆分明细',
        v_restorepath_type_id, v_splitdetail_type_id, '1:N', FALSE,
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
    );

    -- 4.19 TollInterval 1--1 SplitDetail (关联)
    INSERT INTO link_types (
        domain_id, name, display_name, description,
        source_object_type_id, target_object_type_id, cardinality, bidirectional,
        created_at, updated_at
    ) VALUES (
        v_domain_id, 'TOLL_INTERVAL_RELATES_SPLIT_DETAIL', '关联', '收费单元关联拆分明细',
        v_tollinterval_type_id, v_splitdetail_type_id, '1:1', FALSE,
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
    );

END $$;

-- 注意：
-- 1. 此脚本仅包含业务域、对象类型、部分属性（User, Vehicle, Medium的部分属性）和所有链接类型
-- 2. 完整属性SQL请参考：省中心联网收费业务数据模型-属性补充-修正版.sql

