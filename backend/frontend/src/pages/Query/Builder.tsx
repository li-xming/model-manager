import { useState, useEffect } from 'react';
import { Card, Form, Input, InputNumber, Button, Select, Table, Space, Typography, Tabs, App } from 'antd';
import { SearchOutlined, ClearOutlined } from '@ant-design/icons';
import { queryApi, objectTypeApi, instanceApi, businessDomainApi, linkTypeApi } from '../../api';
import type { ObjectType, BusinessDomain, LinkType } from '../../types/api';

const { Title } = Typography;
const { Option } = Select;
const { TabPane } = Tabs;

const QueryBuilder = () => {
  const [form] = Form.useForm();
  const { message } = App.useApp();
  const [loading, setLoading] = useState(false);
  const [objectTypes, setObjectTypes] = useState<ObjectType[]>([]);
  const [domains, setDomains] = useState<BusinessDomain[]>([]);
  const [selectedDomainId, setSelectedDomainId] = useState<string | undefined>(undefined);
  const [sourceInstances, setSourceInstances] = useState<any[]>([]);
  const [targetInstances, setTargetInstances] = useState<any[]>([]);
  const [neighborInstances, setNeighborInstances] = useState<any[]>([]);
  const [relatedInstances, setRelatedInstances] = useState<any[]>([]);
  const [results, setResults] = useState<any[]>([]);
  const [activeTab, setActiveTab] = useState<string>('object-type');
  const [graphData, setGraphData] = useState<{ nodes: any[]; links: any[] }>({ nodes: [], links: [] });
  const [zoom, setZoom] = useState(1);
  const [pan, setPan] = useState<{ x: number; y: number }>({ x: 0, y: 0 });
  const [isPanning, setIsPanning] = useState(false);
  const [lastPanPosition, setLastPanPosition] = useState<{ x: number; y: number } | null>(null);

  useEffect(() => {
    loadDomains();
  }, []);

  useEffect(() => {
    loadObjectTypes();
    // eslint-disable-next-line react-hooks/exhaustive-deps
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

  const loadObjectTypes = async () => {
    try {
      const params: any = { current: 1, size: 1000 };
      if (selectedDomainId) {
        params.domainId = selectedDomainId;
      }
      const response = await objectTypeApi.list(params);
      if (response.code === 200) {
        setObjectTypes(response.data.records || []);
      }
    } catch (error) {
      message.error('加载对象类型失败');
    }
  };

  const loadInstancesByObjectType = async (objectTypeName: string, setter: (instances: any[]) => void) => {
    try {
      const response = await instanceApi.list(objectTypeName, { current: 1, size: 1000 });
      if (response.code === 200) {
        setter(response.data.records || []);
      }
    } catch (error) {
      console.error('加载实例列表失败:', error);
      setter([]);
    }
  };

  const getInstanceDisplayName = (instance: any): string => {
    return instance?.name || instance?.displayName || instance?.id || '';
  };

  const buildGraphData = (data: any[]): { nodes: any[]; links: any[] } => {
    const nodesMap = new Map<string, any>();
    const links: any[] = [];

    const ensureNode = (id: string, label?: string, type?: string) => {
      if (!id) return;
      if (!nodesMap.has(id)) {
        nodesMap.set(id, {
          id,
          label: label || id,
          type,
        });
      }
    };

    const processItem = (item: any) => {
      if (Array.isArray(item)) {
        item.forEach(processItem);
        return;
      }

      // 优先识别对象类型 / 实例级边
      const edgeKeys: Array<[string, string, string]> = [
        ['sourceObjectTypeId', 'targetObjectTypeId', 'objectType'],
        ['sourceInstanceId', 'targetInstanceId', 'instance'],
        ['source', 'target', 'generic'],
        ['from', 'to', 'generic'],
      ];

      let hasEdge = false;
      for (const [sk, tk, edgeType] of edgeKeys) {
        if (item && item[sk] && item[tk]) {
          const sid = String(item[sk]);
          const tid = String(item[tk]);

          // 如果是对象类型关系，优先使用对象类型的显示名称
          if (sk === 'sourceObjectTypeId' && tk === 'targetObjectTypeId') {
            const sourceObj = objectTypes.find(o => o.id === item[sk]);
            const targetObj = objectTypes.find(o => o.id === item[tk]);
            ensureNode(
              sid,
              sourceObj?.displayName || sourceObj?.name || sid,
              'objectType',
            );
            ensureNode(
              tid,
              targetObj?.displayName || targetObj?.name || tid,
              'objectType',
            );
          } else {
            ensureNode(sid, undefined, edgeType);
            ensureNode(tid, undefined, edgeType);
          }

          links.push({
            source: sid,
            target: tid,
            label: item.displayName || item.name || edgeType,
          });
          hasEdge = true;
          break;
        }
      }

      if (!hasEdge && item && item.id) {
        const id = String(item.id);
        ensureNode(id, item.displayName || item.name || id);
      }
    };

    (data || []).forEach(processItem);

    return {
      nodes: Array.from(nodesMap.values()),
      links,
    };
  };

  /**
   * 针对“可达对象类型”查询，基于起点和返回的对象类型列表构建对象类型级图
   * 节点：起点 + 所有可达对象类型
   * 边：这些节点之间所有存在的 LinkType（通过后端 linkTypeApi 按对象类型拉取）
   */
  const buildReachableGraphData = async (objectTypeName: string, reachableTypes: ObjectType[]) => {
    // 找到起点对象类型
    const root = objectTypes.find(o => o.name === objectTypeName)
      || reachableTypes.find(o => o.name === objectTypeName);

    const allTypesMap = new Map<string, ObjectType>();
    if (root) {
      allTypesMap.set(root.id, root);
    }
    (reachableTypes || []).forEach(t => {
      allTypesMap.set(t.id, t);
    });

    const allTypes = Array.from(allTypesMap.values());
    const typeIdSet = new Set(allTypes.map(t => t.id));

    // 拉取这些对象类型相关的所有链接类型
    const linkTypeResults = await Promise.all(
      allTypes.map(async (t) => {
        try {
          const res = await linkTypeApi.getByObjectTypeId(t.id);
          if (res.code === 200 && res.data) {
            return res.data as LinkType[];
          }
        } catch (e) {
          // 单个失败不影响整体
        }
        return [] as LinkType[];
      }),
    );

    const allLinkTypes: LinkType[] = ([] as LinkType[]).concat(...linkTypeResults);

    const edgeKeySet = new Set<string>();
    const links: any[] = [];

    allLinkTypes.forEach((lt) => {
      const sid = lt.sourceObjectTypeId;
      const tid = lt.targetObjectTypeId;
      if (!typeIdSet.has(sid) || !typeIdSet.has(tid)) {
        return;
      }
      const key = [sid, tid, lt.id].sort().join('|');
      if (edgeKeySet.has(key)) {
        return;
      }
      edgeKeySet.add(key);
      links.push({
        source: sid,
        target: tid,
        label: lt.displayName || lt.name,
      });
    });

    const nodes = allTypes.map(t => ({
      id: t.id,
      label: t.displayName || t.name,
      type: 'objectType',
    }));

    return { nodes, links };
  };

  // ==================== 对象类型关系查询处理 ====================
  const handleObjectTypeQuery = async (values: any) => {
    setLoading(true);
    try {
      const { queryType, objectTypeName, sourceObjectTypeName, targetObjectTypeName, depth, maxDepth } = values;

      if (queryType === 'link-types') {
        // 查询对象类型的所有链接类型
        const response = await queryApi.findLinkTypesByObjectType(objectTypeName);
        if (response.code === 200) {
          const list = response.data || [];
          setResults(list);
          setGraphData(buildGraphData(list));
          message.success(`找到 ${response.data?.length || 0} 个链接类型`);
        }
      } else if (queryType === 'path') {
        // 查询对象类型之间的关系路径
        const response = await queryApi.findObjectTypePath(
          sourceObjectTypeName,
          targetObjectTypeName,
          maxDepth || 5
        );
        if (response.code === 200) {
          const list = response.data || [];
          setResults(list);
          setGraphData(buildGraphData(list));
          message.success(`找到 ${response.data?.length || 0} 条路径`);
        }
      } else if (queryType === 'reachable') {
        // 查询对象类型的可达对象类型
        const response = await queryApi.findReachableObjectTypes(objectTypeName, depth || 2);
        if (response.code === 200) {
          const list = (response.data || []) as ObjectType[];
          setResults(list);
          // 基于起点和可达对象类型，构建包含边的对象类型级图
          const graph = await buildReachableGraphData(objectTypeName, list);
          setGraphData(graph);
          message.success(`找到 ${response.data?.length || 0} 个可达对象类型`);
        }
      }
    } catch (error: any) {
      message.error(error.message || '查询失败');
    } finally {
      setLoading(false);
    }
  };

  // ==================== 实例关系查询处理 ====================
  const handleInstanceQuery = async (values: any) => {
    setLoading(true);
    try {
      const { queryType, instanceId, linkTypeName, depth } = values;

      if (queryType === 'neighbors') {
        // 查询邻居节点
        const response = await queryApi.findNeighbors(instanceId, linkTypeName);
        if (response.code === 200) {
          const list = response.data || [];
          setResults(list);
          setGraphData(buildGraphData(list));
          message.success(`找到 ${response.data?.length || 0} 个邻居节点`);
        }
      } else if (queryType === 'related') {
        // 查询关联实例
        const response = await queryApi.findRelated(instanceId, depth || 2);
        if (response.code === 200) {
          const list = response.data || [];
          setResults(list);
          setGraphData(buildGraphData(list));
          message.success(`找到 ${response.data?.length || 0} 个关联实例`);
        }
      } else if (queryType === 'path') {
        // 查询路径
        const { sourceInstanceId, targetInstanceId, sourceObjectType, targetObjectType, linkTypeName, maxDepth } = values;
        if (!sourceObjectType || !targetObjectType) {
          message.error('路径查询需要指定源对象类型和目标对象类型');
          return;
        }
        const response = await queryApi.findPath(
          sourceObjectType,
          sourceInstanceId,
          targetObjectType,
          targetInstanceId,
          linkTypeName || '',
          maxDepth || 5
        );
        if (response.code === 200) {
          const list = response.data || [];
          setResults(list);
          setGraphData(buildGraphData(list));
          message.success(`找到 ${response.data?.length || 0} 条路径`);
        }
      }
    } catch (error: any) {
      message.error(error.message || '查询失败');
    } finally {
      setLoading(false);
    }
  };

  const handleClear = () => {
    form.resetFields();
    setResults([]);
    setSourceInstances([]);
    setTargetInstances([]);
    setNeighborInstances([]);
    setRelatedInstances([]);
    setGraphData({ nodes: [], links: [] });
    setZoom(1);
    setPan({ x: 0, y: 0 });
    setIsPanning(false);
    setLastPanPosition(null);
  };

  const getColumnTitle = (key: string): string => {
    const map: Record<string, string> = {
      id: 'ID',
      name: '名称',
      displayName: '显示名称',
      description: '描述',
      sourceObjectTypeId: '源对象类型ID',
      targetObjectTypeId: '目标对象类型ID',
      sourceInstanceId: '源实例ID',
      targetInstanceId: '目标实例ID',
      linkTypeId: '链接类型ID',
      cardinality: '关系基数',
      bidirectional: '双向',
      createdAt: '创建时间',
      updatedAt: '更新时间',
      objectTypeName: '对象类型名称',
      depth: '深度',
    };
    return map[key] || key;
  };

  const columns = results.length > 0 && typeof results[0] === 'object' && !Array.isArray(results[0])
    ? Object.keys(results[0])
        // 隐藏所有 ID 字段（id、xxxId）
        .filter(key => key.toLowerCase() !== 'id' && !key.endsWith('Id') && !key.endsWith('ID') && !key.endsWith('id'))
        .map(key => ({
          title: getColumnTitle(key),
          dataIndex: key,
          key: key,
          render: (value: any) => {
            if (value === null || value === undefined) return '-';
            if (typeof value === 'object') return JSON.stringify(value);
            if (typeof value === 'boolean') return value ? '是' : '否';
            return String(value);
          },
        }))
    : [];

  // 对象类型关系查询表单
  const renderObjectTypeQueryForm = () => (
    <Form form={form} layout="vertical" onFinish={handleObjectTypeQuery}>
      <Form.Item label="业务域">
        <Select
          placeholder="请选择业务域（可选）"
          allowClear
          value={selectedDomainId}
          onChange={(value) => {
            setSelectedDomainId(value);
            handleClear();
          }}
          showSearch
          optionFilterProp="label"
        >
          {domains.map(domain => (
            <Select.Option key={domain.id} value={domain.id} label={domain.displayName}>
              {domain.displayName} ({domain.code})
            </Select.Option>
          ))}
        </Select>
      </Form.Item>
      <Form.Item name="queryType" label="查询类型" rules={[{ required: true, message: '请选择查询类型' }]}>
        <Select placeholder="请选择查询类型" onChange={() => {
          form.resetFields(['objectTypeName', 'sourceObjectTypeName', 'targetObjectTypeName', 'depth', 'maxDepth']);
        }}>
          <Option value="link-types">查询对象类型的链接类型</Option>
          <Option value="path">查询对象类型关系路径</Option>
          <Option value="reachable">查询可达对象类型</Option>
        </Select>
      </Form.Item>

      <Form.Item noStyle shouldUpdate={(prevValues, currentValues) => prevValues.queryType !== currentValues.queryType}>
        {({ getFieldValue }) => {
          const queryType = getFieldValue('queryType');

          if (queryType === 'link-types') {
            return (
              <Form.Item name="objectTypeName" label="对象类型" rules={[{ required: true, message: '请选择对象类型' }]}>
                <Select placeholder="请选择对象类型" showSearch optionFilterProp="label">
                  {objectTypes.map(type => (
                    <Select.Option key={type.id} value={type.name} label={type.displayName}>
                      {type.displayName} ({type.name})
                    </Select.Option>
                  ))}
                </Select>
              </Form.Item>
            );
          }

          if (queryType === 'path') {
            return (
              <>
                <Form.Item name="sourceObjectTypeName" label="源对象类型" rules={[{ required: true, message: '请选择源对象类型' }]}>
                  <Select placeholder="请选择源对象类型" showSearch optionFilterProp="label">
                    {objectTypes.map(type => (
                      <Select.Option key={type.id} value={type.name} label={type.displayName}>
                        {type.displayName} ({type.name})
                      </Select.Option>
                    ))}
                  </Select>
                </Form.Item>
                <Form.Item name="targetObjectTypeName" label="目标对象类型" rules={[{ required: true, message: '请选择目标对象类型' }]}>
                  <Select placeholder="请选择目标对象类型" showSearch optionFilterProp="label">
                    {objectTypes.map(type => (
                      <Select.Option key={type.id} value={type.name} label={type.displayName}>
                        {type.displayName} ({type.name})
                      </Select.Option>
                    ))}
                  </Select>
                </Form.Item>
                <Form.Item name="maxDepth" label="最大深度" initialValue={5}>
                  <InputNumber min={1} max={10} placeholder="默认：5" style={{ width: '100%' }} />
                </Form.Item>
              </>
            );
          }

          if (queryType === 'reachable') {
            return (
              <>
                <Form.Item name="objectTypeName" label="对象类型" rules={[{ required: true, message: '请选择对象类型' }]}>
                  <Select placeholder="请选择对象类型" showSearch optionFilterProp="label">
                    {objectTypes.map(type => (
                      <Select.Option key={type.id} value={type.name} label={type.displayName}>
                        {type.displayName} ({type.name})
                      </Select.Option>
                    ))}
                  </Select>
                </Form.Item>
                <Form.Item name="depth" label="查询深度" initialValue={2}>
                  <InputNumber min={1} max={10} placeholder="默认：2" style={{ width: '100%' }} />
                </Form.Item>
              </>
            );
          }

          return null;
        }}
      </Form.Item>

      <Form.Item>
        <Space>
          <Button type="primary" htmlType="submit" icon={<SearchOutlined />} loading={loading}>
            查询
          </Button>
          <Button onClick={handleClear} icon={<ClearOutlined />}>
            清空
          </Button>
        </Space>
      </Form.Item>
    </Form>
  );

  // 实例关系查询表单
  const renderInstanceQueryForm = () => (
    <Form form={form} layout="vertical" onFinish={handleInstanceQuery}>
      <Form.Item label="业务域">
        <Select
          placeholder="请选择业务域（可选）"
          allowClear
          value={selectedDomainId}
          onChange={(value) => {
            setSelectedDomainId(value);
            handleClear();
          }}
          showSearch
          optionFilterProp="label"
        >
          {domains.map(domain => (
            <Select.Option key={domain.id} value={domain.id} label={domain.displayName}>
              {domain.displayName} ({domain.code})
            </Select.Option>
          ))}
        </Select>
      </Form.Item>
      <Form.Item name="queryType" label="查询类型" rules={[{ required: true, message: '请选择查询类型' }]}>
        <Select placeholder="请选择查询类型" onChange={() => {
          form.resetFields(['linkTypeName', 'instanceId', 'sourceInstanceId', 'targetInstanceId', 'depth', 'objectTypeName', 'sourceObjectType', 'targetObjectType', 'maxDepth']);
          setSourceInstances([]);
          setTargetInstances([]);
          setNeighborInstances([]);
          setRelatedInstances([]);
        }}>
          <Option value="neighbors">查询邻居节点</Option>
          <Option value="related">查询关联实例</Option>
          <Option value="path">查询路径</Option>
        </Select>
      </Form.Item>

      <Form.Item noStyle shouldUpdate={(prevValues, currentValues) => prevValues.queryType !== currentValues.queryType}>
        {({ getFieldValue }) => {
          const queryType = getFieldValue('queryType');

          if (queryType === 'neighbors') {
            return (
              <>
                <Form.Item
                  name="objectTypeName"
                  label="对象类型"
                  rules={[{ required: true, message: '请选择对象类型' }]}
                >
                  <Select
                    placeholder="请选择对象类型"
                    showSearch
                    optionFilterProp="label"
                    onChange={async (value) => {
                      form.setFieldsValue({ instanceId: undefined });
                      setNeighborInstances([]);
                      if (value) {
                        await loadInstancesByObjectType(value, setNeighborInstances);
                      }
                    }}
                  >
                    {objectTypes.map(type => (
                      <Select.Option key={type.id} value={type.name} label={type.displayName}>
                        {type.displayName} ({type.name})
                      </Select.Option>
                    ))}
                  </Select>
                </Form.Item>
                <Form.Item name="instanceId" label="实例" rules={[{ required: true, message: '请选择实例' }]}>
                  <Select
                    placeholder="请选择实例"
                    showSearch
                    filterOption={(input, option) =>
                      (option?.children as string)?.toLowerCase().includes(input.toLowerCase())
                    }
                    disabled={!form.getFieldValue('objectTypeName')}
                  >
                    {neighborInstances.map(instance => (
                      <Select.Option key={instance.id} value={instance.id}>
                        {getInstanceDisplayName(instance)}
                      </Select.Option>
                    ))}
                  </Select>
                </Form.Item>
                <Form.Item name="linkTypeName" label="链接类型名称">
                  <Input placeholder="可选，不填则查询所有链接类型" />
                </Form.Item>
              </>
            );
          }

          if (queryType === 'related') {
            return (
              <>
                <Form.Item
                  name="objectTypeName"
                  label="对象类型"
                  rules={[{ required: true, message: '请选择对象类型' }]}
                >
                  <Select
                    placeholder="请选择对象类型"
                    showSearch
                    optionFilterProp="label"
                    onChange={async (value) => {
                      form.setFieldsValue({ instanceId: undefined });
                      setRelatedInstances([]);
                      if (value) {
                        await loadInstancesByObjectType(value, setRelatedInstances);
                      }
                    }}
                  >
                    {objectTypes.map(type => (
                      <Select.Option key={type.id} value={type.name} label={type.displayName}>
                        {type.displayName} ({type.name})
                      </Select.Option>
                    ))}
                  </Select>
                </Form.Item>
                <Form.Item name="instanceId" label="实例" rules={[{ required: true, message: '请选择实例' }]}>
                  <Select
                    placeholder="请选择实例"
                    showSearch
                    filterOption={(input, option) =>
                      (option?.children as string)?.toLowerCase().includes(input.toLowerCase())
                    }
                    disabled={!form.getFieldValue('objectTypeName')}
                  >
                    {relatedInstances.map(instance => (
                      <Select.Option key={instance.id} value={instance.id}>
                        {getInstanceDisplayName(instance)}
                      </Select.Option>
                    ))}
                  </Select>
                </Form.Item>
                <Form.Item name="depth" label="查询深度" initialValue={2}>
                  <InputNumber min={1} max={10} placeholder="默认：2" style={{ width: '100%' }} />
                </Form.Item>
              </>
            );
          }

          if (queryType === 'path') {
            return (
              <>
                <Form.Item name="sourceObjectType" label="源对象类型" rules={[{ required: true, message: '请选择源对象类型' }]}>
                  <Select
                    placeholder="请选择源对象类型"
                    showSearch
                    optionFilterProp="label"
                    onChange={async (value) => {
                      form.setFieldsValue({ sourceInstanceId: undefined });
                      setSourceInstances([]);
                      if (value) {
                        await loadInstancesByObjectType(value, setSourceInstances);
                      }
                    }}
                  >
                    {objectTypes.map(type => (
                      <Select.Option key={type.id} value={type.name} label={type.displayName}>
                        {type.displayName} ({type.name})
                      </Select.Option>
                    ))}
                  </Select>
                </Form.Item>
                <Form.Item name="sourceInstanceId" label="源实例" rules={[{ required: true, message: '请选择源实例' }]}>
                  <Select
                    placeholder="请选择源实例"
                    showSearch
                    filterOption={(input, option) =>
                      (option?.children as string)?.toLowerCase().includes(input.toLowerCase())
                    }
                    disabled={!form.getFieldValue('sourceObjectType')}
                  >
                    {sourceInstances.map(instance => (
                      <Select.Option key={instance.id} value={instance.id}>
                        {getInstanceDisplayName(instance)}
                      </Select.Option>
                    ))}
                  </Select>
                </Form.Item>
                <Form.Item name="targetObjectType" label="目标对象类型" rules={[{ required: true, message: '请选择目标对象类型' }]}>
                  <Select
                    placeholder="请选择目标对象类型"
                    showSearch
                    optionFilterProp="label"
                    onChange={async (value) => {
                      form.setFieldsValue({ targetInstanceId: undefined });
                      setTargetInstances([]);
                      if (value) {
                        await loadInstancesByObjectType(value, setTargetInstances);
                      }
                    }}
                  >
                    {objectTypes.map(type => (
                      <Select.Option key={type.id} value={type.name} label={type.displayName}>
                        {type.displayName} ({type.name})
                      </Select.Option>
                    ))}
                  </Select>
                </Form.Item>
                <Form.Item name="targetInstanceId" label="目标实例" rules={[{ required: true, message: '请选择目标实例' }]}>
                  <Select
                    placeholder="请选择目标实例"
                    showSearch
                    filterOption={(input, option) =>
                      (option?.children as string)?.toLowerCase().includes(input.toLowerCase())
                    }
                    disabled={!form.getFieldValue('targetObjectType')}
                  >
                    {targetInstances.map(instance => (
                      <Select.Option key={instance.id} value={instance.id}>
                        {getInstanceDisplayName(instance)}
                      </Select.Option>
                    ))}
                  </Select>
                </Form.Item>
                <Form.Item name="linkTypeName" label="链接类型名称">
                  <Input placeholder="可选，不填则查询所有链接类型" />
                </Form.Item>
                <Form.Item name="maxDepth" label="最大深度" initialValue={5}>
                  <InputNumber min={1} max={10} placeholder="默认：5" style={{ width: '100%' }} />
                </Form.Item>
              </>
            );
          }

          return null;
        }}
      </Form.Item>

      <Form.Item>
        <Space>
          <Button type="primary" htmlType="submit" icon={<SearchOutlined />} loading={loading}>
            查询
          </Button>
          <Button onClick={handleClear} icon={<ClearOutlined />}>
            清空
          </Button>
        </Space>
      </Form.Item>
    </Form>
  );

  return (
    <div>
      <Title level={2} style={{ marginBottom: 24 }}>查询构建器</Title>

      <Tabs
        activeKey={activeTab}
        onChange={(key) => {
          setActiveTab(key);
          handleClear();
        }}
        items={[
          {
            key: 'object-type',
            label: '对象类型关系查询',
            children: (
              <Card title="对象类型关系查询（模型级别）" style={{ borderRadius: 8, marginBottom: 24 }}>
                <div style={{ marginBottom: 16, color: '#666' }}>
                  <p>查询对象类型之间的链接类型关系，用于了解数据模型的结构。</p>
                  <ul style={{ margin: '8px 0', paddingLeft: 20 }}>
                    <li>查询对象类型的链接类型：查看某个对象类型定义了哪些链接类型</li>
                    <li>查询对象类型关系路径：查看两个对象类型之间可以通过哪些链接类型连接</li>
                    <li>查询可达对象类型：查看某个对象类型可以连接到哪些其他对象类型</li>
                  </ul>
                </div>
                {renderObjectTypeQueryForm()}
              </Card>
            ),
          },
          {
            key: 'instance',
            label: '实例关系查询',
            children: (
              <Card title="实例关系查询（数据级别）" style={{ borderRadius: 8, marginBottom: 24 }}>
                <div style={{ marginBottom: 16, color: '#666' }}>
                  <p>查询实例之间的链接实例关系，用于查询实际数据之间的关系。</p>
                  <ul style={{ margin: '8px 0', paddingLeft: 20 }}>
                    <li>查询邻居节点：查看与某个实例直接关联的所有实例</li>
                    <li>查询关联实例：查看与某个实例在指定深度内关联的所有实例</li>
                    <li>查询路径：查看两个实例之间的所有可能路径</li>
                  </ul>
                </div>
                {renderInstanceQueryForm()}
              </Card>
            ),
          },
        ]}
      />

      {results.length > 0 && (
        <Card title="查询结果" style={{ borderRadius: 8 }}>
          <Tabs
            defaultActiveKey="graph"
            items={[
              {
                key: 'graph',
                label: '图形视图',
                children: (
              <div
                style={{
                  height: 500,
                  position: 'relative',
                  cursor: isPanning ? 'grabbing' : 'grab',
                  overflow: 'hidden',
                }}
                onWheel={(e) => {
                  e.preventDefault();
                  const delta = e.deltaY;
                  setZoom((prev) => {
                    const factor = delta > 0 ? 0.9 : 1.1;
                    const next = Math.min(3, Math.max(0.5, prev * factor));
                    return next;
                  });
                }}
                onMouseDown={(e) => {
                  e.preventDefault();
                  setIsPanning(true);
                  setLastPanPosition({ x: e.clientX, y: e.clientY });
                }}
                onMouseMove={(e) => {
                  if (!isPanning || !lastPanPosition) return;
                  const dx = e.clientX - lastPanPosition.x;
                  const dy = e.clientY - lastPanPosition.y;
                  setPan((prev) => ({
                    x: prev.x + dx / zoom,
                    y: prev.y + dy / zoom,
                  }));
                  setLastPanPosition({ x: e.clientX, y: e.clientY });
                }}
                onMouseUp={() => {
                  setIsPanning(false);
                  setLastPanPosition(null);
                }}
                onMouseLeave={() => {
                  setIsPanning(false);
                  setLastPanPosition(null);
                }}
              >
                <svg width="100%" height="100%" viewBox="-250 -250 500 500">
                  <g transform={`translate(${pan.x} ${pan.y}) scale(${zoom})`}>
                    {/* 边 */}
                    {graphData.links.map((link, idx) => {
                      const sourceIndex = graphData.nodes.findIndex(n => n.id === link.source);
                      const targetIndex = graphData.nodes.findIndex(n => n.id === link.target);
                      if (sourceIndex === -1 || targetIndex === -1) return null;
                      const angleS = (2 * Math.PI * sourceIndex) / graphData.nodes.length;
                      const angleT = (2 * Math.PI * targetIndex) / graphData.nodes.length;
                      const r = 180;
                      const sx = r * Math.cos(angleS);
                      const sy = r * Math.sin(angleS);
                      const tx = r * Math.cos(angleT);
                      const ty = r * Math.sin(angleT);
                      return (
                        <g key={idx}>
                          <line x1={sx} y1={sy} x2={tx} y2={ty} stroke="#999" strokeWidth={1.2} />
                          {link.label && (
                            <text
                              x={(sx + tx) / 2}
                              y={(sy + ty) / 2}
                              fill="#666"
                              fontSize={10}
                              textAnchor="middle"
                            >
                              {link.label}
                            </text>
                          )}
                        </g>
                      );
                    })}
                    {/* 点 */}
                    {graphData.nodes.map((node, index) => {
                      const angle = (2 * Math.PI * index) / graphData.nodes.length;
                      const r = 180;
                      const x = r * Math.cos(angle);
                      const y = r * Math.sin(angle);
                      const color = node.type === 'objectType'
                        ? '#1677ff'
                        : node.type === 'instance'
                        ? '#52c41a'
                        : '#faad14';
                      return (
                        <g key={node.id}>
                          <circle cx={x} cy={y} r={10} fill={color} stroke="#fff" strokeWidth={1.5} />
                          <text
                            x={x}
                            y={y - 14}
                            fill="#333"
                            fontSize={10}
                            textAnchor="middle"
                          >
                            {node.label || node.id}
                          </text>
                        </g>
                      );
                    })}
                  </g>
                </svg>
              </div>
                ),
              },
              {
                key: 'table',
                label: '表格视图',
                children: (
              <div
                style={{
                  maxHeight: 'calc(100vh - 320px)',
                  overflow: 'auto',
                }}
              >
                {Array.isArray(results[0]) && results[0].length > 0 && typeof results[0][0] === 'object' ? (
                  <div>
                    <p>找到 {results.length} 条路径：</p>
                    {results.map((path, index) => (
                      <Card key={index} size="small" style={{ marginBottom: 16 }}>
                        <div>路径 {index + 1}:</div>
                        <pre>{JSON.stringify(path, null, 2)}</pre>
                      </Card>
                    ))}
                  </div>
                ) : (
                  <Table
                    dataSource={results.map((item, index) => ({ ...item, key: index }))}
                    columns={columns}
                    pagination={false}
                    scroll={{ x: 'max-content' }}
                  />
                )}
              </div>
                ),
              },
            ]}
          />
        </Card>
      )}
    </div>
  );
};

export default QueryBuilder;
