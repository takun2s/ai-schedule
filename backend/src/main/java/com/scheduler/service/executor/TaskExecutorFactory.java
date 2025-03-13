package com.scheduler.service.executor;

import com.scheduler.model.Task;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TaskExecutorFactory {
    
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    
    public TaskExecutor getExecutor(Task task) {
        switch (task.getType().toUpperCase()) {
            case "COMMAND":
                return new CommandExecutor();
            case "HTTP":
                return new HttpExecutor(restTemplate, objectMapper);
            case "PYTHON":
                return new PythonExecutor();
            case "JAR":
                return new JarExecutor();
            case "SPARK":
                return new SparkExecutor();
            default:
                throw new IllegalArgumentException("Unsupported task type: " + task.getType());
        }
    }
}
