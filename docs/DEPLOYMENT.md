# 🚀 部署指南

## 🎯 概述

本文档详细说明了微服务秒杀系统的部署方法，包括开发环境和生产环境的部署步骤。

## 🔧 环境要求

### 基础环境
- **操作系统**: Windows 10/11, macOS, Linux
- **JDK**: 17 或更高版本
- **Maven**: 3.8 或更高版本
- **Docker**: 20.0 或更高版本
- **Docker Compose**: 2.0 或更高版本
- **Node.js**: 16 或更高版本
- **npm**: 8 或更高版本

### 硬件要求
- **CPU**: 4核心或更多
- **内存**: 8GB 或更多
- **磁盘**: 20GB 可用空间
- **网络**: 稳定的网络连接

## 🐳 Docker部署（推荐）

### 1. 克隆项目
```bash
git clone <repository-url>
cd seckill-system
```

### 2. 一键启动所有服务
```bash
# 构建项目
mvn clean package

# 启动所有服务（包括基础设施和微服务）
docker-compose up -d
```

### 3. 验证部署
```bash
# 查看所有服务状态
docker-compose ps

# 查看服务日志
docker-compose logs -f
```

### 4. 启动前端应用
```bash
cd frontend
npm install
npm run dev
```

### 5. 访问应用
- **前端应用**: http://localhost:3000
- **API网关**: http://localhost:9000
- **Nacos控制台**: http://localhost:8848/nacos

## 🔨 手动部署

### 1. 启动基础设施服务

#### MySQL
```bash
docker run -d \
  --name mysql \
  -p 3306:3306 \
  -e MYSQL_ROOT_PASSWORD=123456 \
  -e MYSQL_DATABASE=seckill \
  mysql:8.0
```

#### Redis
```bash
docker run -d \
  --name redis \
  -p 6379:6379 \
  redis:7.0
```

#### RabbitMQ
```bash
docker run -d \
  --name rabbitmq \
  -p 5672:5672 \
  -p 15672:15672 \
  -e RABBITMQ_DEFAULT_USER=guest \
  -e RABBITMQ_DEFAULT_PASS=guest \
  rabbitmq:3.11-management
```

#### Nacos
```bash
docker run -d \
  --name nacos \
  -p 8848:8848 \
  -e MODE=standalone \
  nacos/nacos-server:v2.2.3
```

### 2. 启动微服务

#### 网关服务
```bash
cd gateway
mvn spring-boot:run
```

#### 用户服务
```bash
cd user
mvn spring-boot:run
```

#### 商品服务
```bash
cd product
mvn spring-boot:run
```

#### 订单服务
```bash
cd order
mvn spring-boot:run
```

#### 任务服务
```bash
cd task
mvn spring-boot:run
```

### 3. 启动前端应用
```bash
cd frontend
npm install
npm run dev
```

## 🏭 生产环境部署

### 1. 环境准备

#### 创建生产配置文件
```bash
# 复制配置文件模板
cp docker-compose.yml docker-compose.prod.yml

# 修改生产环境配置
vim docker-compose.prod.yml
```

#### 修改数据库配置
```yaml
# docker-compose.prod.yml
services:
  mysql:
    environment:
      - MYSQL_ROOT_PASSWORD=your_secure_password
      - MYSQL_DATABASE=seckill_prod
    volumes:
      - mysql_data_prod:/var/lib/mysql
```

### 2. 构建生产镜像
```bash
# 构建所有服务的生产镜像
docker-compose -f docker-compose.prod.yml build

# 或者单独构建某个服务
docker build -t seckill/gateway:prod ./gateway
```

### 3. 启动生产环境
```bash
# 启动生产环境
docker-compose -f docker-compose.prod.yml up -d

# 查看服务状态
docker-compose -f docker-compose.prod.yml ps
```

### 4. 配置反向代理（Nginx）
```nginx
# /etc/nginx/sites-available/seckill
server {
    listen 80;
    server_name your-domain.com;

    # 前端静态文件
    location / {
        root /var/www/seckill/dist;
        try_files $uri $uri/ /index.html;
    }

    # API代理
    location /api/ {
        proxy_pass http://localhost:9000;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }
}
```

## 🔍 健康检查

