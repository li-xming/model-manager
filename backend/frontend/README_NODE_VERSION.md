# Node.js 版本要求

## 问题说明

如果遇到以下错误：
```
TypeError: crypto$2.getRandomValues is not a function
```

这是因为 Node.js 版本过低导致的。Vite 5.x 需要 Node.js 18 或更高版本。

## 解决方案

### 方案1：升级 Node.js（推荐）

1. **下载并安装 Node.js 18+**
   - 访问 https://nodejs.org/
   - 下载 LTS 版本（推荐 18.x 或 20.x）
   - 安装后重启终端

2. **验证版本**
   ```bash
   node --version
   ```
   应该显示 v18.x.x 或更高版本

3. **重新安装依赖**
   ```bash
   cd backend/frontend
   rm -rf node_modules package-lock.json
   npm install
   ```

4. **启动开发服务器**
   ```bash
   npm run dev
   ```

### 方案2：使用 nvm 管理 Node.js 版本（推荐开发者）

如果您使用 nvm（Node Version Manager）：

1. **安装 nvm（如果未安装）**
   - Windows: https://github.com/coreybutler/nvm-windows
   - macOS/Linux: https://github.com/nvm-sh/nvm

2. **安装并使用 Node.js 18**
   ```bash
   nvm install 18
   nvm use 18
   ```

3. **验证版本**
   ```bash
   node --version
   ```

4. **重新安装依赖并启动**
   ```bash
   cd backend/frontend
   rm -rf node_modules package-lock.json
   npm install
   npm run dev
   ```

### 方案3：降级 Vite（不推荐）

如果无法升级 Node.js，可以降级到 Vite 4.x：

1. **修改 package.json**
   ```json
   "devDependencies": {
     "vite": "^4.5.0",
     "@vitejs/plugin-react": "^4.2.1"
   }
   ```

2. **重新安装依赖**
   ```bash
   cd backend/frontend
   rm -rf node_modules package-lock.json
   npm install
   ```

注意：降级可能导致其他兼容性问题，建议优先升级 Node.js。

## 当前 Node.js 版本

运行以下命令查看当前版本：
```bash
node --version
```

## 项目要求

- **Node.js**: >= 18.0.0
- **npm**: >= 9.0.0（通常随 Node.js 一起安装）

