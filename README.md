# 数据模型管理平台

基于 Palantir Foundry 本体论理念的企业级数据模型管理平台，为组织提供统一的数据语义层，实现数据模型的规范化定义、管理和应用。

## 项目概述

数据模型管理平台旨在帮助企业：

- **统一数据语义**：通过本体论系统建立统一的数据模型语言
- **数据模型可视化**：提供直观的数据模型设计和管理界面
- **关系映射管理**：支持复杂的数据对象关系定义和查询
- **数据治理**：确保数据模型的一致性和可追溯性
- **灵活扩展**：支持自定义对象类型、关系类型和操作类型

## 核心概念

### 对象类型（Object Type）
表示组织中的实体或事件，例如"客户"、"资产"、"交易"等。每个对象类型定义了该实体的属性和行为。

### 属性（Property）
描述对象类型的特征或数据字段，例如"客户"的"姓名"、"地址"等。

### 链接类型（Link Type）
定义两个对象类型之间的关系，例如"客户"与"订单"之间的"下单"关系。

### 操作类型（Action Type）
定义如何修改对象类型的操作，例如"更新客户信息"、"取消订单"等。

### 接口（Interface）
描述对象类型及其功能的本体论类型，提供对象类型的多态性。

### 函数（Function）
基于代码的逻辑单元，接受输入参数并返回输出。

## 技术栈

### 后端
- Java 8 (JDK 1.8)
- Spring Boot 2.x
- MyBatis-Plus
- PostgreSQL 14+
- Redis 7+
- Kafka（可选，非强制依赖）

### 前端
- React 18+ / Vue 3+
- TypeScript
- Ant Design / Element Plus
- Vite

### 开发工具
- Docker & Docker Compose
- Git
- GitHub Actions / GitLab CI

## 项目结构

```
data-model-platform/
├── backend/                 # 后端代码（Spring Boot）
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/       # Java源码
│   │   │   └── resources/  # 配置文件
│   │   └── test/           # 测试代码
│   └── pom.xml             # Maven依赖（或 build.gradle）
├── frontend/               # 前端代码
│   ├── src/
│   │   ├── components/     # 组件
│   │   ├── pages/          # 页面
│   │   ├── services/       # API服务
│   │   └── store/          # 状态管理
│   └── package.json        # Node依赖
├── docs/                   # 文档
│   └── design-and-development-plan.md  # 设计与开发计划
├── docker/                 # Docker配置
└── README.md               # 项目说明
```

## 快速开始

### 前置要求
- Java 8 (JDK 1.8)
- Maven 3.6+ 
- Node.js 18+
- PostgreSQL 14+
- Redis 7+
- Kafka（可选，如果需要消息队列功能）
- Docker & Docker Compose (可选)

### 安装步骤

1. **克隆项目**
```bash
git clone <repository-url>
cd data-model-platform
```

2. **后端设置**
```bash
cd backend
python -m venv venv
source venv/bin/activate  # Windows: venv\Scripts\activate
pip install -r requirements.txt
```

3. **数据库初始化**
```bash
# 配置数据库连接信息
# 运行数据库迁移
alembic upgrade head
```

4. **前端设置**
```bash
cd frontend
npm install
```

5. **启动服务**

后端：
```bash
cd backend
# 使用 Maven
mvn spring-boot:run

# 或直接运行主类
java -jar target/data-model-platform-1.0.0.jar
```

前端：
```bash
cd frontend
npm run dev
```

### Docker 方式启动

```bash
docker-compose up -d
```



### 开发阶段
1. ✅ 基础架构搭建（2周）
2. ⏳ 核心本体论功能（3周）
3. ⏳ 数据实例管理（2周）
4. ⏳ 查询与关系处理（2周）
5. ⏳ 前端界面开发（4周）
6. ⏳ 高级功能（2周）
7. ⏳ 权限与安全（2周）
8. ⏳ 测试与优化（2周）
9. ⏳ 部署与文档（1周）

## API 文档

启动后端服务后，访问以下地址查看 API 文档：
- Swagger UI: http://localhost:8080/swagger-ui.html
- Knife4j UI: http://localhost:8080/doc.html（如果使用Knife4j）


## 许可证

本项目采用 [MIT License](LICENSE) 许可证。


## 参考资料

- [Palantir Foundry 文档](https://www.palantir.com/docs/foundry/)
- [Spring Boot 文档](https://spring.io/projects/spring-boot)
- [MyBatis-Plus 文档](https://baomidou.com/)
- [React 文档](https://react.dev/)

---

**注意**：本项目目前处于开发阶段，API 和功能可能会有变更。

