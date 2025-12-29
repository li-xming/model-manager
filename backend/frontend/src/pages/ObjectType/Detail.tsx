import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Card, Descriptions, Button, Table, Space, Popconfirm, Modal, Form, Input, Select, InputNumber, Switch, App, Tag, Checkbox, Alert } from 'antd';
import { EditOutlined, PlusOutlined, DeleteOutlined, ArrowLeftOutlined, StarOutlined, SyncOutlined, EyeOutlined } from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import { objectTypeApi, propertyApi, metaModelApi, objectTypeDataSourceApi, dataSourceApi, instanceApi } from '../../api';
import type { ObjectType, Property, PropertyDTO, MetaProperty, DataSource, ObjectTypeDataSourceTable, TableColumnInfo, BatchCreatePropertiesFromTableDTO } from '../../types/api';

const { TextArea } = Input;

const ObjectTypeDetail = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [objectType, setObjectType] = useState<ObjectType | null>(null);
  const [properties, setProperties] = useState<Property[]>([]);
  const [allProperties, setAllProperties] = useState<Property[]>([]); // 存储所有属性，用于分页和已存在判断
  const [propertyPagination, setPropertyPagination] = useState({
    current: 1,
    pageSize: 10,
    total: 0,
  });
  const [propertyModalVisible, setPropertyModalVisible] = useState(false);
  const [editingProperty, setEditingProperty] = useState<Property | null>(null);
  const [metaProperties, setMetaProperties] = useState<MetaProperty[]>([]);
  const [form] = Form.useForm();
  const { message } = App.useApp();
  
  // 数据源相关状态
  const [dataSourceTables, setDataSourceTables] = useState<ObjectTypeDataSourceTable[]>([]);
  const [allDataSources, setAllDataSources] = useState<DataSource[]>([]);
  const [defaultDataSourceTableId, setDefaultDataSourceTableId] = useState<string | null>(null);
  const [dataSourceModalVisible, setDataSourceModalVisible] = useState(false);
  const [dataSourceForm] = Form.useForm();
  
  // 表选择相关状态
  const [selectedDataSourceId, setSelectedDataSourceId] = useState<string | undefined>(undefined);
  const [schemas, setSchemas] = useState<string[]>([]);
  const [selectedSchema, setSelectedSchema] = useState<string | undefined>(undefined);
  const [tables, setTables] = useState<string[]>([]);
  const [loadingTables, setLoadingTables] = useState(false);
  
  // 同步属性相关状态
  const [syncPropertyModalVisible, setSyncPropertyModalVisible] = useState(false);
  const [selectedDataSourceTable, setSelectedDataSourceTable] = useState<ObjectTypeDataSourceTable | null>(null);
  const [tableColumns, setTableColumns] = useState<TableColumnInfo[]>([]);
  const [selectedColumns, setSelectedColumns] = useState<string[]>([]);
  const [loadingColumns, setLoadingColumns] = useState(false);
  const [skipExisting, setSkipExisting] = useState(true);
  const [syncForm] = Form.useForm();
  
  // 实例列表相关状态
  const [recentInstances, setRecentInstances] = useState<Record<string, any>[]>([]);
  const [instancesLoading, setInstancesLoading] = useState(false);

  useEffect(() => {
    if (id) {
      loadObjectType();
      loadProperties();
      loadMetaProperties();
      loadDataSources();
      loadAllDataSources();
    }
  }, [id]);
  
  useEffect(() => {
    if (objectType?.name) {
      loadRecentInstances();
    }
  }, [objectType?.name]);

  const loadMetaProperties = async () => {
    try {
      const response = await metaModelApi.getAllMetaProperties();
      if (response.code === 200) {
        setMetaProperties(response.data || []);
      }
    } catch (error) {
      console.error('加载元属性失败:', error);
    }
  };

  const loadObjectType = async () => {
    setLoading(true);
    try {
      const response = await objectTypeApi.getById(id!);
      if (response.code === 200) {
        setObjectType(response.data);
      }
    } catch (error) {
      message.error('加载对象类型失败');
    } finally {
      setLoading(false);
    }
  };

  const loadProperties = async (current: number = propertyPagination.current, pageSize: number = propertyPagination.pageSize) => {
    try {
      const response = await propertyApi.getByObjectTypeId(id!);
      if (response.code === 200) {
        const allPropertiesData = response.data || [];
        setAllProperties(allPropertiesData); // 保存所有属性
        setPropertyPagination({
          current,
          pageSize,
          total: allPropertiesData.length,
        });
        
        // 手动分页
        const start = (current - 1) * pageSize;
        const end = start + pageSize;
        const pagedProperties = allPropertiesData.slice(start, end);
        setProperties(pagedProperties);
      }
    } catch (error) {
      message.error('加载属性失败');
    }
  };

  const loadDataSources = async () => {
    try {
      const response = await objectTypeDataSourceApi.getDataSourceTables(id!);
      if (response.code === 200) {
        setDataSourceTables(response.data || []);
        // 查找默认数据源表
        const defaultTable = (response.data || []).find(table => table.isDefault);
        setDefaultDataSourceTableId(defaultTable?.id || null);
      }
    } catch (error) {
      message.error('加载数据源表列表失败');
    }
  };

  const loadAllDataSources = async () => {
    try {
      const response = await dataSourceApi.list({ current: 1, size: 1000 });
      if (response.code === 200) {
        setAllDataSources(response.data.records || []);
      }
    } catch (error) {
      console.error('加载所有数据源失败:', error);
    }
  };

  const handleDeleteProperty = async (propertyId: string) => {
    try {
      await propertyApi.delete(id!, propertyId);
      message.success('删除成功');
      // 如果当前页没有数据了，跳转到上一页
      if (properties.length === 1 && propertyPagination.current > 1) {
        loadProperties(propertyPagination.current - 1, propertyPagination.pageSize);
      } else {
        loadProperties(propertyPagination.current, propertyPagination.pageSize);
      }
    } catch (error: any) {
      message.error(error.message || '删除失败');
    }
  };

  const handlePropertySubmit = async (values: PropertyDTO) => {
    try {
      if (editingProperty) {
        await propertyApi.update(id!, editingProperty.id, values);
        message.success('更新成功');
      } else {
        await propertyApi.create(id!, values);
        message.success('创建成功');
        setPropertyModalVisible(false);
        setEditingProperty(null);
        form.resetFields();
        // 创建新属性后，重新加载并跳转到最后一页
        const response = await propertyApi.getByObjectTypeId(id!);
        if (response.code === 200) {
          const allPropertiesData = response.data || [];
          const totalPages = Math.ceil(allPropertiesData.length / propertyPagination.pageSize) || 1;
          loadProperties(totalPages, propertyPagination.pageSize);
          return;
        }
      }
      setPropertyModalVisible(false);
      setEditingProperty(null);
      form.resetFields();
      loadProperties(propertyPagination.current, propertyPagination.pageSize);
    } catch (error: any) {
      message.error(error.message || '操作失败');
    }
  };

  const openPropertyModal = (property?: Property) => {
    if (property) {
      setEditingProperty(property);
      form.setFieldsValue(property);
    } else {
      setEditingProperty(null);
      form.resetFields();
    }
    setPropertyModalVisible(true);
  };

  const propertyColumns: ColumnsType<Property> = [
    {
      title: '名称',
      dataIndex: 'name',
      key: 'name',
    },
    {
      title: '数据类型',
      dataIndex: 'dataType',
      key: 'dataType',
    },
    {
      title: '必填',
      dataIndex: 'required',
      key: 'required',
      render: (value) => (value ? '是' : '否'),
    },
    {
      title: '默认值',
      dataIndex: 'defaultValue',
      key: 'defaultValue',
    },
    {
      title: '排序',
      dataIndex: 'sortOrder',
      key: 'sortOrder',
    },
    {
      title: '描述',
      dataIndex: 'description',
      key: 'description',
      ellipsis: true,
    },
    {
      title: '操作',
      key: 'action',
      render: (_, record) => (
        <Space size="middle">
          <Button
            type="link"
            size="small"
            icon={<EditOutlined />}
            onClick={() => openPropertyModal(record)}
          >
            编辑
          </Button>
          <Popconfirm
            title="确定要删除吗？"
            onConfirm={() => handleDeleteProperty(record.id)}
          >
            <Button type="link" size="small" danger icon={<DeleteOutlined />}>
              删除
            </Button>
          </Popconfirm>
        </Space>
      ),
    },
  ];

  const dataTypes = [
    'STRING', 'INTEGER', 'LONG', 'DOUBLE', 'BOOLEAN', 'DATE', 'TIMESTAMP',
    'UUID', 'TEXT', 'JSON', 'BLOB'
  ];

  // 数据源管理相关函数
  const handleDataSourceChange = async (datasourceId: string) => {
    if (!datasourceId) {
      setSelectedDataSourceId(undefined);
      setSchemas([]);
      setSelectedSchema(undefined);
      setTables([]);
      // 清空表单中的schemaName和tableName
      dataSourceForm.setFieldsValue({ schemaName: undefined, tableName: undefined });
      return;
    }
    
    setSelectedDataSourceId(datasourceId);
    setSelectedSchema(undefined);
    setTables([]);
    // 清空表单中的schemaName和tableName
    dataSourceForm.setFieldsValue({ schemaName: undefined, tableName: undefined });
    
    // 加载Schema列表（如果支持）
    try {
      const schemaResponse = await dataSourceApi.getSchemaList(datasourceId);
      if (schemaResponse.code === 200 && schemaResponse.data && schemaResponse.data.length > 0) {
        setSchemas(schemaResponse.data);
      } else {
        setSchemas([]);
      }
    } catch (error) {
      console.error('加载Schema列表失败:', error);
      setSchemas([]);
    }
    
    // 加载表列表（如果有Schema则使用，否则直接加载）
    await loadTableList(datasourceId, undefined);
  };
  
  const handleSchemaChange = async (schemaName: string | undefined) => {
    setSelectedSchema(schemaName);
    // 清空表单中的tableName
    dataSourceForm.setFieldsValue({ tableName: undefined });
    setTables([]);
    
    if (selectedDataSourceId) {
      await loadTableList(selectedDataSourceId, schemaName);
    }
  };
  
  const loadTableList = async (datasourceId: string, schemaName?: string) => {
    setLoadingTables(true);
    try {
      const response = await dataSourceApi.getTableList(datasourceId, schemaName);
      if (response.code === 200) {
        const tableList = response.data || [];
        setTables(tableList);
        console.log('加载表列表成功:', tableList);
      } else {
        console.error('加载表列表失败，响应:', response);
        message.error('加载表列表失败：' + (response.message || '未知错误'));
        setTables([]);
      }
    } catch (error: any) {
      console.error('加载表列表异常:', error);
      message.error('加载表列表失败：' + (error.message || '网络错误'));
      setTables([]);
    } finally {
      setLoadingTables(false);
    }
  };
  
  const handleAddDataSource = async (values: { 
    datasourceId: string; 
    tableName?: string;  // 存储库模式下可选
    schemaName?: string;
    isStorage?: boolean;  // 是否作为存储库
    isDefault?: boolean; 
    priority?: number;
  }) => {
    try {
      // 如果是存储库模式，不需要表名（表名由系统生成）
      const tableName = values.isStorage ? undefined : values.tableName;
      if (!values.isStorage && !tableName) {
        message.error('非存储库模式下，表名不能为空');
        return;
      }
      
      await objectTypeDataSourceApi.addDataSourceTable(
        id!, 
        values.datasourceId, 
        tableName!,
        values.schemaName,
        values.isStorage,
        values.isDefault, 
        values.priority
      );
      message.success('添加数据源表成功');
      setDataSourceModalVisible(false);
      dataSourceForm.resetFields();
      setSelectedDataSourceId(undefined);
      setSchemas([]);
      setSelectedSchema(undefined);
      setTables([]);
      loadDataSources();
    } catch (error: any) {
      message.error(error.message || '添加数据源表失败');
    }
  };

  const handleRemoveDataSource = async (tableId: string) => {
    try {
      await objectTypeDataSourceApi.removeDataSourceTable(id!, tableId);
      message.success('移除数据源表成功');
      loadDataSources();
    } catch (error: any) {
      message.error(error.message || '移除数据源表失败');
    }
  };

  const handleSetDefault = async (tableId: string) => {
    try {
      await objectTypeDataSourceApi.setDefault(id!, tableId);
      message.success('设置默认数据源表成功');
      loadDataSources();
    } catch (error: any) {
      message.error(error.message || '设置默认数据源表失败');
    }
  };
  
  const openDataSourceModal = () => {
    setDataSourceModalVisible(true);
    setSelectedDataSourceId(undefined);
    setSchemas([]);
    setSelectedSchema(undefined);
    setTables([]);
    dataSourceForm.resetFields();
  };

  const getTypeDisplayName = (type: string) => {
    const typeMap: Record<string, string> = {
      MYSQL: 'MySQL',
      POSTGRESQL: 'PostgreSQL',
      ORACLE: 'Oracle',
      SQL_SERVER: 'SQL Server',
      MARIADB: 'MariaDB',
      MONGODB: 'MongoDB',
    };
    return typeMap[type] || type;
  };

  const dataSourceTableColumns: ColumnsType<ObjectTypeDataSourceTable> = [
    {
      title: '数据源',
      key: 'datasource',
      render: (_: any, record: ObjectTypeDataSourceTable) => (
        <div>
          <div>{record.datasource.name}</div>
          <div style={{ fontSize: '12px', color: '#999' }}>
            {getTypeDisplayName(record.datasource.type)} - {record.datasource.host}:{record.datasource.port}
          </div>
        </div>
      ),
    },
    {
      title: 'Schema',
      dataIndex: 'schemaName',
      key: 'schemaName',
      render: (schemaName?: string) => schemaName || '-',
    },
    {
      title: '表名',
      dataIndex: 'tableName',
      key: 'tableName',
    },
    {
      title: '状态',
      key: 'status',
      render: (_: any, record: ObjectTypeDataSourceTable) => {
        const status = record.datasource.status;
        if (status === 'ACTIVE') return <Tag color="success">正常</Tag>;
        if (status === 'ERROR') return <Tag color="error">错误</Tag>;
        return <Tag>未激活</Tag>;
      },
    },
    {
      title: '默认',
      key: 'default',
      render: (_: any, record: ObjectTypeDataSourceTable) => {
        if (record.isDefault) {
          return <Tag color="gold" icon={<StarOutlined />}>默认</Tag>;
        }
        return '-';
      },
    },
    {
      title: '优先级',
      dataIndex: 'priority',
      key: 'priority',
    },
    {
      title: '操作',
      key: 'action',
      render: (_: any, record: ObjectTypeDataSourceTable) => (
        <Space size="middle">
          {!record.isDefault && (
            <Button
              type="link"
              size="small"
              onClick={() => handleSetDefault(record.id)}
            >
              设为默认
            </Button>
          )}
          <Popconfirm
            title="确定要移除该数据源表吗？"
            onConfirm={() => handleRemoveDataSource(record.id)}
          >
            <Button type="link" size="small" danger>
              移除
            </Button>
          </Popconfirm>
        </Space>
      ),
    },
  ];

  // 获取未关联的数据源列表（用于添加对话框）
  const getAvailableDataSources = () => {
    // 获取已关联的数据源ID集合（允许同一数据源关联不同表）
    // 这里我们可以显示所有启用的数据源，允许同一数据源关联多张表
    return allDataSources.filter(ds => ds.enabled);
  };
  
  // 同步属性相关函数
  const openSyncPropertyModal = () => {
    setSyncPropertyModalVisible(true);
    setSelectedDataSourceTable(null);
    setTableColumns([]);
    setSelectedColumns([]);
    setSkipExisting(true);
    syncForm.resetFields();
  };
  
  const handleDataSourceTableChange = async (tableId: string) => {
    const table = dataSourceTables.find(t => t.id === tableId);
    if (!table) {
      return;
    }
    
    setSelectedDataSourceTable(table);
    setTableColumns([]);
    setSelectedColumns([]);
    
    // 加载表字段信息
    setLoadingColumns(true);
    try {
      const response = await dataSourceApi.getTableColumns(
        table.datasource.id,
        table.tableName,
        table.schemaName
      );
      if (response.code === 200) {
        // 过滤系统保留字段
        const filteredColumns = (response.data || []).filter(
          col => !reservedFields.has(col.columnName.toLowerCase())
        );
        setTableColumns(filteredColumns);
        
        // 自动选择所有字段（排除已存在的）
        const autoSelected = filteredColumns
          .filter(col => !existingPropertyNames.has(col.columnName))
          .map(col => col.columnName);
        setSelectedColumns(autoSelected);
      }
    } catch (error) {
      message.error('加载表字段信息失败');
    } finally {
      setLoadingColumns(false);
    }
  };
  
  const handleSyncProperties = async () => {
    if (!selectedDataSourceTable || selectedColumns.length === 0) {
      message.warning('请至少选择一个字段');
      return;
    }
    
    try {
      const dto: BatchCreatePropertiesFromTableDTO = {
        datasourceId: selectedDataSourceTable.datasource.id,
        tableName: selectedDataSourceTable.tableName,
        schemaName: selectedDataSourceTable.schemaName,
        columnNames: selectedColumns,
        skipExisting: skipExisting,
      };
      
      const response = await propertyApi.batchCreateFromTable(id!, dto);
      if (response.code === 200) {
        const result = response.data;
        let successMessage = `成功创建 ${result.created} 个属性`;
        if (result.skipped > 0) {
          successMessage += `，跳过 ${result.skipped} 个已存在的属性`;
        }
        if (result.failed > 0) {
          successMessage += `，${result.failed} 个属性创建失败`;
        }
        message.success(successMessage);
        
        // 关闭对话框并刷新属性列表
        setSyncPropertyModalVisible(false);
        // 同步属性后，重新加载并跳转到最后一页显示新创建的属性
        const response = await propertyApi.getByObjectTypeId(id!);
        if (response.code === 200) {
          const allPropertiesData = response.data || [];
          const totalPages = Math.ceil(allPropertiesData.length / propertyPagination.pageSize) || 1;
          loadProperties(totalPages, propertyPagination.pageSize);
        } else {
          loadProperties(propertyPagination.current, propertyPagination.pageSize);
        }
      }
    } catch (error: any) {
      message.error(error.message || '批量创建属性失败');
    }
  };
  
  // 系统保留字段列表
  const reservedFields = new Set(['id', 'class_id', 'datasource_id', 'created_at', 'updated_at', 'created_by', 'updated_by']);
  
  // 获取已存在的属性名集合（使用所有属性，而不是分页后的属性）
  const existingPropertyNames = new Set(allProperties.map(p => p.name));
  
  // 加载最近实例
  const loadRecentInstances = async () => {
    if (!objectType?.name) return;
    
    setInstancesLoading(true);
    try {
      const response = await instanceApi.list(objectType.name, {
        current: 1,
        size: 5, // 只显示最近5条
      });
      if (response.code === 200) {
        const pageData = response.data;
        setRecentInstances(pageData.records || []);
      }
    } catch (error) {
      // 实例加载失败不显示错误，可能是表还不存在
      console.error('加载最近实例失败:', error);
      setRecentInstances([]);
    } finally {
      setInstancesLoading(false);
    }
  };
  
  // 动态生成实例列（只显示前几个关键属性）
  const instanceColumns: ColumnsType<Record<string, any>> = [
    {
      title: 'ID',
      dataIndex: 'id',
      key: 'id',
      width: 200,
      ellipsis: true,
    },
    // 只显示前3个属性（按排序顺序）
    ...allProperties
      .sort((a, b) => a.sortOrder - b.sortOrder)
      .slice(0, 3)
      .map(prop => ({
        title: prop.name,
        dataIndex: prop.name,
        key: prop.name,
        ellipsis: true,
        render: (value: any) => {
          if (value === null || value === undefined) return '-';
          if (typeof value === 'object') return JSON.stringify(value);
          return String(value);
        },
      })),
    {
      title: '创建时间',
      dataIndex: 'created_at',
      key: 'created_at',
      width: 180,
    },
    {
      title: '操作',
      key: 'action',
      width: 100,
      fixed: 'right' as const,
      render: (_: any, record: Record<string, any>) => (
        <Button
          type="link"
          size="small"
          onClick={() => navigate(`/instances/${objectType?.name}/${record.id}/edit`)}
        >
          查看
        </Button>
      ),
    },
  ];

  return (
    <div>
      <div style={{ marginBottom: 24, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <div>
          <Button
            icon={<ArrowLeftOutlined />}
            onClick={() => navigate('/object-types')}
            style={{ marginRight: 16 }}
          >
            返回
          </Button>
          <h2 style={{ margin: 0, display: 'inline' }}>对象类型详情</h2>
        </div>
        <Button
          type="primary"
          icon={<EditOutlined />}
          onClick={() => navigate(`/object-types/${id}/edit`)}
        >
          编辑
        </Button>
      </div>

      <Card title="对象类型信息" loading={loading} style={{ borderRadius: 8, marginBottom: 24 }}>
        <Descriptions column={2} bordered>
          <Descriptions.Item label="名称">{objectType?.name}</Descriptions.Item>
          <Descriptions.Item label="显示名称">{objectType?.displayName}</Descriptions.Item>
          <Descriptions.Item label="描述" span={2}>{objectType?.description || '-'}</Descriptions.Item>
          <Descriptions.Item label="主键">{objectType?.primaryKey}</Descriptions.Item>
          <Descriptions.Item label="创建时间">{objectType?.createdAt}</Descriptions.Item>
        </Descriptions>
      </Card>

      <Card
        title="属性列表"
        extra={
          <Space>
            {dataSourceTables.length > 0 && (
              <Button 
                icon={<SyncOutlined />} 
                onClick={openSyncPropertyModal}
              >
                从关联表同步属性
              </Button>
            )}
            <Button type="primary" icon={<PlusOutlined />} onClick={() => openPropertyModal()}>
              新建属性
            </Button>
          </Space>
        }
        style={{ borderRadius: 8, marginBottom: 24 }}
      >
        <Table
          columns={propertyColumns}
          dataSource={properties}
          rowKey="id"
          pagination={{
            current: propertyPagination.current,
            pageSize: propertyPagination.pageSize,
            total: propertyPagination.total,
            showSizeChanger: true,
            showTotal: (total) => `共 ${total} 条`,
            pageSizeOptions: ['10', '20', '50', '100'],
            onChange: (page, pageSize) => {
              loadProperties(page, pageSize);
            },
            onShowSizeChange: (current, size) => {
              loadProperties(1, size);
            },
          }}
        />
      </Card>

      <Card
        title="最近实例"
        extra={
          objectType?.name && (
            <Button
              type="link"
              icon={<EyeOutlined />}
              onClick={() => navigate(`/instances/${objectType.name}`)}
            >
              查看全部
            </Button>
          )
        }
        style={{ borderRadius: 8, marginBottom: 24 }}
      >
        {instancesLoading ? (
          <Table
            columns={instanceColumns}
            dataSource={[]}
            rowKey="id"
            pagination={false}
            loading={true}
          />
        ) : recentInstances.length === 0 ? (
          <div style={{ textAlign: 'center', padding: '40px 0', color: '#999' }}>
            暂无实例数据
            {objectType?.name && (
              <div style={{ marginTop: 8 }}>
                <Button
                  type="link"
                  onClick={() => navigate(`/instances/${objectType.name}/new`)}
                >
                  创建第一个实例
                </Button>
              </div>
            )}
          </div>
        ) : (
          <Table
            columns={instanceColumns}
            dataSource={recentInstances}
            rowKey="id"
            pagination={false}
            size="small"
          />
        )}
      </Card>

      <Card
        title="关联数据源表"
        extra={
          <Button 
            type="primary" 
            icon={<PlusOutlined />} 
            onClick={openDataSourceModal}
            disabled={getAvailableDataSources().length === 0}
          >
            添加数据源表
          </Button>
        }
        style={{ borderRadius: 8 }}
      >
        {dataSourceTables.length === 0 ? (
          <div style={{ textAlign: 'center', padding: '40px 0', color: '#999' }}>
            暂无关联数据源表，点击"添加数据源表"按钮进行关联
          </div>
        ) : (
          <Table
            columns={dataSourceTableColumns}
            dataSource={dataSourceTables}
            rowKey="id"
            pagination={false}
          />
        )}
      </Card>

      <Modal
        title={editingProperty ? '编辑属性' : '新建属性'}
        open={propertyModalVisible}
        onCancel={() => {
          setPropertyModalVisible(false);
          setEditingProperty(null);
          form.resetFields();
        }}
        footer={null}
        width={600}
      >
        <Form
          form={form}
          layout="vertical"
          onFinish={handlePropertySubmit}
          initialValues={{
            dataType: 'STRING',
            required: false,
            sortOrder: 0,
          }}
        >
          <Form.Item
            name="name"
            label="名称"
            rules={[
              { required: true, message: '请输入属性名称' },
              { pattern: /^[a-zA-Z][a-zA-Z0-9_]*$/, message: '名称必须以字母开头，只能包含字母、数字和下划线' },
            ]}
          >
            <Input placeholder="请输入属性名称" disabled={!!editingProperty} />
          </Form.Item>

          <Form.Item
            name="dataType"
            label="数据类型"
            rules={[{ required: true, message: '请选择数据类型' }]}
          >
            <Select placeholder="请选择数据类型">
              {dataTypes.map(type => (
                <Select.Option key={type} value={type}>{type}</Select.Option>
              ))}
            </Select>
          </Form.Item>

          <Form.Item
            name="description"
            label="描述"
          >
            <TextArea rows={3} placeholder="请输入描述" />
          </Form.Item>

          <Form.Item
            name="required"
            label="必填"
            valuePropName="checked"
          >
            <Switch />
          </Form.Item>

          <Form.Item
            name="defaultValue"
            label="默认值"
          >
            <Input placeholder="请输入默认值" />
          </Form.Item>

          <Form.Item
            name="sortOrder"
            label="排序"
          >
            <InputNumber min={0} placeholder="排序值，数字越小越靠前" style={{ width: '100%' }} />
          </Form.Item>

          <Form.Item
            name="metaPropertyId"
            label="元属性"
            tooltip="选择该属性基于的元属性（默认为'META_PROPERTY'）"
          >
            <Select
              placeholder="请选择元属性（可选，默认为'META_PROPERTY'）"
              allowClear
              showSearch
              filterOption={(input, option) =>
                (option?.label ?? '').toLowerCase().includes(input.toLowerCase())
              }
              options={metaProperties.map(mp => ({ 
                value: mp.id, 
                label: `${mp.displayName || mp.name}${mp.isBuiltin ? ' (内置)' : ''}` 
              }))}
            />
          </Form.Item>

          <Form.Item>
            <Space>
              <Button type="primary" htmlType="submit">
                保存
              </Button>
              <Button onClick={() => {
                setPropertyModalVisible(false);
                setEditingProperty(null);
                form.resetFields();
              }}>
                取消
              </Button>
            </Space>
          </Form.Item>
        </Form>
      </Modal>

      <Modal
        title="添加数据源表"
        open={dataSourceModalVisible}
        onCancel={() => {
          setDataSourceModalVisible(false);
          dataSourceForm.resetFields();
          setSelectedDataSourceId(undefined);
          setSchemas([]);
          setSelectedSchema(undefined);
          setTables([]);
        }}
        footer={null}
        width={600}
      >
        <Form
          form={dataSourceForm}
          layout="vertical"
          onFinish={handleAddDataSource}
        >
          <Form.Item
            name="datasourceId"
            label="数据源"
            rules={[{ required: true, message: '请选择数据源' }]}
          >
            <Select 
              placeholder="请选择数据源" 
              showSearch 
              optionFilterProp="label"
              onChange={handleDataSourceChange}
            >
              {getAvailableDataSources().map(ds => (
                <Select.Option key={ds.id} value={ds.id} label={ds.name}>
                  {ds.name} ({getTypeDisplayName(ds.type)}) - {ds.host}:{ds.port}
                </Select.Option>
              ))}
            </Select>
          </Form.Item>

          <Form.Item
            name="isStorage"
            label="是否作为存储库"
            tooltip="勾选后，该数据源将作为对象实例的存储库，表名由系统自动生成。不勾选时，需要指定具体的表名。"
            valuePropName="checked"
          >
            <Switch />
          </Form.Item>

          <Form.Item
            noStyle
            shouldUpdate={(prevValues, currentValues) => prevValues.isStorage !== currentValues.isStorage}
          >
            {({ getFieldValue }) => {
              const isStorage = getFieldValue('isStorage');
              return isStorage ? null : (
                <>
                  {schemas.length > 0 && (
                    <Form.Item
                      name="schemaName"
                      label="Schema"
                      tooltip="该数据源支持Schema，请选择Schema（可选）"
                    >
                      <Select 
                        placeholder="请选择Schema（可选）" 
                        allowClear
                        onChange={handleSchemaChange}
                      >
                        {schemas.map(schema => (
                          <Select.Option key={schema} value={schema}>
                            {schema}
                          </Select.Option>
                        ))}
                      </Select>
                    </Form.Item>
                  )}

                  <Form.Item
                    name="tableName"
                    label="表名"
                    rules={[{ required: true, message: '请选择表名' }]}
                    dependencies={['datasourceId', 'schemaName']}
                  >
                    <Select 
                      placeholder={selectedDataSourceId ? (loadingTables ? '加载中...' : '请选择表名') : '请先选择数据源'}
                      disabled={!selectedDataSourceId || loadingTables}
                      loading={loadingTables}
                      showSearch
                      filterOption={(input, option) =>
                        (option?.label ?? '').toLowerCase().includes(input.toLowerCase())
                      }
                    >
                      {tables.map(table => (
                        <Select.Option key={table} value={table} label={table}>
                          {table}
                        </Select.Option>
                      ))}
                    </Select>
                  </Form.Item>
                </>
              );
            }}
          </Form.Item>

          <Form.Item
            name="isDefault"
            label="设为默认"
            valuePropName="checked"
          >
            <Switch />
          </Form.Item>

          <Form.Item
            name="priority"
            label="优先级"
            tooltip="数字越大优先级越高（用于排序）"
          >
            <InputNumber min={0} max={100} style={{ width: '100%' }} placeholder="优先级（可选）" />
          </Form.Item>

          <Form.Item>
            <Space>
              <Button type="primary" htmlType="submit">
                保存
              </Button>
              <Button onClick={() => {
                setDataSourceModalVisible(false);
                dataSourceForm.resetFields();
                setSelectedDataSourceId(undefined);
                setSchemas([]);
                setSelectedSchema(undefined);
                setTables([]);
              }}>
                取消
              </Button>
            </Space>
          </Form.Item>
        </Form>
      </Modal>

      <Modal
        title="从关联表同步属性"
        open={syncPropertyModalVisible}
        onCancel={() => {
          setSyncPropertyModalVisible(false);
          setSelectedDataSourceTable(null);
          setTableColumns([]);
          setSelectedColumns([]);
          syncForm.resetFields();
        }}
        width={800}
        footer={[
          <Button key="cancel" onClick={() => {
            setSyncPropertyModalVisible(false);
            setSelectedDataSourceTable(null);
            setTableColumns([]);
            setSelectedColumns([]);
            syncForm.resetFields();
          }}>
            取消
          </Button>,
          <Button 
            key="sync" 
            type="primary" 
            onClick={handleSyncProperties}
            disabled={!selectedDataSourceTable || selectedColumns.length === 0 || loadingColumns}
            loading={loadingColumns}
          >
            同步选中字段 ({selectedColumns.length})
          </Button>,
        ]}
      >
        <Form form={syncForm} layout="vertical">
          <Form.Item
            label="选择数据源表"
            required
          >
            <Select
              placeholder="请选择数据源表"
              value={selectedDataSourceTable?.id}
              onChange={handleDataSourceTableChange}
              showSearch
              optionFilterProp="label"
            >
              {dataSourceTables.map(table => {
                const schemaInfo = table.schemaName ? `${table.schemaName}.` : '';
                const displayName = `${table.datasource.name} - ${schemaInfo}${table.tableName}`;
                return (
                  <Select.Option key={table.id} value={table.id} label={displayName}>
                    {displayName}
                  </Select.Option>
                );
              })}
            </Select>
          </Form.Item>

          {selectedDataSourceTable && (
            <>
              <Form.Item
                label="字段列表"
                tooltip="选择要从表同步的字段（系统保留字段已自动过滤）"
              >
                {loadingColumns ? (
                  <div style={{ textAlign: 'center', padding: '40px 0' }}>
                    加载中...
                  </div>
                ) : tableColumns.length === 0 ? (
                  <Alert message="该表没有可用字段" type="warning" />
                ) : (
                  <div style={{ maxHeight: '400px', overflowY: 'auto', border: '1px solid #d9d9d9', borderRadius: '4px', padding: '12px' }}>
                    <Checkbox.Group
                      value={selectedColumns}
                      onChange={(values) => setSelectedColumns(values as string[])}
                      style={{ width: '100%' }}
                    >
                      <Space direction="vertical" style={{ width: '100%' }}>
                        {tableColumns.map(column => {
                          const isExisting = existingPropertyNames.has(column.columnName);
                          return (
                            <div key={column.columnName} style={{ 
                              padding: '8px',
                              backgroundColor: isExisting ? '#fffbe6' : 'transparent',
                              borderRadius: '4px'
                            }}>
                              <Checkbox value={column.columnName} disabled={isExisting && skipExisting}>
                                <Space>
                                  <span style={{ fontWeight: 500 }}>{column.columnName}</span>
                                  <Tag color="blue">{column.dataType}</Tag>
                                  {column.primaryKey && <Tag color="red">主键</Tag>}
                                  {column.nullable === false && <Tag color="orange">必填</Tag>}
                                  {isExisting && (
                                    <Tag color="warning">已存在</Tag>
                                  )}
                                </Space>
                                {column.comment && (
                                  <div style={{ marginLeft: '24px', fontSize: '12px', color: '#999', marginTop: '4px' }}>
                                    {column.comment}
                                  </div>
                                )}
                              </Checkbox>
                            </div>
                          );
                        })}
                      </Space>
                    </Checkbox.Group>
                  </div>
                )}
              </Form.Item>

              <Form.Item>
                <Space>
                  <Checkbox
                    checked={skipExisting}
                    onChange={(e) => setSkipExisting(e.target.checked)}
                  >
                    跳过已存在的属性名
                  </Checkbox>
                  <Button 
                    type="link" 
                    size="small"
                    onClick={() => {
                      if (skipExisting) {
                        const notExisting = tableColumns
                          .filter(col => !existingPropertyNames.has(col.columnName))
                          .map(col => col.columnName);
                        setSelectedColumns(notExisting);
                      } else {
                        setSelectedColumns(tableColumns.map(col => col.columnName));
                      }
                    }}
                  >
                    {skipExisting ? '全选未存在的' : '全选'}
                  </Button>
                  <Button 
                    type="link" 
                    size="small"
                    onClick={() => setSelectedColumns([])}
                  >
                    清空选择
                  </Button>
                </Space>
              </Form.Item>
            </>
          )}
        </Form>
      </Modal>
    </div>
  );
};

export default ObjectTypeDetail;
