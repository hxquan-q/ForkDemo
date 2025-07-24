#!/bin/bash

echo "========================================"
echo "           秒杀系统启动脚本"
echo "========================================"
echo

echo "[1/4] 检查Docker环境..."
if ! command -v docker &> /dev/null; then
    echo "❌ Docker未安装，请先安装Docker"
    exit 1
fi

if ! docker info &> /dev/null; then
    echo "❌ Docker未启动，请先启动Docker服务"
    exit 1
fi
echo "✅ Docker环境正常"

echo
echo "[2/4] 构建项目..."
if ! mvn clean package -DskipTests; then
    echo "❌ 项目构建失败"
    exit 1
fi
echo "✅ 项目构建成功"

echo
echo "[3/4] 启动后端服务..."
if ! docker-compose up -d; then
    echo "❌ 后端服务启动失败"
    exit 1
fi
echo "✅ 后端服务启动成功"

echo
echo "[4/4] 等待服务就绪..."
sleep 30
echo "✅ 服务就绪"

echo
echo "========================================"
echo "           启动完成！"
echo "========================================"
echo
echo "🌐 访问地址："
echo "  前端界面: http://localhost:3001"
echo "  Nacos控制台: http://localhost:8848/nacos (nacos/nacos)"
echo "  RabbitMQ管理: http://localhost:15672 (guest/guest)"
echo "  XXL-Job控制台: http://localhost:8080/xxl-job-admin (admin/123456)"
echo
echo "📋 服务状态："
docker-compose ps
echo
echo "🚀 启动前端服务..."
echo "  请在新的终端窗口中执行："
echo "  cd frontend"
echo "  npm install"
echo "  npm run dev"
echo
