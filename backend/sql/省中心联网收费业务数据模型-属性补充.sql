-- ============================================
-- 省中心联网收费业务数据模型 - 完整属性SQL
-- 根据PlantUML实体关系图生成
-- 注意：此文件需要在主SQL脚本执行后运行
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

    -- User 属性 - 12个属性
    INSERT INTO datamodel_property (object_type_id, property_code, property_name, data_type, field_name, field_type, required, sort_order, create_time, update_time, status, is_deleted) VALUES
    (v_user_type_id, 'id', '车主编号', 'STRING', 'id', 'VARCHAR', TRUE, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_user_type_id, 'ownerName', '机动车所有人姓名', 'STRING', 'owner_name', 'VARCHAR', FALSE, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_user_type_id, 'ownerIdType', '机动车所有人证件类型', 'STRING', 'owner_id_type', 'VARCHAR', FALSE, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_user_type_id, 'ownerIdNum', '机动车所有人证件号', 'STRING', 'owner_id_num', 'VARCHAR', FALSE, 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_user_type_id, 'registeredType', '录入方式，1-线上，2-线下', 'INTEGER', 'registered_type', 'INTEGER', FALSE, 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_user_type_id, 'channelId', '录入渠道编号', 'STRING', 'channel_id', 'VARCHAR', FALSE, 6, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_user_type_id, 'registeredTime', '录入时间', 'STRING', 'registered_time', 'VARCHAR', FALSE, 7, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_user_type_id, 'ownerTel', '机动车所有人联系电话', 'STRING', 'owner_tel', 'VARCHAR', FALSE, 8, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_user_type_id, 'address', '机动车所有人地址', 'STRING', 'address', 'VARCHAR', FALSE, 9, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_user_type_id, 'contact', '指定联系人姓名，法人客户独有', 'STRING', 'contact', 'VARCHAR', FALSE, 10, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_user_type_id, 'transportIdNum', '道路运输证编号，法人客户独有', 'STRING', 'transport_id_num', 'VARCHAR', FALSE, 11, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_user_type_id, 'licenseIdNum', '经营许可证编号，法人客户独有', 'STRING', 'license_id_num', 'VARCHAR', FALSE, 12, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0);

    -- Vehicle 属性 - 30个属性
    INSERT INTO datamodel_property (object_type_id, property_code, property_name, data_type, field_name, field_type, required, sort_order, create_time, update_time, status, is_deleted) VALUES
    (v_vehicle_type_id, 'id', '车辆编号，由车牌号码+间隔符+车牌颜色组成', 'STRING', 'id', 'VARCHAR', TRUE, 13, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_vehicle_type_id, 'plateNum', '车牌号码', 'STRING', 'plate_num', 'VARCHAR', FALSE, 14, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_vehicle_type_id, 'plateColor', '车牌颜色，2位数字', 'INTEGER', 'plate_color', 'INTEGER', FALSE, 15, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_vehicle_type_id, 'type', '收费车型，整数', 'INTEGER', 'type', 'INTEGER', FALSE, 16, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_vehicle_type_id, 'vehicleType', '行驶证车辆类型', 'STRING', 'vehicle_type', 'VARCHAR', FALSE, 17, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_vehicle_type_id, 'vehicleModel', '行驶证品牌型号', 'STRING', 'vehicle_model', 'VARCHAR', FALSE, 18, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_vehicle_type_id, 'useCharacter', '车辆使用性质', 'STRING', 'use_character', 'VARCHAR', FALSE, 19, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_vehicle_type_id, 'VIN', '车辆识别代号', 'STRING', 'vin', 'VARCHAR', FALSE, 20, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_vehicle_type_id, 'engineNum', '车辆发动机号', 'STRING', 'engine_num', 'VARCHAR', FALSE, 21, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_vehicle_type_id, 'registerDate', '注册日期', 'STRING', 'register_date', 'VARCHAR', FALSE, 22, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_vehicle_type_id, 'issueDate', '发证日期', 'STRING', 'issue_date', 'VARCHAR', FALSE, 23, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_vehicle_type_id, 'fileNum', '档案编号', 'STRING', 'file_num', 'VARCHAR', FALSE, 24, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_vehicle_type_id, 'approvedCount', '核定载人数', 'INTEGER', 'approved_count', 'INTEGER', FALSE, 25, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_vehicle_type_id, 'totalMass', '总质量，Kg', 'INTEGER', 'total_mass', 'INTEGER', FALSE, 26, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_vehicle_type_id, 'maintenanceMass', '整备质量，Kg', 'INTEGER', 'maintenance_mass', 'INTEGER', FALSE, 27, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_vehicle_type_id, 'permittedWeight', '核定载质量，Kg', 'INTEGER', 'permitted_weight', 'INTEGER', FALSE, 28, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_vehicle_type_id, 'outsideDimensions', '外廓尺寸', 'STRING', 'outside_dimensions', 'VARCHAR', FALSE, 29, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_vehicle_type_id, 'permittedTowWeight', '准牵引总质量，Kg', 'INTEGER', 'permitted_tow_weight', 'INTEGER', FALSE, 30, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_vehicle_type_id, 'testRecord', '检验记录', 'STRING', 'test_record', 'VARCHAR', FALSE, 31, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_vehicle_type_id, 'wheelCount', '车轮数', 'INTEGER', 'wheel_count', 'INTEGER', FALSE, 32, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_vehicle_type_id, 'axleCount', '车轴数', 'INTEGER', 'axle_count', 'INTEGER', FALSE, 33, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_vehicle_type_id, 'axleDistance', '轴距，mm', 'INTEGER', 'axle_distance', 'INTEGER', FALSE, 34, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_vehicle_type_id, 'axlsType', '轴型', 'STRING', 'axls_type', 'VARCHAR', FALSE, 35, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_vehicle_type_id, 'registeredType', '录入方式，1-线上，2-线下', 'INTEGER', 'registered_type', 'INTEGER', FALSE, 36, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_vehicle_type_id, 'channelId', '录入渠道编号', 'STRING', 'channel_id', 'VARCHAR', FALSE, 37, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_vehicle_type_id, 'registeredTime', '录入时间', 'STRING', 'registered_time', 'VARCHAR', FALSE, 38, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_vehicle_type_id, 'userId', '所属客户信息编号', 'STRING', 'user_id', 'VARCHAR', FALSE, 39, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_vehicle_type_id, 'obuId', 'OBU编号', 'STRING', 'obu_id', 'VARCHAR', FALSE, 40, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_vehicle_type_id, 'vehicleSign', '前后装标识，1-前装2-后装', 'INTEGER', 'vehicle_sign', 'INTEGER', FALSE, 41, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_vehicle_type_id, 'accountNumber', '关联账户编号', 'STRING', 'account_number', 'VARCHAR', FALSE, 42, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0);

    -- Medium 属性 - 8个属性
    INSERT INTO datamodel_property (object_type_id, property_code, property_name, data_type, field_name, field_type, required, sort_order, create_time, update_time, status, is_deleted) VALUES
    (v_medium_type_id, 'id', '通行介质编号', 'STRING', 'id', 'VARCHAR', TRUE, 43, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_medium_type_id, 'type', '通行介质类型', 'STRING', 'type', 'VARCHAR', FALSE, 44, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_medium_type_id, 'channelId', '渠道编号', 'STRING', 'channel_id', 'VARCHAR', FALSE, 45, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_medium_type_id, 'plateNum', '车牌号码', 'STRING', 'plate_num', 'VARCHAR', FALSE, 46, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_medium_type_id, 'plateColor', '车牌颜色', 'INTEGER', 'plate_color', 'INTEGER', FALSE, 47, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_medium_type_id, 'status', '状态', 'INTEGER', 'status', 'INTEGER', FALSE, 48, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_medium_type_id, 'enableTime', '启用时间', 'DATE', 'enable_time', 'DATE', FALSE, 49, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_medium_type_id, 'expireTime', '到期时间', 'DATE', 'expire_time', 'DATE', FALSE, 50, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0);

    -- Tollroad 属性 - 15个属性
    INSERT INTO datamodel_property (object_type_id, property_code, property_name, data_type, field_name, field_type, required, sort_order, create_time, update_time, status, is_deleted) VALUES
    (v_tollroad_type_id, 'id', '收费公路编号，由公路编号和省域编号组成', 'STRING', 'id', 'VARCHAR', TRUE, 51, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollroad_type_id, 'roadNum', '公路编号', 'STRING', 'road_num', 'VARCHAR', FALSE, 52, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollroad_type_id, 'provinceId', '省域编号，2位数字', 'STRING', 'province_id', 'VARCHAR', FALSE, 53, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollroad_type_id, 'name', '收费公路名称', 'STRING', 'name', 'VARCHAR', FALSE, 54, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollroad_type_id, 'level', '技术等级', 'INTEGER', 'level', 'INTEGER', FALSE, 55, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollroad_type_id, 'startSite', '起始计费位置地点', 'STRING', 'start_site', 'VARCHAR', FALSE, 56, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollroad_type_id, 'startStakeNum', '起始位置桩号', 'STRING', 'start_stake_num', 'VARCHAR', FALSE, 57, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollroad_type_id, 'startLat', '起始计费位置纬度', 'STRING', 'start_lat', 'VARCHAR', FALSE, 58, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollroad_type_id, 'startLng', '起始计费位置经度', 'STRING', 'start_lng', 'VARCHAR', FALSE, 59, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollroad_type_id, 'startStationId', '起点收费站编号', 'STRING', 'start_station_id', 'VARCHAR', FALSE, 60, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollroad_type_id, 'endSite', '终止计费位置地点', 'STRING', 'end_site', 'VARCHAR', FALSE, 61, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollroad_type_id, 'endStakeNum', '终止位置桩号', 'STRING', 'end_stake_num', 'VARCHAR', FALSE, 62, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollroad_type_id, 'endLat', '终止计费位置纬度', 'STRING', 'end_lat', 'VARCHAR', FALSE, 63, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollroad_type_id, 'endLng', '终止计费位置经度', 'STRING', 'end_lng', 'VARCHAR', FALSE, 64, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollroad_type_id, 'endStationId', '终点收费站编号', 'STRING', 'end_station_id', 'VARCHAR', FALSE, 65, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0);

    -- Sectionowner 属性 - 13个属性
    INSERT INTO datamodel_property (object_type_id, property_code, property_name, data_type, field_name, field_type, required, sort_order, create_time, update_time, status, is_deleted) VALUES
    (v_sectionowner_type_id, 'id', '收费公路业主编号，由省域编号+参与方类型+业主顺序码组成', 'STRING', 'id', 'VARCHAR', TRUE, 66, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_sectionowner_type_id, 'provinceId', '省域编号，2位数字', 'STRING', 'province_id', 'VARCHAR', FALSE, 67, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_sectionowner_type_id, 'type', '参与方类型，2位数字', 'STRING', 'type', 'VARCHAR', FALSE, 68, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_sectionowner_type_id, 'num', '业主顺序码，3位数字', 'STRING', 'num', 'VARCHAR', FALSE, 69, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_sectionowner_type_id, 'name', '业主单位名称', 'STRING', 'name', 'VARCHAR', FALSE, 70, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_sectionowner_type_id, 'contact', '联系人姓名', 'STRING', 'contact', 'VARCHAR', FALSE, 71, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_sectionowner_type_id, 'tel', '联系电话', 'STRING', 'tel', 'VARCHAR', FALSE, 72, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_sectionowner_type_id, 'address', '地址', 'STRING', 'address', 'VARCHAR', FALSE, 73, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_sectionowner_type_id, 'bank', '开户行', 'STRING', 'bank', 'VARCHAR', FALSE, 74, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_sectionowner_type_id, 'bankAddr', '开户行地址', 'STRING', 'bank_addr', 'VARCHAR', FALSE, 75, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_sectionowner_type_id, 'bankAccount', '开户行账号', 'STRING', 'bank_account', 'VARCHAR', FALSE, 76, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_sectionowner_type_id, 'taxpayerCode', '纳税人识别号', 'STRING', 'taxpayer_code', 'VARCHAR', FALSE, 77, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_sectionowner_type_id, 'creditCode', '统一社会信用代码/组织机构代码证号', 'STRING', 'credit_code', 'VARCHAR', FALSE, 78, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0);

    -- Section 属性 - 24个属性
    INSERT INTO datamodel_property (object_type_id, property_code, property_name, data_type, field_name, field_type, required, sort_order, create_time, update_time, status, is_deleted) VALUES
    (v_section_type_id, 'id', '收费路段编号，收费路段编号=收费公路编号+收费路段顺序码+保留位', 'STRING', 'id', 'VARCHAR', TRUE, 79, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_section_type_id, 'tollRoadId', '收费公路编号', 'STRING', 'toll_road_id', 'VARCHAR', FALSE, 80, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_section_type_id, 'num', '收费路段顺序码，3位数字', 'STRING', 'num', 'VARCHAR', FALSE, 81, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_section_type_id, 'reservedNum', '保留位，1位数字默认为"0"', 'STRING', 'reserved_num', 'VARCHAR', FALSE, 82, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_section_type_id, 'name', '收费路段名称', 'STRING', 'name', 'VARCHAR', FALSE, 83, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_section_type_id, 'type', '路段性质，1-经营性2-还贷性', 'INTEGER', 'type', 'INTEGER', FALSE, 84, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_section_type_id, 'length', '路段里程，单位：m', 'INTEGER', 'length', 'INTEGER', FALSE, 85, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_section_type_id, 'online', '联网收费状态，1-联网收费2-独立收费', 'INTEGER', 'online', 'INTEGER', FALSE, 86, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_section_type_id, 'startLat', '起始计费位置纬度', 'STRING', 'start_lat', 'VARCHAR', FALSE, 87, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_section_type_id, 'startLng', '起始计费位置经度', 'STRING', 'start_lng', 'VARCHAR', FALSE, 88, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_section_type_id, 'startStakeNum', '起始计费位置桩号', 'STRING', 'start_stake_num', 'VARCHAR', FALSE, 89, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_section_type_id, 'endStakeNum', '终止计费位置桩号', 'STRING', 'end_stake_num', 'VARCHAR', FALSE, 90, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_section_type_id, 'endLat', '终止计费位置纬度', 'STRING', 'end_lat', 'VARCHAR', FALSE, 91, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_section_type_id, 'endLng', '终止计费位置经度', 'STRING', 'end_lng', 'VARCHAR', FALSE, 92, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_section_type_id, 'tax', '是否纳税，1-是2-否', 'INTEGER', 'tax', 'INTEGER', FALSE, 93, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_section_type_id, 'taxRate', '税率', 'STRING', 'tax_rate', 'VARCHAR', FALSE, 94, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_section_type_id, 'sectionOwnerId', '所属业主编号', 'STRING', 'section_owner_id', 'VARCHAR', FALSE, 95, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_section_type_id, 'chargeType', '路段收费方式，1-开放式2-封闭式', 'INTEGER', 'charge_type', 'INTEGER', FALSE, 96, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_section_type_id, 'tollRoads', '重合收费公路编号', 'STRING', 'toll_roads', 'VARCHAR', FALSE, 97, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_section_type_id, 'builtTime', '开工时间', 'STRING', 'built_time', 'VARCHAR', FALSE, 98, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_section_type_id, 'startTime', '通车时间', 'STRING', 'start_time', 'VARCHAR', FALSE, 99, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_section_type_id, 'endTime', '停止收费时间', 'STRING', 'end_time', 'VARCHAR', FALSE, 100, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_section_type_id, 'nextTaxRate', '待用税率', 'STRING', 'next_tax_rate', 'VARCHAR', FALSE, 101, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_section_type_id, 'nextRateDate', '待用税率生效时间', 'STRING', 'next_rate_date', 'VARCHAR', FALSE, 102, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0);

    -- Tollstation 属性 - 32个属性
    INSERT INTO datamodel_property (object_type_id, property_code, property_name, data_type, field_name, field_type, required, sort_order, create_time, update_time, status, is_deleted) VALUES
    (v_tollstation_type_id, 'id', '收费站编号，收费站编号=收费路段编号+收费站顺序码+保留位', 'STRING', 'id', 'VARCHAR', TRUE, 103, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollstation_type_id, 'sectionId', '收费路段编号', 'STRING', 'section_id', 'VARCHAR', FALSE, 104, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollstation_type_id, 'num', '收费站顺序码，2位数字', 'STRING', 'num', 'VARCHAR', FALSE, 105, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollstation_type_id, 'reservedNum', '保留位，1位数字默认为"0"', 'STRING', 'reserved_num', 'VARCHAR', FALSE, 106, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollstation_type_id, 'name', '收费站名称', 'STRING', 'name', 'VARCHAR', FALSE, 107, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollstation_type_id, 'tollPlazaCount', '收费广场数量', 'INTEGER', 'toll_plaza_count', 'INTEGER', FALSE, 108, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollstation_type_id, 'stationHex', '收费站HEX字符串', 'STRING', 'station_hex', 'VARCHAR', FALSE, 109, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollstation_type_id, 'lineType', '线路类型', 'STRING', 'line_type', 'VARCHAR', FALSE, 110, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollstation_type_id, 'operators', '网络所属运营商', 'STRING', 'operators', 'VARCHAR', FALSE, 111, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollstation_type_id, 'dataMergePoint', '数据汇聚点', 'STRING', 'data_merge_point', 'VARCHAR', FALSE, 112, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollstation_type_id, 'imei', 'IMEI号', 'STRING', 'imei', 'VARCHAR', FALSE, 113, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollstation_type_id, 'ip', '接入设备ip', 'STRING', 'ip', 'VARCHAR', FALSE, 114, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollstation_type_id, 'snmpVersion', 'snmp协议版本号', 'STRING', 'snmp_version', 'VARCHAR', FALSE, 115, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollstation_type_id, 'snmpPort', 'snmp端口', 'INTEGER', 'snmp_port', 'INTEGER', FALSE, 116, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollstation_type_id, 'community', '团体名称', 'STRING', 'community', 'VARCHAR', FALSE, 117, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollstation_type_id, 'securityName', '用户名', 'STRING', 'security_name', 'VARCHAR', FALSE, 118, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollstation_type_id, 'securityLevel', '安全级别', 'STRING', 'security_level', 'VARCHAR', FALSE, 119, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollstation_type_id, 'authentication', '认证协议', 'STRING', 'authentication', 'VARCHAR', FALSE, 120, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollstation_type_id, 'authKey', '认证密钥', 'STRING', 'auth_key', 'VARCHAR', FALSE, 121, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollstation_type_id, 'encryption', '加密算法', 'STRING', 'encryption', 'VARCHAR', FALSE, 122, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollstation_type_id, 'secretKey', '加密密钥', 'STRING', 'secret_key', 'VARCHAR', FALSE, 123, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollstation_type_id, 'serverManuID', '站级服务器厂商代码', 'STRING', 'server_manu_id', 'VARCHAR', FALSE, 124, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollstation_type_id, 'serversysName', '站级服务器操作系统名称', 'STRING', 'serversys_name', 'VARCHAR', FALSE, 125, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollstation_type_id, 'serversysVer', '站级服务器操作系统版本号', 'STRING', 'serversys_ver', 'VARCHAR', FALSE, 126, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollstation_type_id, 'serverDateVer', '站级服务器数据库系统版本号', 'STRING', 'server_date_ver', 'VARCHAR', FALSE, 127, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollstation_type_id, 'type', '类型，1-省界站2-普通站', 'INTEGER', 'type', 'INTEGER', FALSE, 128, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollstation_type_id, 'status', '状态，1-在建2-运行3-撤销4-停用', 'INTEGER', 'status', 'INTEGER', FALSE, 129, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollstation_type_id, 'realType', '实体类型，1-实体2-虚拟', 'INTEGER', 'real_type', 'INTEGER', FALSE, 130, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollstation_type_id, 'regionName', '所在地级市', 'STRING', 'region_name', 'VARCHAR', FALSE, 131, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollstation_type_id, 'countryName', '所在区县名称', 'STRING', 'country_name', 'VARCHAR', FALSE, 132, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollstation_type_id, 'regionalismCode', '区县六位行政区划编码', 'STRING', 'regionalism_code', 'VARCHAR', FALSE, 133, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollstation_type_id, 'agencyGantryIds', '代收门架编号', 'STRING', 'agency_gantry_ids', 'VARCHAR', FALSE, 134, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0);

    -- Tollplaza 属性 - 17个属性
    INSERT INTO datamodel_property (object_type_id, property_code, property_name, data_type, field_name, field_type, required, sort_order, create_time, update_time, status, is_deleted) VALUES
    (v_tollplaza_type_id, 'id', '收费广场编号，收费广场编号=收费站编号+收费广场类型+收费广场顺序码+保留位', 'STRING', 'id', 'VARCHAR', TRUE, 135, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollplaza_type_id, 'tollStationId', '收费站编号', 'STRING', 'toll_station_id', 'VARCHAR', FALSE, 136, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollplaza_type_id, 'type', '收费广场类型', 'INTEGER', 'type', 'INTEGER', FALSE, 137, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollplaza_type_id, 'num', '收费车道顺序码，2位数字', 'STRING', 'num', 'VARCHAR', FALSE, 138, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollplaza_type_id, 'reservedNum', '保留位，1位数字默认为"0"', 'STRING', 'reserved_num', 'VARCHAR', FALSE, 139, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollplaza_type_id, 'tidalTime', '潮汐车道反向时间', 'STRING', 'tidal_time', 'VARCHAR', FALSE, 140, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollplaza_type_id, 'startTime', '起始日期', 'STRING', 'start_time', 'VARCHAR', FALSE, 141, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollplaza_type_id, 'endTime', '终止日期', 'STRING', 'end_time', 'VARCHAR', FALSE, 142, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollplaza_type_id, 'status', '使用状态，1-停用2-在用', 'INTEGER', 'status', 'INTEGER', FALSE, 143, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollplaza_type_id, 'laneHex', '车道HEX字符串', 'STRING', 'lane_hex', 'VARCHAR', FALSE, 144, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollplaza_type_id, 'rsuManUID', 'RSU厂商代码', 'STRING', 'rsu_man_uid', 'VARCHAR', FALSE, 145, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollplaza_type_id, 'rsuModel', 'RSU型号', 'STRING', 'rsu_model', 'VARCHAR', FALSE, 146, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollplaza_type_id, 'rsuID', 'RSU编号', 'STRING', 'rsu_id', 'VARCHAR', FALSE, 147, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollplaza_type_id, 'entryExitType', '出入口类型，1-入口2-出口3-潮汐', 'INTEGER', 'entry_exit_type', 'INTEGER', FALSE, 148, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollplaza_type_id, 'railingPos', '车道栏杆位置，1-前2-中3-后9-无', 'INTEGER', 'railing_pos', 'INTEGER', FALSE, 149, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollplaza_type_id, 'ifContainLimitWeight', '是否有治超，1-有2-无', 'INTEGER', 'if_contain_limit_weight', 'INTEGER', FALSE, 150, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollplaza_type_id, 'VPLRManUID', '牌识厂商代码', 'STRING', 'vplr_man_uid', 'VARCHAR', FALSE, 151, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0);

    -- Tollgantry 属性 - 72个属性
    INSERT INTO datamodel_property (object_type_id, property_code, property_name, data_type, field_name, field_type, required, sort_order, create_time, update_time, status, is_deleted) VALUES
    (v_tollgantry_type_id, 'id', '收费门架编号，收费门架编号=收费单元编号+顺序码+保留位', 'STRING', 'id', 'VARCHAR', TRUE, 152, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollgantry_type_id, 'tollIntervalId', '收费单元编号', 'STRING', 'toll_interval_id', 'VARCHAR', FALSE, 153, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollgantry_type_id, 'num', '顺序码，2位数字', 'STRING', 'num', 'VARCHAR', FALSE, 154, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollgantry_type_id, 'reservedNum', '保留位，1位数字默认为"0"', 'STRING', 'reserved_num', 'VARCHAR', FALSE, 155, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollgantry_type_id, 'type', '门架类型，0-路段收费门架1-省界收费门架', 'INTEGER', 'type', 'INTEGER', FALSE, 156, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollgantry_type_id, 'boundaryType', '省界入/出口标识，1-省界入口2-省界出口', 'INTEGER', 'boundary_type', 'INTEGER', FALSE, 157, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollgantry_type_id, 'gantrySign', '门架标志', 'STRING', 'gantry_sign', 'VARCHAR', FALSE, 158, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollgantry_type_id, 'tollIntervals', '收费单元编码组合，门架代收其他收费单元时，收费单元编码以管道符“｜”分割', 'STRING', 'toll_intervals', 'VARCHAR', FALSE, 159, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollgantry_type_id, 'lat', '纬度', 'STRING', 'lat', 'VARCHAR', FALSE, 160, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollgantry_type_id, 'lng', '经度', 'STRING', 'lng', 'VARCHAR', FALSE, 161, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollgantry_type_id, 'pileNumber', '桩号', 'STRING', 'pile_number', 'VARCHAR', FALSE, 162, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollgantry_type_id, 'status', '使用状态，1-停用2-在用', 'INTEGER', 'status', 'INTEGER', FALSE, 163, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollgantry_type_id, 'startTime', '起始日期', 'STRING', 'start_time', 'VARCHAR', FALSE, 164, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollgantry_type_id, 'endTime', '终止日期', 'STRING', 'end_time', 'VARCHAR', FALSE, 165, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollgantry_type_id, 'etcGantryHex', 'ETC门架的HEX字符串', 'STRING', 'etc_gantry_hex', 'VARCHAR', FALSE, 166, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollgantry_type_id, 'gantryType', '门架种类，1-实体门架2-虚拟门架', 'INTEGER', 'gantry_type', 'INTEGER', FALSE, 167, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollgantry_type_id, 'laneCount', '车道数', 'STRING', 'lane_count', 'VARCHAR', FALSE, 168, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollgantry_type_id, 'reEtcGantryHex', '反向门架HEX字符串', 'STRING', 're_etc_gantry_hex', 'VARCHAR', FALSE, 169, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollgantry_type_id, 'agencyGantryIds', '代收门架编号', 'STRING', 'agency_gantry_ids', 'VARCHAR', FALSE, 170, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollgantry_type_id, 'rsuManUID', '门架的RSU厂商代码', 'STRING', 'rsu_man_uid', 'VARCHAR', FALSE, 171, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollgantry_type_id, 'rsuModel', '门架的RSU型号', 'STRING', 'rsu_model', 'VARCHAR', FALSE, 172, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollgantry_type_id, 'rsuID', '门架的RSU编号', 'STRING', 'rsu_id', 'VARCHAR', FALSE, 173, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollgantry_type_id, 'VPLRUID', '门架的高清车牌识别设备厂商代码', 'STRING', 'vplruid', 'VARCHAR', FALSE, 174, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollgantry_type_id, 'VPLRModel', '门架的高清车牌识别设备型号', 'STRING', 'vplr_model', 'VARCHAR', FALSE, 175, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollgantry_type_id, 'VPLRID', '门架的高清车牌识别设备编号', 'STRING', 'vplrid', 'VARCHAR', FALSE, 176, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollgantry_type_id, 'HDVUID', '门架的高清摄像机设备厂商代码', 'STRING', 'hdvuid', 'VARCHAR', FALSE, 177, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollgantry_type_id, 'HDVModel', '门架的高清摄像机设备型号', 'STRING', 'hdv_model', 'VARCHAR', FALSE, 178, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollgantry_type_id, 'HDVID', '门架的高清摄像机设备编号', 'STRING', 'hdvid', 'VARCHAR', FALSE, 179, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollgantry_type_id, 'controllerUID', '门架控制机设备厂商代码', 'STRING', 'controller_uid', 'VARCHAR', FALSE, 180, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollgantry_type_id, 'controllerModel', '门架控制机设备型号', 'STRING', 'controller_model', 'VARCHAR', FALSE, 181, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollgantry_type_id, 'controllerID', '门架控制机设备编号', 'STRING', 'controller_id', 'VARCHAR', FALSE, 182, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollgantry_type_id, 'controllerSysVer', '门架控制机操作系统软件版本', 'STRING', 'controller_sys_ver', 'VARCHAR', FALSE, 183, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollgantry_type_id, 'serverUID', '门架服务器设备厂商代码', 'STRING', 'server_uid', 'VARCHAR', FALSE, 184, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollgantry_type_id, 'serverModel', '门架服务器设备型号', 'STRING', 'server_model', 'VARCHAR', FALSE, 185, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollgantry_type_id, 'serverID', '门架服务器设备编号', 'STRING', 'server_id', 'VARCHAR', FALSE, 186, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollgantry_type_id, 'serverSysVer', '门架服务器操作系统软件版本', 'STRING', 'server_sys_ver', 'VARCHAR', FALSE, 187, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollgantry_type_id, 'serverDBVer', '门架服务器数据库系统软件版本', 'STRING', 'server_db_ver', 'VARCHAR', FALSE, 188, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollgantry_type_id, 'vehDetectorUID', '门架的车辆检测器设备厂商代码', 'STRING', 'veh_detector_uid', 'VARCHAR', FALSE, 189, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollgantry_type_id, 'vehDetectorModel', '门架的车辆检测器设备型号', 'STRING', 'veh_detector_model', 'VARCHAR', FALSE, 190, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollgantry_type_id, 'vehDetectorID', '门架的车辆检测器设备编号', 'STRING', 'veh_detector_id', 'VARCHAR', FALSE, 191, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollgantry_type_id, 'weatherDetectorUID', '门架的车辆气象检测设备厂商代码', 'STRING', 'weather_detector_uid', 'VARCHAR', FALSE, 192, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollgantry_type_id, 'weatherDetectorModel', '门架的气象检测设备型号', 'STRING', 'weather_detector_model', 'VARCHAR', FALSE, 193, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollgantry_type_id, 'weatherDetectorID', '门架的气象检测设备编号', 'STRING', 'weather_detector_id', 'VARCHAR', FALSE, 194, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollgantry_type_id, 'classDetectorUID', '门架的车型检测设备厂商代码', 'STRING', 'class_detector_uid', 'VARCHAR', FALSE, 195, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollgantry_type_id, 'classDetectorModel', '门架的车型检测设备型号', 'STRING', 'class_detector_model', 'VARCHAR', FALSE, 196, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollgantry_type_id, 'classDetectorID', '门架的车型检测设备编号', 'STRING', 'class_detector_id', 'VARCHAR', FALSE, 197, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollgantry_type_id, 'loadDetectionUID', '门架的断面称重检测设备厂商代码', 'STRING', 'load_detection_uid', 'VARCHAR', FALSE, 198, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollgantry_type_id, 'loadDetectionModel', '门架的断面称重检测设备型号', 'STRING', 'load_detection_model', 'VARCHAR', FALSE, 199, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollgantry_type_id, 'loadDetectionID', '门架的断面称重检测设备编号', 'STRING', 'load_detection_id', 'VARCHAR', FALSE, 200, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollgantry_type_id, 'tempControllerUID', '门架的温控设备厂商代码', 'STRING', 'temp_controller_uid', 'VARCHAR', FALSE, 201, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollgantry_type_id, 'tempControllerModel', '门架的温控设备型号', 'STRING', 'temp_controller_model', 'VARCHAR', FALSE, 202, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollgantry_type_id, 'tempControllerID', '门架的温控设备编号', 'STRING', 'temp_controller_id', 'VARCHAR', FALSE, 203, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollgantry_type_id, 'powerControllerUID', '门架的供电设备厂商代码', 'STRING', 'power_controller_uid', 'VARCHAR', FALSE, 204, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollgantry_type_id, 'powerControllerModel', '门架的供电设备型号', 'STRING', 'power_controller_model', 'VARCHAR', FALSE, 205, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollgantry_type_id, 'powerControllerID', '门架的供电设备编号', 'STRING', 'power_controller_id', 'VARCHAR', FALSE, 206, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollgantry_type_id, 'safeEquipUID', '门架的安全接入设备厂商代码', 'STRING', 'safe_equip_uid', 'VARCHAR', FALSE, 207, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollgantry_type_id, 'safeEquipModel', '门架的安全接入设备型号', 'STRING', 'safe_equip_model', 'VARCHAR', FALSE, 208, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollgantry_type_id, 'safeEquipID', '门架的安全接入设备编号', 'STRING', 'safe_equip_id', 'VARCHAR', FALSE, 209, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollgantry_type_id, 'lineType', '线路类型', 'STRING', 'line_type', 'VARCHAR', FALSE, 210, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollgantry_type_id, 'operators', '网络所属运营商', 'STRING', 'operators', 'VARCHAR', FALSE, 211, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollgantry_type_id, 'dataMergePoint', '数据汇聚点', 'STRING', 'data_merge_point', 'VARCHAR', FALSE, 212, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollgantry_type_id, 'imei', 'IMEI号', 'STRING', 'imei', 'VARCHAR', FALSE, 213, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollgantry_type_id, 'ip', '接入设备ip', 'STRING', 'ip', 'VARCHAR', FALSE, 214, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollgantry_type_id, 'snmpVersion', 'snmp协议版本号', 'STRING', 'snmp_version', 'VARCHAR', FALSE, 215, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollgantry_type_id, 'snmpPort', 'snmp端口', 'INTEGER', 'snmp_port', 'INTEGER', FALSE, 216, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollgantry_type_id, 'community', '团体名称', 'STRING', 'community', 'VARCHAR', FALSE, 217, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollgantry_type_id, 'securityName', '用户名', 'STRING', 'security_name', 'VARCHAR', FALSE, 218, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollgantry_type_id, 'securityLevel', '安全级别', 'STRING', 'security_level', 'VARCHAR', FALSE, 219, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollgantry_type_id, 'authentication', '认证协议', 'STRING', 'authentication', 'VARCHAR', FALSE, 220, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollgantry_type_id, 'authKey', '认证密钥', 'STRING', 'auth_key', 'VARCHAR', FALSE, 221, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollgantry_type_id, 'encryption', '加密算法', 'STRING', 'encryption', 'VARCHAR', FALSE, 222, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollgantry_type_id, 'secretKey', '加密密钥', 'STRING', 'secret_key', 'VARCHAR', FALSE, 223, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0);

    -- Tollinterval 属性 - 19个属性
    INSERT INTO datamodel_property (object_type_id, property_code, property_name, data_type, field_name, field_type, required, sort_order, create_time, update_time, status, is_deleted) VALUES
    (v_tollinterval_type_id, 'id', '收费断面编号，收费单元编号=收费路段编号+收费单元顺序码+上下行+保留位', 'STRING', 'id', 'VARCHAR', TRUE, 224, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollinterval_type_id, 'sectionId', '收费路段编号', 'STRING', 'section_id', 'VARCHAR', FALSE, 225, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollinterval_type_id, 'num', '收费单元顺序码，3位数字', 'STRING', 'num', 'VARCHAR', FALSE, 226, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollinterval_type_id, 'direction', '上下行，1-上行2-下行', 'INTEGER', 'direction', 'INTEGER', FALSE, 227, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollinterval_type_id, 'reservedNum', '保留位，1位数字默认为"0"', 'STRING', 'reserved_num', 'VARCHAR', FALSE, 228, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollinterval_type_id, 'name', '收费单元名称', 'STRING', 'name', 'VARCHAR', FALSE, 229, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollinterval_type_id, 'type', '所在路段性质，1-经营性2-还贷性', 'INTEGER', 'type', 'INTEGER', FALSE, 230, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollinterval_type_id, 'length', '起止里程，单位：m', 'INTEGER', 'length', 'INTEGER', FALSE, 231, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollinterval_type_id, 'startLat', '起始计费位置纬度', 'STRING', 'start_lat', 'VARCHAR', FALSE, 232, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollinterval_type_id, 'startLng', '起始计费位置经度', 'STRING', 'start_lng', 'VARCHAR', FALSE, 233, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollinterval_type_id, 'startStakeNum', '起始计费位置桩号', 'STRING', 'start_stake_num', 'VARCHAR', FALSE, 234, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollinterval_type_id, 'endStakeNum', '终止计费位置桩号', 'STRING', 'end_stake_num', 'VARCHAR', FALSE, 235, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollinterval_type_id, 'endLat', '终止计费位置纬度', 'STRING', 'end_lat', 'VARCHAR', FALSE, 236, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollinterval_type_id, 'endLng', '终止计费位置经度', 'STRING', 'end_lng', 'VARCHAR', FALSE, 237, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollinterval_type_id, 'tollRoads', '重合收费公路编号', 'STRING', 'toll_roads', 'VARCHAR', FALSE, 238, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollinterval_type_id, 'endTime', '停止收费时间', 'STRING', 'end_time', 'VARCHAR', FALSE, 239, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollinterval_type_id, 'provinceType', '省界标识，0-非省界1-省界', 'INTEGER', 'province_type', 'INTEGER', FALSE, 240, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollinterval_type_id, 'beginTime', '开始收费时间', 'STRING', 'begin_time', 'VARCHAR', FALSE, 241, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tollinterval_type_id, 'verticalSectionType', '收费单元类型，1-实体2-虚拟', 'INTEGER', 'vertical_section_type', 'INTEGER', FALSE, 242, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0);

    -- Tolllane 属性 - 17个属性
    INSERT INTO datamodel_property (object_type_id, property_code, property_name, data_type, field_name, field_type, required, sort_order, create_time, update_time, status, is_deleted) VALUES
    (v_tolllane_type_id, 'id', '收费车道编号，收费车道编号=收费广场编号+收费车道顺序码+保留位', 'STRING', 'id', 'VARCHAR', TRUE, 243, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tolllane_type_id, 'tollPlazaId', '收费广场编号', 'STRING', 'toll_plaza_id', 'VARCHAR', FALSE, 244, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tolllane_type_id, 'num', '收费车道顺序码，2位数字', 'STRING', 'num', 'VARCHAR', FALSE, 245, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tolllane_type_id, 'reservedNum', '保留位，1位数字默认为"0"', 'STRING', 'reserved_num', 'VARCHAR', FALSE, 246, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tolllane_type_id, 'type', '车道类型，1-ETC2-MTC3-混合', 'INTEGER', 'type', 'INTEGER', FALSE, 247, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tolllane_type_id, 'tidalTime', '潮汐车道反向时间', 'STRING', 'tidal_time', 'VARCHAR', FALSE, 248, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tolllane_type_id, 'startTime', '起始日期', 'STRING', 'start_time', 'VARCHAR', FALSE, 249, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tolllane_type_id, 'endTime', '终止日期', 'STRING', 'end_time', 'VARCHAR', FALSE, 250, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tolllane_type_id, 'status', '使用状态，1-停用2-在用', 'INTEGER', 'status', 'INTEGER', FALSE, 251, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tolllane_type_id, 'laneHex', '车道HEX字符串', 'STRING', 'lane_hex', 'VARCHAR', FALSE, 252, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tolllane_type_id, 'rsuManUID', 'RSU厂商代码', 'STRING', 'rsu_man_uid', 'VARCHAR', FALSE, 253, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tolllane_type_id, 'rsuModel', 'RSU型号', 'STRING', 'rsu_model', 'VARCHAR', FALSE, 254, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tolllane_type_id, 'rsuID', 'RSU编号', 'STRING', 'rsu_id', 'VARCHAR', FALSE, 255, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tolllane_type_id, 'entryExitType', '出入口类型，1-入口2-出口3-潮汐', 'INTEGER', 'entry_exit_type', 'INTEGER', FALSE, 256, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tolllane_type_id, 'railingPos', '车道栏杆位置，1-前2-中3-后9-无', 'INTEGER', 'railing_pos', 'INTEGER', FALSE, 257, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tolllane_type_id, 'ifContainLimitWeight', '是否有治超，1-有2-无', 'INTEGER', 'if_contain_limit_weight', 'INTEGER', FALSE, 258, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_tolllane_type_id, 'VPLRManUID', '牌识厂商代码', 'STRING', 'vplr_man_uid', 'VARCHAR', FALSE, 259, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0);

    -- Transaction 属性 - 18个属性
    INSERT INTO datamodel_property (object_type_id, property_code, property_name, data_type, field_name, field_type, required, sort_order, create_time, update_time, status, is_deleted) VALUES
    (v_transaction_type_id, 'id', '交易流水编号', 'STRING', 'id', 'VARCHAR', TRUE, 260, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_transaction_type_id, 'identifyPointId', '标识点标识', 'STRING', 'identify_point_id', 'VARCHAR', FALSE, 261, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_transaction_type_id, 'identifyPointHex', '标识点Hex码', 'STRING', 'identify_point_hex', 'VARCHAR', FALSE, 262, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_transaction_type_id, 'passId', '通行标识', 'STRING', 'pass_id', 'VARCHAR', FALSE, 263, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_transaction_type_id, 'mediumId', '通行介质编号', 'STRING', 'medium_id', 'VARCHAR', FALSE, 264, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_transaction_type_id, 'mediaType', '介质类型', 'INTEGER', 'media_type', 'INTEGER', FALSE, 265, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_transaction_type_id, 'exitFeeType', '出口收费类型', 'INTEGER', 'exit_fee_type', 'INTEGER', FALSE, 266, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_transaction_type_id, 'provinceId', '省份编号', 'INTEGER', 'province_id', 'INTEGER', FALSE, 267, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_transaction_type_id, 'transTime', '交易时间', 'DATE', 'trans_time', 'DATE', FALSE, 268, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_transaction_type_id, 'plateNum', '车牌号码', 'STRING', 'plate_num', 'VARCHAR', FALSE, 269, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_transaction_type_id, 'plateColor', '车牌颜色', 'INTEGER', 'plate_color', 'INTEGER', FALSE, 270, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_transaction_type_id, 'payFee', '支付费用', 'DECIMAL', 'pay_fee', 'DECIMAL', FALSE, 271, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_transaction_type_id, 'fee', '费用', 'DECIMAL', 'fee', 'DECIMAL', FALSE, 272, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_transaction_type_id, 'discountFee', '优惠费用', 'DECIMAL', 'discount_fee', 'DECIMAL', FALSE, 273, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_transaction_type_id, 'intervals', '收费单元编号，多个编号使用“｜”隔开', 'STRING', 'intervals', 'VARCHAR', FALSE, 274, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_transaction_type_id, 'fee', '收费单元费用，多个费用使用“｜”隔开', 'STRING', 'fee', 'VARCHAR', FALSE, 275, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_transaction_type_id, 'payFee', '收费单元支付费用，多个费用使用“｜”隔开', 'STRING', 'pay_fee', 'VARCHAR', FALSE, 276, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_transaction_type_id, 'discountFee', '收费单元优惠费用，多个费用使用“｜”隔开', 'STRING', 'discount_fee', 'VARCHAR', FALSE, 277, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0);

    -- Path 属性 - 10个属性
    INSERT INTO datamodel_property (object_type_id, property_code, property_name, data_type, field_name, field_type, required, sort_order, create_time, update_time, status, is_deleted) VALUES
    (v_path_type_id, 'id', '车辆通行路径标识', 'STRING', 'id', 'VARCHAR', TRUE, 278, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_path_type_id, 'passId', '通行标识', 'STRING', 'pass_id', 'VARCHAR', FALSE, 279, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_path_type_id, 'plateNum', '车牌号码', 'STRING', 'plate_num', 'VARCHAR', FALSE, 280, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_path_type_id, 'plateColor', '车牌颜色', 'INTEGER', 'plate_color', 'INTEGER', FALSE, 281, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_path_type_id, 'enTime', '入口时间', 'DATE', 'en_time', 'DATE', FALSE, 282, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_path_type_id, 'exTime', '出口时间', 'DATE', 'ex_time', 'DATE', FALSE, 283, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_path_type_id, 'enTollLaneId', '入口车道编号', 'STRING', 'en_toll_lane_id', 'VARCHAR', FALSE, 284, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_path_type_id, 'exTollLaneId', '出口车道编号', 'STRING', 'ex_toll_lane_id', 'VARCHAR', FALSE, 285, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_path_type_id, 'enTollStationId', '入口收费站编号', 'STRING', 'en_toll_station_id', 'VARCHAR', FALSE, 286, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_path_type_id, 'exTollStationId', '出口收费站编号', 'STRING', 'ex_toll_station_id', 'VARCHAR', FALSE, 287, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0);

    -- Pathdetail 属性 - 12个属性
    INSERT INTO datamodel_property (object_type_id, property_code, property_name, data_type, field_name, field_type, required, sort_order, create_time, update_time, status, is_deleted) VALUES
    (v_pathdetail_type_id, 'id', '交易标识', 'STRING', 'id', 'VARCHAR', TRUE, 288, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_pathdetail_type_id, 'pathId', '车辆通行路径标识', 'STRING', 'path_id', 'VARCHAR', FALSE, 289, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_pathdetail_type_id, 'passId', '通行标识', 'STRING', 'pass_id', 'VARCHAR', FALSE, 290, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_pathdetail_type_id, 'plateNum', '车牌号码', 'STRING', 'plate_num', 'VARCHAR', FALSE, 291, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_pathdetail_type_id, 'plateColor', '车牌颜色', 'INTEGER', 'plate_color', 'INTEGER', FALSE, 292, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_pathdetail_type_id, 'identifyPointId', '标识点标识', 'STRING', 'identify_point_id', 'VARCHAR', FALSE, 293, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_pathdetail_type_id, 'identifyPointHex', '标识点Hex码', 'STRING', 'identify_point_hex', 'VARCHAR', FALSE, 294, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_pathdetail_type_id, 'intervals', '收费单元编号，多个编号使用“｜”隔开', 'STRING', 'intervals', 'VARCHAR', FALSE, 295, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_pathdetail_type_id, 'fee', '收费单元费用，多个费用使用“｜”隔开', 'STRING', 'fee', 'VARCHAR', FALSE, 296, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_pathdetail_type_id, 'payFee', '收费单元支付费用，多个费用使用“｜”隔开', 'STRING', 'pay_fee', 'VARCHAR', FALSE, 297, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_pathdetail_type_id, 'discountFee', '收费单元优惠费用，多个费用使用“｜”隔开', 'STRING', 'discount_fee', 'VARCHAR', FALSE, 298, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_pathdetail_type_id, 'transTime', '交易时间', 'DATE', 'trans_time', 'DATE', FALSE, 299, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0);

    -- Restorepath 属性 - 10个属性
    INSERT INTO datamodel_property (object_type_id, property_code, property_name, data_type, field_name, field_type, required, sort_order, create_time, update_time, status, is_deleted) VALUES
    (v_restorepath_type_id, 'id', '车辆拟合路径标识', 'STRING', 'id', 'VARCHAR', TRUE, 300, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_restorepath_type_id, 'passId', '通行标识', 'STRING', 'pass_id', 'VARCHAR', FALSE, 301, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_restorepath_type_id, 'plateNum', '车牌号码', 'STRING', 'plate_num', 'VARCHAR', FALSE, 302, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_restorepath_type_id, 'plateColor', '车牌颜色', 'INTEGER', 'plate_color', 'INTEGER', FALSE, 303, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_restorepath_type_id, 'enTime', '入口时间', 'DATE', 'en_time', 'DATE', FALSE, 304, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_restorepath_type_id, 'exTime', '出口时间', 'DATE', 'ex_time', 'DATE', FALSE, 305, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_restorepath_type_id, 'enTollLaneId', '入口车道编号', 'STRING', 'en_toll_lane_id', 'VARCHAR', FALSE, 306, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_restorepath_type_id, 'exTollLaneId', '出口车道编号', 'STRING', 'ex_toll_lane_id', 'VARCHAR', FALSE, 307, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_restorepath_type_id, 'enTollStationId', '入口收费站编号', 'STRING', 'en_toll_station_id', 'VARCHAR', FALSE, 308, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_restorepath_type_id, 'exTollStationId', '出口收费站编号', 'STRING', 'ex_toll_station_id', 'VARCHAR', FALSE, 309, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0);

    -- Restorepathdetail 属性 - 12个属性
    INSERT INTO datamodel_property (object_type_id, property_code, property_name, data_type, field_name, field_type, required, sort_order, create_time, update_time, status, is_deleted) VALUES
    (v_restorepathdetail_type_id, 'id', '交易标识', 'STRING', 'id', 'VARCHAR', TRUE, 310, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_restorepathdetail_type_id, 'restorePathId', '车辆拟合路径标识', 'STRING', 'restore_path_id', 'VARCHAR', FALSE, 311, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_restorepathdetail_type_id, 'passId', '通行标识', 'STRING', 'pass_id', 'VARCHAR', FALSE, 312, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_restorepathdetail_type_id, 'plateNum', '车牌号码', 'STRING', 'plate_num', 'VARCHAR', FALSE, 313, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_restorepathdetail_type_id, 'plateColor', '车牌颜色', 'INTEGER', 'plate_color', 'INTEGER', FALSE, 314, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_restorepathdetail_type_id, 'identifyPointId', '标识点标识', 'STRING', 'identify_point_id', 'VARCHAR', FALSE, 315, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_restorepathdetail_type_id, 'identifyPointHex', '标识点Hex码', 'STRING', 'identify_point_hex', 'VARCHAR', FALSE, 316, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_restorepathdetail_type_id, 'intervals', '收费单元编号，多个编号使用“｜”隔开', 'STRING', 'intervals', 'VARCHAR', FALSE, 317, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_restorepathdetail_type_id, 'fee', '收费单元费用，多个费用使用“｜”隔开', 'STRING', 'fee', 'VARCHAR', FALSE, 318, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_restorepathdetail_type_id, 'payFee', '收费单元支付费用，多个费用使用“｜”隔开', 'STRING', 'pay_fee', 'VARCHAR', FALSE, 319, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_restorepathdetail_type_id, 'discountFee', '收费单元优惠费用，多个费用使用“｜”隔开', 'STRING', 'discount_fee', 'VARCHAR', FALSE, 320, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_restorepathdetail_type_id, 'transTime', '交易时间', 'DATE', 'trans_time', 'DATE', FALSE, 321, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0);

    -- Splitdetail 属性 - 6个属性
    INSERT INTO datamodel_property (object_type_id, property_code, property_name, data_type, field_name, field_type, required, sort_order, create_time, update_time, status, is_deleted) VALUES
    (v_splitdetail_type_id, 'passId', '通行标识', 'STRING', 'pass_id', 'VARCHAR', TRUE, 322, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_splitdetail_type_id, 'transactionId', '交易编号', 'STRING', 'transaction_id', 'VARCHAR', FALSE, 323, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_splitdetail_type_id, 'intervalId', '收费单元编号', 'STRING', 'interval_id', 'VARCHAR', FALSE, 324, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_splitdetail_type_id, 'tollIntervalFee', '收费单元费用', 'STRING', 'toll_interval_fee', 'VARCHAR', FALSE, 325, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_splitdetail_type_id, 'tollIntervalPayFee', '收费单元支付费用', 'STRING', 'toll_interval_pay_fee', 'VARCHAR', FALSE, 326, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0),
    (v_splitdetail_type_id, 'tollIntervalDiscountFee', '收费单元优惠费用', 'STRING', 'toll_interval_discount_fee', 'VARCHAR', FALSE, 327, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 0);

END $$;
