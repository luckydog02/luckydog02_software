package com.shanzhu.hospital.ai.client;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.shanzhu.hospital.config.AiConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DeepSeek API客户端
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DeepSeekClient {
    
    private final AiConfig aiConfig;
    
    /**
     * 流式对话
     * 
     * @param messages 对话消息列表
     * @param streamCallback 流式回调
     */
    public void streamChat(List<Map<String, String>> messages, StreamCallback streamCallback) {
        CloseableHttpClient httpClient = null;
        try {
            String url = aiConfig.getApiBaseUrl() + "/chat/completions";
            log.info("请求DeepSeek API: {}", url);
            log.info("请求消息数量: {}", messages.size());
            log.info("API Key前缀: {}", aiConfig.getApiKey() != null ? 
                aiConfig.getApiKey().substring(0, Math.min(10, aiConfig.getApiKey().length())) + "..." : "null");
            
            httpClient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(url);
            
            // 设置请求头
            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setHeader("Authorization", "Bearer " + aiConfig.getApiKey());
            
            // 构建请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", aiConfig.getModel());
            requestBody.put("messages", messages);
            requestBody.put("stream", true);
            requestBody.put("temperature", 0.7);
            
            String requestBodyJson = JSON.toJSONString(requestBody);
            log.info("流式请求体: {}", requestBodyJson);
            
            StringEntity entity = new StringEntity(requestBodyJson, StandardCharsets.UTF_8);
            httpPost.setEntity(entity);
            
            // 执行请求
            log.info("发送流式请求到DeepSeek API...");
            CloseableHttpResponse response = httpClient.execute(httpPost);
            
            // 检查HTTP状态码
            int statusCode = response.getStatusLine().getStatusCode();
            log.info("流式API响应状态码: {}", statusCode);
            
            if (statusCode != 200) {
                HttpEntity errorEntity = response.getEntity();
                String errorMessage = "API请求失败，状态码: " + statusCode;
                if (errorEntity != null) {
                    try {
                        String errorText = EntityUtils.toString(errorEntity, StandardCharsets.UTF_8);
                        log.error("API错误响应: {}", errorText);
                        JSONObject errorJson = JSON.parseObject(errorText);
                        if (errorJson.containsKey("error")) {
                            JSONObject error = errorJson.getJSONObject("error");
                            errorMessage = error.getString("message") != null ? 
                                error.getString("message") : errorMessage;
                        }
                    } catch (Exception e) {
                        log.warn("解析错误响应失败", e);
                    }
                }
                response.close();
                streamCallback.onError(new RuntimeException(errorMessage));
                return;
            }
            
            HttpEntity responseEntity = response.getEntity();
            
            if (responseEntity != null) {
                BufferedReader reader = new BufferedReader(
                    new InputStreamReader(responseEntity.getContent(), StandardCharsets.UTF_8)
                );
                
                String line;
                StringBuilder fullContent = new StringBuilder();
                boolean hasContent = false;
                
                while ((line = reader.readLine()) != null) {
                    log.debug("收到原始行: {}", line);
                    
                    // 跳过空行
                    if (line.trim().isEmpty()) {
                        continue;
                    }
                    
                    // 处理SSE格式：data: {...} 或直接是JSON
                    String data = null;
                    if (line.startsWith("data: ")) {
                        data = line.substring(6).trim();
                    } else if (line.trim().startsWith("{")) {
                        data = line.trim();
                    }
                    
                    if (data != null && !data.isEmpty()) {
                        if ("[DONE]".equals(data)) {
                            log.info("收到结束标记 [DONE]");
                            break;
                        }
                        
                        try {
                            JSONObject jsonObject = JSON.parseObject(data);
                            log.debug("解析JSON对象: {}", jsonObject.toJSONString());
                            
                            // 检查是否有error字段
                            if (jsonObject.containsKey("error")) {
                                JSONObject error = jsonObject.getJSONObject("error");
                                String errorMsg = error != null ? error.getString("message") : "未知错误";
                                log.error("API返回错误: {}", errorMsg);
                                throw new RuntimeException("API错误: " + errorMsg);
                            }
                            
                            JSONArray choices = jsonObject.getJSONArray("choices");
                            if (choices != null && choices.size() > 0) {
                                JSONObject choice = choices.getJSONObject(0);
                                
                                // 检查是否有错误
                                String finishReason = choice.getString("finish_reason");
                                if (finishReason != null && "error".equals(finishReason)) {
                                    log.error("AI返回错误，finish_reason: error, 数据: {}", data);
                                    continue;
                                }
                                
                                JSONObject delta = choice.getJSONObject("delta");
                                if (delta != null) {
                                    String content = delta.getString("content");
                                    if (content != null && !content.isEmpty()) {
                                        hasContent = true;
                                        fullContent.append(content);
                                        log.debug("提取内容片段: {}", content);
                                        streamCallback.onContent(content);
                                    }
                                }
                            } else {
                                log.warn("响应中没有choices数组，数据: {}", data);
                            }
                        } catch (Exception e) {
                            log.warn("解析流式响应失败，行内容: {}, 错误: {}", line, e.getMessage());
                            // 如果是JSON解析错误，可能是格式问题，继续处理下一行
                            if (!(e instanceof com.alibaba.fastjson.JSONException)) {
                                throw e;
                            }
                        }
                    }
                }
                
                log.info("流式响应处理完成，收到内容长度: {}", fullContent.length());
                
                if (!hasContent && fullContent.length() == 0) {
                    log.error("未收到任何内容，可能是API响应格式异常");
                    String debugInfo = "未收到任何内容。请检查：\n" +
                        "1. API Key是否有效\n" +
                        "2. 网络连接是否正常\n" +
                        "3. DeepSeek API服务是否可用\n" +
                        "4. 查看后端日志获取详细错误信息";
                    streamCallback.onError(new RuntimeException(debugInfo));
                    return;
                }
                
                streamCallback.onComplete(fullContent.toString());
            } else {
                log.error("API响应实体为空");
                streamCallback.onError(new RuntimeException("API响应为空"));
                return;
            }
            
            response.close();
            if (httpClient != null) {
                httpClient.close();
            }
            
        } catch (Exception e) {
            log.error("流式对话请求失败: {}", e.getMessage(), e);
            // 打印详细错误信息
            if (e.getCause() != null) {
                log.error("错误原因: {}", e.getCause().getMessage());
            }
            // 打印堆栈跟踪
            e.printStackTrace();
            
            try {
                if (httpClient != null) {
                    httpClient.close();
                }
            } catch (Exception ex) {
                log.warn("关闭HTTP客户端失败", ex);
            }
            
            streamCallback.onError(e);
        }
    }
    
    /**
     * 普通对话（非流式）
     */
    public String chat(List<Map<String, String>> messages) {
        CloseableHttpClient httpClient = null;
        try {
            httpClient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(aiConfig.getApiBaseUrl() + "/chat/completions");
            
            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setHeader("Authorization", "Bearer " + aiConfig.getApiKey());
            
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", aiConfig.getModel());
            requestBody.put("messages", messages);
            requestBody.put("temperature", 0.7);
            
            StringEntity entity = new StringEntity(JSON.toJSONString(requestBody), StandardCharsets.UTF_8);
            httpPost.setEntity(entity);
            
            CloseableHttpResponse response = httpClient.execute(httpPost);
            
            // 检查HTTP状态码
            int statusCode = response.getStatusLine().getStatusCode();
            log.info("非流式API响应状态码: {}", statusCode);
            
            if (statusCode != 200) {
                HttpEntity errorEntity = response.getEntity();
                String errorMessage = "API请求失败，状态码: " + statusCode;
                if (errorEntity != null) {
                    try {
                        String errorText = EntityUtils.toString(errorEntity, StandardCharsets.UTF_8);
                        log.error("API错误响应: {}", errorText);
                        JSONObject errorJson = JSON.parseObject(errorText);
                        if (errorJson.containsKey("error")) {
                            JSONObject error = errorJson.getJSONObject("error");
                            errorMessage = error.getString("message") != null ? 
                                error.getString("message") : errorMessage;
                        }
                    } catch (Exception e) {
                        log.warn("解析错误响应失败", e);
                    }
                }
                response.close();
                httpClient.close();
                throw new RuntimeException(errorMessage);
            }
            
            HttpEntity responseEntity = response.getEntity();
            
            if (responseEntity != null) {
                String responseText = EntityUtils.toString(responseEntity, StandardCharsets.UTF_8);
                log.info("API响应内容: {}", responseText);
                
                JSONObject jsonObject = JSON.parseObject(responseText);
                
                // 检查是否有error字段
                if (jsonObject.containsKey("error")) {
                    JSONObject error = jsonObject.getJSONObject("error");
                    String errorMsg = error != null ? error.getString("message") : "未知错误";
                    log.error("API返回错误: {}", errorMsg);
                    throw new RuntimeException("API错误: " + errorMsg);
                }
                
                JSONArray choices = jsonObject.getJSONArray("choices");
                if (choices != null && choices.size() > 0) {
                    JSONObject choice = choices.getJSONObject(0);
                    JSONObject message = choice.getJSONObject("message");
                    String content = message != null ? message.getString("content") : null;
                    if (content != null && !content.isEmpty()) {
                        return content;
                    } else {
                        log.warn("响应中content为空，完整响应: {}", responseText);
                        throw new RuntimeException("API返回空内容");
                    }
                } else {
                    log.warn("响应中没有choices数组，完整响应: {}", responseText);
                    throw new RuntimeException("API响应格式异常，缺少choices数组");
                }
            } else {
                log.error("API响应实体为空");
                throw new RuntimeException("API响应为空");
            }
            
        } catch (Exception e) {
            log.error("对话请求失败", e);
            e.printStackTrace();
            throw new RuntimeException("AI服务调用失败: " + e.getMessage(), e);
        } finally {
            try {
                if (httpClient != null) {
                    httpClient.close();
                }
            } catch (Exception e) {
                log.warn("关闭HTTP客户端失败", e);
            }
        }
    }
    
    /**
     * 流式回调接口
     */
    public interface StreamCallback {
        void onContent(String content);
        void onComplete(String fullContent);
        void onError(Exception e);
    }
}

