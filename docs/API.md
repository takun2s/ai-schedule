# API 文档

## 1. 接口规范

### 1.1 请求格式
- 请求方法：GET、POST、PUT、DELETE
- 请求头：
  ```
  Content-Type: application/json
  Authorization: Bearer {token}
  ```
- 请求参数：
  - GET：URL参数
  - POST/PUT：JSON格式
  - DELETE：URL参数

### 1.2 响应格式
```json
{
    "code": 200,
    "message": "success",
    "data": {
        // 响应数据
    }
}
```

### 1.3 状态码
- 200：成功
- 400：请求参数错误
- 401：未授权
- 403：禁止访问
- 404：资源不存在
- 500：服务器错误

## 2. 用户接口

### 2.1 用户登录
- 请求路径：`/api/v1/user/login`
- 请求方法：POST
- 请求参数：
```json
{
    "username": "string",
    "password": "string"
}
```
- 响应数据：
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "token": "string",
        "userInfo": {
            "id": "number",
            "username": "string",
            "realName": "string",
            "email": "string",
            "phone": "string"
        }
    }
}
```

### 2.2 用户登出
- 请求路径：`/api/v1/user/logout`
- 请求方法：POST
- 请求头：
  ```
  Authorization: Bearer {token}
  ```
- 响应数据：
```json
{
    "code": 200,
    "message": "success"
}
```

### 2.3 获取用户信息
- 请求路径：`/api/v1/user/info`
- 请求方法：GET
- 请求头：
  ```
  Authorization: Bearer {token}
  ```
- 响应数据：
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "id": "number",
        "username": "string",
        "realName": "string",
        "email": "string",
        "phone": "string"
    }
}
```

### 2.4 修改密码
- 请求路径：`/api/v1/user/password`
- 请求方法：PUT
- 请求头：
  ```
  Authorization: Bearer {token}
  ```
- 请求参数：
```json
{
    "oldPassword": "string",
    "newPassword": "string"
}
```
- 响应数据：
```json
{
    "code": 200,
    "message": "success"
}
```

## 3. 任务接口

### 3.1 创建任务
- 请求路径：`/api/v1/task`
- 请求方法：POST
- 请求头：
  ```
  Authorization: Bearer {token}
  ```
- 请求参数：
```json
{
    "name": "string",
    "type": "string",
    "cron": "string",
    "timeout": "number",
    "content": "string",
    "params": "object",
    "alertEnabled": "boolean",
    "alertType": "string",
    "alertReceiver": "string",
    "alertCondition": "string"
}
```
- 响应数据：
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "id": "number"
    }
}
```

### 3.2 更新任务
- 请求路径：`/api/v1/task/{id}`
- 请求方法：PUT
- 请求头：
  ```
  Authorization: Bearer {token}
  ```
- 请求参数：
```json
{
    "name": "string",
    "type": "string",
    "cron": "string",
    "timeout": "number",
    "content": "string",
    "params": "object",
    "alertEnabled": "boolean",
    "alertType": "string",
    "alertReceiver": "string",
    "alertCondition": "string"
}
```
- 响应数据：
```json
{
    "code": 200,
    "message": "success"
}
```

### 3.3 删除任务
- 请求路径：`/api/v1/task/{id}`
- 请求方法：DELETE
- 请求头：
  ```
  Authorization: Bearer {token}
  ```
- 响应数据：
```json
{
    "code": 200,
    "message": "success"
}
```

### 3.4 获取任务列表
- 请求路径：`/api/v1/task`
- 请求方法：GET
- 请求头：
  ```
  Authorization: Bearer {token}
  ```
- 请求参数：
  - page：页码
  - size：每页大小
  - name：任务名称
  - type：任务类型
  - status：任务状态
- 响应数据：
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "total": "number",
        "list": [{
            "id": "number",
            "name": "string",
            "type": "string",
            "cron": "string",
            "status": "number",
            "createTime": "string",
            "updateTime": "string"
        }]
    }
}
```

### 3.5 获取任务详情
- 请求路径：`/api/v1/task/{id}`
- 请求方法：GET
- 请求头：
  ```
  Authorization: Bearer {token}
  ```
- 响应数据：
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "id": "number",
        "name": "string",
        "type": "string",
        "cron": "string",
        "timeout": "number",
        "content": "string",
        "params": "object",
        "status": "number",
        "alertEnabled": "boolean",
        "alertType": "string",
        "alertReceiver": "string",
        "alertCondition": "string",
        "createTime": "string",
        "updateTime": "string"
    }
}
```

### 3.6 执行任务
- 请求路径：`/api/v1/task/{id}/execute`
- 请求方法：POST
- 请求头：
  ```
  Authorization: Bearer {token}
  ```
- 响应数据：
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "executionId": "number"
    }
}
```

