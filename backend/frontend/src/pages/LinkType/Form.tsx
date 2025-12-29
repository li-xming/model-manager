import { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { Form, Input, Button, Card, Space, Select, Switch, App } from 'antd';
import { SaveOutlined, CloseOutlined } from '@ant-design/icons';
import { linkTypeApi, objectTypeApi, businessDomainApi, metaModelApi } from '../../api';
import type { LinkTypeDTO, ObjectType, BusinessDomain, MetaLinkType } from '../../types/api';

const { TextArea } = Input;

const LinkTypeForm = () => {
  const navigate = useNavigate();
  const { id } = useParams<{ id: string }>();
  const [form] = Form.useForm();
  const [objectTypes, setObjectTypes] = useState<ObjectType[]>([]);
  const [domains, setDomains] = useState<BusinessDomain[]>([]);
  const [metaLinkTypes, setMetaLinkTypes] = useState<MetaLinkType[]>([]);
  const [loading, setLoading] = useState(false);
  const isEdit = !!id;
  const { message } = App.useApp();

  useEffect(() => {
    loadObjectTypes();
    loadDomains();
    loadMetaLinkTypes();
    if (isEdit) {
      loadData();
    }
  }, [id]);

  const loadMetaLinkTypes = async () => {
    try {
      const response = await metaModelApi.getAllMetaLinkTypes();
      if (response.code === 200) {
        setMetaLinkTypes(response.data || []);
      }
    } catch (error) {
      console.error('加载元关系类型失败:', error);
    }
  };

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
      const response = await objectTypeApi.list({ current: 1, size: 1000 });
      if (response.code === 200) {
        setObjectTypes(response.data.records || []);
      }
    } catch (error) {
      message.error('加载对象类型失败');
    }
  };

  const loadData = async () => {
    setLoading(true);
    try {
      const response = await linkTypeApi.getById(id!);
      if (response.code === 200) {
        form.setFieldsValue(response.data);
      }
    } catch (error) {
      message.error('加载数据失败');
    } finally {
      setLoading(false);
    }
  };

  const handleSubmit = async (values: LinkTypeDTO) => {
    try {
      let linkTypeId: string;
      if (isEdit) {
        await linkTypeApi.update(id!, values);
        message.success('更新成功');
        linkTypeId = id!;
      } else {
        const response = await linkTypeApi.create(values);
        if (response.code === 200 && response.data) {
          message.success('创建成功');
          linkTypeId = response.data.id;
        } else {
          message.error('创建失败，无法获取链接类型ID');
          return;
        }
      }
      // 跳转到详情页
      navigate(`/link-types/${linkTypeId}`);
    } catch (error: any) {
      message.error(error.message || '操作失败');
    }
  };

  return (
    <div>
      <div style={{ marginBottom: 24 }}>
        <h2 style={{ margin: 0 }}>{isEdit ? '编辑链接类型' : '新建链接类型'}</h2>
      </div>
      <Card style={{ borderRadius: 8 }} loading={loading}>
        <Form
          form={form}
          layout="vertical"
          onFinish={handleSubmit}
          initialValues={{
            bidirectional: false,
            cardinality: 'MANY_TO_MANY',
            metaLinkTypeId: 'META_LINK_TYPE',  // 默认元关系类型
          }}
        >
          <Form.Item
            name="name"
            label="名称"
            rules={[
              { required: true, message: '请输入名称' },
              { pattern: /^[a-zA-Z][a-zA-Z0-9_]*$/, message: '名称必须以字母开头，只能包含字母、数字和下划线' },
            ]}
          >
            <Input placeholder="请输入链接类型名称" disabled={isEdit} />
          </Form.Item>

          <Form.Item
            name="displayName"
            label="显示名称"
            rules={[{ required: true, message: '请输入显示名称' }]}
          >
            <Input placeholder="请输入显示名称" />
          </Form.Item>

          <Form.Item
            name="description"
            label="描述"
          >
            <TextArea rows={4} placeholder="请输入描述" />
          </Form.Item>

          <Form.Item
            name="bidirectional"
            label="双向关系"
            valuePropName="checked"
            initialValue={false}
          >
            <Switch />
          </Form.Item>

          <Form.Item
            name="sourceObjectTypeId"
            label="源对象类型"
            rules={[{ required: true, message: '请选择源对象类型' }]}
          >
            <Select placeholder="请选择源对象类型" showSearch optionFilterProp="label">
              {objectTypes.map(type => (
                <Select.Option key={type.id} value={type.id} label={type.displayName}>
                  {type.displayName} ({type.name})
                </Select.Option>
              ))}
            </Select>
          </Form.Item>

          <Form.Item
            name="targetObjectTypeId"
            label="目标对象类型"
            rules={[{ required: true, message: '请选择目标对象类型' }]}
          >
            <Select placeholder="请选择目标对象类型" showSearch optionFilterProp="label">
              {objectTypes.map(type => (
                <Select.Option key={type.id} value={type.id} label={type.displayName}>
                  {type.displayName} ({type.name})
                </Select.Option>
              ))}
            </Select>
          </Form.Item>

          <Form.Item
            name="cardinality"
            label="关系基数"
            rules={[{ required: true, message: '请选择关系基数' }]}
          >
            <Select placeholder="请选择关系基数">
              <Select.Option value="ONE_TO_ONE">一对一 (ONE_TO_ONE)</Select.Option>
              <Select.Option value="ONE_TO_MANY">一对多 (ONE_TO_MANY)</Select.Option>
              <Select.Option value="MANY_TO_ONE">多对一 (MANY_TO_ONE)</Select.Option>
              <Select.Option value="MANY_TO_MANY">多对多 (MANY_TO_MANY)</Select.Option>
            </Select>
          </Form.Item>

          <Form.Item
            name="domainId"
            label="业务域"
            tooltip="选择此链接类型所属的业务域（可选）"
          >
            <Select placeholder="请选择业务域（可选）" allowClear showSearch optionFilterProp="label">
              {domains.map(domain => (
                <Select.Option key={domain.id} value={domain.id} label={domain.displayName}>
                  {domain.displayName} ({domain.code})
                </Select.Option>
              ))}
            </Select>
          </Form.Item>

          <Form.Item
            name="metaLinkTypeId"
            label="元关系类型"
            tooltip="选择该链接类型基于的元关系类型（默认为'META_LINK_TYPE'）"
          >
            <Select
              placeholder="请选择元关系类型（可选，默认为'META_LINK_TYPE'）"
              allowClear
              showSearch
              filterOption={(input, option) =>
                (option?.label ?? '').toLowerCase().includes(input.toLowerCase())
              }
              options={metaLinkTypes.map(mlt => ({ 
                value: mlt.id, 
                label: `${mlt.displayName || mlt.name}${mlt.isBuiltin ? ' (内置)' : ''}` 
              }))}
            />
          </Form.Item>

          <Form.Item>
            <Space>
              <Button type="primary" htmlType="submit" icon={<SaveOutlined />}>
                保存
              </Button>
              <Button onClick={() => navigate('/link-types')} icon={<CloseOutlined />}>
                取消
              </Button>
            </Space>
          </Form.Item>
        </Form>
      </Card>
    </div>
  );
};

export default LinkTypeForm;

