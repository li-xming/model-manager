import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { Table, Button, Space, Popconfirm, Card, App } from 'antd';
import { PlusOutlined, EditOutlined, DeleteOutlined } from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import { businessDomainApi } from '../../api';
import type { BusinessDomain, PageResult } from '../../types/api';

const DomainList = () => {
  const navigate = useNavigate();
  const { message } = App.useApp();
  const [loading, setLoading] = useState(false);
  const [dataSource, setDataSource] = useState<BusinessDomain[]>([]);
  const [pagination, setPagination] = useState({
    current: 1,
    pageSize: 10,
    total: 0,
  });

  useEffect(() => {
    loadData();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [pagination.current, pagination.pageSize]);

  const loadData = async () => {
    setLoading(true);
    try {
      const response = await businessDomainApi.list({
        current: pagination.current,
        size: pagination.pageSize,
      });
      if (response.code === 200) {
        const pageData = response.data as PageResult<BusinessDomain>;
        setDataSource(pageData.records || []);
        setPagination({
          ...pagination,
          total: pageData.total || 0,
        });
      }
    } catch (error) {
      message.error('加载业务域失败');
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (id: string) => {
    try {
      await businessDomainApi.delete(id);
      message.success('删除成功');
      loadData();
    } catch (error) {
      message.error('删除失败');
    }
  };

  const columns: ColumnsType<BusinessDomain> = [
    {
      title: '编码',
      dataIndex: 'code',
      key: 'code',
    },
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
            onClick={() => navigate(`/domains/${record.id}/graph`)}
          >
            对象视图
          </Button>
          <Button
            type="link"
            size="small"
            icon={<EditOutlined />}
            onClick={() => navigate(`/domains/${record.id}/edit`)}
          >
            编辑
          </Button>
          <Popconfirm
            title="确定要删除该业务域吗？"
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
        <h2 style={{ margin: 0 }}>业务域管理</h2>
        <Button type="primary" icon={<PlusOutlined />} onClick={() => navigate('/domains/new')}>
          新建业务域
        </Button>
      </div>
      <Card style={{ borderRadius: 8 }}>
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

export default DomainList;


