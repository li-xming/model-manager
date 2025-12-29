/**
 * API类型定义
 */

// 统一响应格式
export interface ResponseVO<T = any> {
  code: number;
  message: string;
  data: T;
}

// 元模型类型定义
export interface MetaClass {
  id: string;
  code: string;
  name: string;
  displayName?: string;
  description?: string;
  metadataSchema?: Record<string, any>;
  isBuiltin: boolean;
  version?: string;
  createdBy?: string;
  createdAt: string;
  updatedAt: string;
}

export interface MetaProperty {
  id: string;
  code: string;
  name: string;
  displayName?: string;
  description?: string;
  metadataSchema?: Record<string, any>;
  isBuiltin: boolean;
  version?: string;
  createdBy?: string;
  createdAt: string;
  updatedAt: string;
}

export interface MetaLinkType {
  id: string;
  code: string;
  name: string;
  displayName?: string;
  description?: string;
  metadataSchema?: Record<string, any>;
  isBuiltin: boolean;
  version?: string;
  createdBy?: string;
  createdAt: string;
  updatedAt: string;
}

export interface MetaActionType {
  id: string;
  code: string;
  name: string;
  displayName?: string;
  description?: string;
  metadataSchema?: Record<string, any>;
  isBuiltin: boolean;
  version?: string;
  createdBy?: string;
  createdAt: string;
  updatedAt: string;
}

export interface MetaDomain {
  id: string;
  code: string;
  name: string;
  displayName?: string;
  description?: string;
  metadataSchema?: Record<string, any>;
  isBuiltin: boolean;
  version?: string;
  createdBy?: string;
  createdAt: string;
  updatedAt: string;
}

// 对象类型
export interface ObjectType {
  id: string;
  name: string;
  displayName: string;
  description?: string;
  primaryKey: string;
  domainId?: string;
  metaClassId?: string;  // 元类ID
  metadata?: Record<string, any>;
  createdAt: string;
  updatedAt: string;
}

export interface ObjectTypeDTO {
  name: string;
  displayName: string;
  description?: string;
  primaryKey?: string;
  domainId?: string;
  metaClassId?: string;  // 元类ID（可选，默认为'META_CLASS'）
  metadata?: Record<string, any>;
}

// 属性
export interface Property {
  id: string;
  objectTypeId: string;
  name: string;
  dataType: string;
  description?: string;
  required: boolean;
  defaultValue?: string;
  constraints?: Record<string, any>;
  metadata?: Record<string, any>;
  metaPropertyId?: string;  // 元属性ID
  sortOrder: number;
  createdAt: string;
  updatedAt: string;
}

export interface PropertyDTO {
  name: string;
  dataType: string;
  description?: string;
  required?: boolean;
  defaultValue?: string;
  constraints?: Record<string, any>;
  metadata?: Record<string, any>;
  metaPropertyId?: string;  // 元属性ID（可选，默认为'META_PROPERTY'）
  sortOrder?: number;
}

// 批量从表创建属性的请求DTO
export interface BatchCreatePropertiesFromTableDTO {
  datasourceId: string;
  tableName: string;
  schemaName?: string;
  columnNames: string[];
  skipExisting?: boolean;
}

// 批量创建属性的结果
export interface BatchCreatePropertiesResult {
  created: number;
  skipped: number;
  failed: number;
  properties: Property[];
  skippedColumns: string[];
  failedColumns: Array<{
    columnName: string;
    errorMessage: string;
  }>;
}

// 链接类型
export interface LinkType {
  id: string;
  name: string;
  displayName: string;
  description?: string;
  sourceObjectTypeId: string;
  targetObjectTypeId: string;
  cardinality: string;
  bidirectional: boolean;
  domainId?: string;
  metaLinkTypeId?: string;  // 元关系类型ID
  metadata?: Record<string, any>;
  createdAt: string;
  updatedAt: string;
}

export interface LinkTypeDTO {
  name: string;
  displayName: string;
  description?: string;
  sourceObjectTypeId: string;
  targetObjectTypeId: string;
  cardinality: string;
  bidirectional?: boolean;
  domainId?: string;
  metaLinkTypeId?: string;  // 元关系类型ID（可选，默认为'META_LINK_TYPE'）
  metadata?: Record<string, any>;
}

// 链接实例
export interface LinkInstance {
  id: string;
  linkTypeId: string;
  sourceInstanceId: string;
  targetInstanceId: string;
  properties?: Record<string, any>;
  createdAt: string;
}

export interface LinkInstanceDTO {
  linkTypeId: string;
  sourceInstanceId: string;
  targetInstanceId: string;
  properties?: Record<string, any>;
}

