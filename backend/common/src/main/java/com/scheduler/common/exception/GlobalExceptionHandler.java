package com.scheduler.common.exception;

import com.scheduler.common.api.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public Result<Object> handleBaseException(BaseException e) {
        log.error("基础异常：{}", e.getMessage());
        return Result.failed(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(AuthenticationException.class)
    public Result<Object> handleAuthenticationException(AuthenticationException e) {
        log.error("认证异常：{}", e.getMessage());
        return Result.unauthorized();
    }

    @ExceptionHandler(AccessDeniedException.class)
    public Result<Object> handleAccessDeniedException(AccessDeniedException e) {
        log.error("权限异常：{}", e.getMessage());
        return Result.forbidden();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Object> handleValidException(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        String message = null;
        if (bindingResult.hasErrors()) {
            FieldError fieldError = bindingResult.getFieldError();
            if (fieldError != null) {
                message = fieldError.getDefaultMessage();
            }
        }
        log.error("参数校验异常：{}", message);
        return Result.failed(message);
    }

    @ExceptionHandler(BindException.class)
    public Result<Object> handleBindException(BindException e) {
        BindingResult bindingResult = e.getBindingResult();
        String message = null;
        if (bindingResult.hasErrors()) {
            FieldError fieldError = bindingResult.getFieldError();
            if (fieldError != null) {
                message = fieldError.getDefaultMessage();
            }
        }
        log.error("参数绑定异常：{}", message);
        return Result.failed(message);
    }

    @ExceptionHandler(Exception.class)
    public Result<Object> handleException(Exception e) {
        log.error("系统异常：", e);
        return Result.failed("系统异常，请联系管理员");
    }
} 