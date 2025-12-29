# 测试用例说明

## 测试结构

测试用例按照以下结构组织：

```
src/test/java/com/example/datamodel/
├── DataModelApplicationTests.java          # 应用启动测试
├── controller/v1/                          # Controller层测试
│   ├── ObjectTypeControllerTest.java       # 对象类型API测试
│   ├── PropertyControllerTest.java         # 属性API测试
│   └── LinkTypeControllerTest.java         # 链接类型API测试
└── service/                                # Service层测试
    └── InstanceServiceTest.java            # 实例服务测试
```

## 运行测试

### 方式一：使用IDE

1. **IntelliJ IDEA**
   - 右键点击测试类或测试方法
   - 选择 "Run Test" 或 "Debug Test"

2. **Eclipse**
   - 右键点击测试类
   - Run As -> JUnit Test

### 方式二：使用Maven命令

```bash
# 运行所有测试
mvn test

# 运行特定测试类
mvn test -Dtest=ObjectTypeControllerTest

# 运行特定测试方法
mvn test -Dtest=ObjectTypeControllerTest#testCreateObjectType
```

## 测试说明

### Controller层测试

使用 `MockMvc` 进行API集成测试：

- **ObjectTypeControllerTest**: 测试对象类型的CRUD操作
  - 创建对象类型
  - 查询对象类型（ID、名称、列表）
  - 更新对象类型
  - 删除对象类型
  - 数据验证测试

- **PropertyControllerTest**: 测试属性的CRUD操作
  - 创建属性
  - 查询属性
  - 更新属性
  - 删除属性
  - 重复名称测试

- **LinkTypeControllerTest**: 测试链接类型的CRUD操作
  - 创建链接类型
  - 查询链接类型
  - 更新链接类型
  - 删除链接类型
  - 对象类型关联查询

### Service层测试

使用Spring Boot Test进行服务层测试：

- **InstanceServiceTest**: 测试实例服务的功能
  - 创建实例
  - 查询实例
  - 更新实例
  - 删除实例
  - 批量操作
  - 数据验证（必需字段、默认值）

## 测试配置

测试使用独立的配置文件 `application-test.yml`，使用H2内存数据库进行测试，避免影响开发数据库。

## 注意事项

1. **事务回滚**: 所有测试类都使用 `@Transactional` 注解，测试结束后数据会自动回滚

2. **测试数据清理**: `@BeforeEach` 方法中会清理测试数据，确保测试环境干净

3. **数据库要求**: 
   - 如果使用H2内存数据库（推荐），无需额外配置
   - 如果使用PostgreSQL，需要确保数据库服务运行

4. **测试顺序**: 测试方法之间相互独立，可以任意顺序运行

## 扩展测试

可以添加更多测试用例：

1. **接口管理测试** - InterfaceControllerTest
2. **函数管理测试** - FunctionControllerTest
3. **操作类型测试** - ActionTypeControllerTest
4. **链接实例测试** - LinkInstanceControllerTest
5. **查询引擎测试** - QueryEngineTest

## 测试覆盖率

运行测试后可以使用以下工具查看覆盖率：

```bash
# 使用JaCoCo生成覆盖率报告
mvn clean test jacoco:report

# 报告位置
target/site/jacoco/index.html
```

