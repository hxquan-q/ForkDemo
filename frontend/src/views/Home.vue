<template>
  <div class="home-container">
    <h1>🧪 微服务API测试平台</h1>
    <p>欢迎使用微服务API测试平台！</p>
    
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card>
          <template #header>
            <span>🔌 API测试</span>
          </template>
          <p>测试各个微服务的API接口</p>
          <el-button type="primary" @click="$router.push('/api-test')">
            开始测试
          </el-button>
        </el-card>
      </el-col>
      
      <el-col :span="8">
        <el-card>
          <template #header>
            <span>📊 服务状态</span>
          </template>
          <p>查看微服务运行状态</p>
          <el-button type="success" @click="$router.push('/service-status')">
            查看状态
          </el-button>
        </el-card>
      </el-col>
      
      <el-col :span="8">
        <el-card>
          <template #header>
            <span>📚 接口文档</span>
          </template>
          <p>查看API接口文档</p>
          <el-button type="info" @click="$router.push('/docs')">
            查看文档
          </el-button>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
// 简单的首页组件
</script>

<style scoped>
.home-container {
  padding: 40px;
  text-align: center;
}

.home-container h1 {
  color: #333;
  margin-bottom: 20px;
}

.home-container p {
  color: #666;
  margin-bottom: 40px;
}
</style>
