@echo off
echo ========================================
echo         前端服务启动脚本
echo ========================================
echo.

echo [1/3] 检查Node.js环境...
node --version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Node.js未安装，请先安装Node.js 16+
    pause
    exit /b 1
)
echo ✅ Node.js环境正常

echo.
echo [2/3] 安装依赖...
if not exist node_modules (
    echo 正在安装npm依赖...
    call npm install
    if %errorlevel% neq 0 (
        echo ❌ 依赖安装失败
        pause
        exit /b 1
    )
    echo ✅ 依赖安装成功
) else (
    echo ✅ 依赖已存在，跳过安装
)

echo.
echo [3/3] 启动开发服务器...
echo 🚀 正在启动前端服务...
echo.
call npm run dev