### 3.7 获取任务日志
- 请求路径：`/api/v1/task/{id}/logs`
- 请求方法：GET
- 请求头：
  ```
  Authorization: Bearer {token}
  ```
- 请求参数：
  - page：页码
  - size：每页大小
  - startTime：开始时间
  - endTime：结束时间
  - status：执行状态
- 响应数据：
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "total": "number",
        "list": [{
            "id": "number",
            "startTime": "string",
            "endTime": "string",
            "duration": "number",
            "status": "number",
            "result": "string",
            "errorMessage": "string"
        }]
    }
}
```

## 4. 执行记录接口

### 4.1 获取执行记录列表
- 请求路径：`/api/v1/execution`
- 请求方法：GET
- 请求头：
  ```
  Authorization: Bearer {token}
  ```
- 请求参数：
  - page：页码
  - size：每页大小
  - taskId：任务ID
  - startTime：开始时间
  - endTime：结束时间
  - status：执行状态
- 响应数据：
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "total": "number",
        "list": [{
            "id": "number",
            "taskId": "number",
            "taskName": "string",
            "startTime": "string",
            "endTime": "string",
            "duration": "number",
            "status": "number",
            "result": "string",
            "errorMessage": "string"
        }]
    }
}
```

### 4.2 获取执行记录详情
- 请求路径：`/api/v1/execution/{id}`
- 请求方法：GET
- 请求头：
  ```
  Authorization: Bearer {token}
  ```
- 响应数据：
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "id": "number",
        "taskId": "number",
        "taskName": "string",
        "startTime": "string",
        "endTime": "string",
        "duration": "number",
        "status": "number",
        "result": "string",
        "errorMessage": "string",
        "logs": [{
            "level": "string",
            "content": "string",
            "createTime": "string"
        }]
    }
}
```

## 5. 告警接口

### 5.1 获取告警列表
- 请求路径：`/api/v1/alert`
- 请求方法：GET
- 请求头：
  ```
  Authorization: Bearer {token}
  ```
- 请求参数：
  - page：页码
  - size：每页大小
  - taskId：任务ID
  - type：告警类型
  - status：告警状态
  - startTime：开始时间
  - endTime：结束时间
- 响应数据：
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "total": "number",
        "list": [{
            "id": "number",
            "taskId": "number",
            "taskName": "string",
            "type": "string",
            "content": "string",
            "receiver": "string",
            "status": "number",
            "sendResult": "string",
            "createTime": "string"
        }]
    }
}
```

### 5.2 获取告警详情
- 请求路径：`/api/v1/alert/{id}`
- 请求方法：GET
- 请求头：
  ```
  Authorization: Bearer {token}
  ```
- 响应数据：
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "id": "number",
        "taskId": "number",
        "taskName": "string",
        "type": "string",
        "content": "string",
        "receiver": "string",
        "status": "number",
        "sendResult": "string",
        "createTime": "string",
        "updateTime": "string"
    }
}
```

### 5.3 更新告警状态
- 请求路径：`/api/v1/alert/{id}/status`
- 请求方法：PUT
- 请求头：
  ```
  Authorization: Bearer {token}
  ```
- 请求参数：
```json
{
    "status": "number"
}
```
- 响应数据：
```json
{
    "code": 200,
    "message": "success"
}
```

## 6. 系统设置接口

### 6.1 获取系统设置
- 请求路径：`/api/v1/setting`
- 请求方法：GET
- 请求头：
  ```
  Authorization: Bearer {token}
  ```
- 请求参数：
  - category：设置类别
- 响应数据：
```json
{
    "code": 200,
    "message": "success",
    "data": [{
        "id": "number",
        "category": "string",
        "key": "string",
        "value": "string",
        "description": "string"
    }]
}
```

### 6.2 更新系统设置
- 请求路径：`/api/v1/setting`
- 请求方法：PUT
- 请求头：
  ```
  Authorization: Bearer {token}
  ```
- 请求参数：
```json
{
    "id": "number",
    "value": "string"
}
```
- 响应数据：
```json
{
    "code": 200,
    "message": "success"
}
```

## 7. 文件上传接口

### 7.1 上传文件
- 请求路径：`/api/v1/file/upload`
- 请求方法：POST
- 请求头：
  ```
  Authorization: Bearer {token}
  Content-Type: multipart/form-data
  ```
- 请求参数：
  - file：文件
- 响应数据：
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "url": "string"
    }
}
``` 