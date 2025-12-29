import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { Table, Button, Space, Popconfirm, Card, Select, App } from 'antd';
import { PlusOutlined, EditOutlined, DeleteOutlined } from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import { linkTypeApi, businessDomainApi } from '../../api';
import type { LinkType, BusinessDomain } from '../../types/api';

const LinkTypeList = () => {
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [dataSource, setDataSource] = useState<LinkType[]>([]);
  const { message } = App.useApp();
  const [domains, setDomains] = useState<BusinessDomain[]>([]);
  const [selectedDomainId, setSelectedDomainId] = useState<string | undefined>(undefined);
  const [pagination, setPagination] = useState({
    current: 1,
    pageSize: 10,
    total: 0,
  });

  useEffect(() => {
    loadDomains();
  }, []);

  useEffect(() => {
    loadData();
  }, [pagination.current, pagination.pageSize, selectedDomainId]);

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
      const response = await linkTypeApi.list(params);
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

  const handleDomainFilterChange = (value: string | undefined) => {
    setSelectedDomainId(value);
    setPagination({ ...pagination, current: 1 });
  };

  const getDomainDisplayName = (domainId?: string) => {
    if (!domainId) return '-';
    const domain = domains.find(d => d.id === domainId);
    return domain ? domain.displayName : domainId;
  };

  const handleDelete = async (id: string) => {
    try {
      await linkTypeApi.delete(id);
      message.success('删除成功');
      loadData();
    } catch (error) {
      message.error('删除失败');
    }
  };

  const columns: ColumnsType<LinkType> = [
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
      title: '基数',
      dataIndex: 'cardinality',
      key: 'cardinality',
    },
    {
      title: '双向',
      dataIndex: 'bidirectional',
      key: 'bidirectional',
      render: (value) => value ? '是' : '否',
    },
    {
      title: '操作',
      key: 'action',
      render: (_, record) => (
        <Space size="middle">
          <Button type="link" onClick={() => navigate(`/link-types/${record.id}`)}>
            详情
          </Button>
          <Button type="link" icon={<EditOutlined />} onClick={() => navigate(`/link-types/${record.id}/edit`)}>
            编辑
          </Button>
          <Popconfirm
            title="确定要删除吗？"
            onConfirm={() => handleDelete(record.id)}
          >
            <Button type="link" danger icon={<DeleteOutlined />}>
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
        <h2 style={{ margin: 0 }}>链接类型管理</h2>
        <Button type="primary" icon={<PlusOutlined />} onClick={() => navigate('/link-types/new')}>
          新建链接类型
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

export default LinkTypeList;

