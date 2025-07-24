# 🚀 微服务高并发测试平台 - 前端

基于Vue 3 + Element Plus的微服务高并发测试平台，提供API测试、压力测试和服务监控功能。

## ✨ 功能特性

### 🧪 API测试平台
- **一键测试** - 支持所有微服务API的在线测试
- **实时监控** - 显示响应状态、耗时、成功率
- **详细响应** - 完整的响应数据和错误信息展示
- **统计分析** - 自动统计成功/失败次数和响应时间

### ⚡ 高并发压力测试
- **双重测试模式** - 前端测试 + 后端测试
- **灵活配置** - 可调节并发用户数、总请求数、测试时长
- **实时统计** - QPS、响应时间、成功率等关键指标
- **可视化图表** - 性能趋势图和实时监控面板
- **系统监控** - CPU、内存等系统资源监控

### 📊 服务状态监控
- **实时健康检查** - 所有微服务的健康状态监控
- **响应时间统计** - 各服务的响应时间分析
- **可视化界面** - 直观的状态展示和监控面板

### 🎨 现代化界面
- **Element Plus UI** - 企业级组件库
- **响应式设计** - 支持各种屏幕尺寸
- **交互友好** - 直观的操作界面

## 🛠️ 技术栈

- **Vue 3 + Composition API** - 现代化前端框架
- **Element Plus** - 企业级Vue 3组件库
- **Vite** - 下一代前端构建工具
- **Vue Router 4** - 官方路由管理器
- **Axios** - HTTP客户端
- **Canvas API** - 性能图表绘制

## 🚀 快速开始

### 📦 1. 安装依赖

```bash
cd frontend
npm install
```

### 🔧 2. 启动开发服务器

```bash
npm run dev
```

访问 http://localhost:3000

### 🏗️ 3. 构建生产版本

```bash
npm run build
```

### 👀 4. 预览生产构建

```bash
npm run preview
```

## 📁 项目结构

```
frontend/
├── public/                     # 静态资源
├── src/
│   ├── components/             # 公共组件
│   ├── views/                  # 页面组件
│   │   ├── ApiTestSimple.vue   # 🧪 API测试页面
│   │   ├── ConcurrencyTest.vue # ⚡ 高并发测试页面
│   │   ├── ServiceStatus.vue   # 📊 服务状态页面
│   │   └── ApiDocs.vue         # 📚 API文档页面
│   ├── router/                 # 路由配置
│   │   └── index.js           # 路由定义
│   ├── utils/                  # 工具函数
│   │   └── api.js             # API请求封装
│   ├── App.vue                 # 根组件
│   └── main.js                 # 入口文件
├── index.html                  # HTML模板
├── package.json                # 项目配置
├── vite.config.js             # Vite配置
└── README.md                   # 项目文档
```

## 🎯 页面功能详解

### 🧪 API测试页面 (`/api-test`)
- **功能**: 在线测试所有微服务API
- **特性**:
  - 一键测试商品、用户、订单API
  - 实时显示响应时间和状态
  - 详细的响应数据展示
  - 成功/失败统计

### ⚡ 高并发测试页面 (`/concurrency-test`)
- **功能**: 专业的高并发压力测试
- **特性**:
  - 前端+后端双重测试模式
  - 可配置并发数、请求数、时长
  - 实时QPS、响应时间监控
  - 性能趋势图表展示
  - 系统资源监控

### 📊 服务状态页面 (`/service-status`)
- **功能**: 实时服务健康监控
- **特性**:
  - 所有微服务状态监控
  - 响应时间统计
  - 基础设施状态检查
  - 可视化状态展示

### 📚 API文档页面 (`/docs`)
- **功能**: 完整的API接口文档
- **特性**:
  - 详细的接口说明
  - 请求/响应示例
  - 参数说明和状态码

## 🔌 API接口

### 🧪 基础业务API
- **用户相关**
  - `POST /api/user/login` - 用户登录 ✅ 已修复LocalDateTime序列化
  - `GET /api/user/info?id=1` - 获取用户信息

- **商品相关**
  - `GET /api/product/list` - 获取商品列表 ✅ 性能优秀