### 服务健康检查脚本
```bash
#!/bin/bash
# health_check.sh

echo "=== 服务健康检查 ==="

# 检查基础设施服务
echo "检查MySQL..."
docker exec mysql mysqladmin ping -h localhost -u root -p123456

echo "检查Redis..."
docker exec redis redis-cli ping

echo "检查RabbitMQ..."
curl -u guest:guest http://localhost:15672/api/overview

echo "检查Nacos..."
curl http://localhost:8848/nacos/v1/ns/operator/metrics

# 检查微服务
echo "检查网关服务..."
curl http://localhost:9000/actuator/health

echo "检查用户服务..."
curl http://localhost:9001/actuator/health

echo "检查商品服务..."
curl http://localhost:9002/actuator/health

echo "检查订单服务..."
curl http://localhost:9003/actuator/health

echo "=== 健康检查完成 ==="
```

### 自动化监控脚本
```bash
#!/bin/bash
# monitor.sh

while true; do
    echo "$(date): 检查服务状态..."
    
    # 检查关键服务
    if ! curl -f http://localhost:9000/actuator/health > /dev/null 2>&1; then
        echo "网关服务异常，尝试重启..."
        docker-compose restart gateway-service
    fi
    
    sleep 60
done
```

## 🚨 故障排除

### 常见问题

#### 1. 端口冲突
```bash
# 查看端口占用
netstat -tulpn | grep :9000

# 修改端口配置
vim docker-compose.yml
```

#### 2. 内存不足
```bash
# 查看内存使用
docker stats

# 增加JVM内存配置
environment:
  - JAVA_OPTS=-Xmx1g -Xms512m
```

#### 3. 数据库连接失败
```bash
# 检查数据库状态
docker logs mysql

# 重置数据库
docker-compose down
docker volume rm demo_mysql_data
docker-compose up -d mysql
```

#### 4. 服务启动失败
```bash
# 查看服务日志
docker-compose logs service-name

# 重启特定服务
docker-compose restart service-name
```

## 📊 性能优化

### JVM调优
```yaml
# docker-compose.yml
services:
  gateway-service:
    environment:
      - JAVA_OPTS=-Xmx2g -Xms1g -XX:+UseG1GC -XX:MaxGCPauseMillis=200
```

### 数据库优化
```sql
-- MySQL配置优化
SET GLOBAL innodb_buffer_pool_size = 1073741824;  -- 1GB
SET GLOBAL max_connections = 1000;
SET GLOBAL query_cache_size = 268435456;  -- 256MB
```

### Redis优化
```bash
# Redis配置
echo "maxmemory 1gb" >> /etc/redis/redis.conf
echo "maxmemory-policy allkeys-lru" >> /etc/redis/redis.conf
```

## 🔒 安全配置

### 1. 修改默认密码
```bash
# MySQL
ALTER USER 'root'@'%' IDENTIFIED BY 'your_secure_password';

# RabbitMQ
rabbitmqctl add_user admin your_secure_password
rabbitmqctl set_user_tags admin administrator
```

### 2. 配置防火墙
```bash
# 只开放必要端口
ufw allow 80
ufw allow 443
ufw allow 22
ufw enable
```

### 3. SSL证书配置
```nginx
server {
    listen 443 ssl;
    ssl_certificate /path/to/certificate.crt;
    ssl_certificate_key /path/to/private.key;
    
    # SSL配置
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers HIGH:!aNULL:!MD5;
}
```

## 📈 监控告警

### Prometheus配置
```yaml
# prometheus.yml
global:
  scrape_interval: 15s

scrape_configs:
  - job_name: 'spring-boot'
    static_configs:
      - targets: ['localhost:9000', 'localhost:9001', 'localhost:9002']
```

### Grafana仪表板
- 导入Spring Boot监控模板
- 配置JVM监控面板
- 设置告警规则

## 🎊 部署验证

### 功能验证清单
- [ ] 前端应用可正常访问
- [ ] API测试平台功能正常
- [ ] 高并发测试平台可用
- [ ] 服务状态监控正常
- [ ] 所有微服务API响应正常
- [ ] 数据库连接正常
- [ ] Redis缓存工作正常
- [ ] 消息队列功能正常

### 性能验证
- [ ] API响应时间 < 100ms
- [ ] 高并发测试QPS > 100
- [ ] 系统稳定性测试通过
- [ ] 内存使用率 < 80%
- [ ] CPU使用率 < 70%

---

**🎉 恭喜！你已经成功部署了完整的微服务高并发测试平台！**
