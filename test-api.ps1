Write-Host "========================================" -ForegroundColor Cyan
Write-Host "         Microservice API Test" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

Write-Host "[1/4] 测试网关服务..." -ForegroundColor Yellow
try {
    $start = Get-Date
    $response = Invoke-WebRequest -Uri "http://localhost:9000/actuator/health" -TimeoutSec 5
    $end = Get-Date
    $time = ($end - $start).TotalMilliseconds
    Write-Host "✅ 网关服务正常 (状态码: $($response.StatusCode), 响应时间: ${time}ms)" -ForegroundColor Green
} catch {
    Write-Host "❌ 网关服务连接失败: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host ""
Write-Host "[2/4] 测试商品列表API..." -ForegroundColor Yellow
try {
    $start = Get-Date
    $response = Invoke-RestMethod -Uri "http://localhost:9000/api/product/list" -Method GET -TimeoutSec 5
    $end = Get-Date
    $time = ($end - $start).TotalMilliseconds
    Write-Host "✅ 商品列表API正常 (返回 $($response.data.Count) 个商品, 响应时间: ${time}ms)" -ForegroundColor Green
} catch {
    Write-Host "❌ 商品列表API失败: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host ""
Write-Host "[3/4] 测试用户登录API..." -ForegroundColor Yellow
try {
    $start = Get-Date
    $body = @{
        username = "admin"
        password = "123456"
    } | ConvertTo-Json
    
    $response = Invoke-RestMethod -Uri "http://localhost:9000/api/user/login" -Method POST -Body $body -ContentType "application/json" -TimeoutSec 5
    $end = Get-Date
    $time = ($end - $start).TotalMilliseconds
    Write-Host "✅ 用户登录API正常 (用户: $($response.data.user.username), 响应时间: ${time}ms)" -ForegroundColor Green
} catch {
    Write-Host "❌ 用户登录API失败: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host ""
Write-Host "[4/4] 测试前端服务..." -ForegroundColor Yellow
try {
    $start = Get-Date
    $response = Invoke-WebRequest -Uri "http://localhost:3001" -TimeoutSec 5
    $end = Get-Date
    $time = ($end - $start).TotalMilliseconds
    Write-Host "✅ 前端服务正常 (状态码: $($response.StatusCode), 响应时间: ${time}ms)" -ForegroundColor Green
} catch {
    Write-Host "❌ 前端服务连接失败: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "           Test Complete!" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Access URLs:" -ForegroundColor White
Write-Host "  API Test Platform: http://localhost:3001" -ForegroundColor Cyan
Write-Host "  Gateway: http://localhost:9000" -ForegroundColor Cyan
Write-Host ""
Write-Host "Available APIs:" -ForegroundColor White
Write-Host "  GET  /api/product/list        - Get product list" -ForegroundColor Gray
Write-Host "  POST /api/user/login          - User login" -ForegroundColor Gray
Write-Host "  GET  /api/user/info?id=1      - Get user info" -ForegroundColor Gray
Write-Host "  GET  /api/order/list?userId=1 - Get order list" -ForegroundColor Gray
Write-Host ""
