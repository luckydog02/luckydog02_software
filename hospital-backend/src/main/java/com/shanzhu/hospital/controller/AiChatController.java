package com.shanzhu.hospital.controller;

import com.shanzhu.hospital.ai.agent.AiAgentService;
import com.shanzhu.hospital.ai.client.DeepSeekClient;
import com.shanzhu.hospital.ai.conversation.ConversationService;
import com.shanzhu.hospital.common.R;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * AI聊天控制器
 * 支持流式响应
 */
@Slf4j
@RestController
@RequestMapping("ai")
@RequiredArgsConstructor
public class AiChatController {
    
    private final AiAgentService aiAgentService;
    private final ConversationService conversationService;
    
    /**
     * 流式对话接口（SSE）
     */
    @GetMapping(value = "/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamChat(
            @RequestParam String message,
            @RequestParam(required = false, defaultValue = "") String sessionId) {
        
        // 生成或使用现有会话ID
        final String finalSessionId;
        if (sessionId == null || sessionId.isEmpty()) {
            finalSessionId = UUID.randomUUID().toString();
        } else {
            finalSessionId = sessionId;
        }
        
        SseEmitter emitter = new SseEmitter(60000L); // 60秒超时
        
        try {
            // 添加用户消息到历史
            conversationService.addMessage(finalSessionId, "user", message);
            
            // 获取对话历史
            List<Map<String, String>> history = conversationService.getHistory(finalSessionId);
            
            // 创建流式回调
            DeepSeekClient.StreamCallback callback = new DeepSeekClient.StreamCallback() {
                @Override
                public void onContent(String content) {
                    try {
                        // 发送SSE事件（使用默认事件名，前端通过onmessage接收）
                        emitter.send(SseEmitter.event()
                            .data(content));
                        log.debug("发送内容片段: {}", content);
                    } catch (IOException e) {
                        log.error("发送SSE消息失败", e);
                        try {
                            emitter.completeWithError(e);
                        } catch (Exception ex) {
                            log.error("完成SSE流失败", ex);
                        }
                    }
                }
                
                @Override
                public void onComplete(String fullContent) {
                    try {
                        // 添加AI回复到历史
                        conversationService.addMessage(finalSessionId, "assistant", fullContent);
                        
                        // 发送完成事件（使用默认事件名）
                        emitter.send(SseEmitter.event()
                            .data("DONE"));
                        emitter.complete();
                        log.info("SSE流完成，会话ID: {}", finalSessionId);
                    } catch (IOException e) {
                        log.error("完成SSE流失败", e);
                        emitter.completeWithError(e);
                    }
                }
                
                @Override
                public void onError(Exception e) {
                    log.error("AI服务调用失败", e);
                    String errorMessage = "抱歉，AI服务暂时不可用，请稍后重试";
                    
                    // 根据错误类型提供更详细的错误信息
                    if (e.getMessage() != null) {
                        String msg = e.getMessage();
                        if (msg.contains("Insufficient Balance") || msg.contains("余额不足")) {
                            errorMessage = "AI服务账户余额不足，请联系管理员充值";
                        } else if (msg.contains("API Key") || msg.contains("401") || msg.contains("403")) {
                            errorMessage = "API Key配置错误，请检查配置";
                        } else if (msg.contains("timeout") || msg.contains("超时")) {
                            errorMessage = "请求超时，请稍后重试";
                        } else if (msg.contains("连接") || msg.contains("Connection")) {
                            errorMessage = "无法连接到AI服务，请检查网络";
                        } else {
                            errorMessage = "错误: " + msg;
                        }
                    }
                    
                    try {
                        emitter.send(SseEmitter.event()
                            .data(errorMessage));
                        emitter.complete();
                    } catch (IOException ex) {
                        log.error("发送错误消息失败", ex);
                        emitter.completeWithError(ex);
                    }
                }
            };
            
            // 异步处理消息，避免阻塞
            new Thread(() -> {
                try {
                    log.info("开始处理消息，会话ID: {}, 消息: {}", finalSessionId, message);
                    aiAgentService.processMessage(message, history, callback);
                } catch (Exception e) {
                    log.error("处理消息异常", e);
                    callback.onError(e);
                }
            }).start();
            
        } catch (Exception e) {
            log.error("处理对话请求失败", e);
            try {
                emitter.send(SseEmitter.event()
                    .data("系统错误: " + e.getMessage()));
                emitter.complete();
            } catch (IOException ex) {
                emitter.completeWithError(ex);
            }
        }
        
        return emitter;
    }
    
    /**
     * 普通对话接口（非流式）
     */
    @PostMapping("/chat")
    public R<ChatResponse> chat(@RequestBody ChatRequest request) {
        try {
            String sessionId = request.getSessionId();
            if (sessionId == null || sessionId.isEmpty()) {
                sessionId = UUID.randomUUID().toString();
            }
            
            // 添加用户消息
            conversationService.addMessage(sessionId, "user", request.getMessage());
            
            // 获取历史
            List<Map<String, String>> history = conversationService.getHistory(sessionId);
            
            // 调用AI（这里需要实现非流式版本）
            // 简化实现：返回提示信息
            String response = "请使用流式接口 /ai/chat/stream 获得更好的体验";
            
            // 添加AI回复
            conversationService.addMessage(sessionId, "assistant", response);
            
            ChatResponse chatResponse = new ChatResponse();
            chatResponse.setSessionId(sessionId);
            chatResponse.setResponse(response);
            
            return R.ok(chatResponse);
            
        } catch (Exception e) {
            log.error("对话处理失败", e);
            return R.error("对话处理失败: " + e.getMessage());
        }
    }
    
    /**
     * 清除会话
     */
    @PostMapping("/chat/clear")
    public R<Boolean> clearSession(@RequestParam String sessionId) {
        conversationService.clearSession(sessionId);
        return R.ok(true);
    }
    
    @Data
    public static class ChatRequest {
        private String message;
        private String sessionId;
    }
    
    @Data
    public static class ChatResponse {
        private String sessionId;
        private String response;
    }
}

