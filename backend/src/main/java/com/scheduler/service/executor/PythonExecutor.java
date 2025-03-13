package com.scheduler.service.executor;

import com.scheduler.model.Task;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
public class PythonExecutor implements TaskExecutor {
    
    private Process currentProcess;
    private volatile boolean isStopped = false;

    @Override
    public void execute(Task task, TaskCallback onSuccess, TaskErrorCallback onError) {
        try {
            if (task.getPythonPath() == null || task.getPythonPath().trim().isEmpty()) {
                throw new IllegalArgumentException("Python script path cannot be empty");
            }

            // 构建命令
            List<String> command = buildCommand(task);
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            processBuilder.redirectErrorStream(true);

            // 如果有requirements.txt，先安装依赖
            if (task.getRequirements() != null && !task.getRequirements().trim().isEmpty()) {
                installDependencies(task.getRequirements());
            }

            // 执行Python脚本
            isStopped = false;
            currentProcess = processBuilder.start();

            // 收集输出
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
                onError.onError(task, new RuntimeException("Python script execution timed out"));
                return;
            }

            int exitCode = currentProcess.exitValue();
            if (exitCode == 0) {
                onSuccess.onComplete(task, output.toString());  // 传递输出到回调
            } else {
                onError.onError(task, new RuntimeException(
                    String.format("Python script failed with exit code %d: %s", exitCode, output)
                ));
            }
        } catch (Exception e) {
            log.error("Failed to execute Python task: {}", task.getId(), e);
            onError.onError(task, e);
        } finally {
            currentProcess = null;
        }
    }

    private void installDependencies(String requirements) throws Exception {
        // 创建临时requirements文件
        File reqFile = File.createTempFile("requirements", ".txt");
        try {
            java.nio.file.Files.write(reqFile.toPath(), requirements.getBytes());
            
            ProcessBuilder pip = new ProcessBuilder("pip", "install", "-r", reqFile.getAbsolutePath());
            Process process = pip.start();
            
            if (!process.waitFor(300, TimeUnit.SECONDS)) {  // 5分钟超时
                process.destroy();
                throw new RuntimeException("Dependencies installation timed out");
            }
            
            if (process.exitValue() != 0) {
                throw new RuntimeException("Failed to install dependencies");
            }
        } finally {
            reqFile.delete();
        }
    }

    private List<String> buildCommand(Task task) {
        List<String> command = new ArrayList<>();
        command.add(task.getPythonVersion() != null ? task.getPythonVersion() : "python3");
        command.add(task.getPythonPath());
        
        if (task.getCommand() != null && !task.getCommand().trim().isEmpty()) {
            String[] args = task.getCommand().split("\\s+");
            command.addAll(Arrays.asList(args));
        }
        
        return command;
    }

    @Override
    public void validate(Task task) {
        if (task.getPythonPath() == null || task.getPythonPath().trim().isEmpty()) {
            throw new IllegalArgumentException("Python script path cannot be empty");
        }
        
        File scriptFile = new File(task.getPythonPath());
        if (!scriptFile.exists() || !scriptFile.isFile()) {
            throw new IllegalArgumentException("Python script file does not exist");
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
        return "PYTHON";
    }
}
