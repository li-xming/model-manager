import { useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { Form, Input, Button, Card, Space, App } from 'antd';
import { SaveOutlined, CloseOutlined } from '@ant-design/icons';
import { businessDomainApi } from '../../api';
import type { BusinessDomain } from '../../types/api';

const { TextArea } = Input;

const DomainForm = () => {
  const navigate = useNavigate();
  const { id } = useParams<{ id: string }>();
  const [form] = Form.useForm();
  const { message } = App.useApp();
  const isEdit = !!id;

  useEffect(() => {
    if (isEdit) {
      loadData();
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [id]);

  const loadData = async () => {
    try {
      const response = await businessDomainApi.getById(id!);
      if (response.code === 200 && response.data) {
        const data = response.data as BusinessDomain;
        form.setFieldsValue(data);
      }
    } catch (error) {
      message.error('加载业务域数据失败');
    }
  };

  const handleSubmit = async (values: BusinessDomain) => {
    try {
      if (isEdit) {
        await businessDomainApi.update(id!, values);
        message.success('更新成功');
      } else {
        await businessDomainApi.create(values);
        message.success('创建成功');
      }
      navigate('/domains');
    } catch (error: any) {
      message.error(error.message || '操作失败');
    }
  };

  return (
    <div>
      <div style={{ marginBottom: 24 }}>
        <h2 style={{ margin: 0 }}>{isEdit ? '编辑业务域' : '新建业务域'}</h2>
      </div>
      <Card style={{ borderRadius: 8 }}>
        <Form
          form={form}
          layout="vertical"
          onFinish={handleSubmit}
        >
          <Form.Item
            name="code"
            label="业务域编码"
            rules={[
              { required: true, message: '请输入业务域编码' },
              { pattern: /^[a-zA-Z][a-zA-Z0-9_-]*$/, message: '编码必须以字母开头，只能包含字母、数字、下划线和中划线' },
            ]}
          >
            <Input placeholder="请输入业务域编码（唯一）" disabled={isEdit} />
          </Form.Item>

          <Form.Item
            name="name"
            label="业务域名称"
            rules={[{ required: true, message: '请输入业务域名称' }]}
          >
            <Input placeholder="请输入业务域名称" />
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

          <Form.Item>
            <Space>
              <Button type="primary" htmlType="submit" icon={<SaveOutlined />}>
                保存
              </Button>
              <Button onClick={() => navigate('/domains')} icon={<CloseOutlined />}>
                取消
              </Button>
            </Space>
          </Form.Item>
        </Form>
      </Card>
    </div>
  );
};

export default DomainForm;


