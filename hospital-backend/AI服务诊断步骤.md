# AI服务诊断步骤

## 问题："抱歉，AI服务暂时不可用，请稍后重试"

### 第一步：检查配置

访问测试接口查看配置：
```
GET http://localhost:9281/ai/test/config
```

**预期响应**：
```json
{
  "status": 200,
  "msg": "成功",
  "data": {
    "apiBaseUrl": "https://api.deepseek.com",
    "apiKey": "sk-16a0803...",
    "model": "deepseek-chat",
    "timeout": "30000"
  }
}
```

### 第二步：测试简单对话

访问测试接口：
```
GET http://localhost:9281/ai/test/simple
```

**如果成功**：应该返回AI的回复文本

**如果失败**：查看返回的错误信息，常见错误：
- `401 Unauthorized`：API Key无效
- `403 Forbidden`：API Key无权限
- `Connection refused`：无法连接到API服务器
- `timeout`：请求超时

### 第三步：查看后端日志

启动后端服务后，查看控制台输出：

1. **查找配置加载日志**：
   ```
   请求DeepSeek API: https://api.deepseek.com/chat/completions
   API Key前缀: sk-16a0803...
   ```

2. **查找API响应日志**：
   ```
   DeepSeek API响应状态码: 200
   收到原始行: data: {...}
   提取内容片段: ...
   ```

3. **查找错误日志**：
   ```
   ERROR 流式对话请求失败: ...
   ERROR API错误响应: {...}
   ```

### 第四步：检查网络连接

在浏览器或Postman中测试：
```bash
curl -X POST https://api.deepseek.com/chat/completions \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer sk-16a0803d759b4c7cb613a6f7251c66c0" \
  -d '{
    "model": "deepseek-chat",
    "messages": [{"role": "user", "content": "你好"}],
    "stream": false
  }'
```

### 第五步：检查前端连接

1. 打开浏览器开发者工具（F12）
2. 切换到 **Network** 标签
3. 在AI聊天页面发送消息
4. 查找 `/ai/chat/stream` 请求
5. 查看请求状态：
   - **200 OK**：连接成功
   - **404 Not Found**：接口路径错误
   - **500 Internal Server Error**：后端处理错误
   - **Failed to fetch**：网络连接失败

6. 查看 **EventStream** 响应（如果状态是200）：
   - 应该看到 `data: ...` 格式的数据流

### 常见问题及解决方案

#### 1. API Key无效
**症状**：返回401或403错误
**解决**：
- 检查API Key是否正确
- 访问 https://platform.deepseek.com 验证API Key状态
- 确认账户余额充足

#### 2. 网络连接失败
**症状**：`Connection refused` 或 `timeout`
**解决**：
- 检查网络连接
- 检查防火墙设置
- 如果使用代理，配置HTTP代理

#### 3. 流式响应格式错误
**症状**：收到数据但无法解析
**解决**：
- 查看后端日志中的"收到原始行"日志
- 检查DeepSeek API的实际响应格式
- 可能需要调整解析逻辑

#### 4. 前端EventSource连接失败
**症状**：浏览器控制台显示连接错误
**解决**：
- 检查 `vue.config.js` 的代理配置
- 确认后端服务在 `http://localhost:9281` 运行
- 检查CORS配置（如果需要）

### 调试技巧

1. **启用详细日志**：
   在 `application.yml` 中添加：
   ```yaml
   logging:
     level:
       com.shanzhu.hospital.ai: DEBUG
   ```

2. **查看完整错误堆栈**：
   后端控制台会打印完整的异常堆栈，查找 `Caused by:` 部分

3. **测试非流式接口**：
   先测试 `/ai/test/simple` 确认基础功能正常

4. **检查API响应**：
   在 `DeepSeekClient.java` 中添加更多日志，查看实际收到的响应内容

