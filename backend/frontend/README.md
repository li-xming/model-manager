# 数据模型管理平台 - 前端

基于 React + TypeScript + Vite + Ant Design 构建的前端应用。

## 技术栈

- **React 18** - UI框架
- **TypeScript** - 类型系统
- **Vite** - 构建工具
- **Ant Design** - UI组件库
- **React Router** - 路由管理
- **Axios** - HTTP请求
- **ECharts** - 图表可视化

## 快速开始

### 安装依赖

```bash
npm install
```

### 启动开发服务器

```bash
npm run dev
```

前端应用将在 http://localhost:3000 启动

### 构建生产版本

```bash
npm run build
```

### 预览生产版本

```bash
npm run preview
```

## 项目结构

```
frontend/
├── src/
│   ├── api/              # API接口定义
│   ├── components/       # 通用组件
│   ├── layouts/          # 布局组件
│   ├── pages/            # 页面组件
│   │   ├── Home/         # 首页
│   │   ├── ObjectType/   # 对象类型管理
│   │   ├── LinkType/     # 链接类型管理
│   │   ├── Instance/     # 实例管理
│   │   └── Query/        # 查询构建器
│   ├── types/            # TypeScript类型定义
│   ├── utils/            # 工具函数
│   ├── App.tsx           # 应用入口
│   └── main.tsx          # 主入口文件
├── public/               # 静态资源
└── package.json          # 依赖配置
```

## 配置说明

### API代理配置

在 `vite.config.ts` 中配置了API代理，将 `/api` 请求代理到后端服务器 `http://localhost:8080`。

### 环境变量

可以创建 `.env` 文件配置环境变量：

```env
VITE_API_BASE_URL=http://localhost:8080/api
```

## 功能模块

### 已实现

- ✅ 项目基础架构
- ✅ 路由配置
- ✅ 基础布局
- ✅ API客户端
- ✅ 类型定义
- ✅ 对象类型列表页面
- ✅ 对象类型详情页面
- ✅ 链接类型列表页面
- ✅ 实例管理页面框架
- ✅ 查询构建器页面框架

### 待实现

- ⏳ 对象类型CRUD表单
- ⏳ 属性管理界面
- ⏳ 链接类型CRUD表单
- ⏳ 实例CRUD表单
- ⏳ 查询构建器完整功能
- ⏳ 关系可视化
- ⏳ 模型设计器

## 开发规范

### 代码风格

- 使用 TypeScript 进行类型检查
- 使用 ESLint 进行代码检查
- 遵循 React Hooks 最佳实践

### 提交规范

- feat: 新功能
- fix: 修复bug
- docs: 文档更新
- style: 代码格式调整
- refactor: 代码重构
- test: 测试相关
- chore: 构建/工具相关

## 常见问题

### 代理不生效

确保后端服务运行在 `http://localhost:8080`，或修改 `vite.config.ts` 中的代理配置。

### 类型错误

运行 `npm run build` 检查类型错误，或使用 IDE 的类型检查功能。
