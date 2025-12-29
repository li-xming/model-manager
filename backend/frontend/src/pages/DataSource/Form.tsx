import { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { Form, Input, Button, Card, Space, Select, InputNumber, Switch, App } from 'antd';
import { SaveOutlined, CloseOutlined } from '@ant-design/icons';
import { dataSourceApi, businessDomainApi } from '../../api';
import type { DataSource, DataSourceDTO, BusinessDomain } from '../../types/api';

const { TextArea } = Input;

const DataSourceForm = () => {
  const navigate = useNavigate();
  const { id } = useParams<{ id: string }>();
  const [form] = Form.useForm();
  const { message } = App.useApp();
  const isEdit = !!id;
  const [domains, setDomains] = useState<BusinessDomain[]>([]);
  const [loading, setLoading] = useState(false);

  const dataSourceTypes = [
    { label: 'MySQL', value: 'MYSQL' },
    { label: 'PostgreSQL', value: 'POSTGRESQL' },
    { label: 'Oracle', value: 'ORACLE' },
    { label: 'SQL Server', value: 'SQL_SERVER' },
    { label: 'MariaDB', value: 'MARIADB' },
    { label: 'MongoDB', value: 'MONGODB' },
  ];

  useEffect(() => {
    loadDomains();
    if (isEdit) {
      loadData();
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
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
      const response = await dataSourceApi.getById(id!);
      if (response.code === 200 && response.data) {
        const data = response.data as DataSource;
        form.setFieldsValue({
          ...data,
          // 编辑时不设置密码
          password: undefined,
        });
      }
    } catch (error: any) {
      message.error(error.message || '加载数据源数据失败');
    } finally {
      setLoading(false);
    }
  };

  const handleSubmit = async (values: any) => {
    try {
      const submitData: DataSourceDTO = {
        ...values,
        // 如果编辑时密码为空，则不提交密码字段
        password: values.password || undefined,
      };
      
      if (isEdit) {
        await dataSourceApi.update(id!, submitData);
        message.success('更新成功');
      } else {
        await dataSourceApi.create(submitData);
        message.success('创建成功');
      }
      navigate('/datasources');
    } catch (error: any) {
      message.error(error.message || '操作失败');
    }
  };

  const getDefaultPort = (type: string) => {
    const portMap: Record<string, number> = {
      MYSQL: 3306,
      POSTGRESQL: 5432,
      ORACLE: 1521,
      SQL_SERVER: 1433,
      MARIADB: 3306,
      MONGODB: 27017,
    };
    return portMap[type] || 3306;
  };

  const handleTypeChange = (type: string) => {
    const defaultPort = getDefaultPort(type);
    form.setFieldsValue({ port: defaultPort });
  };

  return (
    <div>
      <div style={{ marginBottom: 24 }}>
        <h2 style={{ margin: 0 }}>{isEdit ? '编辑数据源' : '新建数据源'}</h2>
      </div>
      <Card style={{ borderRadius: 8 }} loading={loading}>
        <Form
          form={form}
          layout="vertical"
          onFinish={handleSubmit}
          initialValues={{
            enabled: true,
            maxConnections: 10,
            minConnections: 2,
            connectionTimeout: 30,
          }}
        >
          <Form.Item
            name="name"
            label="数据源名称"
            rules={[{ required: true, message: '请输入数据源名称' }]}
          >
            <Input placeholder="请输入数据源名称" />
          </Form.Item>

          <Form.Item
            name="code"
            label="数据源代码"
            rules={[
              { required: true, message: '请输入数据源代码' },
              { pattern: /^[a-zA-Z][a-zA-Z0-9_-]*$/, message: '代码必须以字母开头，只能包含字母、数字、下划线和中划线' },
            ]}
          >
            <Input placeholder="请输入数据源代码（唯一）" disabled={isEdit} />
          </Form.Item>

          <Form.Item
            name="type"
            label="数据源类型"
            rules={[{ required: true, message: '请选择数据源类型' }]}
          >
            <Select placeholder="请选择数据源类型" onChange={handleTypeChange} disabled={isEdit}>
              {dataSourceTypes.map((type) => (
                <Select.Option key={type.value} value={type.value}>
                  {type.label}
                </Select.Option>
              ))}
            </Select>
          </Form.Item>

          <Form.Item
            name="domainId"
            label="业务域"
          >
            <Select placeholder="请选择业务域（可选）" allowClear>
              {domains.map((domain) => (
                <Select.Option key={domain.id} value={domain.id}>
                  {domain.displayName || domain.name}
                </Select.Option>
              ))}
            </Select>
          </Form.Item>

          <Form.Item
            name="description"
            label="描述"
          >
            <TextArea rows={3} placeholder="请输入描述" />
          </Form.Item>

          <Card size="small" title="连接信息" style={{ marginBottom: 16 }}>
            <Form.Item
              name="host"
              label="主机地址"
              rules={[{ required: true, message: '请输入主机地址' }]}
            >
              <Input placeholder="例如：192.168.1.100 或 localhost" />
            </Form.Item>

            <Form.Item
              name="port"
              label="端口号"
              rules={[{ required: true, message: '请输入端口号' }]}
            >
              <InputNumber min={1} max={65535} style={{ width: '100%' }} placeholder="端口号" />
            </Form.Item>

            <Form.Item
              name="databaseName"
              label="数据库名"
            >
              <Input placeholder="数据库名称（可选）" />
            </Form.Item>

            <Form.Item
              name="schemaName"
              label="Schema名"
            >
              <Input placeholder="Schema名称（可选，PostgreSQL/Oracle使用）" />
            </Form.Item>

            <Form.Item
              name="username"
              label="用户名"
              rules={[{ required: true, message: '请输入用户名' }]}
            >
              <Input placeholder="请输入用户名" />
            </Form.Item>

            <Form.Item
              name="password"
              label="密码"
              rules={isEdit ? [] : [{ required: true, message: '请输入密码' }]}
            >
              <Input.Password placeholder={isEdit ? '留空则不修改密码' : '请输入密码'} />
            </Form.Item>

            <Form.Item
              name="connectionUrl"
              label="连接URL"
            >
              <TextArea rows={2} placeholder="完整的连接URL（可选，如果不提供则自动构建）" />
            </Form.Item>
          </Card>

          <Card size="small" title="连接池配置" style={{ marginBottom: 16 }}>
            <Form.Item
              name="maxConnections"
              label="最大连接数"
            >
              <InputNumber min={1} max={100} style={{ width: '100%' }} />
            </Form.Item>

            <Form.Item
              name="minConnections"
              label="最小连接数"
            >
              <InputNumber min={0} max={50} style={{ width: '100%' }} />
            </Form.Item>

            <Form.Item
              name="connectionTimeout"
              label="连接超时时间（秒）"
            >
              <InputNumber min={1} max={300} style={{ width: '100%' }} />
            </Form.Item>
          </Card>

          <Form.Item
            name="enabled"
            label="启用状态"
            valuePropName="checked"
          >
            <Switch />
          </Form.Item>

          <Form.Item>
            <Space>
              <Button type="primary" htmlType="submit" icon={<SaveOutlined />}>
                保存
              </Button>
              <Button icon={<CloseOutlined />} onClick={() => navigate('/datasources')}>
                取消
              </Button>
            </Space>
          </Form.Item>
        </Form>
      </Card>
    </div>
  );
};

export default DataSourceForm;

