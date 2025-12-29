import { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { Form, Input, Button, Card, Space, Select, App } from 'antd';
import { SaveOutlined, CloseOutlined } from '@ant-design/icons';
import { interfaceApi, businessDomainApi, actionTypeApi } from '../../api';
import type { InterfaceDTO, BusinessDomain, ActionType } from '../../types/api';

const { TextArea } = Input;

const InterfaceForm = () => {
  const navigate = useNavigate();
  const { id } = useParams<{ id: string }>();
  const [form] = Form.useForm();
  const [domains, setDomains] = useState<BusinessDomain[]>([]);
  const [actionTypes, setActionTypes] = useState<ActionType[]>([]);
  const [loading, setLoading] = useState(false);
  const isEdit = !!id;
  const { message } = App.useApp();

  useEffect(() => {
    loadDomains();
    loadActionTypes();
    if (isEdit) {
      loadData();
    }
  }, [id]);

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

  const loadActionTypes = async () => {
    try {
      const response = await actionTypeApi.list({ current: 1, size: 1000 });
      if (response.code === 200) {
        setActionTypes(response.data.records || []);
      }
    } catch (error) {
      console.error('加载操作类型失败:', error);
    }
  };

  const loadData = async () => {
    setLoading(true);
    try {
      const response = await interfaceApi.getById(id!);
      if (response.code === 200) {
        const data = response.data;
        // 处理JSON字段
        form.setFieldsValue({
          ...data,
          requiredProperties: data.requiredProperties ? JSON.stringify(data.requiredProperties, null, 2) : '',
        });
      }
    } catch (error) {
      message.error('加载数据失败');
    } finally {
      setLoading(false);
    }
  };

  const handleSubmit = async (values: any) => {
    try {
      // 处理JSON字符串转换为对象
      const submitData: InterfaceDTO = {
        ...values,
        requiredProperties: values.requiredProperties ? JSON.parse(values.requiredProperties) : undefined,
      };

      if (isEdit) {
        await interfaceApi.update(id!, submitData);
        message.success('更新成功');
      } else {
        await interfaceApi.create(submitData);
        message.success('创建成功');
      }
      navigate('/interfaces');
    } catch (error: any) {
      if (error.message?.includes('JSON')) {
        message.error('JSON格式错误，请检查必需属性的格式');
      } else {
        message.error(error.message || '操作失败');
      }
    }
  };

  return (
    <div>
      <div style={{ marginBottom: 24 }}>
        <h2 style={{ margin: 0 }}>{isEdit ? '编辑接口' : '新建接口'}</h2>
      </div>
      <Card style={{ borderRadius: 8 }} loading={loading}>
        <Form
          form={form}
          layout="vertical"
          onFinish={handleSubmit}
        >
          <Form.Item
            name="name"
            label="名称"
            rules={[
              { required: true, message: '请输入名称' },
              { pattern: /^[a-zA-Z][a-zA-Z0-9_]*$/, message: '名称必须以字母开头，只能包含字母、数字和下划线' },
            ]}
          >
            <Input placeholder="请输入接口名称" disabled={isEdit} />
          </Form.Item>

          <Form.Item
            name="displayName"
            label="显示名称"
            rules={[{ required: true, message: '请输入显示名称' }]}
          >
            <Input placeholder="请输入显示名称" />
          </Form.Item>

          <Form.Item
            name="method"
            label="HTTP 方法"
            rules={[{ required: true, message: '请选择HTTP方法' }]}
          >
            <Select placeholder="请选择HTTP方法">
              <Select.Option value="GET">GET</Select.Option>
              <Select.Option value="POST">POST</Select.Option>
              <Select.Option value="PUT">PUT</Select.Option>
              <Select.Option value="DELETE">DELETE</Select.Option>
            </Select>
          </Form.Item>

          <Form.Item
            name="path"
            label="URL 路径"
            rules={[{ required: true, message: '请输入URL路径' }]}
          >
            <Input placeholder="/api/xxx/yyy" />
          </Form.Item>

          <Form.Item
            name="description"
            label="描述"
          >
            <TextArea rows={4} placeholder="请输入描述" />
          </Form.Item>

          <Form.Item
            name="actionTypeId"
            label="绑定操作类型"
            tooltip="选择当外部调用此接口时要触发的操作类型（可选）"
          >
            <Select placeholder="请选择操作类型（可选）" allowClear showSearch optionFilterProp="label">
              {actionTypes.map(action => (
                <Select.Option
                  key={action.id}
                  value={action.id}
                  label={action.displayName}
                >
                  {action.displayName} ({action.name})
                </Select.Option>
              ))}
            </Select>
          </Form.Item>

          <Form.Item
            name="requiredProperties"
            label="必需属性 (JSON)"
            tooltip="定义实现此接口的对象类型必须拥有的属性，JSON格式"
          >
            <TextArea rows={6} placeholder='例如: {"name": "string", "id": "string"}' />
          </Form.Item>

          <Form.Item
            name="domainId"
            label="业务域"
            tooltip="选择此接口所属的业务域（可选）"
          >
            <Select placeholder="请选择业务域（可选）" allowClear showSearch optionFilterProp="label">
              {domains.map(domain => (
                <Select.Option key={domain.id} value={domain.id} label={domain.displayName}>
                  {domain.displayName} ({domain.code})
                </Select.Option>
              ))}
            </Select>
          </Form.Item>

          <Form.Item>
            <Space>
              <Button type="primary" htmlType="submit" icon={<SaveOutlined />}>
                保存
              </Button>
              <Button onClick={() => navigate('/interfaces')} icon={<CloseOutlined />}>
                取消
              </Button>
            </Space>
          </Form.Item>
        </Form>
      </Card>
    </div>
  );
};

export default InterfaceForm;