// 操作类型
export interface ActionType {
  id: string;
  name: string;
  displayName: string;
  description?: string;
  targetObjectTypeId: string;
  inputSchema?: Record<string, any>;
  outputSchema?: Record<string, any>;
  requiresApproval: boolean;
  handlerFunction?: string;
  domainId?: string;
  metaActionTypeId?: string;  // 元操作类型ID
  metadata?: Record<string, any>;
  createdAt: string;
  updatedAt: string;
}

export interface ActionTypeDTO {
  name: string;
  displayName: string;
  description?: string;
  targetObjectTypeId: string;
  inputSchema?: Record<string, any>;
  outputSchema?: Record<string, any>;
  requiresApproval?: boolean;
  handlerFunction?: string;
  domainId?: string;
  metaActionTypeId?: string;  // 元操作类型ID（可选，默认为'META_ACTION_TYPE'）
  metadata?: Record<string, any>;
}

// 接口
export interface Interface {
  id: string;
  name: string;
  displayName: string;
  description?: string;
  method: string;
  path: string;
  actionTypeId?: string;
  requiredProperties?: Record<string, any>;
  domainId?: string;
  metadata?: Record<string, any>;
  createdAt: string;
  updatedAt: string;
}

export interface InterfaceDTO {
  name: string;
  displayName: string;
  description?: string;
  method: string;
  path: string;
  actionTypeId?: string;
  requiredProperties?: Record<string, any>;
  domainId?: string;
  metadata?: Record<string, any>;
}

// 函数
export interface Function {
  id: string;
  name: string;
  displayName: string;
  description?: string;
  code: string;
  inputSchema?: Record<string, any>;
  returnType: string;
  version: string;
  domainId?: string;
  metadata?: Record<string, any>;
  createdAt: string;
  updatedAt: string;
}

export interface FunctionDTO {
  name: string;
  displayName: string;
  description?: string;
  code: string;
  inputSchema?: Record<string, any>;
  returnType: string;
  version?: string;
  domainId?: string;
  metadata?: Record<string, any>;
}

// 实例
export interface InstanceDTO {
  properties: Record<string, any>;
  storageDatasourceId?: string;  // 存储库ID（可选，对应 object_type_datasources.id，is_storage=true）
  datasourceId?: string;  // 数据源ID（可选，已废弃，请使用 storageDatasourceId）
}

// 业务域
export interface BusinessDomain {
  id: string;
  code: string;
  name: string;
  displayName: string;
  description?: string;
  metaDomainId?: string;  // 元域ID
  metadata?: Record<string, any>;
  createdAt: string;
  updatedAt: string;
}

// 数据源
export interface DataSource {
  id: string;
  name: string;
  code: string;
  type: string; // MYSQL, POSTGRESQL, ORACLE等
  description?: string;
  host: string;
  port: number;
  databaseName?: string;
  schemaName?: string;
  username: string;
  password?: string; // 通常不返回
  connectionUrl?: string;
  maxConnections?: number;
  minConnections?: number;
  connectionTimeout?: number;
  enabled: boolean;
  status?: string; // ACTIVE, INACTIVE, ERROR
  lastTestTime?: string;
  lastTestResult?: string; // SUCCESS, FAILED
  lastTestMessage?: string;
  domainId?: string;
  metadata?: Record<string, any>;
  createdAt: string;
  updatedAt: string;
}

export interface DataSourceDTO {
  name: string;
  code: string;
  type: string;
  description?: string;
  host: string;
  port: number;
  databaseName?: string;
  schemaName?: string;
  username: string;
  password: string;
  connectionUrl?: string;
  maxConnections?: number;
  minConnections?: number;
  connectionTimeout?: number;
  enabled?: boolean;
  domainId?: string;
  metadata?: Record<string, any>;
}

// 对象类型-数据源关联
export interface ObjectTypeDataSource {
  id: string;
  objectTypeId: string;
  datasourceId: string;
  tableName?: string;  // 表名（新增）
  schemaName?: string; // Schema名称（新增，可选）
  isDefault: boolean;
  priority: number;
  description?: string;
  metadata?: Record<string, any>;
  createdAt: string;
  updatedAt: string;
}

// 对象类型-数据源表关联（包含完整数据源信息）
export interface ObjectTypeDataSourceTable {
  id: string;
  objectTypeId: string;
  datasource: DataSource;
  tableName: string;
  schemaName?: string;
  isStorage?: boolean;  // 是否作为对象实例的存储库
  isDefault: boolean;
  priority: number;
  description?: string;
}

// 表字段信息
export interface TableColumnInfo {
  columnName: string;
  dataType: string;
  length?: number;
  nullable?: boolean;
  defaultValue?: string;
  primaryKey?: boolean;
  comment?: string;
  scale?: number;
}

// 分页参数
export interface PageParams {
  current: number;
  size: number;
  domainId?: string; // 业务域过滤（可选）
  type?: string; // 数据源类型过滤（可选）
}

// 分页结果
export interface PageResult<T> {
  records: T[];
  total: number;
  size: number;
  current: number;
}

