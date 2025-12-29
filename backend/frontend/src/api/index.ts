/**
 * API接口定义
 */
import request from '../utils/request';
import type { 
  ResponseVO, 
  ObjectType, 
  ObjectTypeDTO, 
  Property, 
  PropertyDTO, 
  LinkType, 
  LinkTypeDTO, 
  LinkInstance, 
  LinkInstanceDTO, 
  ActionType, 
  ActionTypeDTO, 
  Interface, 
  InterfaceDTO, 
  Function, 
  FunctionDTO, 
  InstanceDTO, 
  BusinessDomain, 
  PageParams, 
  PageResult,
  MetaClass,
  MetaProperty,
  MetaLinkType,
  MetaActionType,
  MetaDomain,
  DataSource,
  DataSourceDTO,
  ObjectTypeDataSource,
  ObjectTypeDataSourceTable,
  TableColumnInfo,
  BatchCreatePropertiesFromTableDTO,
  BatchCreatePropertiesResult
} from '../types/api';

// 元模型API
export const metaModelApi = {
  // ==================== MetaClass ====================
  // 获取所有元类（包括内置和用户扩展）
  getAllMetaClasses: (): Promise<ResponseVO<MetaClass[]>> =>
    request.get('/v1/meta-models/meta-classes'),
  
  // 获取平台内置的元类
  getBuiltinMetaClasses: (): Promise<ResponseVO<MetaClass[]>> =>
    request.get('/v1/meta-models/meta-classes/builtin'),
  
  // 获取用户扩展的元类
  getUserDefinedMetaClasses: (): Promise<ResponseVO<MetaClass[]>> =>
    request.get('/v1/meta-models/meta-classes/custom'),
  
  // 根据ID获取元类
  getMetaClassById: (id: string): Promise<ResponseVO<MetaClass>> =>
    request.get(`/v1/meta-models/meta-classes/${id}`),
  
  // 创建自定义元类（管理员权限）
  createCustomMetaClass: (data: any): Promise<ResponseVO<MetaClass>> =>
    request.post('/v1/meta-models/meta-classes', data),
  
  // 更新元类（仅用户扩展）
  updateMetaClass: (id: string, data: any): Promise<ResponseVO<MetaClass>> =>
    request.put(`/v1/meta-models/meta-classes/${id}`, data),
  
  // 删除元类（仅用户扩展）
  deleteMetaClass: (id: string): Promise<ResponseVO<void>> =>
    request.delete(`/v1/meta-models/meta-classes/${id}`),
  
  // ==================== MetaProperty ====================
  // 获取所有元属性（包括内置和用户扩展）
  getAllMetaProperties: (): Promise<ResponseVO<MetaProperty[]>> =>
    request.get('/v1/meta-models/meta-properties'),
  
  // 获取平台内置的元属性
  getBuiltinMetaProperties: (): Promise<ResponseVO<MetaProperty[]>> =>
    request.get('/v1/meta-models/meta-properties/builtin'),
  
  // 获取用户扩展的元属性
  getUserDefinedMetaProperties: (): Promise<ResponseVO<MetaProperty[]>> =>
    request.get('/v1/meta-models/meta-properties/custom'),
  
  // 根据ID获取元属性
  getMetaPropertyById: (id: string): Promise<ResponseVO<MetaProperty>> =>
    request.get(`/v1/meta-models/meta-properties/${id}`),
  
  // ==================== MetaLinkType ====================
  // 获取所有元关系类型（包括内置和用户扩展）
  getAllMetaLinkTypes: (): Promise<ResponseVO<MetaLinkType[]>> =>
    request.get('/v1/meta-models/meta-link-types'),
  
  // 获取平台内置的元关系类型
  getBuiltinMetaLinkTypes: (): Promise<ResponseVO<MetaLinkType[]>> =>
    request.get('/v1/meta-models/meta-link-types/builtin'),
  
  // 获取用户扩展的元关系类型
  getUserDefinedMetaLinkTypes: (): Promise<ResponseVO<MetaLinkType[]>> =>
    request.get('/v1/meta-models/meta-link-types/custom'),
  
  // 根据ID获取元关系类型
  getMetaLinkTypeById: (id: string): Promise<ResponseVO<MetaLinkType>> =>
    request.get(`/v1/meta-models/meta-link-types/${id}`),
  
  // ==================== MetaActionType ====================
  // 获取所有元操作类型（包括内置和用户扩展）
  getAllMetaActionTypes: (): Promise<ResponseVO<MetaActionType[]>> =>
    request.get('/v1/meta-models/meta-action-types'),
  
  // 获取平台内置的元操作类型
  getBuiltinMetaActionTypes: (): Promise<ResponseVO<MetaActionType[]>> =>
    request.get('/v1/meta-models/meta-action-types/builtin'),
  
  // 获取用户扩展的元操作类型
  getUserDefinedMetaActionTypes: (): Promise<ResponseVO<MetaActionType[]>> =>
    request.get('/v1/meta-models/meta-action-types/custom'),
  
  // 根据ID获取元操作类型
  getMetaActionTypeById: (id: string): Promise<ResponseVO<MetaActionType>> =>
    request.get(`/v1/meta-models/meta-action-types/${id}`),
  
  // ==================== MetaDomain ====================
  // 获取所有元域（包括内置和用户扩展）
  getAllMetaDomains: (): Promise<ResponseVO<MetaDomain[]>> =>
    request.get('/v1/meta-models/meta-domains'),
  
  // 获取平台内置的元域
  getBuiltinMetaDomains: (): Promise<ResponseVO<MetaDomain[]>> =>
    request.get('/v1/meta-models/meta-domains/builtin'),
  
  // 获取用户扩展的元域
  getUserDefinedMetaDomains: (): Promise<ResponseVO<MetaDomain[]>> =>
    request.get('/v1/meta-models/meta-domains/custom'),
  
  // 根据ID获取元域
  getMetaDomainById: (id: string): Promise<ResponseVO<MetaDomain>> =>
    request.get(`/v1/meta-models/meta-domains/${id}`),
};