- **订单相关**
  - `GET /api/order/list?userId=1&page=1&size=10` - 获取订单列表 ✅ 已修复500错误

### ⚡ 高并发测试API
- **负载测试**
  - `POST /api/loadtest/execute` - 执行负载测试
  - `GET /api/loadtest/system-info` - 获取系统信息

## 🔑 演示账号

- **用户名**: admin
- **密码**: 123456

## 🔧 开发指南

### 🌐 API配置

在 `src/utils/api.js` 中配置API基础地址：

```javascript
const api = axios.create({
  baseURL: 'http://localhost:9000',  // API网关地址
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})
```

### 🛣️ 路由配置

在 `src/router/index.js` 中配置页面路由：

```javascript
const routes = [
  { path: '/', component: () => import('../views/ApiTestSimple.vue') },
  { path: '/api-test', component: () => import('../views/ApiTestSimple.vue') },
  { path: '/concurrency-test', component: () => import('../views/ConcurrencyTest.vue') },
  { path: '/service-status', component: () => import('../views/ServiceStatus.vue') },
  { path: '/docs', component: () => import('../views/ApiDocs.vue') }
]
```

### 📊 高并发测试集成

```javascript
// 前端压力测试
const startFrontendTest = async () => {
  const promises = []
  for (let i = 0; i < concurrency; i++) {
    promises.push(sendRequest())
  }
  await Promise.all(promises)
}

// 后端压力测试
const startBackendTest = async () => {
  const response = await api.post('/api/loadtest/execute', {
    targetUrl: '/api/product/list',
    method: 'GET',
    concurrency: 10,
    totalRequests: 100,
    duration: 30
  })
  return response.data
}
```

### 🎨 样式规范

- 使用Element Plus的设计规范
- 响应式布局适配不同屏幕尺寸
- 统一的颜色主题和间距
- 支持暗色主题

## 🚀 部署说明

### 🔧 开发环境
```bash
npm run dev
```
- 访问地址: http://localhost:3000
- 热重载: 支持
- 开发工具: Vue DevTools

### 🏭 生产环境
```bash
# 构建
npm run build

# 预览
npm run preview

# 部署到Nginx
cp -r dist/* /var/www/html/
```

### 🐳 Docker部署
```dockerfile
FROM nginx:alpine
COPY dist/ /usr/share/nginx/html/
COPY nginx.conf /etc/nginx/nginx.conf
EXPOSE 80
```

## 📊 性能表现

### 🏆 测试结果
- **前端压力测试**: QPS 61.46/s，平均响应时间 135.78ms
- **后端压力测试**: QPS 210.08/s，平均响应时间 34.99ms
- **成功率**: 100%
- **用户体验**: 优秀的交互和视觉效果

## 🌐 浏览器支持

| 浏览器 | 版本要求 | 状态 |
|--------|----------|------|
| **Chrome** | >= 87 | ✅ 完全支持 |
| **Firefox** | >= 78 | ✅ 完全支持 |
| **Safari** | >= 14 | ✅ 完全支持 |
| **Edge** | >= 88 | ✅ 完全支持 |

## 🚨 故障排除

### 常见问题

1. **API请求失败**
   - 检查后端服务是否启动（端口9000）
   - 检查网络连接
   - 查看浏览器控制台错误信息

2. **高并发测试失败**
   - 检查目标API是否正常
   - 降低并发数重新测试
   - 查看详细错误信息

3. **页面空白**
   - 检查JavaScript控制台错误
   - 确认依赖是否正确安装
   - 清除浏览器缓存

4. **性能图表不显示**
   - 检查Canvas API支持
   - 确认浏览器版本
   - 刷新页面重试

## 🎊 功能亮点

### ✅ 已实现功能
- ✅ **API测试平台**: 支持所有微服务API测试
- ✅ **高并发压力测试**: 前端+后端双重测试模式
- ✅ **服务状态监控**: 实时健康检查
- ✅ **性能可视化**: 图表展示和趋势分析
- ✅ **系统监控**: CPU、内存资源监控
- ✅ **响应式设计**: 支持各种设备

---

**🎉 现在你拥有了一个功能完整的微服务高并发测试平台前端！**
