import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { Table, Button, Space, Popconfirm, Card, Select, Tag, Switch, App } from 'antd';
import { PlusOutlined, EditOutlined, DeleteOutlined, ThunderboltOutlined } from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import { dataSourceApi, businessDomainApi } from '../../api';
import type { DataSource, BusinessDomain, PageResult } from '../../types/api';

const DataSourceList = () => {
  const navigate = useNavigate();
  const { message } = App.useApp();
  const [loading, setLoading] = useState(false);
  const [testingIds, setTestingIds] = useState<Set<string>>(new Set());
  const [dataSource, setDataSource] = useState<DataSource[]>([]);
  const [domains, setDomains] = useState<BusinessDomain[]>([]);
  const [selectedDomainId, setSelectedDomainId] = useState<string | undefined>(undefined);
  const [selectedType, setSelectedType] = useState<string | undefined>(undefined);
  const [pagination, setPagination] = useState({
    current: 1,
    pageSize: 10,
    total: 0,
  });

  const dataSourceTypes = [
    { label: 'MySQL', value: 'MYSQL' },
    { label: 'PostgreSQL', value: 'POSTGRESQL' },
    { label: 'Oracle', value: 'ORACLE' },
    { label: 'SQL Server', value: 'SQL_SERVER' },
    { label: 'MariaDB', value: 'MARIADB' },
    { label: 'MongoDB', value: 'MONGODB' },
  ];

  useEffect(() => {
    loadDomains();
  }, []);

  useEffect(() => {
    loadData();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [pagination.current, pagination.pageSize, selectedDomainId, selectedType]);

  const loadDomains = async () => {
    try {
      const response = await businessDomainApi.list({ current: 1, size: 1000 });
      if (response.code === 200) {
        setDomains(response.data.records || []);
      }
    } catch (error) {
      console.error('加载业务域失败:', error);
    }
  };

  const loadData = async () => {
    setLoading(true);
    try {
      const params: any = {
        current: pagination.current,
        size: pagination.pageSize,
      };
      if (selectedDomainId) {
        params.domainId = selectedDomainId;
      }
      if (selectedType) {
        params.type = selectedType;
      }
      const response = await dataSourceApi.list(params);
      if (response.code === 200) {
        const pageData = response.data as PageResult<DataSource>;
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

  const handleDomainFilterChange = (value: string | undefined) => {
    setSelectedDomainId(value);
    setPagination({ ...pagination, current: 1 });
  };

  const handleTypeFilterChange = (value: string | undefined) => {
    setSelectedType(value);
    setPagination({ ...pagination, current: 1 });
  };

  const handleDelete = async (id: string) => {
    try {
      await dataSourceApi.delete(id);
      message.success('删除成功');
      loadData();
    } catch (error: any) {
      message.error(error.message || '删除失败');
    }
  };

  const handleTestConnection = async (id: string) => {
    setTestingIds((prev) => new Set(prev).add(id));
    try {
      const response = await dataSourceApi.testConnection(id);
      if (response.code === 200 && response.data) {
        message.success('连接测试成功');
      } else {
        message.error('连接测试失败');
      }
      loadData(); // 刷新数据以更新测试结果
    } catch (error: any) {
      message.error(error.message || '连接测试失败');
    } finally {
      setTestingIds((prev) => {
        const next = new Set(prev);
        next.delete(id);
        return next;
      });
    }
  };

  const handleEnabledChange = async (id: string, enabled: boolean) => {
    try {
      await dataSourceApi.setEnabled(id, enabled);
      message.success(enabled ? '已启用' : '已禁用');
      loadData();
    } catch (error: any) {
      message.error(error.message || '操作失败');
    }
  };

  const getDomainDisplayName = (domainId?: string) => {
    if (!domainId) return '-';
    const domain = domains.find((d) => d.id === domainId);
    return domain ? domain.displayName || domain.name : domainId;
  };

  const getTypeDisplayName = (type: string) => {
    const typeItem = dataSourceTypes.find((t) => t.value === type);
    return typeItem ? typeItem.label : type;
  };

  const getStatusTag = (status?: string) => {
    switch (status) {
      case 'ACTIVE':
        return <Tag color="success">正常</Tag>;
      case 'ERROR':
        return <Tag color="error">错误</Tag>;
      case 'INACTIVE':
        return <Tag color="default">未激活</Tag>;
      default:
        return <Tag>未知</Tag>;
    }
  };

  const columns: ColumnsType<DataSource> = [
    {
      title: '名称',
      dataIndex: 'name',
      key: 'name',
    },
    {
      title: '代码',
      dataIndex: 'code',
      key: 'code',
    },
    {
      title: '类型',
      dataIndex: 'type',
      key: 'type',
      render: (type: string) => getTypeDisplayName(type),
    },
    {
      title: '主机',
      dataIndex: 'host',
      key: 'host',
      render: (host: string, record: DataSource) => `${host}:${record.port}`,
    },
    {
      title: '数据库',
      dataIndex: 'databaseName',
      key: 'databaseName',
      render: (text: string) => text || '-',
    },
    {
      title: '业务域',
      dataIndex: 'domainId',
      key: 'domainId',
      render: (domainId: string) => getDomainDisplayName(domainId),
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      render: (status: string) => getStatusTag(status),
    },
    {
      title: '启用',
      dataIndex: 'enabled',
      key: 'enabled',
      render: (enabled: boolean, record: DataSource) => (
        <Switch
          checked={enabled}
          onChange={(checked) => handleEnabledChange(record.id, checked)}
        />
      ),
    },
    {
      title: '最后测试',
      key: 'lastTest',
      render: (_: any, record: DataSource) => {
        if (record.lastTestTime) {
          return (
            <div>
              <div>{new Date(record.lastTestTime).toLocaleString()}</div>
              {record.lastTestResult === 'SUCCESS' ? (
                <Tag color="success">成功</Tag>
              ) : (
                <Tag color="error">失败</Tag>
              )}
            </div>
          );
        }
        return '-';
      },
    },
    {
      title: '操作',
      key: 'action',
      width: 200,
      render: (_: any, record: DataSource) => (
        <Space size="middle">
          <Button
            type="link"
            size="small"
            icon={<ThunderboltOutlined />}
            loading={testingIds.has(record.id)}
            onClick={() => handleTestConnection(record.id)}
          >
            测试
          </Button>
          <Button
            type="link"
            size="small"
            icon={<EditOutlined />}
            onClick={() => navigate(`/datasources/${record.id}/edit`)}
          >
            编辑
          </Button>
          <Popconfirm
            title="确定要删除该数据源吗？"
            onConfirm={() => handleDelete(record.id)}
            okText="确定"
            cancelText="取消"
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
      <Card style={{ marginBottom: 16 }}>
        <Space style={{ width: '100%', justifyContent: 'space-between' }}>
          <Space>
            <Button type="primary" icon={<PlusOutlined />} onClick={() => navigate('/datasources/new')}>
              新增数据源
            </Button>
          </Space>
          <Space>
            <Select
              placeholder="筛选业务域"
              allowClear
              style={{ width: 200 }}
              value={selectedDomainId}
              onChange={handleDomainFilterChange}
            >
              {domains.map((domain) => (
                <Select.Option key={domain.id} value={domain.id}>
                  {domain.displayName || domain.name}
                </Select.Option>
              ))}
            </Select>
            <Select
              placeholder="筛选类型"
              allowClear
              style={{ width: 150 }}
              value={selectedType}
              onChange={handleTypeFilterChange}
            >
              {dataSourceTypes.map((type) => (
                <Select.Option key={type.value} value={type.value}>
                  {type.label}
                </Select.Option>
              ))}
            </Select>
          </Space>
        </Space>
      </Card>

      <Card>
        <Table
          columns={columns}
          dataSource={dataSource}
          rowKey="id"
          loading={loading}
          pagination={{
            current: pagination.current,
            pageSize: pagination.pageSize,
            total: pagination.total,
            showSizeChanger: true,
            showTotal: (total) => `共 ${total} 条`,
            onChange: (page, pageSize) => {
              setPagination({ ...pagination, current: page, pageSize });
            },
          }}
        />
      </Card>
    </div>
  );
};

export default DataSourceList;

