package com.shanzhu.hospital.ai.embedding;

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
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 向量嵌入服务
 * 使用DeepSeek API生成文本向量
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmbeddingService {
    
    private final AiConfig aiConfig;
    
    /**
     * 生成文本向量
     * 
     * @param text 文本内容
     * @return 向量数组
     */
    public double[] generateEmbedding(String text) {
        try {
            // 注意：DeepSeek可能不支持embedding API
            // 这里使用简化方案：基于文本特征生成伪向量
            // 实际生产环境建议使用专门的embedding模型如text-embedding-ada-002
            
            // 简化实现：基于文本哈希和特征生成向量
            return generateSimpleEmbedding(text);
            
        } catch (Exception e) {
            log.error("生成向量失败", e);
            return generateSimpleEmbedding(text);
        }
    }
    
    /**
     * 简化的向量生成（基于文本特征）
     * 实际应该调用embedding API
     */
    private double[] generateSimpleEmbedding(String text) {
        // 生成固定维度的向量（例如1536维，类似OpenAI的embedding）
        int dimension = 1536;
        double[] embedding = new double[dimension];
        
        // 基于文本内容生成伪向量（实际应使用embedding模型）
        int hash = text.hashCode();
        for (int i = 0; i < dimension; i++) {
            embedding[i] = Math.sin((hash + i) * 0.1) * 0.5 + 0.5;
        }
        
        // 归一化
        double norm = 0.0;
        for (double v : embedding) {
            norm += v * v;
        }
        norm = Math.sqrt(norm);
        if (norm > 0) {
            for (int i = 0; i < dimension; i++) {
                embedding[i] /= norm;
            }
        }
        
        return embedding;
    }
}

