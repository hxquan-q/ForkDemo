package com.hxq.gateway.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 负载测试控制器
 * 提供高并发压力测试功能
 */
@Slf4j
@RestController
@RequestMapping("/api/loadtest")
@CrossOrigin(origins = "*")
public class LoadTestController {

    @Autowired
    private RestTemplate restTemplate;

    private final ExecutorService executorService = Executors.newFixedThreadPool(100);

    /**
     * 负载测试请求参数
     */
    public static class LoadTestRequest {
        private String targetUrl;           // 目标URL
        private String method = "GET";      // HTTP方法
        private int concurrency = 10;      // 并发数
        private int totalRequests = 100;   // 总请求数
        private int duration = 30;         // 测试时长(秒)
        private Map<String, String> headers = new HashMap<>();  // 请求头
        private String body;               // 请求体

        // Getters and Setters
        public String getTargetUrl() { return targetUrl; }
        public void setTargetUrl(String targetUrl) { this.targetUrl = targetUrl; }
        public String getMethod() { return method; }
        public void setMethod(String method) { this.method = method; }
        public int getConcurrency() { return concurrency; }
        public void setConcurrency(int concurrency) { this.concurrency = concurrency; }
        public int getTotalRequests() { return totalRequests; }
        public void setTotalRequests(int totalRequests) { this.totalRequests = totalRequests; }
        public int getDuration() { return duration; }
        public void setDuration(int duration) { this.duration = duration; }
        public Map<String, String> getHeaders() { return headers; }
        public void setHeaders(Map<String, String> headers) { this.headers = headers; }
        public String getBody() { return body; }
        public void setBody(String body) { this.body = body; }
    }

    /**
     * 负载测试结果
     */
    public static class LoadTestResult {
        private int totalRequests;          // 总请求数
        private int successCount;           // 成功请求数
        private int failureCount;           // 失败请求数
        private double successRate;         // 成功率
        private double avgResponseTime;     // 平均响应时间
        private long minResponseTime;       // 最小响应时间
        private long maxResponseTime;       // 最大响应时间
        private double qps;                 // 每秒请求数
        private long testDuration;          // 实际测试时长(毫秒)
        private String startTime;           // 开始时间
        private String endTime;             // 结束时间
        private Map<String, Integer> statusCodeCount = new HashMap<>();  // 状态码统计
        private List<String> errors = new ArrayList<>();  // 错误信息

        // Getters and Setters
        public int getTotalRequests() { return totalRequests; }
        public void setTotalRequests(int totalRequests) { this.totalRequests = totalRequests; }
        public int getSuccessCount() { return successCount; }
        public void setSuccessCount(int successCount) { this.successCount = successCount; }
        public int getFailureCount() { return failureCount; }
        public void setFailureCount(int failureCount) { this.failureCount = failureCount; }
        public double getSuccessRate() { return successRate; }
        public void setSuccessRate(double successRate) { this.successRate = successRate; }
        public double getAvgResponseTime() { return avgResponseTime; }
        public void setAvgResponseTime(double avgResponseTime) { this.avgResponseTime = avgResponseTime; }
        public long getMinResponseTime() { return minResponseTime; }
        public void setMinResponseTime(long minResponseTime) { this.minResponseTime = minResponseTime; }
        public long getMaxResponseTime() { return maxResponseTime; }
        public void setMaxResponseTime(long maxResponseTime) { this.maxResponseTime = maxResponseTime; }
        public double getQps() { return qps; }
        public void setQps(double qps) { this.qps = qps; }
        public long getTestDuration() { return testDuration; }
        public void setTestDuration(long testDuration) { this.testDuration = testDuration; }
        public String getStartTime() { return startTime; }
        public void setStartTime(String startTime) { this.startTime = startTime; }
        public String getEndTime() { return endTime; }
        public void setEndTime(String endTime) { this.endTime = endTime; }
        public Map<String, Integer> getStatusCodeCount() { return statusCodeCount; }
        public void setStatusCodeCount(Map<String, Integer> statusCodeCount) { this.statusCodeCount = statusCodeCount; }
        public List<String> getErrors() { return errors; }
        public void setErrors(List<String> errors) { this.errors = errors; }
    }

