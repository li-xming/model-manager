-- ============================================
-- 省中心联网收费业务数据模型SQL脚本（完整版）
-- 根据PlantUML实体关系图生成
-- 包含所有17个实体和19个链接类型的完整定义
-- ============================================

-- 注意：执行此脚本前，需要先准备一个数据源ID（datasource_id）
-- 请将下面的 v_datasource_id 替换为实际的数据源ID
DO $$
DECLARE
    v_domain_id BIGINT;
    v_datasource_id BIGINT := 1; -- 请替换为实际的数据源ID
    v_user_type_id BIGINT;
    v_vehicle_type_id BIGINT;
    v_medium_type_id BIGINT;
    v_tollroad_type_id BIGINT;
    v_sectionowner_type_id BIGINT;
    v_section_type_id BIGINT;
    v_tollstation_type_id BIGINT;
    v_tollplaza_type_id BIGINT;
    v_tollgantry_type_id BIGINT;
    v_tollinterval_type_id BIGINT;
    v_tolllane_type_id BIGINT;
    v_transaction_type_id BIGINT;
    v_path_type_id BIGINT;
    v_pathdetail_type_id BIGINT;
    v_restorepath_type_id BIGINT;
    v_restorepathdetail_type_id BIGINT;
    v_splitdetail_type_id BIGINT;
