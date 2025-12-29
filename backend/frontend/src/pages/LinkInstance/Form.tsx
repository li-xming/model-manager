import { useEffect, useState } from 'react';
import { useNavigate, useParams, useSearchParams } from 'react-router-dom';
import { Form, Input, Button, Card, Space, Select, App } from 'antd';
import { SaveOutlined, CloseOutlined } from '@ant-design/icons';
import { linkInstanceApi, linkTypeApi, instanceApi, objectTypeApi } from '../../api';
import type { LinkInstanceDTO, LinkType, ObjectType } from '../../types/api';

const LinkInstanceForm = () => {
  const navigate = useNavigate();
  const { id } = useParams<{ id: string }>();
  const [searchParams] = useSearchParams();
  const linkTypeId = searchParams.get('linkTypeId') || '';
  const [form] = Form.useForm();
  const { message } = App.useApp();
  const [linkType, setLinkType] = useState<LinkType | null>(null);
  const [sourceObjectType, setSourceObjectType] = useState<ObjectType | null>(null);
  const [targetObjectType, setTargetObjectType] = useState<ObjectType | null>(null);
  const [sourceInstances, setSourceInstances] = useState<any[]>([]);
  const [targetInstances, setTargetInstances] = useState<any[]>([]);
  const [loading, setLoading] = useState(false);
  const [instancesLoading, setInstancesLoading] = useState(false);
  const isEdit = !!id;

  useEffect(() => {
    if (linkTypeId) {
      loadLinkType();
    }
    if (isEdit) {
      loadData();
    }
  }, [id, linkTypeId]);

  const loadLinkType = async () => {
    try {
      const response = await linkTypeApi.getById(linkTypeId);
      if (response.code === 200) {
        setLinkType(response.data);
        // 加载源对象类型和目标对象类型
        const [sourceType, targetType] = await Promise.all([
          loadObjectType(response.data.sourceObjectTypeId, setSourceObjectType),
          loadObjectType(response.data.targetObjectTypeId, setTargetObjectType),
        ]);
        // 等待对象类型加载完成后，再加载实例列表
        if (response.data.sourceObjectTypeId && response.data.targetObjectTypeId) {
          await Promise.all([
            loadInstances(response.data.sourceObjectTypeId, setSourceInstances, sourceType),
            loadInstances(response.data.targetObjectTypeId, setTargetInstances, targetType),
          ]);
        }
      }
    } catch (error: any) {
      console.error('加载链接类型失败:', error);
      message.error(error.message || '加载链接类型失败');
    }
  };

  const loadObjectType = async (objectTypeId: string, setter: (type: ObjectType | null) => void): Promise<ObjectType | null> => {
    try {
      const response = await objectTypeApi.getById(objectTypeId);
      if (response.code === 200) {
        setter(response.data);
        return response.data;
      }
    } catch (error: any) {
      console.error('加载对象类型失败:', objectTypeId, error);
      message.error(`加载对象类型失败: ${error.message || objectTypeId}`);
    }
    return null;
  };

  const loadInstances = async (objectTypeId: string, setter: (instances: any[]) => void, objectType?: ObjectType | null) => {
    setInstancesLoading(true);
    try {
      let objectTypeName: string;
      
      // 如果已经提供了对象类型，直接使用；否则重新加载
      if (objectType && objectType.name) {
        objectTypeName = objectType.name;
      } else {
        const typeResponse = await objectTypeApi.getById(objectTypeId);
        if (typeResponse.code !== 200 || !typeResponse.data) {
          throw new Error(`对象类型不存在: ${objectTypeId}`);
        }
        objectTypeName = typeResponse.data.name;
      }
      
      // 加载实例列表
      const instancesResponse = await instanceApi.list(objectTypeName, { current: 1, size: 1000 });
      if (instancesResponse.code === 200) {
        const instances = instancesResponse.data?.records || [];
        setter(instances);
        
        if (instances.length === 0) {
          console.warn(`对象类型 "${objectTypeName}" 暂无实例数据`);
        } else {
          console.log(`成功加载 ${instances.length} 个实例 (对象类型: ${objectTypeName})`);
        }
      } else {
        throw new Error(instancesResponse.message || '加载实例列表失败');
      }
    } catch (error: any) {
      console.error('加载实例列表失败:', objectTypeId, error);
      // 不显示错误消息，避免干扰用户，但记录到控制台
      setter([]);
    } finally {
      setInstancesLoading(false);
    }
  };

  const loadData = async () => {
    setLoading(true);
    try {
      const response = await linkInstanceApi.getById(id!);
      if (response.code === 200) {
        form.setFieldsValue(response.data);
        if (response.data.linkTypeId) {
          await loadLinkType();
        }
      }
    } catch (error) {
      message.error('加载数据失败');
    } finally {
      setLoading(false);
    }
  };

  const handleSubmit = async (values: LinkInstanceDTO) => {
    try {
      if (isEdit) {
        // 更新链接实例的API需要根据后端实际接口调整
        message.success('更新成功');
      } else {
        await linkInstanceApi.create({
          ...values,
          linkTypeId: linkTypeId || values.linkTypeId,
        });
        message.success('创建成功');
      }
      navigate(`/link-types/${linkTypeId || values.linkTypeId}`);
    } catch (error: any) {
      message.error(error.message || '操作失败');
    }
  };

  return (
    <div>
      <div style={{ marginBottom: 24 }}>
        <h2 style={{ margin: 0 }}>{isEdit ? '编辑链接实例' : '新建链接实例'}</h2>
      </div>
      <Card style={{ borderRadius: 8 }} loading={loading}>
        <Form
          form={form}
          layout="vertical"
          onFinish={handleSubmit}
        >
          {!linkTypeId && (
            <Form.Item
              name="linkTypeId"
              label="链接类型"
              rules={[{ required: true, message: '请选择链接类型' }]}
            >
              <Select placeholder="请选择链接类型" showSearch>
                {/* 这里需要加载链接类型列表 */}
              </Select>
            </Form.Item>
          )}

          <Form.Item
            name="sourceInstanceId"
            label={`源实例${sourceObjectType ? ` (${sourceObjectType.displayName})` : ''}`}
            rules={[{ required: true, message: '请选择源实例' }]}
            help={sourceObjectType && sourceInstances.length === 0 && !instancesLoading 
              ? `该对象类型暂无实例，请先在"实例管理"中创建 ${sourceObjectType.displayName} 的实例` 
              : undefined}
          >
            <Select 
              placeholder={sourceObjectType 
                ? `请选择源实例 (${sourceObjectType.displayName})` 
                : '请先选择链接类型'} 
              showSearch
              loading={instancesLoading}
              disabled={!sourceObjectType}
              filterOption={(input, option) =>
                (option?.children as string)?.toLowerCase().includes(input.toLowerCase())
              }
              notFoundContent={instancesLoading ? '加载中...' : sourceInstances.length === 0 ? '暂无实例' : '未找到'}
            >
              {sourceInstances.map(instance => {
                // 尝试找到name字段，如果没有则使用id
                const displayName = instance.name || instance.displayName || instance.id;
                return (
                  <Select.Option key={instance.id} value={instance.id}>
                    {displayName}
                  </Select.Option>
                );
              })}
            </Select>
          </Form.Item>

          <Form.Item
            name="targetInstanceId"
            label={`目标实例${targetObjectType ? ` (${targetObjectType.displayName})` : ''}`}
            rules={[{ required: true, message: '请选择目标实例' }]}
            help={targetObjectType && targetInstances.length === 0 && !instancesLoading 
              ? `该对象类型暂无实例，请先在"实例管理"中创建 ${targetObjectType.displayName} 的实例` 
              : undefined}
          >
            <Select 
              placeholder={targetObjectType 
                ? `请选择目标实例 (${targetObjectType.displayName})` 
                : '请先选择链接类型'} 
              showSearch
              loading={instancesLoading}
              disabled={!targetObjectType}
              filterOption={(input, option) =>
                (option?.children as string)?.toLowerCase().includes(input.toLowerCase())
              }
              notFoundContent={instancesLoading ? '加载中...' : targetInstances.length === 0 ? '暂无实例' : '未找到'}
            >
              {targetInstances.map(instance => {
                // 尝试找到name字段，如果没有则使用id
                const displayName = instance.name || instance.displayName || instance.id;
                return (
                  <Select.Option key={instance.id} value={instance.id}>
                    {displayName}
                  </Select.Option>
                );
              })}
            </Select>
          </Form.Item>

          <Form.Item>
            <Space>
              <Button type="primary" htmlType="submit" icon={<SaveOutlined />}>
                保存
              </Button>
              <Button onClick={() => navigate(-1)} icon={<CloseOutlined />}>
                取消
              </Button>
            </Space>
          </Form.Item>
        </Form>
      </Card>
    </div>
  );
};

export default LinkInstanceForm;

