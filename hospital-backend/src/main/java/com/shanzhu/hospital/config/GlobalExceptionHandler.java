package com.shanzhu.hospital.config;

import com.shanzhu.hospital.common.R;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    public R<?> handleRuntimeException(RuntimeException e) {
        System.err.println("捕获到 RuntimeException: " + e.getMessage());
        e.printStackTrace();
        return R.error("服务器内部错误: " + e.getMessage());
    }

    /**
     * 处理所有异常
     */
    @ExceptionHandler(Exception.class)
    public R<?> handleException(Exception e) {
        System.err.println("捕获到 Exception: " + e.getMessage());
        e.printStackTrace();
        return R.error("服务器内部错误: " + e.getMessage());
    }
}

