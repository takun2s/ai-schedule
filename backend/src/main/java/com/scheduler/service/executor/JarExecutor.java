package com.scheduler.service.executor;

import com.scheduler.model.Task;
import lombok.extern.slf4j.Slf4j;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
public class JarExecutor implements TaskExecutor {
    
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
                onError.onError(task, new RuntimeException("JAR execution timed out"));
                return;
            }

            int exitCode = currentProcess.exitValue();
            if (exitCode == 0) {
                onSuccess.onComplete(task, output.toString());
            } else {
                onError.onError(task, new RuntimeException(
                    String.format("JAR execution failed with exit code %d: %s", exitCode, output)
                ));
            }
        } catch (Exception e) {
            log.error("Failed to execute JAR task: {}", task.getId(), e);
            onError.onError(task, e);
        } finally {
            currentProcess = null;
        }
    }

    private List<String> buildCommand(Task task) {
        List<String> command = new ArrayList<>();
        command.add("java");
        
        // 添加 Java 选项
        if (task.getJavaOpts() != null && !task.getJavaOpts().isEmpty()) {
            command.addAll(Arrays.asList(task.getJavaOpts().split("\\s+")));
        }
        
        // jar参数
        command.add("-jar");
        command.add(task.getJarPath());
        
        // 主类名
        if (task.getMainClass() != null && !task.getMainClass().isEmpty()) {
            command.add(task.getMainClass());
        }
        
        // 命令行参数
        if (task.getCommand() != null && !task.getCommand().isEmpty()) {
            command.addAll(Arrays.asList(task.getCommand().split("\\s+")));
        }
        
        return command;
    }

    @Override
    public void validate(Task task) {
        if (task.getJarPath() == null || task.getJarPath().trim().isEmpty()) {
            throw new IllegalArgumentException("JAR path cannot be empty");
        }
        
        File jarFile = new File(task.getJarPath());
        if (!jarFile.exists() || !jarFile.isFile()) {
            throw new IllegalArgumentException("JAR file does not exist: " + task.getJarPath());
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
        return "JAR";
    }
}
