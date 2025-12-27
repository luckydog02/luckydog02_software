package com.shanzhu.hospital.controller;

import com.shanzhu.hospital.ai.client.DeepSeekClient;
import com.shanzhu.hospital.config.AiConfig;
import com.shanzhu.hospital.common.R;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AI测试控制器
 * 用于测试AI服务配置
 */
@Slf4j
@RestController
@RequestMapping("ai/test")
@RequiredArgsConstructor
public class AiTestController {
    
    private final AiConfig aiConfig;
    private final DeepSeekClient deepSeekClient;
    
    /**
     * 测试配置
     */
    @GetMapping("/config")
    public R<Map<String, String>> testConfig() {
        Map<String, String> config = new HashMap<>();
        config.put("apiBaseUrl", aiConfig.getApiBaseUrl());
        config.put("model", aiConfig.getModel());
        config.put("apiKey", aiConfig.getApiKey() != null ? 
            aiConfig.getApiKey().substring(0, Math.min(10, aiConfig.getApiKey().length())) + "..." : "未配置");
        config.put("timeout", String.valueOf(aiConfig.getTimeout()));
        
        log.info("AI配置信息: {}", config);
        return R.ok(config);
    }
    
    /**
     * 测试简单对话
     */
    @GetMapping("/simple")
    public R<String> testSimple() {
        try {
            log.info("开始测试简单对话");
            List<Map<String, String>> messages = new ArrayList<>();
            Map<String, String> userMsg = new HashMap<>();
            userMsg.put("role", "user");
            userMsg.put("content", "你好，请回复'测试成功'");
            messages.add(userMsg);
            
            log.info("发送消息到DeepSeek API");
            String response = deepSeekClient.chat(messages);
            log.info("收到响应: {}", response);
            
            if (response == null || response.isEmpty()) {
                return R.error("API返回空响应");
            }
            
            return R.ok(response);
            
        } catch (Exception e) {
            log.error("测试对话失败", e);
            e.printStackTrace();
            
            String errorMsg = e.getMessage();
            String userFriendlyMsg = "测试失败: " + errorMsg;
            
            // 提供更友好的错误提示
            if (errorMsg != null) {
                if (errorMsg.contains("Insufficient Balance") || errorMsg.contains("余额不足")) {
                    userFriendlyMsg = "测试失败: AI服务账户余额不足，请联系管理员充值";
                } else if (errorMsg.contains("API Key") || errorMsg.contains("401") || errorMsg.contains("403")) {
                    userFriendlyMsg = "测试失败: API Key配置错误，请检查配置";
                } else if (errorMsg.contains("timeout") || errorMsg.contains("超时")) {
                    userFriendlyMsg = "测试失败: 请求超时，请稍后重试";
                } else if (errorMsg.contains("连接") || errorMsg.contains("Connection")) {
                    userFriendlyMsg = "测试失败: 无法连接到AI服务，请检查网络";
                }
            }
            
            return R.error(userFriendlyMsg + "\n详细错误: " + 
                (e.getCause() != null ? e.getCause().getMessage() : "无"));
        }
    }
}

