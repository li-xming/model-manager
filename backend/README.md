# 数据模型管理平台 - 后端服务

## 技术栈

- Java 8 (JDK 1.8)
- Spring Boot 2.7.18
- MyBatis-Plus 3.5.3.1
- PostgreSQL 14+
- Redis 7+
- Knife4j 4.1.0 (API文档)

## 项目结构

```
backend/
├── src/
│   ├── main/
│   │   ├── java/com/example/datamodel/
│   │   │   ├── DataModelApplication.java    # 启动类
│   │   │   ├── controller/                  # 控制器
│   │   │   ├── service/                     # 服务层
│   │   │   ├── mapper/                      # Mapper接口
│   │   │   ├── entity/                      # 实体类
│   │   │   ├── dto/                         # 数据传输对象
│   │   │   ├── vo/                          # 视图对象
│   │   │   ├── config/                      # 配置类
│   │   │   ├── exception/                   # 异常处理
│   │   │   └── utils/                       # 工具类
│   │   └── resources/
│   │       ├── application.yml              # 主配置文件
│   │       ├── application-dev.yml          # 开发环境配置
│   │       ├── application-prod.yml         # 生产环境配置
│   │       └── mapper/                      # MyBatis XML映射文件
│   └── test/                                # 测试代码
└── pom.xml                                  # Maven依赖配置
```

## 快速开始

### 前置要求

- JDK 1.8
- Maven 3.6+
- PostgreSQL 14+
- Redis 7+

### 配置数据库

1. 创建数据库：
```sql
CREATE DATABASE datamodel;
```

2. 执行数据库初始化脚本（参考文档中的数据库设计）

3. 修改 `application.yml` 中的数据库连接信息

### 运行项目

```bash
# 编译项目
mvn clean compile

# 运行项目
mvn spring-boot:run

# 或者打包后运行
mvn clean package
java -jar target/data-model-platform-1.0.0-SNAPSHOT.jar
```

### 访问API文档

启动项目后，访问以下地址查看API文档：

- Knife4j UI: http://localhost:8080/api/doc.html
- Swagger JSON: http://localhost:8080/api/v3/api-docs

## API接口

### 对象类型管理

- `POST /api/v1/object-types` - 创建对象类型
- `PUT /api/v1/object-types/{id}` - 更新对象类型
- `GET /api/v1/object-types/{id}` - 根据ID查询对象类型
- `GET /api/v1/object-types/name/{name}` - 根据名称查询对象类型
- `GET /api/v1/object-types` - 分页查询对象类型列表
- `DELETE /api/v1/object-types/{id}` - 删除对象类型

### 链接类型管理

- `POST /api/v1/link-types` - 创建链接类型
- `PUT /api/v1/link-types/{id}` - 更新链接类型
- `GET /api/v1/link-types/{id}` - 根据ID查询链接类型
- `GET /api/v1/link-types/name/{name}` - 根据名称查询链接类型
- `GET /api/v1/link-types/object-type/{objectTypeId}` - 根据对象类型ID查询相关链接类型
- `GET /api/v1/link-types` - 分页查询链接类型列表
- `DELETE /api/v1/link-types/{id}` - 删除链接类型

## 开发说明

### 代码规范

- 使用 Lombok 简化代码
- 统一使用 ResponseVO 作为接口返回类型
- 使用 @Validated 进行参数校验
- 使用统一异常处理（GlobalExceptionHandler）

### 数据库

- 使用 MyBatis-Plus 进行数据库操作
- ID类型使用 UUID
- 时间字段使用 LocalDateTime

### 注意事项

- 当前版本暂不实现认证授权功能
- Kafka消息队列为可选依赖，如不使用可移除相关配置

