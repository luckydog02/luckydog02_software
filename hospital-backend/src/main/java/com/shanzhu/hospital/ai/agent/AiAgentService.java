package com.shanzhu.hospital.ai.agent;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.shanzhu.hospital.ai.client.DeepSeekClient;
import com.shanzhu.hospital.ai.vector.VectorStore;
import com.shanzhu.hospital.mapper.ArrangeMapper;
import com.shanzhu.hospital.mapper.DoctorUserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * AI Agent服务
 * 支持意图识别、Function Calling、多轮对话
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AiAgentService {
    
    private final DeepSeekClient deepSeekClient;
    private final VectorStore vectorStore;
    private final ArrangeMapper arrangeMapper;
    private final DoctorUserMapper doctorUserMapper;
    
    /**
     * 处理用户消息
     * 
     * @param userMessage 用户消息
     * @param conversationHistory 对话历史
     * @param streamCallback 流式回调
     */
    public void processMessage(String userMessage, List<Map<String, String>> conversationHistory, 
                              DeepSeekClient.StreamCallback streamCallback) {
        try {
            // 1. 意图识别
            String intent = recognizeIntent(userMessage, conversationHistory);
            log.info("识别意图: {}", intent);
            
            // 2. 根据意图处理
            List<Map<String, String>> messages = buildMessages(userMessage, conversationHistory, intent);
            
            // 3. 流式调用AI
            deepSeekClient.streamChat(messages, streamCallback);
            
        } catch (Exception e) {
            log.error("处理消息失败", e);
            streamCallback.onError(e);
        }
    }
    
    /**
     * 意图识别
     */
    private String recognizeIntent(String userMessage, List<Map<String, String>> conversationHistory) {
        // 简单的关键词匹配（实际可以使用更复杂的NLP模型）
        String message = userMessage.toLowerCase();
        
        if (message.contains("排班") || message.contains("预约") || message.contains("挂号") || 
            message.contains("什么时候") || message.contains("时间")) {
            return "QUERY_SCHEDULE";
        }
        
        if (message.contains("流程") || message.contains("怎么") || message.contains("如何") ||
            message.contains("须知") || message.contains("注意")) {
            return "QUERY_POLICY";
        }
        
        if (message.contains("科室") || message.contains("挂什么") || message.contains("看什么") ||
            message.contains("症状") || message.contains("难受") || message.contains("疼")) {
            return "FIND_DEPARTMENT";
        }
        
        return "GENERAL_QA";
    }
    
    /**
     * 构建消息列表
     */
    private List<Map<String, String>> buildMessages(String userMessage, 
                                                   List<Map<String, String>> conversationHistory,
                                                   String intent) {
        List<Map<String, String>> messages = new ArrayList<>();
        
        // System Prompt
        String systemPrompt = buildSystemPrompt(intent);
        Map<String, String> systemMsg = new HashMap<>();
        systemMsg.put("role", "system");
        systemMsg.put("content", systemPrompt);
        messages.add(systemMsg);
        
        // 添加对话历史
        messages.addAll(conversationHistory);
        
        // 添加当前用户消息
        Map<String, String> userMsg = new HashMap<>();
        userMsg.put("role", "user");
        userMsg.put("content", userMessage);
        messages.add(userMsg);
        
        return messages;
    }
    
    /**
     * 构建系统提示词
     */
    private String buildSystemPrompt(String intent) {
        StringBuilder prompt = new StringBuilder();
        
        prompt.append("你是一个【医院智能分诊助手】。\n\n");
        
        // 根据意图添加不同的提示
        switch (intent) {
            case "FIND_DEPARTMENT":
                prompt.append("【你的职责】\n");
                prompt.append("- 根据患者提供的症状描述，在给定的候选科室中，选择最合适的一个\n");
                prompt.append("- 你的回答仅用于\"预约挂号辅助\"，不构成任何医疗诊断或治疗建议\n\n");
                prompt.append("【严格限制】\n");
                prompt.append("- 你不能新增候选科室\n");
                prompt.append("- 你不能给出治疗方案\n");
                prompt.append("- 如果患者描述不清晰，你应该追问具体症状，而不是胡乱猜测\n\n");
                
                // 添加科室信息
                prompt.append("【候选科室列表】\n");
                List<VectorStore.VectorDocument> deptDocs = vectorStore.getAllDocuments().stream()
                    .filter(doc -> doc.getMetadata() != null && doc.getMetadata().contains("科室介绍"))
                    .collect(Collectors.toList());
                for (VectorStore.VectorDocument doc : deptDocs) {
                    prompt.append(doc.getContent()).append("\n");
                }
                break;
                
            case "QUERY_SCHEDULE":
                prompt.append("【你的职责】\n");
                prompt.append("- 帮助患者查询医生排班信息\n");
                prompt.append("- 你可以在需要真实排班信息时，调用系统提供的函数\n");
                prompt.append("- 不要猜测排班信息，必须调用函数获取真实数据\n\n");
                break;
                
            case "QUERY_POLICY":
                prompt.append("【你的职责】\n");
                prompt.append("- 回答患者关于挂号流程、就诊须知、检查项目说明等问题\n");
                prompt.append("- 使用通俗、简洁的语言\n");
                prompt.append("- 若问题超出范围，请明确说明无法回答\n\n");
                
                // 添加就诊须知信息
                List<VectorStore.VectorDocument> policyDocs = vectorStore.getAllDocuments().stream()
                    .filter(doc -> doc.getMetadata() != null && doc.getMetadata().contains("就诊须知"))
                    .collect(Collectors.toList());
                for (VectorStore.VectorDocument doc : policyDocs) {
                    prompt.append(doc.getContent()).append("\n");
                }
                break;
                
            default:
                prompt.append("【你的职责】\n");
                prompt.append("- 回答患者关于医院常规就诊事项的问题\n");
                prompt.append("- 使用通俗、简洁的语言\n");
                prompt.append("- 禁止回答疾病诊断、用药建议、个性化治疗方案\n\n");
        }
        
        return prompt.toString();
    }
    
    /**
     * Function Calling: 查询医生排班
     */
    public String queryDoctorSchedule(String doctorName, String section, String date) {
        try {
            // 查询排班信息
            List<Map<String, Object>> schedules = new ArrayList<>();
            
            // 这里应该调用实际的数据库查询
            // 简化实现
            if (date == null || date.isEmpty()) {
                date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            }
            
            // 实际应该查询arrange表
            // 这里返回示例数据
            Map<String, Object> schedule = new HashMap<>();
            schedule.put("doctorName", doctorName != null ? doctorName : "医生");
            schedule.put("section", section != null ? section : "科室");
            schedule.put("date", date);
            schedule.put("timeSlots", Arrays.asList("08:00-09:00", "09:00-10:00", "10:00-11:00"));
            schedules.add(schedule);
            
            return JSON.toJSONString(schedules);
            
        } catch (Exception e) {
            log.error("查询排班失败", e);
            return "查询排班信息失败，请稍后重试";
        }
    }
}