    /**
     * 执行负载测试
     */
    @PostMapping("/execute")
    public ResponseEntity<Map<String, Object>> executeLoadTest(@RequestBody LoadTestRequest request) {
        log.info("开始执行负载测试: 目标URL={}, 并发数={}, 总请求数={}", 
                request.getTargetUrl(), request.getConcurrency(), request.getTotalRequests());

        try {
            LoadTestResult result = performLoadTest(request);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "负载测试完成");
            response.put("data", result);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("负载测试执行失败", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "负载测试执行失败: " + e.getMessage());
            response.put("data", null);
            
            return ResponseEntity.ok(response);
        }
    }

    /**
     * 执行负载测试的核心逻辑
     */
    private LoadTestResult performLoadTest(LoadTestRequest request) throws InterruptedException {
        LoadTestResult result = new LoadTestResult();
        
        // 统计变量
        AtomicInteger totalRequests = new AtomicInteger(0);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);
        AtomicLong totalResponseTime = new AtomicLong(0);
        AtomicLong minResponseTime = new AtomicLong(Long.MAX_VALUE);
        AtomicLong maxResponseTime = new AtomicLong(0);
        Map<String, AtomicInteger> statusCodeCount = new ConcurrentHashMap<>();
        List<String> errors = Collections.synchronizedList(new ArrayList<>());

        // 记录开始时间
        long startTime = System.currentTimeMillis();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        result.setStartTime(LocalDateTime.now().format(formatter));

        // 创建CountDownLatch来等待所有任务完成
        CountDownLatch latch = new CountDownLatch(request.getConcurrency());
        
        // 计算每个线程需要执行的请求数
        int requestsPerThread = request.getTotalRequests() / request.getConcurrency();
        int remainingRequests = request.getTotalRequests() % request.getConcurrency();

        // 启动并发任务
        for (int i = 0; i < request.getConcurrency(); i++) {
            int threadRequests = requestsPerThread + (i < remainingRequests ? 1 : 0);
            
            executorService.submit(() -> {
                try {
                    for (int j = 0; j < threadRequests; j++) {
                        executeRequest(request, totalRequests, successCount, failureCount, 
                                     totalResponseTime, minResponseTime, maxResponseTime, 
                                     statusCodeCount, errors);
                        
                        // 添加小延迟避免过于密集的请求
                        Thread.sleep(10);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    latch.countDown();
                }
            });
        }

        // 等待所有任务完成或超时
        boolean completed = latch.await(request.getDuration() + 10, TimeUnit.SECONDS);
        
        // 记录结束时间
        long endTime = System.currentTimeMillis();
        result.setEndTime(LocalDateTime.now().format(formatter));
        result.setTestDuration(endTime - startTime);

        // 计算统计结果
        int total = totalRequests.get();
        int success = successCount.get();
        int failure = failureCount.get();
        
        result.setTotalRequests(total);
        result.setSuccessCount(success);
        result.setFailureCount(failure);
        result.setSuccessRate(total > 0 ? (double) success / total * 100 : 0);
        result.setAvgResponseTime(total > 0 ? (double) totalResponseTime.get() / total : 0);
        result.setMinResponseTime(minResponseTime.get() == Long.MAX_VALUE ? 0 : minResponseTime.get());
        result.setMaxResponseTime(maxResponseTime.get());
        result.setQps(total > 0 ? (double) total / (result.getTestDuration() / 1000.0) : 0);
        
        // 转换状态码统计
        Map<String, Integer> statusCodes = new HashMap<>();
        statusCodeCount.forEach((key, value) -> statusCodes.put(key, value.get()));
        result.setStatusCodeCount(statusCodes);
        
        // 限制错误信息数量
        result.setErrors(errors.size() > 10 ? errors.subList(0, 10) : errors);

        log.info("负载测试完成: 总请求={}, 成功={}, 失败={}, 成功率={}%, 平均响应时间={}ms, QPS={}", 
                total, success, failure, String.format("%.2f", result.getSuccessRate()), 
                String.format("%.2f", result.getAvgResponseTime()), String.format("%.2f", result.getQps()));

        return result;
    }

    /**
     * 执行单个请求
     */
    private void executeRequest(LoadTestRequest request, AtomicInteger totalRequests, 
                              AtomicInteger successCount, AtomicInteger failureCount,
                              AtomicLong totalResponseTime, AtomicLong minResponseTime, 
                              AtomicLong maxResponseTime, Map<String, AtomicInteger> statusCodeCount,
                              List<String> errors) {
        long requestStart = System.currentTimeMillis();
        
        try {
            // 构建完整的URL
            String fullUrl = "http://localhost:9000" + request.getTargetUrl();
            
            ResponseEntity<String> response;
            if ("POST".equalsIgnoreCase(request.getMethod()) && request.getTargetUrl().contains("/login")) {
                // 特殊处理登录请求
                String loginBody = "{\"username\":\"admin\",\"password\":\"123456\"}";
                org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
                headers.set("Content-Type", "application/json");
                org.springframework.http.HttpEntity<String> entity = new org.springframework.http.HttpEntity<>(loginBody, headers);
                response = restTemplate.postForEntity(fullUrl, entity, String.class);
            } else {
                // GET请求
                response = restTemplate.getForEntity(fullUrl, String.class);
            }
            
            long responseTime = System.currentTimeMillis() - requestStart;
            
            // 更新统计信息
            totalRequests.incrementAndGet();
            successCount.incrementAndGet();
            totalResponseTime.addAndGet(responseTime);
            
            // 更新最小/最大响应时间
            minResponseTime.updateAndGet(current -> Math.min(current, responseTime));
            maxResponseTime.updateAndGet(current -> Math.max(current, responseTime));
            
            // 统计状态码
            String statusCode = String.valueOf(response.getStatusCodeValue());
            statusCodeCount.computeIfAbsent(statusCode, k -> new AtomicInteger(0)).incrementAndGet();
            
        } catch (Exception e) {
            long responseTime = System.currentTimeMillis() - requestStart;
            
            totalRequests.incrementAndGet();
            failureCount.incrementAndGet();
            totalResponseTime.addAndGet(responseTime);
            
            // 记录错误信息
            if (errors.size() < 10) {
                errors.add(e.getMessage());
            }
            
            // 统计错误状态码
            statusCodeCount.computeIfAbsent("ERROR", k -> new AtomicInteger(0)).incrementAndGet();
        }
    }

    /**
     * 获取系统性能信息
     */
    @GetMapping("/system-info")
    public ResponseEntity<Map<String, Object>> getSystemInfo() {
        Map<String, Object> systemInfo = new HashMap<>();
        
        Runtime runtime = Runtime.getRuntime();
        systemInfo.put("availableProcessors", runtime.availableProcessors());
        systemInfo.put("totalMemory", runtime.totalMemory() / 1024 / 1024 + " MB");
        systemInfo.put("freeMemory", runtime.freeMemory() / 1024 / 1024 + " MB");
        systemInfo.put("maxMemory", runtime.maxMemory() / 1024 / 1024 + " MB");
        systemInfo.put("usedMemory", (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024 + " MB");
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", systemInfo);
        
        return ResponseEntity.ok(response);
    }
}