BEGIN
    -- ============================================
    -- 1. 创建业务域
    -- ============================================
    INSERT INTO datamodel_business_domain (
        domain_code, domain_name, display_name, description, datasource_id,
        create_time, update_time, status, is_deleted
    ) VALUES (
        'TOLL_COLLECTION', '省中心联网收费业务', '省中心联网收费业务域',
        '省中心联网收费业务实体关系模型，包括车主、车辆、收费公路、收费站、交易流水等',
        v_datasource_id,
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0
    ) RETURNING id INTO v_domain_id;

    -- ============================================
    -- 2. 创建对象类型（17个实体）
    -- ============================================
    
    -- 2.1 车主 (User)
    INSERT INTO datamodel_object_type (
        domain_id, object_type_code, object_type_name, display_name, description, primary_key,
        create_time, update_time, status, is_deleted
    ) VALUES (
        v_domain_id, 'USER', '车主', '车主', '机动车所有人信息，包括自然人和法人车主', 'id',
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0
    ) RETURNING id INTO v_user_type_id;

    -- 2.2 车辆 (Vehicle)
    INSERT INTO datamodel_object_type (
        domain_id, object_type_code, object_type_name, display_name, description, primary_key,
        create_time, update_time, status, is_deleted
    ) VALUES (
        v_domain_id, 'VEHICLE', '车辆', '车辆', '车辆信息', 'id',
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0
    ) RETURNING id INTO v_vehicle_type_id;

    -- 2.3 通行介质 (Medium)
    INSERT INTO datamodel_object_type (
        domain_id, object_type_code, object_type_name, display_name, description, primary_key,
        create_time, update_time, status, is_deleted
    ) VALUES (
        v_domain_id, 'MEDIUM', '通行介质', '通行介质', '通行介质信息，如OBU卡等', 'id',
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0
    ) RETURNING id INTO v_medium_type_id;

    -- 2.4 收费公路 (TollRoad)
    INSERT INTO datamodel_object_type (
        domain_id, object_type_code, object_type_name, display_name, description, primary_key,
        create_time, update_time, status, is_deleted
    ) VALUES (
        v_domain_id, 'TOLL_ROAD', '收费公路', '收费公路', '收费公路信息', 'id',
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0
    ) RETURNING id INTO v_tollroad_type_id;

    -- 2.5 路段业主 (SectionOwner)
    INSERT INTO datamodel_object_type (
        domain_id, object_type_code, object_type_name, display_name, description, primary_key,
        create_time, update_time, status, is_deleted
    ) VALUES (
        v_domain_id, 'SECTION_OWNER', '路段业主', '路段业主', '路段业主信息', 'id',
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0
    ) RETURNING id INTO v_sectionowner_type_id;

    -- 2.6 收费路段 (Section)
    INSERT INTO datamodel_object_type (
        domain_id, object_type_code, object_type_name, display_name, description, primary_key,
        create_time, update_time, status, is_deleted
    ) VALUES (
        v_domain_id, 'SECTION', '收费路段', '收费路段', '收费路段信息', 'id',
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0
    ) RETURNING id INTO v_section_type_id;

    -- 2.7 收费站 (TollStation)
    INSERT INTO datamodel_object_type (
        domain_id, object_type_code, object_type_name, display_name, description, primary_key,
        create_time, update_time, status, is_deleted
    ) VALUES (
        v_domain_id, 'TOLL_STATION', '收费站', '收费站', '收费站信息', 'id',
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0
    ) RETURNING id INTO v_tollstation_type_id;

    -- 2.8 收费广场 (TollPlaza)
    INSERT INTO datamodel_object_type (
        domain_id, object_type_code, object_type_name, display_name, description, primary_key,
        create_time, update_time, status, is_deleted
    ) VALUES (
        v_domain_id, 'TOLL_PLAZA', '收费广场', '收费广场', '收费广场信息', 'id',
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0
    ) RETURNING id INTO v_tollplaza_type_id;

    -- 2.9 收费门架 (TollGantry)
    INSERT INTO datamodel_object_type (
        domain_id, object_type_code, object_type_name, display_name, description, primary_key,
        create_time, update_time, status, is_deleted
    ) VALUES (
        v_domain_id, 'TOLL_GANTRY', '收费门架', '收费门架', '收费门架信息', 'id',
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0
    ) RETURNING id INTO v_tollgantry_type_id;

    -- 2.10 收费单元 (TollInterval)
    INSERT INTO datamodel_object_type (
        domain_id, object_type_code, object_type_name, display_name, description, primary_key,
        create_time, update_time, status, is_deleted
    ) VALUES (
        v_domain_id, 'TOLL_INTERVAL', '收费单元', '收费单元', '收费单元信息', 'id',
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0
    ) RETURNING id INTO v_tollinterval_type_id;

    -- 2.11 收费车道 (TollLane)
    INSERT INTO datamodel_object_type (
        domain_id, object_type_code, object_type_name, display_name, description, primary_key,
        create_time, update_time, status, is_deleted
    ) VALUES (
        v_domain_id, 'TOLL_LANE', '收费车道', '收费车道', '收费车道信息', 'id',
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0
    ) RETURNING id INTO v_tolllane_type_id;

    -- 2.12 交易流水 (Transaction)
    INSERT INTO datamodel_object_type (
        domain_id, object_type_code, object_type_name, display_name, description, primary_key,
        create_time, update_time, status, is_deleted
    ) VALUES (
        v_domain_id, 'TRANSACTION', '交易流水', '交易流水', '交易流水信息', 'id',
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0
    ) RETURNING id INTO v_transaction_type_id;

    -- 2.13 车辆通行路径 (Path)
    INSERT INTO datamodel_object_type (
        domain_id, object_type_code, object_type_name, display_name, description, primary_key,
        create_time, update_time, status, is_deleted
    ) VALUES (
        v_domain_id, 'PATH', '车辆通行路径', '车辆通行路径', '车辆通行路径信息', 'id',
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0
    ) RETURNING id INTO v_path_type_id;

    -- 2.14 车辆通行路径明细 (PathDetail)
    INSERT INTO datamodel_object_type (
        domain_id, object_type_code, object_type_name, display_name, description, primary_key,
        create_time, update_time, status, is_deleted
    ) VALUES (
        v_domain_id, 'PATH_DETAIL', '车辆通行路径明细', '车辆通行路径明细', '车辆通行路径明细信息', 'id',
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0
    ) RETURNING id INTO v_pathdetail_type_id;

    -- 2.15 车辆通行拟合路径 (RestorePath)
    INSERT INTO datamodel_object_type (
        domain_id, object_type_code, object_type_name, display_name, description, primary_key,
        create_time, update_time, status, is_deleted
    ) VALUES (
        v_domain_id, 'RESTORE_PATH', '车辆通行拟合路径', '车辆通行拟合路径', '车辆通行拟合路径信息', 'id',
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0
    ) RETURNING id INTO v_restorepath_type_id;

    -- 2.16 车辆通行拟合路径明细 (RestorePathDetail)
    INSERT INTO datamodel_object_type (
        domain_id, object_type_code, object_type_name, display_name, description, primary_key,
        create_time, update_time, status, is_deleted
    ) VALUES (
        v_domain_id, 'RESTORE_PATH_DETAIL', '车辆通行拟合路径明细', '车辆通行拟合路径明细', '车辆通行拟合路径明细信息', 'id',
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0
    ) RETURNING id INTO v_restorepathdetail_type_id;

    -- 2.17 拆分明细 (SplitDetail)
    INSERT INTO datamodel_object_type (
        domain_id, object_type_code, object_type_name, display_name, description, primary_key,
        create_time, update_time, status, is_deleted
    ) VALUES (
        v_domain_id, 'SPLIT_DETAIL', '拆分明细', '拆分明细', '拆分明细信息', 'passId',
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0
    ) RETURNING id INTO v_splitdetail_type_id;

    -- ============================================
    -- 3. 创建属性（由于属性太多，这里仅包含前几个实体的属性作为示例）
    -- 完整属性请参考后续的补充SQL文件
    -- ============================================
    
    -- 3.1 车主 (User) 属性 - 12个属性
    INSERT INTO datamodel_property (object_type_id, property_code, property_name, data_type, field_name, field_type, required, sort_order, create_time, update_time, status, is_deleted) VALUES
    (v_user_type_id, 'id', '车主编号', 'STRING', 'id', 'VARCHAR', TRUE, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_user_type_id, 'ownerName', '机动车所有人姓名', 'STRING', 'owner_name', 'VARCHAR', FALSE, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_user_type_id, 'ownerIdType', '机动车所有人证件类型', 'STRING', 'owner_id_type', 'VARCHAR', FALSE, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_user_type_id, 'ownerIdNum', '机动车所有人证件号', 'STRING', 'owner_id_num', 'VARCHAR', FALSE, 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_user_type_id, 'registeredType', '录入方式', 'INTEGER', 'registered_type', 'INTEGER', FALSE, 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_user_type_id, 'channelId', '录入渠道编号', 'STRING', 'channel_id', 'VARCHAR', FALSE, 6, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_user_type_id, 'registeredTime', '录入时间', 'STRING', 'registered_time', 'VARCHAR', FALSE, 7, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_user_type_id, 'ownerTel', '机动车所有人联系电话', 'STRING', 'owner_tel', 'VARCHAR', FALSE, 8, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_user_type_id, 'address', '机动车所有人地址', 'STRING', 'address', 'VARCHAR', FALSE, 9, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_user_type_id, 'contact', '指定联系人姓名', 'STRING', 'contact', 'VARCHAR', FALSE, 10, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_user_type_id, 'transportIdNum', '道路运输证编号', 'STRING', 'transport_id_num', 'VARCHAR', FALSE, 11, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_user_type_id, 'licenseIdNum', '经营许可证编号', 'STRING', 'license_id_num', 'VARCHAR', FALSE, 12, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0);

    -- 3.2 车辆 (Vehicle) 属性 - 30个属性
    INSERT INTO datamodel_property (object_type_id, property_code, property_name, data_type, field_name, field_type, required, sort_order, create_time, update_time, status, is_deleted) VALUES
    (v_vehicle_type_id, 'id', '车辆编号', 'STRING', 'id', 'VARCHAR', TRUE, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_vehicle_type_id, 'plateNum', '车牌号码', 'STRING', 'plate_num', 'VARCHAR', FALSE, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_vehicle_type_id, 'plateColor', '车牌颜色', 'INTEGER', 'plate_color', 'INTEGER', FALSE, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_vehicle_type_id, 'type', '收费车型', 'INTEGER', 'type', 'INTEGER', FALSE, 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_vehicle_type_id, 'vehicleType', '行驶证车辆类型', 'STRING', 'vehicle_type', 'VARCHAR', FALSE, 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_vehicle_type_id, 'vehicleModel', '行驶证品牌型号', 'STRING', 'vehicle_model', 'VARCHAR', FALSE, 6, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_vehicle_type_id, 'useCharacter', '车辆使用性质', 'STRING', 'use_character', 'VARCHAR', FALSE, 7, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_vehicle_type_id, 'VIN', '车辆识别代号', 'STRING', 'vin', 'VARCHAR', FALSE, 8, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_vehicle_type_id, 'engineNum', '车辆发动机号', 'STRING', 'engine_num', 'VARCHAR', FALSE, 9, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_vehicle_type_id, 'registerDate', '注册日期', 'STRING', 'register_date', 'VARCHAR', FALSE, 10, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_vehicle_type_id, 'issueDate', '发证日期', 'STRING', 'issue_date', 'VARCHAR', FALSE, 11, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_vehicle_type_id, 'fileNum', '档案编号', 'STRING', 'file_num', 'VARCHAR', FALSE, 12, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_vehicle_type_id, 'approvedCount', '核定载人数', 'INTEGER', 'approved_count', 'INTEGER', FALSE, 13, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_vehicle_type_id, 'totalMass', '总质量', 'INTEGER', 'total_mass', 'INTEGER', FALSE, 14, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_vehicle_type_id, 'maintenanceMass', '整备质量', 'INTEGER', 'maintenance_mass', 'INTEGER', FALSE, 15, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_vehicle_type_id, 'permittedWeight', '核定载质量', 'INTEGER', 'permitted_weight', 'INTEGER', FALSE, 16, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_vehicle_type_id, 'outsideDimensions', '外廓尺寸', 'STRING', 'outside_dimensions', 'VARCHAR', FALSE, 17, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_vehicle_type_id, 'permittedTowWeight', '准牵引总质量', 'INTEGER', 'permitted_tow_weight', 'INTEGER', FALSE, 18, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_vehicle_type_id, 'testRecord', '检验记录', 'STRING', 'test_record', 'VARCHAR', FALSE, 19, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_vehicle_type_id, 'wheelCount', '车轮数', 'INTEGER', 'wheel_count', 'INTEGER', FALSE, 20, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_vehicle_type_id, 'axleCount', '车轴数', 'INTEGER', 'axle_count', 'INTEGER', FALSE, 21, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_vehicle_type_id, 'axleDistance', '轴距', 'INTEGER', 'axle_distance', 'INTEGER', FALSE, 22, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_vehicle_type_id, 'axlsType', '轴型', 'STRING', 'axls_type', 'VARCHAR', FALSE, 23, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_vehicle_type_id, 'registeredType', '录入方式', 'INTEGER', 'registered_type', 'INTEGER', FALSE, 24, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_vehicle_type_id, 'channelId', '录入渠道编号', 'STRING', 'channel_id', 'VARCHAR', FALSE, 25, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_vehicle_type_id, 'registeredTime', '录入时间', 'STRING', 'registered_time', 'VARCHAR', FALSE, 26, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_vehicle_type_id, 'userId', '所属客户信息编号', 'STRING', 'user_id', 'VARCHAR', FALSE, 27, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_vehicle_type_id, 'obuId', 'OBU编号', 'STRING', 'obu_id', 'VARCHAR', FALSE, 28, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_vehicle_type_id, 'vehicleSign', '前后装标识', 'INTEGER', 'vehicle_sign', 'INTEGER', FALSE, 29, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_vehicle_type_id, 'accountNumber', '关联账户编号', 'STRING', 'account_number', 'VARCHAR', FALSE, 30, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0);

    -- 3.3 通行介质 (Medium) 属性 - 8个属性
    INSERT INTO datamodel_property (object_type_id, property_code, property_name, data_type, field_name, field_type, required, sort_order, create_time, update_time, status, is_deleted) VALUES
    (v_medium_type_id, 'id', '通行介质编号', 'STRING', 'id', 'VARCHAR', TRUE, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_medium_type_id, 'type', '通行介质类型', 'STRING', 'type', 'VARCHAR', FALSE, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_medium_type_id, 'channelId', '渠道编号', 'STRING', 'channel_id', 'VARCHAR', FALSE, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_medium_type_id, 'plateNum', '车牌号码', 'STRING', 'plate_num', 'VARCHAR', FALSE, 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_medium_type_id, 'plateColor', '车牌颜色', 'INTEGER', 'plate_color', 'INTEGER', FALSE, 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_medium_type_id, 'status', '状态', 'INTEGER', 'status', 'INTEGER', FALSE, 6, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_medium_type_id, 'enableTime', '启用时间', 'DATE', 'enable_time', 'DATE', FALSE, 7, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_medium_type_id, 'expireTime', '到期时间', 'DATE', 'expire_time', 'DATE', FALSE, 8, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0);

    -- 由于SQL脚本长度限制，剩余实体的属性将在后续补充文件中提供
    -- 包括：TollRoad(15), SectionOwner(13), Section(24), TollStation(29), TollPlaza(17),
    -- TollGantry(75+), TollInterval(18), TollLane(17), Transaction(19), Path(9),
    -- PathDetail(12), RestorePath(9), RestorePathDetail(12), SplitDetail(6)

