<template>
  <div class="api-docs-container">
    <div class="header-section">
      <h2>📚 API接口文档</h2>
    </div>

    <el-tabs v-model="activeTab" type="border-card">
      <!-- 用户服务 -->
      <el-tab-pane label="👤 用户服务" name="user">
        <div class="service-intro">
          <h3>用户服务 (User Service)</h3>
          <p>负责用户认证、用户信息管理等功能</p>
          <p><strong>服务地址:</strong> http://localhost:9001</p>
        </div>

        <div class="api-section">
          <h4>POST /api/user/login</h4>
          <p class="api-desc">用户登录接口</p>
          
          <el-descriptions :column="1" border>
            <el-descriptions-item label="请求方式">POST</el-descriptions-item>
            <el-descriptions-item label="请求路径">/api/user/login</el-descriptions-item>
            <el-descriptions-item label="Content-Type">application/json</el-descriptions-item>
          </el-descriptions>

          <h5>请求参数</h5>
          <el-table :data="userLoginParams" border>
            <el-table-column prop="name" label="参数名" width="120" />
            <el-table-column prop="type" label="类型" width="100" />
            <el-table-column prop="required" label="必填" width="80">
              <template #default="scope">
                <el-tag :type="scope.row.required ? 'danger' : 'info'" size="small">
                  {{ scope.row.required ? '是' : '否' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="description" label="说明" />
          </el-table>

          <h5>请求示例</h5>
          <pre class="code-block">{{ userLoginExample }}</pre>

          <h5>响应示例</h5>
          <pre class="code-block">{{ userLoginResponse }}</pre>
        </div>

        <div class="api-section">
          <h4>GET /api/user/info</h4>
          <p class="api-desc">获取用户信息</p>
          
          <el-descriptions :column="1" border>
            <el-descriptions-item label="请求方式">GET</el-descriptions-item>
            <el-descriptions-item label="请求路径">/api/user/info</el-descriptions-item>
          </el-descriptions>

          <h5>请求参数</h5>
          <el-table :data="userInfoParams" border>
            <el-table-column prop="name" label="参数名" width="120" />
            <el-table-column prop="type" label="类型" width="100" />
            <el-table-column prop="required" label="必填" width="80">
              <template #default="scope">
                <el-tag :type="scope.row.required ? 'danger' : 'info'" size="small">
                  {{ scope.row.required ? '是' : '否' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="description" label="说明" />
          </el-table>

          <h5>响应示例</h5>
          <pre class="code-block">{{ userInfoResponse }}</pre>
        </div>
      </el-tab-pane>

      <!-- 商品服务 -->
      <el-tab-pane label="🛍️ 商品服务" name="product">
        <div class="service-intro">
          <h3>商品服务 (Product Service)</h3>
          <p>负责商品管理、库存管理、秒杀逻辑等功能</p>
          <p><strong>服务地址:</strong> http://localhost:9002</p>
        </div>

        <div class="api-section">
          <h4>GET /api/product/list</h4>
          <p class="api-desc">获取商品列表</p>
          
          <el-descriptions :column="1" border>
            <el-descriptions-item label="请求方式">GET</el-descriptions-item>
            <el-descriptions-item label="请求路径">/api/product/list</el-descriptions-item>
          </el-descriptions>

          <h5>响应示例</h5>
          <pre class="code-block">{{ productListResponse }}</pre>
        </div>

        <div class="api-section">
          <h4>POST /api/seckill/start</h4>
          <p class="api-desc">开始秒杀</p>
          
          <el-descriptions :column="1" border>
            <el-descriptions-item label="请求方式">POST</el-descriptions-item>
            <el-descriptions-item label="请求路径">/api/seckill/start</el-descriptions-item>
            <el-descriptions-item label="Content-Type">application/json</el-descriptions-item>
          </el-descriptions>

          <h5>请求参数</h5>
          <el-table :data="seckillParams" border>
            <el-table-column prop="name" label="参数名" width="120" />
            <el-table-column prop="type" label="类型" width="100" />
            <el-table-column prop="required" label="必填" width="80">
              <template #default="scope">
                <el-tag :type="scope.row.required ? 'danger' : 'info'" size="small">
                  {{ scope.row.required ? '是' : '否' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="description" label="说明" />
          </el-table>

          <h5>请求示例</h5>
          <pre class="code-block">{{ seckillExample }}</pre>

          <h5>响应示例</h5>
          <pre class="code-block">{{ seckillResponse }}</pre>
        </div>
      </el-tab-pane>

      <!-- 订单服务 -->
      <el-tab-pane label="📋 订单服务" name="order">
        <div class="service-intro">
          <h3>订单服务 (Order Service)</h3>
          <p>负责订单创建、订单查询、订单状态管理等功能</p>
          <p><strong>服务地址:</strong> http://localhost:9003</p>
        </div>

        <div class="api-section">
          <h4>GET /api/order/list</h4>
          <p class="api-desc">获取订单列表</p>
          
          <el-descriptions :column="1" border>
            <el-descriptions-item label="请求方式">GET</el-descriptions-item>
            <el-descriptions-item label="请求路径">/api/order/list</el-descriptions-item>
          </el-descriptions>

          <h5>请求参数</h5>
          <el-table :data="orderListParams" border>
            <el-table-column prop="name" label="参数名" width="120" />
            <el-table-column prop="type" label="类型" width="100" />
            <el-table-column prop="required" label="必填" width="80">
              <template #default="scope">
                <el-tag :type="scope.row.required ? 'danger' : 'info'" size="small">
                  {{ scope.row.required ? '是' : '否' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="description" label="说明" />
          </el-table>

          <h5>响应示例</h5>
          <pre class="code-block">{{ orderListResponse }}</pre>
        </div>
      </el-tab-pane>

      <!-- 通用说明 -->
      <el-tab-pane label="📖 通用说明" name="common">
        <div class="common-docs">
          <h3>通用响应格式</h3>
          <p>所有API接口都遵循统一的响应格式：</p>
          <pre class="code-block">{
  "success": true,
  "code": 200,
  "message": "操作成功",
  "data": { ... }
}</pre>

          <h3>状态码说明</h3>
          <el-table :data="statusCodes" border>
            <el-table-column prop="code" label="状态码" width="100" />
            <el-table-column prop="message" label="说明" />
          </el-table>

          <h3>错误处理</h3>
          <p>当请求失败时，响应格式如下：</p>
          <pre class="code-block">{
  "success": false,
  "code": 400,
  "message": "请求参数错误",
  "data": null
}</pre>

          <h3>认证说明</h3>
          <p>部分接口需要用户认证，请在请求头中添加：</p>
          <pre class="code-block">Authorization: Bearer &lt;token&gt;</pre>
        </div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const activeTab = ref('user')

// 用户登录参数
const userLoginParams = [
  { name: 'username', type: 'string', required: true, description: '用户名' },
  { name: 'password', type: 'string', required: true, description: '密码' }
]

// 用户信息参数
const userInfoParams = [
  { name: 'id', type: 'number', required: true, description: '用户ID' }
]

// 秒杀参数
const seckillParams = [
  { name: 'productId', type: 'number', required: true, description: '商品ID' },
  { name: 'userId', type: 'number', required: true, description: '用户ID' }
]

// 订单列表参数
const orderListParams = [
  { name: 'userId', type: 'number', required: true, description: '用户ID' },
  { name: 'page', type: 'number', required: false, description: '页码，默认1' },
  { name: 'size', type: 'number', required: false, description: '每页大小，默认10' }
]

// 状态码
const statusCodes = [
  { code: 200, message: '请求成功' },
  { code: 400, message: '请求参数错误' },
  { code: 401, message: '未授权' },
  { code: 403, message: '禁止访问' },
  { code: 404, message: '资源不存在' },
  { code: 500, message: '服务器内部错误' }
]

// 示例数据
const userLoginExample = `{
  "username": "admin",
  "password": "123456"
}`

const userLoginResponse = `{
  "success": true,
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "user": {
      "id": 1,
      "username": "admin",
      "phone": "13800000000",
      "email": "admin@example.com"
    }
  }
}`

const userInfoResponse = `{
  "success": true,
  "code": 200,
  "message": "获取成功",
  "data": {
    "id": 1,
    "username": "admin",
    "phone": "13800000000",
    "email": "admin@example.com",
    "createTime": "2024-01-01T00:00:00"
  }
}`

const productListResponse = `{
  "success": true,
  "code": 200,
  "message": "获取成功",
  "data": [
    {
      "id": 1,
      "name": "iPhone 15 Pro",
      "description": "Apple iPhone 15 Pro 256GB",
      "price": 8999.00,
      "seckillPrice": 7999.00,
      "stock": 100,
      "startTime": "2024-01-01T10:00:00",
      "endTime": "2024-01-01T12:00:00"
    }
  ]
}`

const seckillExample = `{
  "productId": 1,
  "userId": 1
}`

const seckillResponse = `{
  "success": true,
  "code": 200,
  "message": "秒杀成功",
  "data": {
    "orderId": "ORD20240101001",
    "productId": 1,
    "userId": 1,
    "status": "SUCCESS"
  }
}`

const orderListResponse = `{
  "success": true,
  "code": 200,
  "message": "获取成功",
  "data": {
    "records": [
      {
        "id": "ORD20240101001",
        "productName": "iPhone 15 Pro",
        "quantity": 1,
        "price": 7999.00,
        "totalAmount": 7999.00,
        "status": 1,
        "createTime": "2024-01-01T10:30:00"
      }
    ],
    "total": 1,
    "current": 1,
    "size": 10
  }
}`
</script>

<style scoped>
.api-docs-container {
  padding: 20px;
}

.header-section {
  margin-bottom: 20px;
}

.header-section h2 {
  color: #333;
  margin: 0;
}

.service-intro {
  margin-bottom: 30px;
  padding: 20px;
  background-color: #f8f9fa;
  border-radius: 8px;
}

.service-intro h3 {
  margin: 0 0 10px 0;
  color: #333;
}

.service-intro p {
  margin: 5px 0;
  color: #666;
}

.api-section {
  margin-bottom: 40px;
  padding-bottom: 20px;
  border-bottom: 1px solid #e4e7ed;
}

.api-section h4 {
  color: #409eff;
  margin: 0 0 10px 0;
  font-family: 'Courier New', monospace;
}

.api-desc {
  color: #666;
  margin-bottom: 20px;
}

.api-section h5 {
  margin: 20px 0 10px 0;
  color: #333;
}

.code-block {
  background-color: #f6f8fa;
  border: 1px solid #e1e4e8;
  border-radius: 6px;
  padding: 16px;
  font-family: 'Courier New', monospace;
  font-size: 14px;
  line-height: 1.45;
  overflow-x: auto;
  white-space: pre;
}

.common-docs h3 {
  margin: 30px 0 15px 0;
  color: #333;
}

.common-docs p {
  margin: 10px 0;
  color: #666;
  line-height: 1.6;
}
</style>
