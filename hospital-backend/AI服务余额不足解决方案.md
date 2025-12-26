# AI服务余额不足解决方案

## 问题描述

当前错误信息：`Insufficient Balance`（账户余额不足）

这表明 DeepSeek API 账户的余额不足以继续使用服务。

## 解决方案

### 方案1：充值账户（推荐）

1. 访问 DeepSeek 平台：https://platform.deepseek.com
2. 登录您的账户
3. 进入"账户"或"余额"页面
4. 充值账户余额
5. 充值完成后，重启后端服务

### 方案2：检查账户状态

1. 登录 DeepSeek 平台
2. 检查：
   - 账户余额
   - API 使用量
   - 是否有欠费记录
   - API Key 是否有效

### 方案3：使用其他 API Key

如果您有其他有效的 API Key：

1. 修改 `application.yml` 中的配置：
   ```yaml
   ai:
     deepseek:
       api-key: ${DEEPSEEK_API_KEY:your-new-api-key}
   ```

2. 或者设置环境变量：
   ```bash
   export DEEPSEEK_API_KEY=your-new-api-key
   ```

3. 重启后端服务

### 方案4：临时禁用 AI 功能

如果暂时无法充值，可以：

1. 在前端隐藏或禁用 AI 聊天入口
2. 或者显示友好的提示信息："AI服务暂时不可用，正在维护中"

## 验证修复

充值或更换 API Key 后：

1. 访问测试接口：`http://localhost:9281/ai/test/simple`
2. 应该返回正常的 AI 回复，而不是错误信息
3. 在前端 AI 聊天页面测试发送消息

## 预防措施

1. **设置余额提醒**：在 DeepSeek 平台设置余额不足提醒
2. **监控使用量**：定期检查 API 使用情况
3. **备用方案**：准备多个 API Key 或账户作为备用

## 当前状态

- ✅ 错误处理已改进，会显示友好的错误提示
- ✅ 前端会正确显示"账户余额不足"的提示
- ⚠️ 需要充值或更换有效的 API Key 才能继续使用

