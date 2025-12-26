package com.shanzhu.hospital.ai.conversation;

import lombok.Data;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 对话管理服务
 * 管理多轮对话历史
 */
@Service
public class ConversationService {
    
    // 存储每个会话的对话历史
    private final Map<String, ConversationSession> sessions = new ConcurrentHashMap<>();
    
    /**
     * 获取或创建会话
     */
    public ConversationSession getOrCreateSession(String sessionId) {
        return sessions.computeIfAbsent(sessionId, k -> new ConversationSession(sessionId));
    }
    
    /**
     * 添加消息到会话
     */
    public void addMessage(String sessionId, String role, String content) {
        ConversationSession session = getOrCreateSession(sessionId);
        Map<String, String> message = new HashMap<>();
        message.put("role", role);
        message.put("content", content);
        session.getMessages().add(message);
        
        // 限制历史长度，保留最近20轮对话
        if (session.getMessages().size() > 40) {
            List<Map<String, String>> messages = session.getMessages();
            messages.subList(0, messages.size() - 40).clear();
        }
    }
    
    /**
     * 获取会话历史
     */
    public List<Map<String, String>> getHistory(String sessionId) {
        ConversationSession session = sessions.get(sessionId);
        if (session == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(session.getMessages());
    }
    
    /**
     * 清除会话
     */
    public void clearSession(String sessionId) {
        sessions.remove(sessionId);
    }
    
    @Data
    public static class ConversationSession {
        private String sessionId;
        private List<Map<String, String>> messages;
        private long lastActiveTime;
        
        public ConversationSession(String sessionId) {
            this.sessionId = sessionId;
            this.messages = new ArrayList<>();
            this.lastActiveTime = System.currentTimeMillis();
        }
        
        public void updateActiveTime() {
            this.lastActiveTime = System.currentTimeMillis();
        }
    }
}