// 对象类型API
export const objectTypeApi = {
  // 创建对象类型
  create: (data: ObjectTypeDTO): Promise<ResponseVO<ObjectType>> =>
    request.post('/v1/object-types', data),
  
  // 更新对象类型
  update: (id: string, data: ObjectTypeDTO): Promise<ResponseVO<ObjectType>> =>
    request.put(`/v1/object-types/${id}`, data),
  
  // 根据ID查询
  getById: (id: string): Promise<ResponseVO<ObjectType>> =>
    request.get(`/v1/object-types/${id}`),
  
  // 根据名称查询
  getByName: (name: string): Promise<ResponseVO<ObjectType>> =>
    request.get(`/v1/object-types/name/${name}`),
  
  // 分页查询列表
  list: (params: PageParams): Promise<ResponseVO<PageResult<ObjectType>>> =>
    request.get('/v1/object-types', { params }),
  
  // 删除
  delete: (id: string): Promise<ResponseVO<void>> =>
    request.delete(`/v1/object-types/${id}`),
};

// 属性API
export const propertyApi = {
  // 创建属性
  create: (objectTypeId: string, data: PropertyDTO): Promise<ResponseVO<Property>> =>
    request.post(`/v1/object-types/${objectTypeId}/properties`, data),
  
  // 更新属性（注意：路径需要objectTypeId和id，但后端实际只需要id，这里需要objectTypeId用于路径）
  update: (objectTypeId: string, id: string, data: PropertyDTO): Promise<ResponseVO<Property>> =>
    request.put(`/v1/object-types/${objectTypeId}/properties/${id}`, data),
  
  // 根据ID查询
  getById: (objectTypeId: string, id: string): Promise<ResponseVO<Property>> =>
    request.get(`/v1/object-types/${objectTypeId}/properties/${id}`),
  
  // 根据对象类型ID查询所有属性
  getByObjectTypeId: (objectTypeId: string): Promise<ResponseVO<Property[]>> =>
    request.get(`/v1/object-types/${objectTypeId}/properties`),
  
  // 删除
  delete: (objectTypeId: string, id: string): Promise<ResponseVO<void>> =>
    request.delete(`/v1/object-types/${objectTypeId}/properties/${id}`),
  
  // 从关联的数据源表批量创建属性
  batchCreateFromTable: (objectTypeId: string, data: BatchCreatePropertiesFromTableDTO): Promise<ResponseVO<BatchCreatePropertiesResult>> =>
    request.post(`/v1/object-types/${objectTypeId}/properties/batch-from-table`, data),
};