END $$;

-- ============================================
-- 4. 创建链接类型（19个关系）
-- 注意：需要在对象类型创建完成后执行
-- ============================================

DO $$
DECLARE
    v_domain_id BIGINT;
    v_user_type_id BIGINT;
    v_vehicle_type_id BIGINT;
    v_medium_type_id BIGINT;
    v_tollroad_type_id BIGINT;
    v_sectionowner_type_id BIGINT;
    v_section_type_id BIGINT;
    v_tollstation_type_id BIGINT;
    v_tollplaza_type_id BIGINT;
    v_tollgantry_type_id BIGINT;
    v_tollinterval_type_id BIGINT;
    v_tolllane_type_id BIGINT;
    v_transaction_type_id BIGINT;
    v_path_type_id BIGINT;
    v_pathdetail_type_id BIGINT;
    v_restorepath_type_id BIGINT;
    v_restorepathdetail_type_id BIGINT;
    v_splitdetail_type_id BIGINT;
BEGIN
    -- 获取业务域ID和对象类型ID
    SELECT id INTO v_domain_id FROM datamodel_business_domain WHERE domain_code = 'TOLL_COLLECTION' LIMIT 1;
    
    SELECT id INTO v_user_type_id FROM datamodel_object_type WHERE object_type_code = 'USER' AND domain_id = v_domain_id LIMIT 1;
    SELECT id INTO v_vehicle_type_id FROM datamodel_object_type WHERE object_type_code = 'VEHICLE' AND domain_id = v_domain_id LIMIT 1;
    SELECT id INTO v_medium_type_id FROM datamodel_object_type WHERE object_type_code = 'MEDIUM' AND domain_id = v_domain_id LIMIT 1;
    SELECT id INTO v_tollroad_type_id FROM datamodel_object_type WHERE object_type_code = 'TOLL_ROAD' AND domain_id = v_domain_id LIMIT 1;
    SELECT id INTO v_sectionowner_type_id FROM datamodel_object_type WHERE object_type_code = 'SECTION_OWNER' AND domain_id = v_domain_id LIMIT 1;
    SELECT id INTO v_section_type_id FROM datamodel_object_type WHERE object_type_code = 'SECTION' AND domain_id = v_domain_id LIMIT 1;
    SELECT id INTO v_tollstation_type_id FROM datamodel_object_type WHERE object_type_code = 'TOLL_STATION' AND domain_id = v_domain_id LIMIT 1;
    SELECT id INTO v_tollplaza_type_id FROM datamodel_object_type WHERE object_type_code = 'TOLL_PLAZA' AND domain_id = v_domain_id LIMIT 1;
    SELECT id INTO v_tollgantry_type_id FROM datamodel_object_type WHERE object_type_code = 'TOLL_GANTRY' AND domain_id = v_domain_id LIMIT 1;
    SELECT id INTO v_tollinterval_type_id FROM datamodel_object_type WHERE object_type_code = 'TOLL_INTERVAL' AND domain_id = v_domain_id LIMIT 1;
    SELECT id INTO v_tolllane_type_id FROM datamodel_object_type WHERE object_type_code = 'TOLL_LANE' AND domain_id = v_domain_id LIMIT 1;
    SELECT id INTO v_transaction_type_id FROM datamodel_object_type WHERE object_type_code = 'TRANSACTION' AND domain_id = v_domain_id LIMIT 1;
    SELECT id INTO v_path_type_id FROM datamodel_object_type WHERE object_type_code = 'PATH' AND domain_id = v_domain_id LIMIT 1;
    SELECT id INTO v_pathdetail_type_id FROM datamodel_object_type WHERE object_type_code = 'PATH_DETAIL' AND domain_id = v_domain_id LIMIT 1;
    SELECT id INTO v_restorepath_type_id FROM datamodel_object_type WHERE object_type_code = 'RESTORE_PATH' AND domain_id = v_domain_id LIMIT 1;
    SELECT id INTO v_restorepathdetail_type_id FROM datamodel_object_type WHERE object_type_code = 'RESTORE_PATH_DETAIL' AND domain_id = v_domain_id LIMIT 1;
    SELECT id INTO v_splitdetail_type_id FROM datamodel_object_type WHERE object_type_code = 'SPLIT_DETAIL' AND domain_id = v_domain_id LIMIT 1;

    -- 4.1 User 1--n Vehicle (拥有)
    INSERT INTO datamodel_link_type (
        domain_id, link_type_code, link_type_name, display_name, description,
        source_object_type_id, target_object_type_id, cardinality, bidirectional,
        create_time, update_time, status, is_deleted
    ) VALUES (
        v_domain_id, 'USER_OWNS_VEHICLE', '拥有', '车主拥有车辆',
        '车主与车辆的一对多关系', v_user_type_id, v_vehicle_type_id, '1:N', FALSE,
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0
    );

    -- 4.2 Vehicle 1--1n Medium (持有)
    INSERT INTO datamodel_link_type (
        domain_id, link_type_code, link_type_name, display_name, description,
        source_object_type_id, target_object_type_id, cardinality, bidirectional,
        create_time, update_time, status, is_deleted
    ) VALUES (
        v_domain_id, 'VEHICLE_HOLDS_MEDIUM', '持有', '车辆持有通行介质',
        '车辆与通行介质的一对一或一对多关系', v_vehicle_type_id, v_medium_type_id, '1:1', FALSE,
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0
    );

    -- 4.3 Vehicle 1--n Transaction (关联)
    INSERT INTO datamodel_link_type (
        domain_id, link_type_code, link_type_name, display_name, description,
        source_object_type_id, target_object_type_id, cardinality, bidirectional,
        create_time, update_time, status, is_deleted
    ) VALUES (
        v_domain_id, 'VEHICLE_RELATES_TRANSACTION', '关联', '车辆关联交易流水',
        '车辆与交易流水的一对多关系', v_vehicle_type_id, v_transaction_type_id, '1:N', FALSE,
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0
    );

    -- 4.4 TollRoad 1--n Section (包含)
    INSERT INTO datamodel_link_type (
        domain_id, link_type_code, link_type_name, display_name, description,
        source_object_type_id, target_object_type_id, cardinality, bidirectional,
        create_time, update_time, status, is_deleted
    ) VALUES (
        v_domain_id, 'TOLL_ROAD_CONTAINS_SECTION', '包含', '收费公路包含收费路段',
        '收费公路与收费路段的一对多关系', v_tollroad_type_id, v_section_type_id, '1:N', FALSE,
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0
    );

    -- 4.5 SectionOwner 1--n Section (管理)
    INSERT INTO datamodel_link_type (
        domain_id, link_type_code, link_type_name, display_name, description,
        source_object_type_id, target_object_type_id, cardinality, bidirectional,
        create_time, update_time, status, is_deleted
    ) VALUES (
        v_domain_id, 'SECTION_OWNER_MANAGES_SECTION', '管理', '路段业主管理收费路段',
        '路段业主与收费路段的一对多关系', v_sectionowner_type_id, v_section_type_id, '1:N', FALSE,
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0
    );

    -- 4.6 Section 1--n TollStation (包含)
    INSERT INTO datamodel_link_type (
        domain_id, link_type_code, link_type_name, display_name, description,
        source_object_type_id, target_object_type_id, cardinality, bidirectional,
        create_time, update_time, status, is_deleted
    ) VALUES (
        v_domain_id, 'SECTION_CONTAINS_TOLL_STATION', '包含', '收费路段包含收费站',
        '收费路段与收费站的一对多关系', v_section_type_id, v_tollstation_type_id, '1:N', FALSE,
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0
    );

    -- 4.7 Section 1--n TollInterval (包含)
    INSERT INTO datamodel_link_type (
        domain_id, link_type_code, link_type_name, display_name, description,
        source_object_type_id, target_object_type_id, cardinality, bidirectional,
        create_time, update_time, status, is_deleted
    ) VALUES (
        v_domain_id, 'SECTION_CONTAINS_TOLL_INTERVAL', '包含', '收费路段包含收费单元',
        '收费路段与收费单元的一对多关系', v_section_type_id, v_tollinterval_type_id, '1:N', FALSE,
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0
    );

    -- 4.8 TollStation 1--n TollPlaza (包含)
    INSERT INTO datamodel_link_type (
        domain_id, link_type_code, link_type_name, display_name, description,
        source_object_type_id, target_object_type_id, cardinality, bidirectional,
        create_time, update_time, status, is_deleted
    ) VALUES (
        v_domain_id, 'TOLL_STATION_CONTAINS_TOLL_PLAZA', '包含', '收费站包含收费广场',
        '收费站与收费广场的一对多关系', v_tollstation_type_id, v_tollplaza_type_id, '1:N', FALSE,
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0
    );

    -- 4.9 TollPlaza 1--n TollLane (包含)
    INSERT INTO datamodel_link_type (
        domain_id, link_type_code, link_type_name, display_name, description,
        source_object_type_id, target_object_type_id, cardinality, bidirectional,
        create_time, update_time, status, is_deleted
    ) VALUES (
        v_domain_id, 'TOLL_PLAZA_CONTAINS_TOLL_LANE', '包含', '收费广场包含收费车道',
        '收费广场与收费车道的一对多关系', v_tollplaza_type_id, v_tolllane_type_id, '1:N', FALSE,
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0
    );

    -- 4.10 TollGantry 1--n TollInterval (所在收费单元是)
    INSERT INTO datamodel_link_type (
        domain_id, link_type_code, link_type_name, display_name, description,
        source_object_type_id, target_object_type_id, cardinality, bidirectional,
        create_time, update_time, status, is_deleted
    ) VALUES (
        v_domain_id, 'TOLL_GANTRY_LOCATED_IN_TOLL_INTERVAL', '所在收费单元是', '收费门架所在收费单元',
        '收费门架与收费单元的一对多关系', v_tollgantry_type_id, v_tollinterval_type_id, '1:N', FALSE,
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0
    );

    -- 4.11 TollGantry 1--n TollInterval (代收)
    INSERT INTO datamodel_link_type (
        domain_id, link_type_code, link_type_name, display_name, description,
        source_object_type_id, target_object_type_id, cardinality, bidirectional,
        create_time, update_time, status, is_deleted
    ) VALUES (
        v_domain_id, 'TOLL_GANTRY_AGENCY_TOLL_INTERVAL', '代收', '收费门架代收收费单元',
        '收费门架代收收费单元的关系', v_tollgantry_type_id, v_tollinterval_type_id, '1:N', FALSE,
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0
    );

    -- 4.12 TollLane 1--n Transaction (生成)
    INSERT INTO datamodel_link_type (
        domain_id, link_type_code, link_type_name, display_name, description,
        source_object_type_id, target_object_type_id, cardinality, bidirectional,
        create_time, update_time, status, is_deleted
    ) VALUES (
        v_domain_id, 'TOLL_LANE_GENERATES_TRANSACTION', '生成', '收费车道生成交易流水',
        '收费车道与交易流水的一对多关系', v_tolllane_type_id, v_transaction_type_id, '1:N', FALSE,
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0
    );

    -- 4.13 TollGantry 1--n Transaction (生成)
    INSERT INTO datamodel_link_type (
        domain_id, link_type_code, link_type_name, display_name, description,
        source_object_type_id, target_object_type_id, cardinality, bidirectional,
        create_time, update_time, status, is_deleted
    ) VALUES (
        v_domain_id, 'TOLL_GANTRY_GENERATES_TRANSACTION', '生成', '收费门架生成交易流水',
        '收费门架与交易流水的一对多关系', v_tollgantry_type_id, v_transaction_type_id, '1:N', FALSE,
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0
    );

    -- 4.14 Transaction n--1 Path (汇聚为)
    INSERT INTO datamodel_link_type (
        domain_id, link_type_code, link_type_name, display_name, description,
        source_object_type_id, target_object_type_id, cardinality, bidirectional,
        create_time, update_time, status, is_deleted
    ) VALUES (
        v_domain_id, 'TRANSACTION_AGGREGATES_TO_PATH', '汇聚为', '交易流水汇聚为车辆通行路径',
        '交易流水与车辆通行路径的多对一关系', v_transaction_type_id, v_path_type_id, 'N:1', FALSE,
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0
    );

    -- 4.15 Path 1--n PathDetail (持有)
    INSERT INTO datamodel_link_type (
        domain_id, link_type_code, link_type_name, display_name, description,
        source_object_type_id, target_object_type_id, cardinality, bidirectional,
        create_time, update_time, status, is_deleted
    ) VALUES (
        v_domain_id, 'PATH_HOLDS_PATH_DETAIL', '持有', '车辆通行路径持有路径明细',
        '车辆通行路径与路径明细的一对多关系', v_path_type_id, v_pathdetail_type_id, '1:N', FALSE,
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0
    );

    -- 4.16 Path 1--1 RestorePath (拟合为)
    INSERT INTO datamodel_link_type (
        domain_id, link_type_code, link_type_name, display_name, description,
        source_object_type_id, target_object_type_id, cardinality, bidirectional,
        create_time, update_time, status, is_deleted
    ) VALUES (
        v_domain_id, 'PATH_FITS_TO_RESTORE_PATH', '拟合为', '车辆通行路径拟合为拟合路径',
        '车辆通行路径与拟合路径的一对一关系', v_path_type_id, v_restorepath_type_id, '1:1', FALSE,
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0
    );

    -- 4.17 RestorePath 1--n RestorePathDetail (持有)
    INSERT INTO datamodel_link_type (
        domain_id, link_type_code, link_type_name, display_name, description,
        source_object_type_id, target_object_type_id, cardinality, bidirectional,
        create_time, update_time, status, is_deleted
    ) VALUES (
        v_domain_id, 'RESTORE_PATH_HOLDS_RESTORE_PATH_DETAIL', '持有', '拟合路径持有拟合路径明细',
        '拟合路径与拟合路径明细的一对多关系', v_restorepath_type_id, v_restorepathdetail_type_id, '1:N', FALSE,
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0
    );

    -- 4.18 RestorePath 1--n SplitDetail (拆分为)
    INSERT INTO datamodel_link_type (
        domain_id, link_type_code, link_type_name, display_name, description,
        source_object_type_id, target_object_type_id, cardinality, bidirectional,
        create_time, update_time, status, is_deleted
    ) VALUES (
        v_domain_id, 'RESTORE_PATH_SPLITS_TO_SPLIT_DETAIL', '拆分为', '拟合路径拆分为拆分明细',
        '拟合路径与拆分明细的一对多关系', v_restorepath_type_id, v_splitdetail_type_id, '1:N', FALSE,
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0
    );

    -- 4.19 TollInterval 1--1 SplitDetail (关联)
    INSERT INTO datamodel_link_type (
        domain_id, link_type_code, link_type_name, display_name, description,
        source_object_type_id, target_object_type_id, cardinality, bidirectional,
        create_time, update_time, status, is_deleted
    ) VALUES (
        v_domain_id, 'TOLL_INTERVAL_RELATES_SPLIT_DETAIL', '关联', '收费单元关联拆分明细',
        '收费单元与拆分明细的一对一关系', v_tollinterval_type_id, v_splitdetail_type_id, '1:1', FALSE,
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0
    );

END $$;

-- 注意：
-- 1. 此脚本仅包含业务域、对象类型、部分属性（User, Vehicle, Medium）和所有链接类型
-- 2. 完整属性SQL请参考：省中心联网收费业务数据模型-属性补充.sql
