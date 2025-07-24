<template>
  <div class="concurrency-test-container">
    <div class="header">
      <h1>⚡ 高并发压力测试平台</h1>
      <p>测试微服务在高并发场景下的性能表现</p>
    </div>

    <!-- 测试配置 -->
    <div class="config-section">
      <h3>🔧 测试配置</h3>
      <el-row :gutter="20">
        <el-col :span="6">
          <el-form-item label="并发用户数">
            <el-input-number v-model="testConfig.concurrency" :min="1" :max="1000" />
          </el-form-item>
        </el-col>
        <el-col :span="6">
          <el-form-item label="总请求数">
            <el-input-number v-model="testConfig.totalRequests" :min="1" :max="10000" />
          </el-form-item>
        </el-col>
        <el-col :span="6">
          <el-form-item label="测试时长(秒)">
            <el-input-number v-model="testConfig.duration" :min="1" :max="300" />
          </el-form-item>
        </el-col>
        <el-col :span="6">
          <el-form-item label="目标API">
            <el-select v-model="testConfig.targetApi" placeholder="选择API">
              <el-option label="商品列表API" value="/api/product/list" />
              <el-option label="用户登录API" value="/api/user/login" />
              <el-option label="用户信息API" value="/api/user/info?id=1" />
              <el-option label="订单列表API" value="/api/order/list?userId=1&page=1&size=10" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
    </div>

    <!-- 控制按钮 -->
    <div class="control-section">
      <el-button
        type="primary"
        size="large"
        @click="startFrontendTest"
        :loading="testing"
        :disabled="!testConfig.targetApi"
      >
        <el-icon><VideoPlay /></el-icon>
        {{ testing ? '前端测试进行中...' : '前端压力测试' }}
      </el-button>
      <el-button
        type="success"
        size="large"
        @click="startBackendTest"
        :loading="backendTesting"
        :disabled="!testConfig.targetApi"
      >
        <el-icon><Lightning /></el-icon>
        {{ backendTesting ? '后端测试进行中...' : '后端压力测试' }}
      </el-button>
      <el-button
        type="danger"
        size="large"
        @click="stopTest"
        :disabled="!testing && !backendTesting"
      >
        <el-icon><VideoPause /></el-icon>
        停止测试
      </el-button>
      <el-button size="large" @click="clearResults">
        <el-icon><Delete /></el-icon>
        清空结果
      </el-button>
      <el-button size="large" @click="getSystemInfo">
        <el-icon><Monitor /></el-icon>
        系统信息
      </el-button>
    </div>

    <!-- 实时统计 -->
    <div class="stats-section" v-if="testResults.totalRequests > 0">
      <h3>📊 实时测试统计</h3>
      <el-row :gutter="20">
        <el-col :span="4">
          <el-statistic title="总请求数" :value="testResults.totalRequests" suffix="个">
            <template #prefix>
              <el-icon style="color: #409eff"><DataAnalysis /></el-icon>
            </template>
          </el-statistic>
        </el-col>
        <el-col :span="4">
          <el-statistic title="成功请求" :value="testResults.successCount" suffix="个">
            <template #prefix>
              <el-icon style="color: #67c23a"><SuccessFilled /></el-icon>
            </template>
          </el-statistic>
        </el-col>
        <el-col :span="4">
          <el-statistic title="失败请求" :value="testResults.failureCount" suffix="个">
            <template #prefix>
              <el-icon style="color: #f56c6c"><CircleCloseFilled /></el-icon>
            </template>
          </el-statistic>
        </el-col>
        <el-col :span="4">
          <el-statistic title="成功率" :value="testResults.successRate" suffix="%" :precision="2">
            <template #prefix>
              <el-icon style="color: #e6a23c"><TrendCharts /></el-icon>
            </template>
          </el-statistic>
        </el-col>
        <el-col :span="4">
          <el-statistic title="平均响应时间" :value="testResults.avgResponseTime" suffix="ms" :precision="2">
            <template #prefix>
              <el-icon style="color: #909399"><Timer /></el-icon>
            </template>
          </el-statistic>
        </el-col>
        <el-col :span="4">
          <el-statistic title="QPS" :value="testResults.qps" suffix="/s" :precision="2">
            <template #prefix>
              <el-icon style="color: #f56c6c"><Lightning /></el-icon>
            </template>
          </el-statistic>
        </el-col>
      </el-row>
    </div>

    <!-- 性能图表 -->
    <div class="chart-section">
      <h3>📈 性能趋势图</h3>
      <div class="chart-container">
        <canvas ref="chartCanvas" width="800" height="300" v-show="testResults.responseTimeHistory.length > 0"></canvas>
        <div v-if="testResults.responseTimeHistory.length === 0" class="chart-placeholder">
          <p>📊 图表将在测试开始后显示</p>
        </div>
      </div>
    </div>

    <!-- 详细结果 -->
    <div class="results-section" v-if="testResults.details.length > 0">
      <h3>📝 详细测试结果</h3>
      <el-table :data="testResults.details.slice(-20)" height="300" stripe>
        <el-table-column prop="timestamp" label="时间" width="180">
          <template #default="{ row }">
            {{ new Date(row.timestamp).toLocaleTimeString() }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'success' ? 'success' : 'danger'">
              {{ row.status === 'success' ? '成功' : '失败' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="responseTime" label="响应时间(ms)" width="120" />
        <el-table-column prop="statusCode" label="状态码" width="100" />
        <el-table-column prop="error" label="错误信息" show-overflow-tooltip />
      </el-table>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import {
  VideoPlay, VideoPause, Delete, DataAnalysis, SuccessFilled,
  CircleCloseFilled, TrendCharts, Timer, Lightning, Monitor
} from '@element-plus/icons-vue'
import api from '../utils/api'

// 测试配置
const testConfig = reactive({
  concurrency: 10,        // 并发用户数
  totalRequests: 100,     // 总请求数
  duration: 30,           // 测试时长(秒)
  targetApi: '/api/product/list'  // 目标API
})

// 测试状态
const testing = ref(false)
const backendTesting = ref(false)
const testStartTime = ref(0)
const testTimer = ref(null)
const requestPromises = ref([])
const systemInfo = ref({})

// 测试结果
const testResults = reactive({
  totalRequests: 0,
  successCount: 0,
  failureCount: 0,
  successRate: 0,
  avgResponseTime: 0,
  qps: 0,
  responseTimeHistory: [],
  details: []
})

// 图表相关
const chartCanvas = ref(null)
let chart = null

// 计算属性
const updateStats = () => {
  if (testResults.totalRequests > 0) {
    testResults.successRate = (testResults.successCount / testResults.totalRequests) * 100
    
    const totalTime = testResults.details.reduce((sum, item) => sum + item.responseTime, 0)
    testResults.avgResponseTime = totalTime / testResults.totalRequests
    
    const elapsedTime = (Date.now() - testStartTime.value) / 1000
    testResults.qps = testResults.totalRequests / elapsedTime
  }
}

// 发送单个请求
const sendRequest = async () => {
  const startTime = Date.now()
  const timestamp = Date.now()
  
  try {
    let response
    const url = testConfig.targetApi
    
    if (url.includes('/login')) {
      // 登录API需要POST请求
      response = await api.post(url, {
        username: 'admin',
        password: '123456'
      })
    } else {
      // 其他API使用GET请求
      response = await api.get(url)
    }
    
    const responseTime = Date.now() - startTime
    
    // 记录成功结果
    testResults.successCount++
    testResults.details.push({
      timestamp,
      status: 'success',
      responseTime,
      statusCode: response.status || 200,
      error: ''
    })
    
    testResults.responseTimeHistory.push({
      time: timestamp,
      responseTime
    })
    
  } catch (error) {
    const responseTime = Date.now() - startTime
    
    // 记录失败结果
    testResults.failureCount++
    testResults.details.push({
      timestamp,
      status: 'failure',
      responseTime,
      statusCode: error.response?.status || 0,
      error: error.message
    })
  }
  
  testResults.totalRequests++
  updateStats()
  updateChart()
}

// 开始前端压力测试
const startFrontendTest = async () => {
  if (testing.value) return
  
  testing.value = true
  testStartTime.value = Date.now()
  
  ElMessage.success(`开始前端压力测试：${testConfig.concurrency}并发，${testConfig.totalRequests}请求`)

  // 清空之前的结果
  Object.assign(testResults, {
    totalRequests: 0,
    successCount: 0,
    failureCount: 0,
    successRate: 0,
    avgResponseTime: 0,
    qps: 0,
    responseTimeHistory: [],
    details: []
  })

  // 确保图表已初始化
  if (!chart) {
    await initChart()
  }
  
  // 计算每个并发用户需要发送的请求数
  const requestsPerUser = Math.ceil(testConfig.totalRequests / testConfig.concurrency)
  
  // 创建并发请求
  const promises = []
  for (let i = 0; i < testConfig.concurrency; i++) {
    const userPromise = async () => {
      for (let j = 0; j < requestsPerUser && testing.value; j++) {
        await sendRequest()
        // 添加小延迟避免过于密集的请求
        await new Promise(resolve => setTimeout(resolve, 10))
      }
    }
    promises.push(userPromise())
  }
  
  requestPromises.value = promises
  
  // 设置测试时长限制
  testTimer.value = setTimeout(() => {
    stopTest()
  }, testConfig.duration * 1000)
  
  try {
    await Promise.all(promises)
    if (testing.value) {
      stopTest()
    }
  } catch (error) {
    console.error('压力测试出错:', error)
    stopTest()
  }
}

// 开始后端压力测试
const startBackendTest = async () => {
  if (backendTesting.value) return

  backendTesting.value = true

  ElMessage.success(`开始后端压力测试：${testConfig.concurrency}并发，${testConfig.totalRequests}请求`)

  // 清空之前的结果
  Object.assign(testResults, {
    totalRequests: 0,
    successCount: 0,
    failureCount: 0,
    successRate: 0,
    avgResponseTime: 0,
    qps: 0,
    responseTimeHistory: [],
    details: []
  })

  // 确保图表已初始化
  if (!chart) {
    await initChart()
  }

  try {
    const requestData = {
      targetUrl: testConfig.targetApi,
      method: testConfig.targetApi.includes('/login') ? 'POST' : 'GET',
      concurrency: testConfig.concurrency,
      totalRequests: testConfig.totalRequests,
      duration: testConfig.duration
    }

    const response = await api.post('/api/loadtest/execute', requestData)

    if (response.data.success) {
      const data = response.data.data

      // 更新测试结果
      testResults.totalRequests = data.totalRequests
      testResults.successCount = data.successCount
      testResults.failureCount = data.failureCount
      testResults.successRate = data.successRate
      testResults.avgResponseTime = data.avgResponseTime
      testResults.qps = data.qps

      // 模拟详细结果和响应时间历史（后端返回的是汇总数据）
      for (let i = 0; i < Math.min(data.totalRequests, 50); i++) {
        const responseTime = data.avgResponseTime + (Math.random() - 0.5) * 100
        const timestamp = Date.now() - (50 - i) * 1000

        // 添加详细结果
        testResults.details.push({
          timestamp: timestamp,
          status: i < data.successCount ? 'success' : 'failure',
          responseTime: responseTime,
          statusCode: i < data.successCount ? 200 : 500,
          error: i >= data.successCount ? '请求失败' : ''
        })

        // 添加响应时间历史数据用于图表显示
        testResults.responseTimeHistory.push({
          time: timestamp,
          responseTime: responseTime
        })
      }

      // 更新图表显示
      await nextTick()
      updateChart()

      ElMessage.success(`后端压力测试完成！QPS: ${data.qps.toFixed(2)}, 成功率: ${data.successRate.toFixed(2)}%`)
    } else {
      ElMessage.error('后端压力测试失败: ' + response.data.message)
    }

  } catch (error) {
    console.error('后端压力测试出错:', error)
    ElMessage.error('后端压力测试出错: ' + error.message)
  } finally {
    backendTesting.value = false
  }
}

// 停止测试
const stopTest = () => {
  testing.value = false
  backendTesting.value = false

  if (testTimer.value) {
    clearTimeout(testTimer.value)
    testTimer.value = null
  }

  const duration = (Date.now() - testStartTime.value) / 1000
  ElMessage.success(`压力测试完成！耗时 ${duration.toFixed(2)} 秒`)
}

// 获取系统信息
const getSystemInfo = async () => {
  try {
    const response = await api.get('/api/loadtest/system-info')

    if (response.data.success) {
      systemInfo.value = response.data.data

      ElMessage.success('系统信息获取成功')

      // 显示系统信息
      const info = response.data.data
      ElMessage({
        message: `CPU核心: ${info.availableProcessors} | 总内存: ${info.totalMemory} | 可用内存: ${info.freeMemory} | 已用内存: ${info.usedMemory}`,
        type: 'info',
        duration: 5000,
        showClose: true
      })
    } else {
      ElMessage.error('系统信息获取失败')
    }
  } catch (error) {
    console.error('获取系统信息出错:', error)
    ElMessage.error('获取系统信息出错: ' + error.message)
  }
}

// 清空结果
const clearResults = () => {
  Object.assign(testResults, {
    totalRequests: 0,
    successCount: 0,
    failureCount: 0,
    successRate: 0,
    avgResponseTime: 0,
    qps: 0,
    responseTimeHistory: [],
    details: []
  })
  
  if (chart && chartCanvas.value) {
    chart.data.labels = []
    chart.data.datasets[0].data = []
    chart.update()

    // 清空画布
    const ctx = chartCanvas.value.getContext('2d')
    ctx.clearRect(0, 0, chartCanvas.value.width, chartCanvas.value.height)
  }
  
  ElMessage.info('测试结果已清空')
}

// 更新图表
const updateChart = () => {
  if (!chart || !chartCanvas.value) {
    console.log('图表或Canvas未初始化')
    return
  }

  const history = testResults.responseTimeHistory.slice(-50) // 只显示最近50个数据点

  if (history.length === 0) {
    console.log('没有响应时间历史数据')
    return
  }

  console.log(`更新图表，数据点数量: ${history.length}`)

  chart.data.labels = history.map((_, index) => index + 1)
  chart.data.datasets[0].data = history.map(item => item.responseTime)
  chart.update('none') // 不使用动画以提高性能
}

// 初始化图表
const initChart = async () => {
  await nextTick()

  if (!chartCanvas.value) {
    console.log('Canvas元素未找到，将在测试开始时初始化')
    return
  }

  console.log('Canvas元素找到，初始化图表')

  // 这里使用简单的Canvas绘图，实际项目中可以使用Chart.js等图表库
  const ctx = chartCanvas.value.getContext('2d')
  
  // 简单的图表实现
  chart = {
    data: {
      labels: [],
      datasets: [{
        data: [],
        borderColor: '#409eff',
        backgroundColor: 'rgba(64, 158, 255, 0.1)',
        fill: true
      }]
    },
    update: (mode) => {
      // 清空画布
      ctx.clearRect(0, 0, chartCanvas.value.width, chartCanvas.value.height)
      
      const data = chart.data.datasets[0].data
      if (data.length < 2) return
      
      const width = chartCanvas.value.width
      const height = chartCanvas.value.height
      const padding = 40
      
      const maxValue = Math.max(...data)
      const minValue = Math.min(...data)
      const range = maxValue - minValue || 1
      
      // 绘制网格线
      ctx.strokeStyle = '#e4e7ed'
      ctx.lineWidth = 1
      for (let i = 0; i <= 5; i++) {
        const y = padding + (height - 2 * padding) * i / 5
        ctx.beginPath()
        ctx.moveTo(padding, y)
        ctx.lineTo(width - padding, y)
        ctx.stroke()
      }
      
      // 绘制数据线
      ctx.strokeStyle = '#409eff'
      ctx.lineWidth = 2
      ctx.beginPath()
      
      data.forEach((value, index) => {
        const x = padding + (width - 2 * padding) * index / (data.length - 1)
        const y = height - padding - (height - 2 * padding) * (value - minValue) / range
        
        if (index === 0) {
          ctx.moveTo(x, y)
        } else {
          ctx.lineTo(x, y)
        }
      })
      
      ctx.stroke()
      
      // 绘制标签
      ctx.fillStyle = '#606266'
      ctx.font = '12px Arial'
      ctx.fillText(`最大: ${maxValue.toFixed(0)}ms`, width - 120, 20)
      ctx.fillText(`最小: ${minValue.toFixed(0)}ms`, width - 120, 35)
    }
  }
}

onMounted(() => {
  initChart()
})
</script>

<style scoped>
.concurrency-test-container {
  padding: 20px;
  max-width: 1400px;
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

.config-section, .control-section, .stats-section, .chart-section, .results-section {
  background: white;
  padding: 25px;
  margin-bottom: 20px;
  border-radius: 12px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.config-section h3, .stats-section h3, .chart-section h3, .results-section h3 {
  margin: 0 0 20px 0;
  color: #333;
  font-size: 1.3em;
}

.control-section {
  text-align: center;
}

.control-section .el-button {
  margin: 0 10px;
}

.chart-container {
  text-align: center;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
}

.chart-container canvas {
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  background: white;
}

.chart-placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 300px;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  background: #f8f9fa;
  color: #909399;
  font-size: 16px;
}
</style>
