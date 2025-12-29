import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { Table, Button, Space, Popconfirm, Card, Select, App } from 'antd';
import { PlusOutlined, EditOutlined, DeleteOutlined } from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import { actionTypeApi, businessDomainApi } from '../../api';
import type { ActionType, BusinessDomain } from '../../types/api';

const ActionTypeList = () => {
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [dataSource, setDataSource] = useState<ActionType[]>([]);
  const { message } = App.useApp();
  const [domains, setDomains] = useState<BusinessDomain[]>([]);
  const [selectedDomainId, setSelectedDomainId] = useState<string | undefined>(undefined);

  useEffect(() => {
    loadDomains();
  }, []);

  useEffect(() => {
    loadData();
  }, [selectedDomainId]);

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
      const params: any = { current: 1, size: 1000 };
      if (selectedDomainId) {
        params.domainId = selectedDomainId;
      }
      const response = await actionTypeApi.list(params);
      if (response.code === 200) {
        setDataSource(response.data.records || []);
      }
    } catch (error) {
      message.error('加载数据失败');
    } finally {
      setLoading(false);
    }
  };

  const handleDomainFilterChange = (value: string | undefined) => {
    setSelectedDomainId(value);
  };

  const getDomainDisplayName = (domainId?: string) => {
    if (!domainId) return '-';
    const domain = domains.find(d => d.id === domainId);
    return domain ? domain.displayName : domainId;
  };

  const handleDelete = async (id: string) => {
    try {
      await actionTypeApi.delete(id);
      message.success('删除成功');
      loadData();
    } catch (error: any) {
      message.error(error.message || '删除失败');
    }
  };

  const columns: ColumnsType<ActionType> = [
    {
      title: '名称',
      dataIndex: 'name',
      key: 'name',
    },
    {
      title: '显示名称',
      dataIndex: 'displayName',
      key: 'displayName',
    },
    {
      title: '业务域',
      dataIndex: 'domainId',
      key: 'domainId',
      render: (_, record) => getDomainDisplayName(record.domainId),
    },
    {
      title: '描述',
      dataIndex: 'description',
      key: 'description',
      ellipsis: true,
    },
    {
      title: '目标对象类型ID',
      dataIndex: 'targetObjectTypeId',
      key: 'targetObjectTypeId',
    },
    {
      title: '需要审批',
      dataIndex: 'requiresApproval',
      key: 'requiresApproval',
      render: (value) => (value ? '是' : '否'),
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
            onClick={() => navigate(`/action-types/${record.id}/edit`)}
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
        <h2 style={{ margin: 0 }}>操作类型管理</h2>
        <Button type="primary" icon={<PlusOutlined />} onClick={() => navigate('/action-types/new')}>
          新建操作类型
        </Button>
      </div>
      <Card style={{ borderRadius: 8 }}>
        <div style={{ marginBottom: 16 }}>
          <Select
            placeholder="按业务域筛选"
            allowClear
            style={{ width: 200 }}
            value={selectedDomainId}
            onChange={handleDomainFilterChange}
            showSearch
            optionFilterProp="label"
          >
            {domains.map(domain => (
              <Select.Option key={domain.id} value={domain.id} label={domain.displayName}>
                {domain.displayName} ({domain.code})
              </Select.Option>
            ))}
          </Select>
        </div>
        <Table
          columns={columns}
          dataSource={dataSource}
          rowKey="id"
          loading={loading}
          pagination={{ pageSize: 10 }}
        />
      </Card>
    </div>
  );
};

export default ActionTypeList;

