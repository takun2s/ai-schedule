package com.scheduler.service.executor;

import com.scheduler.model.Task;
import lombok.extern.slf4j.Slf4j;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
public class SparkExecutor implements TaskExecutor {
    
    private Process currentProcess;
    private volatile boolean isStopped = false;

    @Override
    public void execute(Task task, TaskCallback onSuccess, TaskErrorCallback onError) {
        try {
            validate(task);
            List<String> command = buildCommand(task);
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            processBuilder.redirectErrorStream(true);

            isStopped = false;
            currentProcess = processBuilder.start();

            StringBuilder output = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(currentProcess.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null && !isStopped) {
                    output.append(line).append("\n");
                }
            }

            if (isStopped) {
                currentProcess.destroy();
                onError.onError(task, new InterruptedException("Task was stopped"));
                return;
            }

            boolean completed = currentProcess.waitFor(task.getTimeout(), TimeUnit.SECONDS);
            
            if (!completed) {
                currentProcess.destroy();
                onError.onError(task, new RuntimeException("Spark job execution timed out"));
                return;
            }

            int exitCode = currentProcess.exitValue();
            if (exitCode == 0) {
                onSuccess.onComplete(task, output.toString());
            } else {
                onError.onError(task, new RuntimeException(
                    String.format("Spark job failed with exit code %d: %s", exitCode, output)
                ));
            }
        } catch (Exception e) {
            log.error("Failed to execute Spark task: {}", task.getId(), e);
            onError.onError(task, e);
        } finally {
            currentProcess = null;
        }
    }

    private List<String> buildCommand(Task task) {
        List<String> command = new ArrayList<>();
        command.add("spark-submit");
        
        // Spark配置
        command.add("--master");
        command.add(task.getSparkMaster());
        
        if (task.getSparkAppName() != null) {
            command.add("--name");
            command.add(task.getSparkAppName());
        }
        
        // 添加Spark配置
        if (task.getSparkConfig() != null && !task.getSparkConfig().isEmpty()) {
            String[] configs = task.getSparkConfig().split(",");
            for (String config : configs) {
                String[] parts = config.split("=", 2);
                if (parts.length == 2) {
                    command.add("--conf");
                    command.add(parts[0].trim() + "=" + parts[1].trim());
                }
            }
        }
        
        // 主类
        if (task.getSparkMainClass() != null) {
            command.add("--class");
            command.add(task.getSparkMainClass());
        }
        
        // JAR包路径
        command.add(task.getJarPath());
        
        // 程序参数
        if (task.getSparkArgs() != null && !task.getSparkArgs().isEmpty()) {
            command.addAll(Arrays.asList(task.getSparkArgs().split("\\s+")));
        }
        
        return command;
    }

    @Override
    public void validate(Task task) {
        if (task.getJarPath() == null || task.getJarPath().trim().isEmpty()) {
            throw new IllegalArgumentException("JAR path cannot be empty");
        }
        
        if (task.getSparkMaster() == null || task.getSparkMaster().trim().isEmpty()) {
            throw new IllegalArgumentException("Spark master URL cannot be empty");
        }
    }

    @Override
    public void stop() {
        isStopped = true;
        if (currentProcess != null) {
            currentProcess.destroy();
        }
    }

    @Override
    public String getType() {
        return "SPARK";
    }
}