// 链接类型API
export const linkTypeApi = {
  // 创建链接类型
  create: (data: LinkTypeDTO): Promise<ResponseVO<LinkType>> =>
    request.post('/v1/link-types', data),
  
  // 更新链接类型
  update: (id: string, data: LinkTypeDTO): Promise<ResponseVO<LinkType>> =>
    request.put(`/v1/link-types/${id}`, data),
  
  // 根据ID查询
  getById: (id: string): Promise<ResponseVO<LinkType>> =>
    request.get(`/v1/link-types/${id}`),
  
  // 根据名称查询
  getByName: (name: string): Promise<ResponseVO<LinkType>> =>
    request.get(`/v1/link-types/name/${name}`),
  
  // 分页查询列表
  list: (params: PageParams): Promise<ResponseVO<PageResult<LinkType>>> =>
    request.get('/v1/link-types', { params }),
  
  // 根据对象类型ID查询相关链接类型
  getByObjectTypeId: (objectTypeId: string): Promise<ResponseVO<LinkType[]>> =>
    request.get(`/v1/link-types/object-type/${objectTypeId}`),
  
  // 删除
  delete: (id: string): Promise<ResponseVO<void>> =>
    request.delete(`/v1/link-types/${id}`),
};

// 链接实例API
export const linkInstanceApi = {
  // 创建链接实例
  create: (data: LinkInstanceDTO): Promise<ResponseVO<LinkInstance>> =>
    request.post('/v1/links', data),
  
  // 根据ID查询
  getById: (id: string): Promise<ResponseVO<LinkInstance>> =>
    request.get(`/v1/links/${id}`),
  
  // 根据链接类型ID查询所有链接实例
  getByLinkTypeId: (linkTypeId: string): Promise<ResponseVO<LinkInstance[]>> =>
    request.get(`/v1/links/link-type/${linkTypeId}`),
  
  // 根据链接类型和源实例查询目标实例
  getByLinkTypeAndSource: (linkTypeId: string, sourceInstanceId: string): Promise<ResponseVO<LinkInstance[]>> =>
    request.get(`/v1/links/link-type/${linkTypeId}/source/${sourceInstanceId}`),
  
  // 根据链接类型和目标实例查询源实例
  getByLinkTypeAndTarget: (linkTypeId: string, targetInstanceId: string): Promise<ResponseVO<LinkInstance[]>> =>
    request.get(`/v1/links/link-type/${linkTypeId}/target/${targetInstanceId}`),
  
  // 删除
  delete: (id: string): Promise<ResponseVO<void>> =>
    request.delete(`/v1/links/${id}`),
};

// 操作类型API
export const actionTypeApi = {
  // 创建操作类型
  create: (data: ActionTypeDTO): Promise<ResponseVO<ActionType>> =>
    request.post('/v1/action-types', data),
  
  // 更新操作类型
  update: (id: string, data: ActionTypeDTO): Promise<ResponseVO<ActionType>> =>
    request.put(`/v1/action-types/${id}`, data),
  
  // 根据ID查询
  getById: (id: string): Promise<ResponseVO<ActionType>> =>
    request.get(`/v1/action-types/${id}`),
  
  // 根据目标对象类型ID查询操作类型列表
  getByTargetObjectTypeId: (targetObjectTypeId: string): Promise<ResponseVO<ActionType[]>> =>
    request.get(`/v1/action-types/target-object-type/${targetObjectTypeId}`),
  
  // 分页查询列表
  list: (params: PageParams): Promise<ResponseVO<PageResult<ActionType>>> =>
    request.get('/v1/action-types', { params }),
  
  // 删除
  delete: (id: string): Promise<ResponseVO<void>> =>
    request.delete(`/v1/action-types/${id}`),
};

