package com.shanzhu.hospital.config;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 拦截器配置
 *
 */
@Component
public class InterceptorConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(
                new JwtInterceptor())
                .addPathPatterns("/**")
                //文件导出
                .excludePathPatterns("/patient/pdf")
                //登录
                .excludePathPatterns("/**/login")
                //病患注册
                .excludePathPatterns("/**/addPatient")
                //AI聊天接口（允许匿名访问）
                .excludePathPatterns("/ai/**")
                //评价接口（如果需要匿名访问，取消注释）
                .excludePathPatterns("/review/**");
                //评价接口（如果需要匿名访问，取消注释）
                // .excludePathPatterns("/review/**");
    }

}
