import { useState } from 'react';
import { Outlet, useNavigate, useLocation } from 'react-router-dom';
import { Layout, Menu, theme, Button } from 'antd';
import {
  HomeOutlined,
  DatabaseOutlined,
  LinkOutlined,
  TableOutlined,
  SearchOutlined,
  MenuFoldOutlined,
  MenuUnfoldOutlined,
  ThunderboltOutlined,
  ApiOutlined,
  FunctionOutlined,
  AppstoreOutlined,
  PartitionOutlined,
  CloudServerOutlined,
} from '@ant-design/icons';

const { Header, Sider, Content } = Layout;

const BasicLayout = () => {
  const [collapsed, setCollapsed] = useState(false);
  const navigate = useNavigate();
  const location = useLocation();
  const {
    token: { colorBgContainer },
  } = theme.useToken();

  const menuItems = [
    {
      key: '/',
      icon: <HomeOutlined />,
      label: '首页',
    },
    {
      key: '/domains',
      icon: <AppstoreOutlined />,
      label: '业务域管理',
    },
    {
      key: '/datasources',
      icon: <CloudServerOutlined />,
      label: '数据源管理',
    },
    {
      key: '/object-types',
      icon: <DatabaseOutlined />,
      label: '对象类型',
    },
    {
      key: '/link-types',
      icon: <LinkOutlined />,
      label: '链接类型',
    },
    {
      key: '/instances',
      icon: <TableOutlined />,
      label: '实例管理',
    },
    {
      key: '/action-types',
      icon: <ThunderboltOutlined />,
      label: '操作类型',
    },
    {
      key: '/interfaces',
      icon: <ApiOutlined />,
      label: '接口管理',
    },
    {
      key: '/functions',
      icon: <FunctionOutlined />,
      label: '函数管理',
    },
    {
      key: '/meta-models',
      icon: <PartitionOutlined />,
      label: '元模型管理',
    },
    {
      key: '/query',
      icon: <SearchOutlined />,
      label: '查询构建器',
    },
  ];

  const handleMenuClick = ({ key }: { key: string }) => {
    navigate(key);
  };

  const selectedKeys = [location.pathname === '/' ? '/' : location.pathname.split('/').slice(0, 2).join('/')];

  return (
    <Layout style={{ minHeight: '100vh', background: '#f0f2f5' }}>
      <Sider 
        trigger={null} 
        collapsible 
        collapsed={collapsed}
        width={240}
        style={{
          overflow: 'auto',
          height: '100vh',
          position: 'fixed',
          left: 0,
          top: 0,
          bottom: 0,
        }}
      >
        <div style={{ 
          height: 64,
          padding: '16px',
          display: 'flex',
          alignItems: 'center',
          justifyContent: collapsed ? 'center' : 'flex-start',
          borderBottom: '1px solid rgba(255, 255, 255, 0.1)',
        }}>
          {collapsed ? (
            <div style={{ 
              width: 32, 
              height: 32, 
              borderRadius: 4,
              background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              color: '#fff',
              fontWeight: 'bold',
              fontSize: 16,
            }}>
              DM
            </div>
          ) : (
            <div style={{ 
              color: '#fff', 
              fontSize: 18, 
              fontWeight: 600,
              letterSpacing: '0.5px',
            }}>
              数据模型平台
            </div>
          )}
        </div>
        <Menu
          theme="dark"
          mode="inline"
          selectedKeys={selectedKeys}
          items={menuItems}
          onClick={handleMenuClick}
          style={{
            borderRight: 0,
            marginTop: 8,
          }}
        />
      </Sider>
      <Layout style={{ marginLeft: collapsed ? 80 : 240, transition: 'all 0.2s' }}>
        <Header style={{ 
          padding: '0 24px', 
          background: '#ffffff',
          boxShadow: '0 1px 4px rgba(0,21,41,.08)',
          display: 'flex',
          alignItems: 'center',
          position: 'sticky',
          top: 0,
          zIndex: 10,
        }}>
          <Button
            type="text"
            icon={collapsed ? <MenuUnfoldOutlined /> : <MenuFoldOutlined />}
            onClick={() => setCollapsed(!collapsed)}
            style={{
              fontSize: 16,
              width: 40,
              height: 40,
            }}
          />
          <div style={{ 
            marginLeft: 16, 
            fontSize: 16, 
            fontWeight: 500,
            color: 'rgba(0, 0, 0, 0.85)',
          }}>
            数据模型管理平台
          </div>
        </Header>
        <Content
          style={{
            margin: '24px',
            padding: '24px',
            minHeight: 280,
            background: colorBgContainer,
            borderRadius: 8,
          }}
        >
          <Outlet />
        </Content>
      </Layout>
    </Layout>
  );
};

export default BasicLayout;
