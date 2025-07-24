<template>
  <div class="api-test-container">
    <el-row :gutter="20">
      <!-- 左侧API列表 -->
      <el-col :span="8">
        <el-card class="api-list-card">
          <template #header>
            <div class="card-header">
              <span>🔌 API接口列表</span>
            </div>
          </template>
          
          <el-collapse v-model="activeServices" accordion>
            <!-- 用户服务 -->
            <el-collapse-item title="👤 用户服务 (User Service)" name="user">
              <div class="api-item" 
                   v-for="api in userApis" 
                   :key="api.key"
                   @click="selectApi(api)"
                   :class="{ active: selectedApi?.key === api.key }">
                <el-tag :type="api.method === 'GET' ? 'success' : 'primary'" size="small">
                  {{ api.method }}
                </el-tag>
                <span class="api-path">{{ api.path }}</span>
                <p class="api-desc">{{ api.description }}</p>
              </div>
            </el-collapse-item>

            <!-- 商品服务 -->
            <el-collapse-item title="🛍️ 商品服务 (Product Service)" name="product">
              <div class="api-item" 
                   v-for="api in productApis" 
                   :key="api.key"
                   @click="selectApi(api)"
                   :class="{ active: selectedApi?.key === api.key }">
                <el-tag :type="api.method === 'GET' ? 'success' : 'primary'" size="small">
                  {{ api.method }}
                </el-tag>
                <span class="api-path">{{ api.path }}</span>
                <p class="api-desc">{{ api.description }}</p>
              </div>
            </el-collapse-item>

            <!-- 订单服务 -->
            <el-collapse-item title="📋 订单服务 (Order Service)" name="order">
              <div class="api-item" 
                   v-for="api in orderApis" 
                   :key="api.key"
                   @click="selectApi(api)"
                   :class="{ active: selectedApi?.key === api.key }">
                <el-tag :type="api.method === 'GET' ? 'success' : 'primary'" size="small">
                  {{ api.method }}
                </el-tag>
                <span class="api-path">{{ api.path }}</span>
                <p class="api-desc">{{ api.description }}</p>
              </div>
            </el-collapse-item>
          </el-collapse>
        </el-card>
      </el-col>

      <!-- 右侧测试区域 -->
      <el-col :span="16">
        <el-card class="test-card" v-if="selectedApi">
          <template #header>
            <div class="card-header">
              <span>🧪 API测试</span>
              <el-button @click="sendRequest" :loading="loading" type="primary">
                发送请求
              </el-button>
            </div>
          </template>

          <!-- 请求信息 -->
          <div class="request-info">
            <h3>{{ selectedApi.description }}</h3>
            <div class="request-line">
              <el-tag :type="selectedApi.method === 'GET' ? 'success' : 'primary'">
                {{ selectedApi.method }}
              </el-tag>
              <el-input v-model="requestUrl" readonly style="margin-left: 10px;">
                <template #prepend>{{ baseUrl }}</template>
              </el-input>
            </div>
          </div>

          <!-- 请求参数 -->
          <div class="request-params" v-if="selectedApi.params">
            <h4>请求参数</h4>
            <el-form :model="requestParams" label-width="120px">
              <el-form-item 
                v-for="param in selectedApi.params" 
                :key="param.name"
                :label="param.name"
              >
                <el-input 
                  v-model="requestParams[param.name]" 
                  :placeholder="param.description"
                  :type="param.type === 'number' ? 'number' : 'text'"
                />
                <div class="param-desc">{{ param.description }}</div>
              </el-form-item>
            </el-form>
          </div>

          <!-- 请求体 -->
          <div class="request-body" v-if="selectedApi.method !== 'GET'">
            <h4>请求体 (JSON)</h4>
            <el-input
              v-model="requestBody"
              type="textarea"
              :rows="6"
              placeholder="请输入JSON格式的请求体"
            />
          </div>

          <!-- 响应结果 -->
          <div class="response-section" v-if="response">
            <h4>响应结果</h4>
            <div class="response-status">
              <el-tag :type="response.success ? 'success' : 'danger'">
                状态: {{ response.status }}
              </el-tag>
              <span class="response-time">耗时: {{ response.time }}ms</span>
            </div>
            <el-input
              v-model="response.data"
              type="textarea"
              :rows="10"
              readonly
              class="response-data"
            />
          </div>
        </el-card>

        <!-- 默认提示 -->
        <el-card v-else class="empty-card">
          <el-empty description="请选择左侧的API接口进行测试" />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { ElMessage } from 'element-plus'
import api from '../utils/api'

const activeServices = ref(['user'])
const selectedApi = ref(null)
const loading = ref(false)
const requestParams = reactive({})
const requestBody = ref('')
const response = ref(null)

const baseUrl = 'http://localhost:9000'

const requestUrl = computed(() => {
  if (!selectedApi.value) return ''
  let url = selectedApi.value.path
  
  // 替换路径参数
  if (selectedApi.value.params) {
    selectedApi.value.params.forEach(param => {
      if (param.in === 'path' && requestParams[param.name]) {
        url = url.replace(`{${param.name}}`, requestParams[param.name])
      }
    })
  }
  
  return url
})

// 用户服务API
const userApis = [
  {
    key: 'user-login',
    method: 'POST',
    path: '/api/user/login',
    description: '用户登录',
    params: [
      { name: 'username', type: 'string', description: '用户名', in: 'body' },
      { name: 'password', type: 'string', description: '密码', in: 'body' }
    ]
  },
  {
    key: 'user-info',
    method: 'GET',
    path: '/api/user/info',
    description: '获取用户信息',
    params: [
      { name: 'id', type: 'number', description: '用户ID', in: 'query' }
    ]
  }
]