// 接口API
export const interfaceApi = {
  // 创建接口
  create: (data: InterfaceDTO): Promise<ResponseVO<Interface>> =>
    request.post('/v1/interfaces', data),
  
  // 更新接口
  update: (id: string, data: InterfaceDTO): Promise<ResponseVO<Interface>> =>
    request.put(`/v1/interfaces/${id}`, data),
  
  // 根据ID查询
  getById: (id: string): Promise<ResponseVO<Interface>> =>
    request.get(`/v1/interfaces/${id}`),
  
  // 根据名称查询
  getByName: (name: string): Promise<ResponseVO<Interface>> =>
    request.get(`/v1/interfaces/name/${name}`),
  
  // 分页查询列表
  list: (params: PageParams): Promise<ResponseVO<PageResult<Interface>>> =>
    request.get('/v1/interfaces', { params }),
  
  // 删除
  delete: (id: string): Promise<ResponseVO<void>> =>
    request.delete(`/v1/interfaces/${id}`),
  
  // 添加接口实现
  addImplementation: (interfaceId: string, objectTypeId: string): Promise<ResponseVO<void>> =>
    request.post(`/v1/interfaces/${interfaceId}/implementations/${objectTypeId}`),
  
  // 移除接口实现
  removeImplementation: (interfaceId: string, objectTypeId: string): Promise<ResponseVO<void>> =>
    request.delete(`/v1/interfaces/${interfaceId}/implementations/${objectTypeId}`),
};

// 函数API
export const functionApi = {
  // 创建函数
  create: (data: FunctionDTO): Promise<ResponseVO<Function>> =>
    request.post('/v1/functions', data),
  
  // 更新函数
  update: (id: string, data: FunctionDTO): Promise<ResponseVO<Function>> =>
    request.put(`/v1/functions/${id}`, data),
  
  // 根据ID查询
  getById: (id: string): Promise<ResponseVO<Function>> =>
    request.get(`/v1/functions/${id}`),
  
  // 根据名称查询
  getByName: (name: string): Promise<ResponseVO<Function>> =>
    request.get(`/v1/functions/name/${name}`),
  
  // 分页查询列表
  list: (params: PageParams): Promise<ResponseVO<PageResult<Function>>> =>
    request.get('/v1/functions', { params }),
  
  // 删除
  delete: (id: string): Promise<ResponseVO<void>> =>
    request.delete(`/v1/functions/${id}`),
};

// 实例API
export const instanceApi = {
  // 创建实例
  create: (objectTypeName: string, data: InstanceDTO): Promise<ResponseVO<Record<string, any>>> =>
    request.post(`/v1/instances/${objectTypeName}`, data),
  
  // 更新实例
  update: (objectTypeName: string, id: string, data: InstanceDTO): Promise<ResponseVO<Record<string, any>>> =>
    request.put(`/v1/instances/${objectTypeName}/${id}`, data),
  
  // 根据ID查询实例
  getById: (objectTypeName: string, id: string): Promise<ResponseVO<Record<string, any>>> =>
    request.get(`/v1/instances/${objectTypeName}/${id}`),
  
  // 分页查询实例列表
  list: (objectTypeName: string, params: PageParams & Record<string, any>): Promise<ResponseVO<PageResult<Record<string, any>>>> =>
    request.get(`/v1/instances/${objectTypeName}`, { params }),
  
  // 删除实例
  delete: (objectTypeName: string, id: string): Promise<ResponseVO<void>> =>
    request.delete(`/v1/instances/${objectTypeName}/${id}`),
  
  // 批量删除实例
  batchDelete: (objectTypeName: string, ids: string[]): Promise<ResponseVO<void>> =>
    request.delete(`/v1/instances/${objectTypeName}/batch`, { data: ids }),
};

