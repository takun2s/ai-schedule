package com.scheduler.common.executor.impl;

import com.scheduler.common.exception.BaseException;
import com.scheduler.common.executor.TaskExecutor;
import com.scheduler.common.model.Task;
import com.scheduler.common.model.TaskExecution;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

/**
 * JAR包任务执行器
 */
@Slf4j
@Component
public class JarTaskExecutor implements TaskExecutor {

    @Override
    public void execute(Task task, TaskExecution execution) {
        String jarPath = task.getContent();
        String params = task.getParams();
        
        try {
            // 检查JAR文件是否存在
            File jarFile = new File(jarPath);
            if (!jarFile.exists()) {
                throw new BaseException("JAR文件不存在：" + jarPath);
            }
            
            // 构建命令
            StringBuilder command = new StringBuilder();
            command.append("java -jar ").append(jarPath);
            if (params != null && !params.isEmpty()) {
                command.append(" ").append(params);
            }
            
            // 执行命令
            Process process = Runtime.getRuntime().exec(new String[]{"/bin/bash", "-c", command.toString()});
            
            // 设置超时时间
            if (!process.waitFor(task.getTimeout(), TimeUnit.SECONDS)) {
                process.destroyForcibly();
                execution.setStatus(3); // 执行失败
                execution.setErrorMessage("JAR执行超时");
                throw new BaseException("JAR执行超时");
            }
            
            // 读取输出
            StringBuilder output = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }
            
            // 检查执行结果
            int exitCode = process.exitValue();
            if (exitCode == 0) {
                execution.setStatus(2); // 执行成功
                execution.setResult(output.toString());
            } else {
                execution.setStatus(3); // 执行失败
                execution.setErrorMessage("JAR执行失败，退出码：" + exitCode);
                throw new BaseException("JAR执行失败，退出码：" + exitCode);
            }
        } catch (Exception e) {
            log.error("JAR任务执行失败", e);
            execution.setStatus(3); // 执行失败
            execution.setErrorMessage(e.getMessage());
            throw new BaseException("JAR任务执行失败: " + e.getMessage());
        }
    }

    @Override
    public Integer getType() {
        return 4; // JAR类型
    }

    @Override
    public String getName() {
        return "JAR任务执行器";
    }

    @Override
    public String getDescription() {
        return "支持JAR包执行的任务执行器";
    }
} 