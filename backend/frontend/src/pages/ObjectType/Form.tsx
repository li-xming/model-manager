import { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { Form, Input, Button, Card, Space, Select, App } from 'antd';
import { SaveOutlined, CloseOutlined } from '@ant-design/icons';
import { objectTypeApi, businessDomainApi, metaModelApi } from '../../api';
import type { ObjectTypeDTO, BusinessDomain, MetaClass } from '../../types/api';

const ObjectTypeForm = () => {
  const navigate = useNavigate();
  const { id } = useParams<{ id: string }>();
  const [form] = Form.useForm();
  const [domains, setDomains] = useState<BusinessDomain[]>([]);
  const [metaClasses, setMetaClasses] = useState<MetaClass[]>([]);
  const isEdit = !!id;
  const { message } = App.useApp();

  useEffect(() => {
    loadDomains();
    loadMetaClasses();
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
      // 业务域加载失败不影响继续操作
      console.error('加载业务域失败:', error);
    }
  };

  const loadMetaClasses = async () => {
    try {
      const response = await metaModelApi.getAllMetaClasses();
      if (response.code === 200) {
        setMetaClasses(response.data || []);
      }
    } catch (error) {
      console.error('加载元类失败:', error);
    }
  };

  const loadData = async () => {
    try {
      const response = await objectTypeApi.getById(id!);
      if (response.code === 200) {
        form.setFieldsValue(response.data);
      }
    } catch (error) {
      message.error('加载数据失败');
    }
  };

  const handleSubmit = async (values: ObjectTypeDTO) => {
    try {
      let objectTypeId: string;
      if (isEdit) {
        await objectTypeApi.update(id!, values);
        message.success('更新成功');
        objectTypeId = id!;
      } else {
        const response = await objectTypeApi.create(values);
        if (response.code === 200 && response.data) {
          message.success('创建成功');
          objectTypeId = response.data.id;
        } else {
          message.error('创建失败，无法获取对象类型ID');
          return;
        }
      }
      // 跳转到详情页，方便用户继续添加属性
      navigate(`/object-types/${objectTypeId}`);
    } catch (error: any) {
      message.error(error.message || '操作失败');
    }
  };

  return (
    <div>
      <div style={{ marginBottom: 24 }}>
        <h2 style={{ margin: 0 }}>{isEdit ? '编辑对象类型' : '新建对象类型'}</h2>
      </div>
      <Card style={{ borderRadius: 8 }}>
        <Form
          form={form}
          layout="vertical"
          onFinish={handleSubmit}
          initialValues={{
            primaryKey: 'id',
            metaClassId: 'META_CLASS',  // 默认元类
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
            <Input placeholder="请输入对象类型名称" disabled={isEdit} />
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
            <Input.TextArea rows={4} placeholder="请输入描述" />
          </Form.Item>

          <Form.Item
            name="primaryKey"
            label="主键字段名"
            rules={[{ required: true, message: '请输入主键字段名' }]}
          >
            <Input placeholder="默认：id" />
          </Form.Item>

          <Form.Item
            name="domainId"
            label="业务域"
            tooltip="选择此对象类型所属的业务域（可选）"
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
            name="metaClassId"
            label="元类"
            tooltip="选择该对象类型基于的元类（默认为'META_CLASS'）"
          >
            <Select
              placeholder="请选择元类（可选，默认为'META_CLASS'）"
              allowClear
              showSearch
              filterOption={(input, option) =>
                (option?.label ?? '').toLowerCase().includes(input.toLowerCase())
              }
              options={metaClasses.map(mc => ({ 
                value: mc.id, 
                label: `${mc.displayName || mc.name}${mc.isBuiltin ? ' (内置)' : ''}` 
              }))}
            />
          </Form.Item>

          <Form.Item>
            <Space>
              <Button type="primary" htmlType="submit" icon={<SaveOutlined />}>
                保存
              </Button>
              <Button onClick={() => navigate('/object-types')} icon={<CloseOutlined />}>
                取消
              </Button>
            </Space>
          </Form.Item>
        </Form>
      </Card>
    </div>
  );
};

export default ObjectTypeForm;

