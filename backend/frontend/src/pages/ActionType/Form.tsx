import { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { Form, Input, Button, Card, Space, Select, Switch, InputNumber, App } from 'antd';
import { SaveOutlined, CloseOutlined } from '@ant-design/icons';
import { actionTypeApi, objectTypeApi, functionApi, businessDomainApi } from '../../api';
import type { ActionTypeDTO, ObjectType, Function, BusinessDomain } from '../../types/api';

const { TextArea } = Input;

const ActionTypeForm = () => {
  const navigate = useNavigate();
  const { id } = useParams<{ id: string }>();
  const [form] = Form.useForm();
  const [objectTypes, setObjectTypes] = useState<ObjectType[]>([]);
  const [functions, setFunctions] = useState<Function[]>([]);
  const [domains, setDomains] = useState<BusinessDomain[]>([]);
  const [metaActionTypes, setMetaActionTypes] = useState<MetaActionType[]>([]);
  const [loading, setLoading] = useState(false);
  const isEdit = !!id;
  const { message } = App.useApp();

  useEffect(() => {
    loadObjectTypes();
    loadFunctions();
    loadDomains();
    loadMetaActionTypes();
    if (isEdit) {
      loadData();
    }
  }, [id]);

  const loadMetaActionTypes = async () => {
    try {
      const response = await metaModelApi.getAllMetaActionTypes();
      if (response.code === 200) {
        setMetaActionTypes(response.data || []);
      }
    } catch (error) {
      console.error('加载元操作类型失败:', error);
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

  const loadFunctions = async () => {
    try {
      const response = await functionApi.list({ current: 1, size: 1000 });
      if (response.code === 200) {
        setFunctions(response.data.records || []);
      }
    } catch (error) {
      // 如果函数列表加载失败，不影响继续操作
      console.error('加载函数列表失败:', error);
    }
  };

  const loadData = async () => {
    setLoading(true);
    try {
      const response = await actionTypeApi.getById(id!);
      if (response.code === 200) {
        const data = response.data;
        // 处理JSON字段
        form.setFieldsValue({
          ...data,
          inputSchema: data.inputSchema ? JSON.stringify(data.inputSchema, null, 2) : '',
          outputSchema: data.outputSchema ? JSON.stringify(data.outputSchema, null, 2) : '',
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
      const submitData: ActionTypeDTO = {
        ...values,
        inputSchema: values.inputSchema ? JSON.parse(values.inputSchema) : undefined,
        outputSchema: values.outputSchema ? JSON.parse(values.outputSchema) : undefined,
        requiresApproval: values.requiresApproval || false,
      };

      if (isEdit) {
        await actionTypeApi.update(id!, submitData);
        message.success('更新成功');
      } else {
        await actionTypeApi.create(submitData);
        message.success('创建成功');
      }
      navigate('/action-types');
    } catch (error: any) {
      if (error.message?.includes('JSON')) {
        message.error('JSON格式错误，请检查输入输出参数Schema的格式');
      } else {
        message.error(error.message || '操作失败');
      }
    }
  };

  return (
    <div>
      <div style={{ marginBottom: 24 }}>
        <h2 style={{ margin: 0 }}>{isEdit ? '编辑操作类型' : '新建操作类型'}</h2>
      </div>
      <Card style={{ borderRadius: 8 }} loading={loading}>
        <Form
          form={form}
          layout="vertical"
          onFinish={handleSubmit}
          initialValues={{
            requiresApproval: false,
            metaActionTypeId: 'META_ACTION_TYPE',  // 默认元操作类型
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
            <Input placeholder="请输入操作类型名称" disabled={isEdit} />
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
            name="handlerFunction"
            label="处理函数"
            tooltip="选择用于处理此操作类型的函数"
          >
            <Select placeholder="请选择处理函数（可选）" showSearch allowClear>
              {functions.map(func => (
                <Select.Option key={func.id} value={func.name}>
                  {func.displayName} ({func.name})
                </Select.Option>
              ))}
            </Select>
          </Form.Item>

          <Form.Item
            name="requiresApproval"
            label="需要审批"
            valuePropName="checked"
          >
            <Switch />
          </Form.Item>

          <Form.Item
            name="inputSchema"
            label="输入参数Schema (JSON)"
            tooltip="定义操作类型所需的输入参数，JSON格式"
          >
            <TextArea rows={6} placeholder='例如: {"param1": "string", "param2": "number"}' />
          </Form.Item>

          <Form.Item
            name="outputSchema"
            label="输出参数Schema (JSON)"
            tooltip="定义操作类型的输出结果，JSON格式"
          >
            <TextArea rows={6} placeholder='例如: {"result": "string"}' />
          </Form.Item>

          <Form.Item
            name="domainId"
            label="业务域"
            tooltip="选择此操作类型所属的业务域（可选）"
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
            name="metaActionTypeId"
            label="元操作类型"
            tooltip="选择该操作类型基于的元操作类型（默认为'META_ACTION_TYPE'）"
          >
            <Select
              placeholder="请选择元操作类型（可选，默认为'META_ACTION_TYPE'）"
              allowClear
              showSearch
              filterOption={(input, option) =>
                (option?.label ?? '').toLowerCase().includes(input.toLowerCase())
              }
              options={metaActionTypes.map(mat => ({ 
                value: mat.id, 
                label: `${mat.displayName || mat.name}${mat.isBuiltin ? ' (内置)' : ''}` 
              }))}
            />
          </Form.Item>

          <Form.Item>
            <Space>
              <Button type="primary" htmlType="submit" icon={<SaveOutlined />}>
                保存
              </Button>
              <Button onClick={() => navigate('/action-types')} icon={<CloseOutlined />}>
                取消
              </Button>
            </Space>
          </Form.Item>
        </Form>
      </Card>
    </div>
  );
};

export default ActionTypeForm;

