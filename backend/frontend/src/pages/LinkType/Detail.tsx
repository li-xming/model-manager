import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Card, Descriptions, Button, Table, Space, Popconfirm, message } from 'antd';
import { EditOutlined, DeleteOutlined, ArrowLeftOutlined, PlusOutlined } from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import { linkTypeApi, linkInstanceApi, objectTypeApi, instanceApi } from '../../api';
import type { LinkType, LinkInstance, ObjectType } from '../../types/api';

interface LinkInstanceWithDetails extends LinkInstance {
  sourceInstance?: Record<string, any>;
  targetInstance?: Record<string, any>;
}

const LinkTypeDetail = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [linkType, setLinkType] = useState<LinkType | null>(null);
  const [sourceObjectType, setSourceObjectType] = useState<ObjectType | null>(null);
  const [targetObjectType, setTargetObjectType] = useState<ObjectType | null>(null);
  const [linkInstances, setLinkInstances] = useState<LinkInstanceWithDetails[]>([]);
  const [instancesLoading, setInstancesLoading] = useState(false);

  useEffect(() => {
    if (id) {
      loadLinkType();
    }
  }, [id]);

  useEffect(() => {
    if (linkType && sourceObjectType && targetObjectType) {
      loadLinkInstances();
    }
  }, [linkType, sourceObjectType, targetObjectType]);

  const loadLinkType = async () => {
    setLoading(true);
    try {
      const response = await linkTypeApi.getById(id!);
      if (response.code === 200) {
        const data = response.data;
        setLinkType(data);
        
        // 加载源对象类型和目标对象类型
        if (data.sourceObjectTypeId) {
          try {
            const sourceResponse = await objectTypeApi.getById(data.sourceObjectTypeId);
            if (sourceResponse.code === 200) {
              setSourceObjectType(sourceResponse.data);
            }
          } catch (error) {
            console.error('加载源对象类型失败:', error);
          }
        }
        
        if (data.targetObjectTypeId) {
          try {
            const targetResponse = await objectTypeApi.getById(data.targetObjectTypeId);
            if (targetResponse.code === 200) {
              setTargetObjectType(targetResponse.data);
            }
          } catch (error) {
            console.error('加载目标对象类型失败:', error);
          }
        }
      }
    } catch (error) {
      message.error('加载链接类型失败');
    } finally {
      setLoading(false);
    }
  };

  const loadLinkInstances = async () => {
    if (!id || !linkType || !sourceObjectType || !targetObjectType) {
      return;
    }
    
    setInstancesLoading(true);
    try {
      // 1. 加载链接实例列表
      const response = await linkInstanceApi.getByLinkTypeId(id);
      if (response.code !== 200) {
        message.error('加载链接实例失败');
        return;
      }

      const instances = response.data || [];
      
      // 2. 为每个链接实例加载源实例和目标实例的详细信息
      const instancesWithDetails: LinkInstanceWithDetails[] = await Promise.all(
        instances.map(async (linkInstance: LinkInstance) => {
          const details: LinkInstanceWithDetails = { ...linkInstance };
          
          try {
            // 加载源实例
            const sourceResponse = await instanceApi.getById(
              sourceObjectType.name,
              linkInstance.sourceInstanceId
            );
            if (sourceResponse.code === 200) {
              details.sourceInstance = sourceResponse.data;
            }
          } catch (error) {
            console.error('加载源实例失败:', error);
          }
          
          try {
            // 加载目标实例
            const targetResponse = await instanceApi.getById(
              targetObjectType.name,
              linkInstance.targetInstanceId
            );
            if (targetResponse.code === 200) {
              details.targetInstance = targetResponse.data;
            }
          } catch (error) {
            console.error('加载目标实例失败:', error);
          }
          
          return details;
        })
      );
      
      setLinkInstances(instancesWithDetails);
    } catch (error) {
      console.error('加载链接实例失败:', error);
      message.error('加载链接实例失败');
    } finally {
      setInstancesLoading(false);
    }
  };

  const handleDelete = async () => {
    try {
      await linkTypeApi.delete(id!);
      message.success('删除成功');
      navigate('/link-types');
    } catch (error: any) {
      message.error(error.message || '删除失败');
    }
  };

  const getInstanceDisplayName = (instance: Record<string, any> | undefined): string => {
    if (!instance) return '-';
    // 优先使用name、displayName或id作为显示名称
    return instance.name || instance.displayName || instance.id || '-';
  };

  const getInstanceDisplayInfo = (instance: Record<string, any> | undefined): string => {
    if (!instance) return '-';
    const displayName = getInstanceDisplayName(instance);
    // 如果显示名称不是ID，则显示名称，否则显示ID
    if (displayName && displayName !== instance.id) {
      return displayName;
    }
    return instance.id || '-';
  };

  const linkInstanceColumns: ColumnsType<LinkInstanceWithDetails> = [
    {
      title: '源实例',
      key: 'sourceInstance',
      render: (_, record) => {
        const instance = record.sourceInstance;
        if (!instance) {
          return <span style={{ color: '#999' }}>{record.sourceInstanceId}</span>;
        }
        const displayInfo = getInstanceDisplayInfo(instance);
        return (
          <div>
            <div>{displayInfo}</div>
            {displayInfo !== record.sourceInstanceId && (
              <div style={{ fontSize: '12px', color: '#999' }}>{record.sourceInstanceId}</div>
            )}
          </div>
        );
      },
    },
    {
      title: '目标实例',
      key: 'targetInstance',
      render: (_, record) => {
        const instance = record.targetInstance;
        if (!instance) {
          return <span style={{ color: '#999' }}>{record.targetInstanceId}</span>;
        }
        const displayInfo = getInstanceDisplayInfo(instance);
        return (
          <div>
            <div>{displayInfo}</div>
            {displayInfo !== record.targetInstanceId && (
              <div style={{ fontSize: '12px', color: '#999' }}>{record.targetInstanceId}</div>
            )}
          </div>
        );
      },
    },
    {
      title: '创建时间',
      dataIndex: 'createdAt',
      key: 'createdAt',
    },
    {
      title: '操作',
      key: 'action',
      render: (_, record) => (
        <Space size="middle">
          <Button
            type="link"
            size="small"
            onClick={() => navigate(`/link-instances/${record.id}/edit`)}
          >
            编辑
          </Button>
          <Popconfirm
            title="确定要删除吗？"
            onConfirm={async () => {
              try {
                await linkInstanceApi.delete(record.id);
                message.success('删除成功');
                loadLinkInstances();
              } catch (error: any) {
                message.error(error.message || '删除失败');
              }
            }}
          >
            <Button type="link" size="small" danger>
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
            onClick={() => navigate('/link-types')}
            style={{ marginRight: 16 }}
          >
            返回
          </Button>
          <h2 style={{ margin: 0, display: 'inline' }}>链接类型详情</h2>
        </div>
        <Space>
          <Button
            type="primary"
            icon={<PlusOutlined />}
            onClick={() => navigate(`/link-instances/new?linkTypeId=${id}`)}
          >
            新建链接实例
          </Button>
          <Button
            icon={<EditOutlined />}
            onClick={() => navigate(`/link-types/${id}/edit`)}
          >
            编辑
          </Button>
          <Popconfirm
            title="确定要删除这个链接类型吗？"
            onConfirm={handleDelete}
          >
            <Button danger icon={<DeleteOutlined />}>
              删除
            </Button>
          </Popconfirm>
        </Space>
      </div>

      <Card title="链接类型信息" loading={loading} style={{ borderRadius: 8, marginBottom: 24 }}>
        <Descriptions column={2} bordered>
          <Descriptions.Item label="名称">{linkType?.name}</Descriptions.Item>
          <Descriptions.Item label="显示名称">{linkType?.displayName}</Descriptions.Item>
          <Descriptions.Item label="描述" span={2}>{linkType?.description || '-'}</Descriptions.Item>
          <Descriptions.Item label="源对象类型">
            {sourceObjectType ? `${sourceObjectType.displayName} (${sourceObjectType.name})` : linkType?.sourceObjectTypeId || '-'}
          </Descriptions.Item>
          <Descriptions.Item label="目标对象类型">
            {targetObjectType ? `${targetObjectType.displayName} (${targetObjectType.name})` : linkType?.targetObjectTypeId || '-'}
          </Descriptions.Item>
          <Descriptions.Item label="关系基数">{linkType?.cardinality}</Descriptions.Item>
          <Descriptions.Item label="双向关系">{linkType?.bidirectional ? '是' : '否'}</Descriptions.Item>
          <Descriptions.Item label="创建时间">{linkType?.createdAt}</Descriptions.Item>
          <Descriptions.Item label="更新时间">{linkType?.updatedAt}</Descriptions.Item>
        </Descriptions>
      </Card>

      <Card
        title="链接实例列表"
        extra={
          <Button type="primary" icon={<PlusOutlined />} onClick={() => navigate(`/link-instances/new?linkTypeId=${id}`)}>
            新建链接实例
          </Button>
        }
        style={{ borderRadius: 8 }}
      >
        <Table
          columns={linkInstanceColumns}
          dataSource={linkInstances}
          rowKey="id"
          loading={instancesLoading}
          pagination={false}
          locale={{ emptyText: '暂无链接实例' }}
        />
      </Card>
    </div>
  );
};

export default LinkTypeDetail;