// 查询API
export const queryApi = {
  // ============ 实例关系查询（数据级别） ============
  
  // 路径查询
  findPath: (sourceObjectType: string, sourceInstanceId: string, targetObjectType: string, targetInstanceId: string, linkTypeName?: string, maxDepth?: number): Promise<ResponseVO<any[][]>> =>
    request.get('/v1/query/path', { 
      params: { 
        sourceObjectType,
        sourceInstanceId, 
        targetObjectType,
        targetInstanceId, 
        linkTypeName,
        maxDepth: maxDepth || 5 
      } 
    }),
  
  // 邻居查询
  findNeighbors: (instanceId: string, linkTypeName?: string, objectType?: string): Promise<ResponseVO<any[]>> =>
    request.get('/v1/query/neighbors', { params: { instanceId, linkTypeName, objectType } }),
  
  // 关联实例查询
  findRelated: (instanceId: string, depth: number): Promise<ResponseVO<string[]>> =>
    request.get('/v1/query/related', { params: { instanceId, depth } }),

  // ============ 对象类型关系查询（模型级别） ============
  
  // 查询对象类型的所有链接类型
  findLinkTypesByObjectType: (objectTypeName: string): Promise<ResponseVO<LinkType[]>> =>
    request.get('/v1/query/object-type/link-types', { params: { objectTypeName } }),
  
  // 查询对象类型之间的关系路径
  findObjectTypePath: (sourceObjectTypeName: string, targetObjectTypeName: string, maxDepth?: number): Promise<ResponseVO<LinkType[][]>> =>
    request.get('/v1/query/object-type/path', { params: { sourceObjectTypeName, targetObjectTypeName, maxDepth: maxDepth || 5 } }),
  
  // 查询对象类型的可达对象类型
  findReachableObjectTypes: (objectTypeName: string, depth?: number): Promise<ResponseVO<ObjectType[]>> =>
    request.get('/v1/query/object-type/reachable', { params: { objectTypeName, depth: depth || 2 } }),
};

// 业务域API
export const businessDomainApi = {
  // 创建业务域
  create: (data: BusinessDomain): Promise<ResponseVO<BusinessDomain>> =>
    request.post('/v1/domains', data),
  
  // 更新业务域
  update: (id: string, data: BusinessDomain): Promise<ResponseVO<BusinessDomain>> =>
    request.put(`/v1/domains/${id}`, data),
  
  // 根据ID查询
  getById: (id: string): Promise<ResponseVO<BusinessDomain>> =>
    request.get(`/v1/domains/${id}`),
  
  // 分页查询列表
  list: (params: PageParams): Promise<ResponseVO<PageResult<BusinessDomain>>> =>
    request.get('/v1/domains', { params }),
  
  // 删除
  delete: (id: string): Promise<ResponseVO<void>> =>
    request.delete(`/v1/domains/${id}`),
};

