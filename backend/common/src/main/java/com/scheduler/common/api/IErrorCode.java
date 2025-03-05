package com.scheduler.common.api;

/**
 * 错误码接口
 */
public interface IErrorCode {
    /**
     * 获取错误码
     */
    int getCode();

    /**
     * 获取错误信息
     */
    String getMessage();
} 