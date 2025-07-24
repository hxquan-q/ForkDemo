<template>
  <div class="api-test-container">
    <div class="header">
      <h1>🧪 微服务API测试平台</h1>
      <p>测试后端微服务接口的可用性</p>
    </div>

    <div class="test-section">
      <h3>📋 快速API测试</h3>
      <div class="button-group">
        <el-button type="primary" @click="testProductList" :loading="loading.product">
          测试商品列表
        </el-button>
        <el-button type="success" @click="testUserLogin" :loading="loading.login">
          测试用户登录
        </el-button>
        <el-button type="info" @click="testUserInfo" :loading="loading.userInfo">
          测试用户信息
        </el-button>
        <el-button type="warning" @click="testOrderList" :loading="loading.order">
          测试订单列表
        </el-button>
        <el-button @click="clearResults">清空结果</el-button>
      </div>
    </div>

    <div class="stats-section">
      <h3>📊 测试结果统计</h3>
      <el-row :gutter="20">
        <el-col :span="8">
          <el-statistic title="成功" :value="successCount" suffix="个">
            <template #prefix>
              <el-icon style="color: #67c23a"><SuccessFilled /></el-icon>
            </template>
          </el-statistic>
        </el-col>
        <el-col :span="8">
          <el-statistic title="失败" :value="failCount" suffix="个">
            <template #prefix>
              <el-icon style="color: #f56c6c"><CircleCloseFilled /></el-icon>
            </template>
          </el-statistic>
        </el-col>
        <el-col :span="8">
          <el-statistic title="总计" :value="totalCount" suffix="个">
            <template #prefix>
              <el-icon style="color: #409eff"><DataAnalysis /></el-icon>
            </template>
          </el-statistic>
        </el-col>
      </el-row>
    </div>

    <div class="results-section">
      <h3>📝 测试结果</h3>
      <div v-if="results.length === 0" class="empty-results">
        <el-empty description="暂无测试结果，点击上方按钮开始测试" />
      </div>
      <div v-else>
        <el-timeline>
          <el-timeline-item
            v-for="(result, index) in results"
            :key="index"
            :type="result.success ? 'success' : 'danger'"
            :timestamp="result.timestamp"
          >
            <el-card>
              <template #header>
                <div class="result-header">
                  <span>{{ result.title }}</span>
                  <el-tag :type="result.success ? 'success' : 'danger'">
                    {{ result.success ? '成功' : '失败' }}
                  </el-tag>
                </div>
              </template>
              <p><strong>响应时间:</strong> {{ result.time }}ms</p>
              <el-collapse>
                <el-collapse-item title="查看详细响应" name="1">
                  <pre>{{ JSON.stringify(result.data, null, 2) }}</pre>
                </el-collapse-item>
              </el-collapse>
            </el-card>
          </el-timeline-item>
        </el-timeline>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { SuccessFilled, CircleCloseFilled, DataAnalysis } from '@element-plus/icons-vue'
import api from '../utils/api'

const baseUrl = 'http://localhost:9000'

const loading = reactive({
  product: false,
  login: false,
  userInfo: false,
  order: false
})

const results = ref([])
const successCount = ref(0)
const failCount = ref(0)
const totalCount = ref(0)

function addResult(title, success, data, time) {
  const result = {
    title,
    success,
    data,
    time,
    timestamp: new Date().toLocaleString()
  }
  
  results.value.unshift(result)
  
  if (success) {
    successCount.value++
    ElMessage.success(`${title} 测试成功`)
  } else {
    failCount.value++
    ElMessage.error(`${title} 测试失败`)
  }
  totalCount.value++
}

async function testProductList() {
  loading.product = true
  const startTime = Date.now()
  
  try {
    const response = await api.get('/api/product/list')
    const endTime = Date.now()
    
    addResult('商品列表API', true, response.data, endTime - startTime)
  } catch (error) {
    const endTime = Date.now()
    addResult('商品列表API', false, { 
      error: error.message,
      status: error.response?.status,
      data: error.response?.data 
    }, endTime - startTime)
  } finally {
    loading.product = false
  }
}

async function testUserLogin() {
  loading.login = true
  const startTime = Date.now()
  
  try {
    const response = await api.post('/api/user/login', {
      username: 'admin',
      password: '123456'
    })
    const endTime = Date.now()
    
    addResult('用户登录API', true, response.data, endTime - startTime)
  } catch (error) {
    const endTime = Date.now()
    addResult('用户登录API', false, { 
      error: error.message,
      status: error.response?.status,
      data: error.response?.data 
    }, endTime - startTime)
  } finally {
    loading.login = false
  }
}

async function testUserInfo() {
  loading.userInfo = true
  const startTime = Date.now()
  
  try {
    const response = await api.get('/api/user/info', {
      params: { id: 1 }
    })
    const endTime = Date.now()
    
    addResult('用户信息API', true, response.data, endTime - startTime)
  } catch (error) {
    const endTime = Date.now()
    addResult('用户信息API', false, { 
      error: error.message,
      status: error.response?.status,
      data: error.response?.data 
    }, endTime - startTime)
  } finally {
    loading.userInfo = false
  }
}

async function testOrderList() {
  loading.order = true
  const startTime = Date.now()
  
  try {
    const response = await api.get('/api/order/list', {
      params: { userId: 1, page: 1, size: 10 }
    })
    const endTime = Date.now()
    
    addResult('订单列表API', true, response.data, endTime - startTime)
  } catch (error) {
    const endTime = Date.now()
    addResult('订单列表API', false, { 
      error: error.message,
      status: error.response?.status,
      data: error.response?.data 
    }, endTime - startTime)
  } finally {
    loading.order = false
  }
}

function clearResults() {
  results.value = []
  successCount.value = 0
  failCount.value = 0
  totalCount.value = 0
  ElMessage.info('测试结果已清空')
}
</script>

<style scoped>
.api-test-container {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.header {
  text-align: center;
  margin-bottom: 30px;
  padding: 30px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 12px;
}

.header h1 {
  margin: 0 0 10px 0;
  font-size: 2.5em;
}

.header p {
  margin: 0;
  font-size: 1.2em;
  opacity: 0.9;
}

.test-section, .stats-section, .results-section {
  background: white;
  padding: 25px;
  margin-bottom: 20px;
  border-radius: 12px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.test-section h3, .stats-section h3, .results-section h3 {
  margin: 0 0 20px 0;
  color: #333;
  font-size: 1.3em;
}

.button-group {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.result-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.empty-results {
  text-align: center;
  padding: 40px;
}

pre {
  background: #f8f9fa;
  padding: 15px;
  border-radius: 6px;
  overflow-x: auto;
  font-size: 12px;
  line-height: 1.4;
}
</style>
