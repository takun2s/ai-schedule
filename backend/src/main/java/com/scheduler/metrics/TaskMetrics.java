package com.scheduler.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class TaskMetrics {

    private final Counter taskExecutionCounter;
    private final Counter taskSuccessCounter;
    private final Counter taskFailureCounter;

    public TaskMetrics(MeterRegistry registry) {
        this.taskExecutionCounter = Counter.builder("scheduler.task.executions")
                .description("Counter of task executions")
                .register(registry);

        this.taskSuccessCounter = Counter.builder("scheduler.task.success")
                .description("Counter of successful task executions")
                .register(registry);

        this.taskFailureCounter = Counter.builder("scheduler.task.failures")
                .description("Counter of failed task executions")
                .register(registry);
    }

    public void incrementTaskExecution() {
        taskExecutionCounter.increment();
    }

    public void incrementTaskSuccess() {
        taskSuccessCounter.increment();
    }

    public void incrementTaskFailure() {
        taskFailureCounter.increment();
    }
}
