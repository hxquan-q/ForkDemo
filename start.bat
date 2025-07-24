@echo off
echo ========================================
echo           秒杀系统启动脚本
echo ========================================
echo.

echo [1/4] 检查Docker环境...
docker --version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Docker未安装或未启动，请先安装Docker Desktop
    pause
    exit /b 1
)
echo ✅ Docker环境正常

echo.
echo [2/4] 构建项目...
call mvn clean package -DskipTests
if %errorlevel% neq 0 (
    echo ❌ 项目构建失败
    pause
    exit /b 1
)
echo ✅ 项目构建成功

echo.
echo [3/4] 启动后端服务...
docker-compose up -d
if %errorlevel% neq 0 (
    echo ❌ 后端服务启动失败
    pause
    exit /b 1
)
echo ✅ 后端服务启动成功

echo.
echo [4/4] 等待服务就绪...
timeout /t 30 /nobreak >nul
echo ✅ 服务就绪

echo.
echo ========================================
echo           启动完成！
echo ========================================
echo.
echo 🌐 访问地址：
echo   前端界面: http://localhost:3001
echo   Nacos控制台: http://localhost:8848/nacos (nacos/nacos)
echo   RabbitMQ管理: http://localhost:15672 (guest/guest)
echo   XXL-Job控制台: http://localhost:8080/xxl-job-admin (admin/123456)
echo.
echo 📋 服务状态：
docker-compose ps
echo.
echo 🚀 启动前端服务...
echo   请在新的命令行窗口中执行：
echo   cd frontend
echo   npm install
echo   npm run dev
echo.
echo 按任意键退出...
pause >nul
