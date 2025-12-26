# AI服务问题排查指南

## 错误信息："抱歉，AI服务暂时不可用，请稍后重试"

### 可能原因及解决方案

#### 1. **后端服务未启动**
- **检查方法**：访问 `http://localhost:9281/ai/test/config` 查看配置信息
- **解决方案**：确保Spring Boot后端服务已启动

#### 2. **API Key配置错误**
- **检查方法**：
  1. 查看后端日志，确认API Key是否正确加载
  2. 访问测试接口：`http://localhost:9281/ai/test/config`
- **解决方案**：
  - 检查 `application.yml` 中的 `ai.deepseek.api-key` 配置
  - 或设置环境变量 `DEEPSEEK_API_KEY`
  - 确保API Key有效且未过期

#### 3. **网络连接问题**
- **检查方法**：
  1. 查看后端日志中的网络错误
  2. 测试能否访问 `https://api.deepseek.com`
- **解决方案**：
  - 检查网络连接
  - 检查防火墙设置
  - 如果使用代理，配置HTTP代理

#### 4. **DeepSeek API服务异常**
- **检查方法**：
  1. 查看后端日志中的HTTP状态码
  2. 状态码 401/403：API Key错误
  3. 状态码 429：请求频率过高
  4. 状态码 500：API服务异常
- **解决方案**：
  - 检查API Key是否有效
  - 检查账户余额
  - 稍后重试

#### 5. **前端连接失败**
- **检查方法**：
  1. 打开浏览器开发者工具（F12）
  2. 查看Console标签的错误信息
  3. 查看Network标签的请求状态
- **解决方案**：
  - 确保前端代理配置正确（`vue.config.js`）
  - 检查后端服务是否在 `http://localhost:9281` 运行
  - 检查CORS配置（如果需要）

### 诊断步骤

1. **测试配置接口**
   ```bash
   curl http://localhost:9281/ai/test/config
   ```
   应该返回配置信息

2. **测试简单对话**
   ```bash
   curl http://localhost:9281/ai/test/simple
   ```
   应该返回AI回复

3. **查看后端日志**
   - 查找 "请求DeepSeek API" 日志
   - 查找 "DeepSeek API响应状态码" 日志
   - 查找错误堆栈信息

4. **检查前端控制台**
   - 打开浏览器开发者工具
   - 查看Console和Network标签
   - 检查EventSource连接状态

### 常见错误码

- **401 Unauthorized**：API Key无效或过期
- **403 Forbidden**：API Key无权限或账户被禁用
- **429 Too Many Requests**：请求频率过高，需要限流
- **500 Internal Server Error**：DeepSeek API服务异常
- **Connection refused**：无法连接到API服务器

### 快速修复

如果问题持续存在，可以：

1. **重启后端服务**
2. **检查API Key**：访问 https://platform.deepseek.com 确认API Key状态
3. **查看详细日志**：检查后端控制台输出的完整错误信息
4. **使用测试接口**：先测试 `/ai/test/simple` 确认基础功能

