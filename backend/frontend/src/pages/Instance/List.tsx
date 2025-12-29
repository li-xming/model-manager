import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Table, Button, Space, Popconfirm, Card, Select, Input, App } from 'antd';
import { PlusOutlined, EditOutlined, DeleteOutlined, ArrowLeftOutlined } from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import { instanceApi, objectTypeApi, propertyApi } from '../../api';
import type { ObjectType, Property } from '../../types/api';

const { Search } = Input;

const InstanceList = () => {
  const { objectTypeName } = useParams<{ objectTypeName: string }>();
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [dataSource, setDataSource] = useState<Record<string, any>[]>([]);
  const [objectType, setObjectType] = useState<ObjectType | null>(null);
  const [properties, setProperties] = useState<Property[]>([]);
  const [pagination, setPagination] = useState({
    current: 1,
    pageSize: 10,
    total: 0,
  });
  const [selectedRowKeys, setSelectedRowKeys] = useState<string[]>([]);
  const { message } = App.useApp();

  useEffect(() => {
    if (objectTypeName) {
      loadObjectType();
    }
  }, [objectTypeName]);

  useEffect(() => {
    if (objectType) {
      loadProperties();
      loadData();
    }
  }, [objectType, pagination.current, pagination.pageSize]);

  const loadObjectType = async () => {
    try {
      const response = await objectTypeApi.getByName(objectTypeName!);
      if (response.code === 200) {
        setObjectType(response.data);
      } else {
        message.error('对象类型不存在');
      }
    } catch (error) {
      message.error('加载对象类型失败');
    }
  };

  const loadProperties = async () => {
    try {
      const response = await propertyApi.getByObjectTypeId(objectType!.id);
      if (response.code === 200) {
        setProperties(response.data || []);
      }
    } catch (error) {
      message.error('加载属性失败');
    }
  };

  const loadData = async () => {
    if (!objectTypeName) return;
    setLoading(true);
    try {
      const response = await instanceApi.list(objectTypeName, {
        current: pagination.current,
        size: pagination.pageSize,
      });
      if (response.code === 200) {
        const pageData = response.data;
        setDataSource(pageData.records || []);
        setPagination({
          ...pagination,
          total: pageData.total || 0,
        });
      }
    } catch (error) {
      message.error('加载数据失败');
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (id: string) => {
    try {
      await instanceApi.delete(objectTypeName!, id);
      message.success('删除成功');
      loadData();
    } catch (error: any) {
      message.error(error.message || '删除失败');
    }
  };

  const handleBatchDelete = async () => {
    if (selectedRowKeys.length === 0) {
      message.warning('请选择要删除的实例');
      return;
    }
    try {
      await instanceApi.batchDelete(objectTypeName!, selectedRowKeys);
      message.success('批量删除成功');
      setSelectedRowKeys([]);
      loadData();
    } catch (error: any) {
      message.error(error.message || '批量删除失败');
    }
  };

  if (!objectTypeName || !objectType) {
    return (
      <Card style={{ borderRadius: 8 }}>
        <div>请选择对象类型</div>
      </Card>
    );
  }

  // 动态生成列
  const columns: ColumnsType<Record<string, any>> = [
    {
      title: 'ID',
      dataIndex: 'id',
      key: 'id',
      width: 200,
      fixed: 'left',
    },
    ...properties
      .sort((a, b) => a.sortOrder - b.sortOrder)
      .map(prop => ({
        title: prop.name,
        dataIndex: prop.name,
        key: prop.name,
        render: (value: any) => {
          if (value === null || value === undefined) return '-';
          if (typeof value === 'object') return JSON.stringify(value);
          return String(value);
        },
      })),
    {
      title: '操作',
      key: 'action',
      fixed: 'right',
      width: 150,
      render: (_, record) => (
        <Space size="middle">
          <Button
            type="link"
            size="small"
            icon={<EditOutlined />}
            onClick={() => navigate(`/instances/${objectTypeName}/${record.id}/edit`)}
          >
            编辑
          </Button>
          <Popconfirm
            title="确定要删除吗？"
            onConfirm={() => handleDelete(record.id)}
          >
            <Button type="link" size="small" danger icon={<DeleteOutlined />}>
              删除
            </Button>
          </Popconfirm>
        </Space>
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
          <h2 style={{ margin: 0, display: 'inline' }}>
            实例管理 - {objectType.displayName} ({objectType.name})
          </h2>
        </div>
        <Space>
          {selectedRowKeys.length > 0 && (
            <Popconfirm
              title={`确定要删除选中的 ${selectedRowKeys.length} 条记录吗？`}
              onConfirm={handleBatchDelete}
            >
              <Button danger>批量删除</Button>
            </Popconfirm>
          )}
          <Button
            type="primary"
            icon={<PlusOutlined />}
            onClick={() => navigate(`/instances/${objectTypeName}/new`)}
          >
            新建实例
          </Button>
        </Space>
      </div>
      <Card style={{ borderRadius: 8 }}>
        <Table
          columns={columns}
          dataSource={dataSource}
          rowKey="id"
          loading={loading}
          scroll={{ x: 'max-content' }}
          rowSelection={{
            selectedRowKeys,
            onChange: (keys) => setSelectedRowKeys(keys as string[]),
          }}
          pagination={{
            current: pagination.current,
            pageSize: pagination.pageSize,
            total: pagination.total,
            showSizeChanger: true,
            showTotal: (total) => `共 ${total} 条`,
            pageSizeOptions: ['10', '20', '50', '100'],
            onChange: (page, pageSize) => {
              setPagination({ ...pagination, current: page, pageSize });
            },
          }}
        />
      </Card>
    </div>
  );
};

export default InstanceList;
