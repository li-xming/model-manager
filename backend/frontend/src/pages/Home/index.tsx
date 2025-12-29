import { Card, Row, Col, Statistic, Button, Typography } from 'antd';
import { DatabaseOutlined, LinkOutlined, TableOutlined, SearchOutlined, ArrowRightOutlined } from '@ant-design/icons';
import { useNavigate } from 'react-router-dom';

const { Title, Paragraph } = Typography;

const Home = () => {
  const navigate = useNavigate();

  return (
    <div>
      <div style={{ marginBottom: 32 }}>
        <Title level={2} style={{ marginBottom: 8 }}>
          数据模型管理平台
        </Title>
        <Paragraph style={{ fontSize: 16, color: 'rgba(0, 0, 0, 0.65)', marginBottom: 0 }}>
          基于 Palantir Foundry 本体论理念的企业级数据模型管理平台，为组织提供统一的数据语义层
        </Paragraph>
      </div>
      
      <Row gutter={[24, 24]}>
        <Col xs={24} sm={12} lg={6}>
          <Card 
            hoverable
            style={{ 
              height: '100%',
              borderRadius: 8,
              boxShadow: '0 2px 8px rgba(0,0,0,0.09)',
            }}
            styles={{ body: { padding: 24 } }}
          >
            <div style={{ marginBottom: 16 }}>
              <DatabaseOutlined style={{ fontSize: 32, color: '#1890ff', marginBottom: 12 }} />
            </div>
            <Statistic
              title={<span style={{ fontSize: 16, fontWeight: 500 }}>对象类型</span>}
              value={0}
              valueStyle={{ fontSize: 28, fontWeight: 600 }}
            />
            <Button 
              type="primary" 
              block
              style={{ marginTop: 24, height: 40 }}
              onClick={() => navigate('/object-types')}
              icon={<ArrowRightOutlined />}
            >
              管理对象类型
            </Button>
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card 
            hoverable
            style={{ 
              height: '100%',
              borderRadius: 8,
              boxShadow: '0 2px 8px rgba(0,0,0,0.09)',
            }}
            styles={{ body: { padding: 24 } }}
          >
            <div style={{ marginBottom: 16 }}>
              <LinkOutlined style={{ fontSize: 32, color: '#52c41a', marginBottom: 12 }} />
            </div>
            <Statistic
              title={<span style={{ fontSize: 16, fontWeight: 500 }}>链接类型</span>}
              value={0}
              valueStyle={{ fontSize: 28, fontWeight: 600 }}
            />
            <Button 
              type="primary" 
              block
              style={{ marginTop: 24, height: 40 }}
              onClick={() => navigate('/link-types')}
              icon={<ArrowRightOutlined />}
            >
              管理链接类型
            </Button>
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card 
            hoverable
            style={{ 
              height: '100%',
              borderRadius: 8,
              boxShadow: '0 2px 8px rgba(0,0,0,0.09)',
            }}
            styles={{ body: { padding: 24 } }}
          >
            <div style={{ marginBottom: 16 }}>
              <TableOutlined style={{ fontSize: 32, color: '#faad14', marginBottom: 12 }} />
            </div>
            <Statistic
              title={<span style={{ fontSize: 16, fontWeight: 500 }}>数据实例</span>}
              value={0}
              valueStyle={{ fontSize: 28, fontWeight: 600 }}
            />
            <Button 
              type="primary" 
              block
              style={{ marginTop: 24, height: 40 }}
              onClick={() => navigate('/instances')}
              icon={<ArrowRightOutlined />}
            >
              管理实例
            </Button>
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card 
            hoverable
            style={{ 
              height: '100%',
              borderRadius: 8,
              boxShadow: '0 2px 8px rgba(0,0,0,0.09)',
            }}
            styles={{ body: { padding: 24 } }}
          >
            <div style={{ marginBottom: 16 }}>
              <SearchOutlined style={{ fontSize: 32, color: '#722ed1', marginBottom: 12 }} />
            </div>
            <Statistic
              title={<span style={{ fontSize: 16, fontWeight: 500 }}>查询构建器</span>}
              value={0}
              valueStyle={{ fontSize: 28, fontWeight: 600 }}
            />
            <Button 
              type="primary" 
              block
              style={{ marginTop: 24, height: 40 }}
              onClick={() => navigate('/query')}
              icon={<ArrowRightOutlined />}
            >
              查询数据
            </Button>
          </Card>
        </Col>
      </Row>

      <Row gutter={[24, 24]} style={{ marginTop: 32 }}>
        <Col xs={24} lg={12}>
          <Card title="核心功能" style={{ borderRadius: 8 }}>
            <ul style={{ paddingLeft: 20, lineHeight: 2 }}>
              <li>统一数据语义：通过本体论系统建立统一的数据模型语言</li>
              <li>数据模型可视化：提供直观的数据模型设计和管理界面</li>
              <li>关系映射管理：支持复杂的数据对象关系定义和查询</li>
              <li>数据治理：确保数据模型的一致性和可追溯性</li>
            </ul>
          </Card>
        </Col>
        <Col xs={24} lg={12}>
          <Card title="平台特性" style={{ borderRadius: 8 }}>
            <ul style={{ paddingLeft: 20, lineHeight: 2 }}>
              <li>灵活扩展：支持自定义对象类型、关系类型和操作类型</li>
              <li>动态表结构：根据对象类型定义自动创建和管理数据库表</li>
              <li>图查询：支持复杂的关系查询和路径查找</li>
              <li>类型安全：完整的类型定义和验证机制</li>
            </ul>
          </Card>
        </Col>
      </Row>
    </div>
  );
};

export default Home;
