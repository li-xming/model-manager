import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Form, Input, Button, Card, Space, InputNumber, Switch, DatePicker, App, Select } from 'antd';
import { SaveOutlined, CloseOutlined, ArrowLeftOutlined } from '@ant-design/icons';
import dayjs from 'dayjs';
import { instanceApi, objectTypeApi, propertyApi, objectTypeDataSourceApi } from '../../api';
import type { ObjectType, Property, ObjectTypeDataSourceTable } from '../../types/api';

const { TextArea } = Input;

const InstanceForm = () => {
  const { objectTypeName, id } = useParams<{ objectTypeName: string; id: string }>();
  const navigate = useNavigate();
  const [form] = Form.useForm();
  const { message } = App.useApp();
  const [loading, setLoading] = useState(false);
  const [objectType, setObjectType] = useState<ObjectType | null>(null);
  const [properties, setProperties] = useState<Property[]>([]);
  const [dataSourceTables, setDataSourceTables] = useState<ObjectTypeDataSourceTable[]>([]);
  const [storageDatasources, setStorageDatasources] = useState<ObjectTypeDataSourceTable[]>([]);
  const isEdit = !!id;

  useEffect(() => {
    if (objectTypeName) {
      loadObjectType();
    }
  }, [objectTypeName]);

  useEffect(() => {
    if (objectType) {
      loadProperties();
      loadDataSources();
      if (isEdit) {
        loadData();
      }
    }
  }, [objectType, id]);

  const loadObjectType = async () => {
    try {
      const response = await objectTypeApi.getByName(objectTypeName!);
      if (response.code === 200) {
        setObjectType(response.data);
      } else {
        message.error('对象类型不存在');
      }
    } catch (error) {
      message.error('加载对象类型失败');
    }
  };

  const loadProperties = async () => {
    try {
      const response = await propertyApi.getByObjectTypeId(objectType!.id);
      if (response.code === 200) {
        const sortedProperties = (response.data || []).sort((a, b) => a.sortOrder - b.sortOrder);
        setProperties(sortedProperties);
      }
    } catch (error) {
      message.error('加载属性失败');
    }
  };

  const loadDataSources = async () => {
    try {
      // 加载所有数据源表（用于兼容旧代码）
      const response = await objectTypeDataSourceApi.getDataSourceTables(objectType!.id);
      if (response.code === 200) {
        setDataSourceTables(response.data || []);
      }
      
      // 加载存储库列表（is_storage = true）
      const storageResponse = await objectTypeDataSourceApi.getStorageDatasources(objectType!.id);
      if (storageResponse.code === 200) {
        setStorageDatasources(storageResponse.data || []);
      }
    } catch (error) {
      console.error('加载数据源表列表失败:', error);
    }
  };
  
  const getDataSourceDisplayName = (table: ObjectTypeDataSourceTable): string => {
    const schemaInfo = table.schemaName ? `${table.schemaName}.` : '';
    return `${table.datasource.name} - ${schemaInfo}${table.tableName}`;
  };

  const loadData = async () => {
    setLoading(true);
    try {
      const response = await instanceApi.getById(objectTypeName!, id!);
      if (response.code === 200) {
        const instanceData = response.data;
        // 转换数据格式以匹配表单
        const formData: Record<string, any> = {};
        Object.keys(instanceData).forEach(key => {
          const value = instanceData[key];
          // 处理日期类型
          if (key.includes('date') || key.includes('Date') || key.includes('time') || key.includes('Time')) {
            if (value) {
              formData[key] = dayjs(value);
            }
          } else {
            formData[key] = value;
          }
        });
        form.setFieldsValue(formData);
      }
    } catch (error) {
      message.error('加载数据失败');
    } finally {
      setLoading(false);
    }
  };

  const handleSubmit = async (values: Record<string, any>) => {
    try {
      // 转换表单数据，处理日期等特殊类型
      // 确保所有属性都被包含，即使值为空
      const submitData: Record<string, any> = {};
      
      // 先添加所有属性，确保必需字段被包含
      properties.forEach(property => {
        const propName = property.name;
        const value = values[propName];
        
        if (value !== undefined && value !== null) {
          if (dayjs.isDayjs(value)) {
            submitData[propName] = value.toISOString();
          } else {
            submitData[propName] = value;
          }
        } else if (property.defaultValue) {
          // 如果有默认值，使用默认值
          submitData[propName] = property.defaultValue;
        } else if (!property.required) {
          // 非必需字段可以为空，但也要包含在提交数据中
          submitData[propName] = null;
        }
        // 必需字段如果没有值，会在表单验证阶段被拦截
      });

      // 将数据包装在properties字段中，符合InstanceDTO格式
      const instanceDto: any = {
        properties: submitData
      };

      // 如果选择了存储库，添加到DTO中（使用新的 storageDatasourceId 字段）
      if (values.storageDatasourceId) {
        instanceDto.storageDatasourceId = values.storageDatasourceId;
      } else if (values.datasourceId) {
        // 兼容旧字段（已废弃）
        instanceDto.datasourceId = values.datasourceId;
      }

      if (isEdit) {
        await instanceApi.update(objectTypeName!, id!, instanceDto);
        message.success('更新成功');
      } else {
        await instanceApi.create(objectTypeName!, instanceDto);
        message.success('创建成功');
      }
      navigate(`/instances/${objectTypeName}`);
    } catch (error: any) {
      message.error(error.message || '操作失败');
    }
  };

  const renderFormItem = (property: Property) => {
    const { name, dataType, required, defaultValue, description } = property;
    const rules = required 
      ? [{ required: true, message: `请输入${name || property.name}` }] 
      : [];

    switch (dataType) {
      case 'INTEGER':
      case 'LONG':
        return (
          <Form.Item key={name} name={name} label={name} rules={rules} tooltip={description}>
            <InputNumber style={{ width: '100%' }} placeholder={`请输入${name}`} />
          </Form.Item>
        );
      case 'DOUBLE':
        return (
          <Form.Item key={name} name={name} label={name} rules={rules} tooltip={description}>
            <InputNumber style={{ width: '100%' }} step={0.01} placeholder={`请输入${name}`} />
          </Form.Item>
        );
      case 'BOOLEAN':
        return (
          <Form.Item
            key={name}
            name={name}
            label={name}
            rules={rules}
            tooltip={description}
            valuePropName="checked"
            initialValue={defaultValue === 'true'}
          >
            <Switch />
          </Form.Item>
        );
      case 'DATE':
        return (
          <Form.Item key={name} name={name} label={name} rules={rules} tooltip={description}>
            <DatePicker style={{ width: '100%' }} format="YYYY-MM-DD" />
          </Form.Item>
        );
      case 'TIMESTAMP':
        return (
          <Form.Item key={name} name={name} label={name} rules={rules} tooltip={description}>
            <DatePicker showTime style={{ width: '100%' }} format="YYYY-MM-DD HH:mm:ss" />
          </Form.Item>
        );
      case 'TEXT':
        return (
          <Form.Item key={name} name={name} label={name} rules={rules} tooltip={description}>
            <TextArea rows={4} placeholder={`请输入${name}`} />
          </Form.Item>
        );
      case 'JSON':
        return (
          <Form.Item key={name} name={name} label={name} rules={rules} tooltip={description}>
            <TextArea rows={4} placeholder='请输入JSON格式数据，如：{"key": "value"}' />
          </Form.Item>
        );
      default:
        return (
          <Form.Item
            key={name}
            name={name}
            label={name}
            rules={rules}
            tooltip={description}
            initialValue={defaultValue}
          >
            <Input placeholder={`请输入${name}`} />
          </Form.Item>
        );
    }
  };

  if (!objectType) {
    return (
      <Card style={{ borderRadius: 8 }}>
        <div>加载中...</div>
      </Card>
    );
  }

  return (
    <div>
      <div style={{ marginBottom: 24, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <div>
          <Button
            icon={<ArrowLeftOutlined />}
            onClick={() => navigate(`/instances/${objectTypeName}`)}
            style={{ marginRight: 16 }}
          >
            返回
          </Button>
          <h2 style={{ margin: 0, display: 'inline' }}>
            {isEdit ? '编辑实例' : '新建实例'} - {objectType.displayName}
          </h2>
        </div>
      </div>
      <Card style={{ borderRadius: 8 }} loading={loading}>
        <Form
          form={form}
          layout="vertical"
          onFinish={handleSubmit}
        >
          {storageDatasources.length > 0 && (
            <Form.Item
              name="storageDatasourceId"
              label="存储库"
              tooltip="选择存储此实例的数据源（可选，如果不选择则使用默认存储库或平台存储）"
            >
              <Select placeholder="请选择存储库（可选）" allowClear showSearch optionFilterProp="label">
                {storageDatasources.map(storage => {
                  const displayName = `${storage.datasource.name} (${storage.datasource.type})`;
                  return (
                    <Select.Option key={storage.id} value={storage.id} label={displayName}>
                      {displayName}
                      {storage.isDefault && <span style={{ color: '#faad14', marginLeft: 8 }}>（默认）</span>}
                    </Select.Option>
                  );
                })}
              </Select>
            </Form.Item>
          )}
          {properties.map(property => renderFormItem(property))}
          <Form.Item>
            <Space>
              <Button type="primary" htmlType="submit" icon={<SaveOutlined />}>
                保存
              </Button>
              <Button onClick={() => navigate(`/instances/${objectTypeName}`)} icon={<CloseOutlined />}>
                取消
              </Button>
            </Space>
          </Form.Item>
        </Form>
      </Card>
    </div>
  );
};

export default InstanceForm;

