package com.bookrecommend.common;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 捕获所有运行时异常
    @ExceptionHandler(RuntimeException.class)
    public Result<Void> handleRuntimeException(RuntimeException e) {
        return Result.error(e.getMessage());
    }

    // 捕获参数校验失败的异常 (@Valid/@NotBlank 等)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getAllErrors().stream()
                .map(error -> error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return Result.error(400, message);
    }

    // 捕获其他所有未处理的异常
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        e.printStackTrace(); // 方便控制台调试
        return Result.error("服务器内部错误: " + e.getMessage());
    }
}
