<template>
  <div id="app">
    <el-container>
      <!-- 头部导航 -->
      <el-header class="header">
        <div class="header-content">
          <h1 class="title">🧪 微服务API测试平台</h1>
          <div class="nav-menu">
            <el-menu
              mode="horizontal"
              :default-active="$route.path"
              background-color="transparent"
              text-color="white"
              active-text-color="#ffd04b"
              @select="handleMenuSelect"
            >
              <el-menu-item index="/">🏠 首页</el-menu-item>
              <el-menu-item index="/api-test">🧪 API测试</el-menu-item>
              <el-menu-item index="/concurrency-test">⚡ 高并发测试</el-menu-item>
              <el-menu-item index="/service-status">📊 服务状态</el-menu-item>
              <el-menu-item index="/docs">📚 接口文档</el-menu-item>
            </el-menu>
          </div>
          <div class="service-info">
            <el-tag type="success" size="small">网关: localhost:9000</el-tag>
          </div>
        </div>
      </el-header>

      <!-- 主要内容 -->
      <el-main class="main">
        <router-view />
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

const handleMenuSelect = (index) => {
  router.push(index)
}
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

#app {
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', '微软雅黑', Arial, sans-serif;
  height: 100vh;
}

.header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 0;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 100%;
  padding: 0 20px;
}

.nav-menu {
  flex: 1;
  display: flex;
  justify-content: center;
}

.nav-menu .el-menu {
  border-bottom: none;
}

.title {
  font-size: 24px;
  font-weight: bold;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.main {
  background-color: #f5f5f5;
  min-height: calc(100vh - 60px);
}
</style>
