import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { Card, Table, Button, Space, message } from 'antd';
import { ArrowRightOutlined } from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import { objectTypeApi } from '../../api';
import type { ObjectType } from '../../types/api';

const InstanceIndex = () => {
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [dataSource, setDataSource] = useState<ObjectType[]>([]);
  const [pagination, setPagination] = useState({
    current: 1,
    pageSize: 10,
    total: 0,
  });

  useEffect(() => {
    loadData();
  }, [pagination.current, pagination.pageSize]);

  const loadData = async () => {
    setLoading(true);
    try {
      const response = await objectTypeApi.list({
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

  const handleManage = (objectType: ObjectType) => {
    navigate(`/instances/${objectType.name}`);
  };

  const columns: ColumnsType<ObjectType> = [
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
      title: '主键',
      dataIndex: 'primaryKey',
      key: 'primaryKey',
    },
    {
      title: '操作',
      key: 'action',
      render: (_, record) => (
        <Button
          type="primary"
          icon={<ArrowRightOutlined />}
          onClick={() => handleManage(record)}
        >
          管理实例
        </Button>
      ),
    },
  ];

  return (
    <div>
      <div style={{ marginBottom: 24 }}>
        <h2 style={{ margin: 0 }}>实例管理</h2>
        <p style={{ color: 'rgba(0, 0, 0, 0.65)', marginTop: 8, marginBottom: 0 }}>
          请选择要管理的对象类型
        </p>
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

export default InstanceIndex;

