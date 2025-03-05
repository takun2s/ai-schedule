package com.scheduler.common.exception;

import com.scheduler.common.api.IErrorCode;

/**
 * 基础异常类
 */
public class BaseException extends RuntimeException {
    private int code;
    private String message;

    public BaseException(IErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
    }

    public BaseException(IErrorCode errorCode, String message) {
        super(message);
        this.code = errorCode.getCode();
        this.message = message;
    }

    public BaseException(int code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public BaseException(String message) {
        super(message);
        this.code = 500;
        this.message = message;
    }

    public BaseException(Throwable cause) {
        super(cause);
        this.code = 500;
        this.message = cause.getMessage();
    }

    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
} 