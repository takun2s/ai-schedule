package com.scheduler.service.executor;

import com.scheduler.model.Task;
import lombok.extern.slf4j.Slf4j;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

@Slf4j
public class CommandExecutor implements TaskExecutor {
    
    private Process currentProcess;
    private volatile boolean isStopped = false;

    @Override
    public void execute(Task task, TaskCallback onSuccess, TaskErrorCallback onError) {
        try {
            if (task.getCommand() == null || task.getCommand().trim().isEmpty()) {
                throw new IllegalArgumentException("Command cannot be empty");
            }

            ProcessBuilder processBuilder = new ProcessBuilder();
            if (System.getProperty("os.name").toLowerCase().startsWith("windows")) {
                processBuilder.command("cmd.exe", "/c", task.getCommand());
            } else {
                processBuilder.command("sh", "-c", task.getCommand());
            }

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
                onError.onError(task, new RuntimeException("Command execution timed out"));
                return;
            }

            int exitCode = currentProcess.exitValue();
            if (exitCode == 0) {
                onSuccess.onComplete(task, output.toString());  // 传递输出
            } else {
                onError.onError(task, new RuntimeException(
                    String.format("Command failed with exit code %d: %s", exitCode, output)
                ));
            }
            
        } catch (Exception e) {
            log.error("Failed to execute command task: {}", task.getId(), e);
            onError.onError(task, e);
        } finally {
            currentProcess = null;
        }
    }

    @Override
    public void validate(Task task) {
        if (task.getCommand() == null || task.getCommand().trim().isEmpty()) {
            throw new IllegalArgumentException("Command cannot be empty");
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
        return "COMMAND";
    }
}
