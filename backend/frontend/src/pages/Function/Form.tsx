import { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { Form, Input, Button, Card, Space, Select, App } from 'antd';
import { SaveOutlined, CloseOutlined } from '@ant-design/icons';
import { functionApi, businessDomainApi } from '../../api';
import type { FunctionDTO, BusinessDomain } from '../../types/api';

const { TextArea } = Input;

const returnTypes = ['STRING', 'INTEGER', 'LONG', 'DOUBLE', 'BOOLEAN', 'DATE', 'TIMESTAMP', 'UUID', 'TEXT', 'JSON', 'OBJECT', 'ARRAY', 'VOID'];

const FunctionForm = () => {
  const navigate = useNavigate();
  const { id } = useParams<{ id: string }>();
  const [form] = Form.useForm();
  const [domains, setDomains] = useState<BusinessDomain[]>([]);
  const [loading, setLoading] = useState(false);
  const isEdit = !!id;
  const { message } = App.useApp();

  useEffect(() => {
    loadDomains();
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

  const loadData = async () => {
    setLoading(true);
    try {
      const response = await functionApi.getById(id!);
      if (response.code === 200) {
        const data = response.data;
        // 处理JSON字段
        form.setFieldsValue({
          ...data,
          inputSchema: data.inputSchema ? JSON.stringify(data.inputSchema, null, 2) : '',
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
      const submitData: FunctionDTO = {
        ...values,
        inputSchema: values.inputSchema ? JSON.parse(values.inputSchema) : undefined,
        version: values.version || '1.0.0',
      };

      if (isEdit) {
        await functionApi.update(id!, submitData);
        message.success('更新成功');
      } else {
        await functionApi.create(submitData);
        message.success('创建成功');
      }
      navigate('/functions');
    } catch (error: any) {
      if (error.message?.includes('JSON')) {
        message.error('JSON格式错误，请检查输入参数Schema的格式');
      } else {
        message.error(error.message || '操作失败');
      }
    }
  };

  return (
    <div>
      <div style={{ marginBottom: 24 }}>
        <h2 style={{ margin: 0 }}>{isEdit ? '编辑函数' : '新建函数'}</h2>
      </div>
      <Card style={{ borderRadius: 8 }} loading={loading}>
        <Form
          form={form}
          layout="vertical"
          onFinish={handleSubmit}
          initialValues={{
            returnType: 'STRING',
            version: '1.0.0',
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
            <Input placeholder="请输入函数名称" disabled={isEdit} />
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
            <TextArea rows={3} placeholder="请输入描述" />
          </Form.Item>

          <Form.Item
            name="returnType"
            label="返回类型"
            rules={[{ required: true, message: '请选择返回类型' }]}
          >
            <Select placeholder="请选择返回类型">
              {returnTypes.map(type => (
                <Select.Option key={type} value={type}>{type}</Select.Option>
              ))}
            </Select>
          </Form.Item>

          <Form.Item
            name="version"
            label="版本号"
            rules={[{ required: true, message: '请输入版本号' }]}
          >
            <Input placeholder="例如：1.0.0" />
          </Form.Item>

          <Form.Item
            name="inputSchema"
            label="输入参数Schema (JSON)"
            tooltip="定义函数的输入参数，JSON格式"
          >
            <TextArea rows={6} placeholder='例如: {"param1": "string", "param2": "number"}' />
          </Form.Item>

          <Form.Item
            name="code"
            label="函数代码"
            rules={[{ required: true, message: '请输入函数代码' }]}
            tooltip="函数的实现代码"
          >
            <TextArea rows={12} placeholder="请输入函数代码" />
          </Form.Item>

          <Form.Item
            name="domainId"
            label="业务域"
            tooltip="选择此函数所属的业务域（可选）"
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
              <Button onClick={() => navigate('/functions')} icon={<CloseOutlined />}>
                取消
              </Button>
            </Space>
          </Form.Item>
        </Form>
      </Card>
    </div>
  );
};

export default FunctionForm;

