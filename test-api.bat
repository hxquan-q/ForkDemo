@echo off
echo ========================================
echo         微服务API测试脚本
echo ========================================
echo.

echo [1/4] 测试网关服务...
curl -s -w "状态码: %%{http_code}, 响应时间: %%{time_total}s\n" -o nul http://localhost:9000/actuator/health
if %errorlevel% neq 0 (
    echo ❌ 网关服务连接失败
) else (
    echo ✅ 网关服务正常
)

echo.
echo [2/4] 测试商品列表API...
curl -s -w "状态码: %%{http_code}, 响应时间: %%{time_total}s\n" -o nul http://localhost:9000/api/product/list
if %errorlevel% neq 0 (
    echo ❌ 商品列表API失败
) else (
    echo ✅ 商品列表API正常
)

echo.
echo [3/4] 测试用户登录API...
curl -s -w "状态码: %%{http_code}, 响应时间: %%{time_total}s\n" -o nul -X POST -H "Content-Type: application/json" -d "{\"username\":\"admin\",\"password\":\"123456\"}" http://localhost:9000/api/user/login
if %errorlevel% neq 0 (
    echo ❌ 用户登录API失败
) else (
    echo ✅ 用户登录API正常
)

echo.
echo [4/4] 测试前端服务...
curl -s -w "状态码: %%{http_code}, 响应时间: %%{time_total}s\n" -o nul http://localhost:3001
if %errorlevel% neq 0 (
    echo ❌ 前端服务连接失败
) else (
    echo ✅ 前端服务正常
)

echo.
echo ========================================
echo           测试完成！
echo ========================================
echo.
echo 🌐 访问地址：
echo   API测试平台: http://localhost:3001
echo   网关地址: http://localhost:9000
echo.
echo 📋 可用的API接口：
echo   GET  /api/product/list        - 获取商品列表
echo   POST /api/user/login          - 用户登录
echo   GET  /api/user/info?id=1      - 获取用户信息
echo   GET  /api/order/list?userId=1 - 获取订单列表
echo.
pause
