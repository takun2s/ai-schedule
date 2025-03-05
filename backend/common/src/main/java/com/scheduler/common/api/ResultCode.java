package com.scheduler.common.api;

/**
 * 错误码枚举
 */
public enum ResultCode implements IErrorCode {
    SUCCESS(200, "操作成功"),
    FAILED(500, "操作失败"),
    UNAUTHORIZED(401, "暂未登录或token已经过期"),
    FORBIDDEN(403, "没有相关权限"),
    VALIDATE_FAILED(404, "参数检验失败"),
    
    // 用户相关错误码
    USER_NOT_EXIST(1001, "用户不存在"),
    USER_PASSWORD_ERROR(1002, "密码错误"),
    USER_ACCOUNT_EXPIRED(1003, "账号已过期"),
    USER_ACCOUNT_LOCKED(1004, "账号已被锁定"),
    USER_ACCOUNT_DISABLED(1005, "账号已被禁用"),
    
    // 任务相关错误码
    TASK_NOT_EXIST(2001, "任务不存在"),
    TASK_ALREADY_EXIST(2002, "任务已存在"),
    TASK_EXECUTION_FAILED(2003, "任务执行失败"),
    TASK_SCHEDULE_FAILED(2004, "任务调度失败"),
    TASK_PARAM_ERROR(2005, "任务参数错误"),
    
    // 系统相关错误码
    SYSTEM_ERROR(9001, "系统错误"),
    SERVICE_UNAVAILABLE(9002, "服务不可用"),
    GATEWAY_TIMEOUT(9003, "网关超时");

    private final int code;
    private final String message;

    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
} 