// 数据源API
export const dataSourceApi = {
  // 创建数据源
  create: (data: DataSourceDTO): Promise<ResponseVO<DataSource>> =>
    request.post('/v1/datasources', data),
  
  // 更新数据源
  update: (id: string, data: DataSourceDTO): Promise<ResponseVO<DataSource>> =>
    request.put(`/v1/datasources/${id}`, data),
  
  // 根据ID查询
  getById: (id: string): Promise<ResponseVO<DataSource>> =>
    request.get(`/v1/datasources/${id}`),
  
  
  // 分页查询列表
  list: (params: PageParams & { type?: string }): Promise<ResponseVO<PageResult<DataSource>>> =>
    request.get('/v1/datasources', { params }),
  
  // 删除
  delete: (id: string): Promise<ResponseVO<void>> =>
    request.delete(`/v1/datasources/${id}`),
  
  // 测试连接
  testConnection: (id: string): Promise<ResponseVO<boolean>> =>
    request.post(`/v1/datasources/${id}/test`),
  
  // 启用/禁用
  setEnabled: (id: string, enabled: boolean): Promise<ResponseVO<void>> =>
    request.put(`/v1/datasources/${id}/enabled?enabled=${enabled}`),
  
  // 获取数据源下的表列表
  getTableList: (id: string, schemaName?: string): Promise<ResponseVO<string[]>> =>
    request.get(`/v1/datasources/${id}/tables`, { params: schemaName ? { schemaName } : {} }),
  
  // 获取表的字段信息
  getTableColumns: (id: string, tableName: string, schemaName?: string): Promise<ResponseVO<TableColumnInfo[]>> =>
    request.get(`/v1/datasources/${id}/tables/${tableName}/columns`, { params: schemaName ? { schemaName } : {} }),
  
  // 获取数据源的Schema列表（如果支持）
  getSchemaList: (id: string): Promise<ResponseVO<string[]>> =>
    request.get(`/v1/datasources/${id}/schemas`),
};

// 对象类型-数据源关联API
export const objectTypeDataSourceApi = {
  // 为对象类型添加数据源表关联
  addDataSourceTable: (
    objectTypeId: string,
    datasourceId: string,
    tableName: string | undefined,
    schemaName?: string,
    isStorage?: boolean,
    isDefault?: boolean,
    priority?: number
  ): Promise<ResponseVO<ObjectTypeDataSource>> => {
    const params: any = { datasourceId, isStorage, isDefault, priority };
    if (tableName !== undefined) {
      params.tableName = tableName;
    }
    if (schemaName !== undefined) {
      params.schemaName = schemaName;
    }
    return request.post(`/v1/object-types/${objectTypeId}/datasources`, null, { params });
  },
  
  // 移除数据源表关联（通过关联ID）
  removeDataSourceTable: (objectTypeId: string, id: string): Promise<ResponseVO<void>> =>
    request.delete(`/v1/object-types/${objectTypeId}/datasources/${id}`),
  
  // 设置默认数据源表
  setDefault: (objectTypeId: string, id: string): Promise<ResponseVO<void>> =>
    request.put(`/v1/object-types/${objectTypeId}/datasources/${id}/default`),
  
  // 获取对象类型关联的所有数据源表
  getDataSourceTables: (objectTypeId: string): Promise<ResponseVO<ObjectTypeDataSourceTable[]>> =>
    request.get(`/v1/object-types/${objectTypeId}/datasources/tables`),
  
  // 获取默认数据源表
  getDefaultDataSourceTable: (objectTypeId: string): Promise<ResponseVO<ObjectTypeDataSourceTable>> =>
    request.get(`/v1/object-types/${objectTypeId}/datasources/tables/default`),
  
  // ========== 以下为废弃接口，保留兼容性 ==========
  // 为对象类型添加数据源（已废弃）
  addDataSource: (
    objectTypeId: string,
    datasourceId: string,
    isDefault?: boolean,
    priority?: number
  ): Promise<ResponseVO<ObjectTypeDataSource>> =>
    request.post(`/v1/object-types/${objectTypeId}/datasources/deprecated`, null, {
      params: { datasourceId, isDefault, priority },
    }),
  
  // 移除数据源关联（已废弃）
  removeDataSource: (objectTypeId: string, datasourceId: string): Promise<ResponseVO<void>> =>
    request.delete(`/v1/object-types/${objectTypeId}/datasources/${datasourceId}`),
  
  // 获取对象类型关联的所有数据源（已废弃）
  getDataSources: (objectTypeId: string): Promise<ResponseVO<DataSource[]>> =>
    request.get(`/v1/object-types/${objectTypeId}/datasources`),
  
  // 获取默认数据源（已废弃）
  getDefaultDataSource: (objectTypeId: string): Promise<ResponseVO<DataSource>> =>
    request.get(`/v1/object-types/${objectTypeId}/datasources/default`),
};

