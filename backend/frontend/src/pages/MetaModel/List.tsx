import { useState, useEffect } from 'react';
import { Card, Table, Tabs, Tag, Button, Space, Modal, Descriptions, Select, App } from 'antd';
import { EyeOutlined } from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import { metaModelApi } from '../../api';
import type { MetaClass, MetaProperty, MetaLinkType, MetaActionType, MetaDomain } from '../../types/api';

type MetaModelType = 'metaClass' | 'metaProperty' | 'metaLinkType' | 'metaActionType' | 'metaDomain';

const MetaModelList = () => {
  const [activeTab, setActiveTab] = useState<MetaModelType>('metaClass');
  const [filterType, setFilterType] = useState<'all' | 'builtin' | 'custom'>('all');
  const [loading, setLoading] = useState(false);
  const [detailVisible, setDetailVisible] = useState(false);
  const [selectedItem, setSelectedItem] = useState<any>(null);
  const { message } = App.useApp();

  // 数据状态
  const [metaClasses, setMetaClasses] = useState<MetaClass[]>([]);
  const [metaProperties, setMetaProperties] = useState<MetaProperty[]>([]);
  const [metaLinkTypes, setMetaLinkTypes] = useState<MetaLinkType[]>([]);
  const [metaActionTypes, setMetaActionTypes] = useState<MetaActionType[]>([]);
  const [metaDomains, setMetaDomains] = useState<MetaDomain[]>([]);

  useEffect(() => {
    loadData(activeTab);
  }, [activeTab, filterType]);

  const loadData = async (type: MetaModelType) => {
    setLoading(true);
    try {
      switch (type) {
        case 'metaClass':
          if (filterType === 'builtin') {
            const builtinResponse = await metaModelApi.getBuiltinMetaClasses();
            if (builtinResponse.code === 200) {
              setMetaClasses(builtinResponse.data || []);
            }
          } else if (filterType === 'custom') {
            const customResponse = await metaModelApi.getUserDefinedMetaClasses();
            if (customResponse.code === 200) {
              setMetaClasses(customResponse.data || []);
            }
          } else {
            const allResponse = await metaModelApi.getAllMetaClasses();
            if (allResponse.code === 200) {
              setMetaClasses(allResponse.data || []);
            }
          }
          break;
        case 'metaProperty':
          if (filterType === 'builtin') {
            const builtinResponse = await metaModelApi.getBuiltinMetaProperties();
            if (builtinResponse.code === 200) {
              setMetaProperties(builtinResponse.data || []);
            }
          } else if (filterType === 'custom') {
            const customResponse = await metaModelApi.getUserDefinedMetaProperties();
            if (customResponse.code === 200) {
              setMetaProperties(customResponse.data || []);
            }
          } else {
            const allResponse = await metaModelApi.getAllMetaProperties();
            if (allResponse.code === 200) {
              setMetaProperties(allResponse.data || []);
            }
          }
          break;
        case 'metaLinkType':
          if (filterType === 'builtin') {
            const builtinResponse = await metaModelApi.getBuiltinMetaLinkTypes();
            if (builtinResponse.code === 200) {
              setMetaLinkTypes(builtinResponse.data || []);
            }
          } else if (filterType === 'custom') {
            const customResponse = await metaModelApi.getUserDefinedMetaLinkTypes();
            if (customResponse.code === 200) {
              setMetaLinkTypes(customResponse.data || []);
            }
          } else {
            const allResponse = await metaModelApi.getAllMetaLinkTypes();
            if (allResponse.code === 200) {
              setMetaLinkTypes(allResponse.data || []);
            }
          }
          break;
        case 'metaActionType':
          if (filterType === 'builtin') {
            const builtinResponse = await metaModelApi.getBuiltinMetaActionTypes();
            if (builtinResponse.code === 200) {
              setMetaActionTypes(builtinResponse.data || []);
            }
          } else if (filterType === 'custom') {
            const customResponse = await metaModelApi.getUserDefinedMetaActionTypes();
            if (customResponse.code === 200) {
              setMetaActionTypes(customResponse.data || []);
            }
          } else {
            const allResponse = await metaModelApi.getAllMetaActionTypes();
            if (allResponse.code === 200) {
              setMetaActionTypes(allResponse.data || []);
            }
          }
          break;
        case 'metaDomain':
          if (filterType === 'builtin') {
            const builtinResponse = await metaModelApi.getBuiltinMetaDomains();
            if (builtinResponse.code === 200) {
              setMetaDomains(builtinResponse.data || []);
            }
          } else if (filterType === 'custom') {
            const customResponse = await metaModelApi.getUserDefinedMetaDomains();
            if (customResponse.code === 200) {
              setMetaDomains(customResponse.data || []);
            }
          } else {
            const allResponse = await metaModelApi.getAllMetaDomains();
            if (allResponse.code === 200) {
              setMetaDomains(allResponse.data || []);
            }
          }
          break;
      }
    } catch (error) {
      message.error('加载数据失败');
    } finally {
      setLoading(false);
    }
  };

  const handleViewDetail = async (type: MetaModelType, id: string) => {
    try {
      let response: any;
      switch (type) {
        case 'metaClass':
          response = await metaModelApi.getMetaClassById(id);
          break;
        case 'metaProperty':
          response = await metaModelApi.getMetaPropertyById(id);
          break;
        case 'metaLinkType':
          response = await metaModelApi.getMetaLinkTypeById(id);
          break;
        case 'metaActionType':
          response = await metaModelApi.getMetaActionTypeById(id);
          break;
        case 'metaDomain':
          response = await metaModelApi.getMetaDomainById(id);
          break;
      }
      if (response.code === 200) {
        setSelectedItem(response.data);
        setDetailVisible(true);
      }
    } catch (error) {
      message.error('加载详情失败');
    }
  };

  // 通用列定义（适用于所有元模型类型）
  const createCommonColumns = (type: MetaModelType): ColumnsType<any> => [
    {
      title: 'ID',
      dataIndex: 'id',
      key: 'id',
      width: 200,
      ellipsis: true,
    },
    {
      title: '代码',
      dataIndex: 'code',
      key: 'code',
      width: 150,
    },
    {
      title: '名称',
      dataIndex: 'name',
      key: 'name',
      width: 150,
    },
    {
      title: '显示名称',
      dataIndex: 'displayName',
      key: 'displayName',
      width: 150,
    },
    {
      title: '类型',
      key: 'isBuiltin',
      width: 100,
      render: (_, record) => (
        <Tag color={record.isBuiltin ? 'blue' : 'green'}>
          {record.isBuiltin ? '内置' : '用户扩展'}
        </Tag>
      ),
    },
    {
      title: '版本',
      dataIndex: 'version',
      key: 'version',
      width: 100,
    },
    {
      title: '创建者',
      dataIndex: 'createdBy',
      key: 'createdBy',
      width: 120,
      render: (text) => text || '-',
    },
    {
      title: '创建时间',
      dataIndex: 'createdAt',
      key: 'createdAt',
      width: 180,
    },
    {
      title: '操作',
      key: 'action',
      width: 100,
      fixed: 'right',
      render: (_, record) => (
        <Button
          type="link"
          size="small"
          icon={<EyeOutlined />}
          onClick={() => handleViewDetail(type, record.id)}
        >
          详情
        </Button>
      ),
    },
  ];


  const getTabTitle = (type: MetaModelType, count: number) => {
    const titles: Record<MetaModelType, string> = {
      metaClass: '元类',
      metaProperty: '元属性',
      metaLinkType: '元关系类型',
      metaActionType: '元操作类型',
      metaDomain: '元域',
    };
    return `${titles[type]} (${count})`;
  };

  const getTableForTab = (type: MetaModelType) => {
    let dataSource: any[] = [];
    switch (type) {
      case 'metaClass':
        dataSource = metaClasses;
        break;
      case 'metaProperty':
        dataSource = metaProperties;
        break;
      case 'metaLinkType':
        dataSource = metaLinkTypes;
        break;
      case 'metaActionType':
        dataSource = metaActionTypes;
        break;
      case 'metaDomain':
        dataSource = metaDomains;
        break;
    }
    return (
      <Table
        columns={createCommonColumns(type)}
        dataSource={dataSource}
        rowKey="id"
        loading={loading}
        scroll={{ x: 'max-content' }}
        pagination={{ pageSize: 10, showSizeChanger: true }}
      />
    );
  };

  const tabItems = [
    {
      key: 'metaClass',
      label: getTabTitle('metaClass', metaClasses.length),
      children: getTableForTab('metaClass'),
    },
    {
      key: 'metaProperty',
      label: getTabTitle('metaProperty', metaProperties.length),
      children: getTableForTab('metaProperty'),
    },
    {
      key: 'metaLinkType',
      label: getTabTitle('metaLinkType', metaLinkTypes.length),
      children: getTableForTab('metaLinkType'),
    },
    {
      key: 'metaActionType',
      label: getTabTitle('metaActionType', metaActionTypes.length),
      children: getTableForTab('metaActionType'),
    },
    {
      key: 'metaDomain',
      label: getTabTitle('metaDomain', metaDomains.length),
      children: getTableForTab('metaDomain'),
    },
  ];

  return (
    <div>
      <div style={{ marginBottom: 24, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <h2 style={{ margin: 0 }}>元模型管理</h2>
        <Space>
          <Select
            value={filterType}
            onChange={(value) => setFilterType(value)}
            style={{ width: 150 }}
          >
            <Select.Option value="all">全部</Select.Option>
            <Select.Option value="builtin">平台内置</Select.Option>
            <Select.Option value="custom">用户扩展</Select.Option>
          </Select>
        </Space>
      </div>
      <Card style={{ borderRadius: 8 }}>
        <Tabs
          activeKey={activeTab}
          onChange={(key) => setActiveTab(key as MetaModelType)}
          items={tabItems}
        />
      </Card>

      {/* 详情Modal */}
      <Modal
        title="元模型详情"
        open={detailVisible}
        onCancel={() => {
          setDetailVisible(false);
          setSelectedItem(null);
        }}
        footer={[
          <Button key="close" onClick={() => {
            setDetailVisible(false);
            setSelectedItem(null);
          }}>
            关闭
          </Button>,
        ]}
        width={800}
      >
        {selectedItem && (
          <Descriptions bordered column={2}>
            <Descriptions.Item label="ID" span={2}>
              {selectedItem.id}
            </Descriptions.Item>
            <Descriptions.Item label="代码">
              {selectedItem.code}
            </Descriptions.Item>
            <Descriptions.Item label="名称">
              {selectedItem.name}
            </Descriptions.Item>
            <Descriptions.Item label="显示名称">
              {selectedItem.displayName || '-'}
            </Descriptions.Item>
            <Descriptions.Item label="类型">
              <Tag color={selectedItem.isBuiltin ? 'blue' : 'green'}>
                {selectedItem.isBuiltin ? '平台内置' : '用户扩展'}
              </Tag>
            </Descriptions.Item>
            <Descriptions.Item label="版本">
              {selectedItem.version || '-'}
            </Descriptions.Item>
            <Descriptions.Item label="创建者">
              {selectedItem.createdBy || '-'}
            </Descriptions.Item>
            <Descriptions.Item label="创建时间">
              {selectedItem.createdAt || '-'}
            </Descriptions.Item>
            <Descriptions.Item label="更新时间">
              {selectedItem.updatedAt || '-'}
            </Descriptions.Item>
            <Descriptions.Item label="描述" span={2}>
              {selectedItem.description || '-'}
            </Descriptions.Item>
            <Descriptions.Item label="元数据Schema" span={2}>
              <pre style={{ margin: 0, maxHeight: 300, overflow: 'auto', whiteSpace: 'pre-wrap' }}>
                {selectedItem.metadataSchema 
                  ? JSON.stringify(selectedItem.metadataSchema, null, 2)
                  : '-'}
              </pre>
            </Descriptions.Item>
          </Descriptions>
        )}
      </Modal>
    </div>
  );
};

export default MetaModelList;