// 商品服务API
const productApis = [
  {
    key: 'product-list',
    method: 'GET',
    path: '/api/product/list',
    description: '获取商品列表'
  },
  {
    key: 'product-detail',
    method: 'GET',
    path: '/api/product/{id}',
    description: '获取商品详情',
    params: [
      { name: 'id', type: 'number', description: '商品ID', in: 'path' }
    ]
  },
  {
    key: 'seckill-start',
    method: 'POST',
    path: '/api/seckill/start',
    description: '开始秒杀',
    params: [
      { name: 'productId', type: 'number', description: '商品ID', in: 'body' },
      { name: 'userId', type: 'number', description: '用户ID', in: 'body' }
    ]
  }
]

// 订单服务API
const orderApis = [
  {
    key: 'order-list',
    method: 'GET',
    path: '/api/order/list',
    description: '获取订单列表',
    params: [
      { name: 'userId', type: 'number', description: '用户ID', in: 'query' },
      { name: 'page', type: 'number', description: '页码', in: 'query' },
      { name: 'size', type: 'number', description: '每页大小', in: 'query' }
    ]
  },
  {
    key: 'order-detail',
    method: 'GET',
    path: '/api/order/{id}',
    description: '获取订单详情',
    params: [
      { name: 'id', type: 'string', description: '订单ID', in: 'path' }
    ]
  },
  {
    key: 'order-cancel',
    method: 'POST',
    path: '/api/order/cancel/{id}',
    description: '取消订单',
    params: [
      { name: 'id', type: 'string', description: '订单ID', in: 'path' }
    ]
  }
]

const selectApi = (apiInfo) => {
  selectedApi.value = apiInfo
  response.value = null
  
  // 重置参数
  Object.keys(requestParams).forEach(key => {
    delete requestParams[key]
  })
  
  // 设置默认参数值
  if (apiInfo.params) {
    apiInfo.params.forEach(param => {
      if (param.name === 'id' && param.type === 'number') {
        requestParams[param.name] = 1
      } else if (param.name === 'userId') {
        requestParams[param.name] = 1
      } else if (param.name === 'page') {
        requestParams[param.name] = 1
      } else if (param.name === 'size') {
        requestParams[param.name] = 10
      }
    })
  }
  
  // 设置默认请求体
  if (apiInfo.method !== 'GET') {
    if (apiInfo.key === 'user-login') {
      requestBody.value = JSON.stringify({
        username: 'admin',
        password: '123456'
      }, null, 2)
    } else if (apiInfo.key === 'seckill-start') {
      requestBody.value = JSON.stringify({
        productId: 1,
        userId: 1
      }, null, 2)
    } else {
      requestBody.value = '{}'
    }
  }
}

const sendRequest = async () => {
  if (!selectedApi.value) return
  
  loading.value = true
  const startTime = Date.now()
  
  try {
    let config = {
      method: selectedApi.value.method,
      url: requestUrl.value
    }
    
    // 添加查询参数
    if (selectedApi.value.params) {
      const queryParams = {}
      selectedApi.value.params.forEach(param => {
        if (param.in === 'query' && requestParams[param.name]) {
          queryParams[param.name] = requestParams[param.name]
        }
      })
      if (Object.keys(queryParams).length > 0) {
        config.params = queryParams
      }
    }
    
    // 添加请求体
    if (selectedApi.value.method !== 'GET' && requestBody.value) {
      try {
        config.data = JSON.parse(requestBody.value)
      } catch (e) {
        ElMessage.error('请求体JSON格式错误')
        return
      }
    }
    
    const result = await api(config)
    const endTime = Date.now()
    
    response.value = {
      success: true,
      status: result.status,
      time: endTime - startTime,
      data: JSON.stringify(result.data, null, 2)
    }
    
    ElMessage.success('请求成功')
    
  } catch (error) {
    const endTime = Date.now()
    
    response.value = {
      success: false,
      status: error.response?.status || 'Network Error',
      time: endTime - startTime,
      data: JSON.stringify({
        error: error.message,
        details: error.response?.data || 'Network connection failed'
      }, null, 2)
    }
    
    ElMessage.error('请求失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.api-test-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.api-list-card {
  height: calc(100vh - 140px);
  overflow-y: auto;
}

.api-item {
  padding: 12px;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  margin-bottom: 8px;
  cursor: pointer;
  transition: all 0.3s;
}

.api-item:hover {
  border-color: #409eff;
  background-color: #f0f9ff;
}

.api-item.active {
  border-color: #409eff;
  background-color: #e6f7ff;
}

.api-path {
  margin-left: 8px;
  font-family: 'Courier New', monospace;
  font-weight: bold;
}

.api-desc {
  margin: 4px 0 0 0;
  font-size: 12px;
  color: #666;
}

.test-card {
  min-height: calc(100vh - 140px);
}

.empty-card {
  height: calc(100vh - 140px);
  display: flex;
  align-items: center;
  justify-content: center;
}

.request-info {
  margin-bottom: 20px;
}

.request-line {
  display: flex;
  align-items: center;
  margin-top: 10px;
}

.request-params,
.request-body,
.response-section {
  margin-top: 20px;
}

.param-desc {
  font-size: 12px;
  color: #999;
  margin-top: 4px;
}

.response-status {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
}

.response-time {
  color: #666;
  font-size: 14px;
}

.response-data {
  font-family: 'Courier New', monospace;
}
</style>
