package com.shanzhu.hospital.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * AI配置类
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "ai.deepseek")
public class AiConfig {
    
    /**
     * API Key
     */
    private String apiKey;
    
    /**
     * API基础地址
     */
    private String apiBaseUrl;
    
    /**
     * 模型名称
     */
    private String model;
    
    /**
     * 超时时间（毫秒）
     */
    private Integer timeout = 30000;
    
    /**
     * 最大重试次数
     */
    private Integer maxRetries = 3;
}

