package com.scheduler.service;

import com.scheduler.dto.DagExecutionDetailDTO;
import com.scheduler.model.TaskExecution;
import com.scheduler.model.DagExecution;
import com.scheduler.repository.TaskExecutionRepository;
import com.scheduler.repository.DagExecutionRepository;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import java.util.List;
import com.scheduler.dto.DagExecutionDTO;
import org.springframework.transaction.annotation.Transactional;
import java.util.stream.Collectors;
import java.util.Comparator;

@Service
@RequiredArgsConstructor
public class ExecutionService {

    private final TaskExecutionRepository taskExecutionRepository;
    private final DagExecutionRepository dagExecutionRepository;

    public List<TaskExecution> getTaskExecutions() {
        return taskExecutionRepository.findTop100ByOrderByStartTimeDesc();
    }

    @Transactional(readOnly = true)
    public List<DagExecutionDTO> getDagExecutions() {
        List<DagExecution> executions = dagExecutionRepository.findAllWithTaskExecutions();

        return executions.stream().map(execution -> {
            DagExecutionDTO dto = new DagExecutionDTO();
            dto.setId(execution.getId());
            dto.setDagId(execution.getDag().getId());
            dto.setDagName(execution.getDagName());
            dto.setStatus(execution.getStatus());
            dto.setStartTime(execution.getStartTime());
            dto.setEndTime(execution.getEndTime());
            dto.setDuration(execution.getDuration());
            dto.setError(execution.getError());
            dto.setCreatedAt(execution.getCreatedAt());

            // 任务执行统计
            List<TaskExecution> tasks = execution.getTaskExecutions();
            dto.setTotalTasks(tasks.size());
            dto.setCompletedTasks((int) tasks.stream()
                    .filter(t -> TaskExecution.STATUS_COMPLETED.equals(t.getStatus())).count());
            dto.setPendingTasks((int) tasks.stream()
                    .filter(t -> TaskExecution.STATUS_PENDING.equals(t.getStatus())).count());
            dto.setRunningTasks((int) tasks.stream()
                    .filter(t -> TaskExecution.STATUS_RUNNING.equals(t.getStatus())).count());
            dto.setFailedTasks((int) tasks.stream()
                    .filter(t -> TaskExecution.STATUS_FAILED.equals(t.getStatus())).count());

            if (dto.getTotalTasks() > 0) {
                dto.setProgress(dto.getCompletedTasks() * 100.0 / dto.getTotalTasks());
            }

            // 设置任务执行列表
            dto.setTaskSummaries(tasks.stream()
                    .map(task -> {
                        DagExecutionDTO.TaskSummary summary = new DagExecutionDTO.TaskSummary();
                        summary.setNodeId(task.getNodeId());
                        summary.setTaskName(task.getTaskName());
                        summary.setStatus(task.getStatus());
                        summary.setStartTime(task.getStartTime());
                        summary.setEndTime(task.getEndTime());
                        return summary;
                    })
                    .sorted(Comparator.comparing(s -> s.getStartTime(), Comparator.nullsLast(Comparator.naturalOrder())))
                    .collect(Collectors.toList()));

            return dto;
        }).collect(Collectors.toList());
    }

    public TaskExecution getTaskExecutionDetail(Long id) {
        return taskExecutionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task execution not found"));
    }

    @Transactional(readOnly = true)
    public DagExecutionDetailDTO getDagExecutionDetail(Long id) {
        DagExecution execution = dagExecutionRepository.findByIdWithTaskExecutions(id)
                .orElseThrow(() -> new RuntimeException("DAG execution not found: " + id));

        DagExecutionDetailDTO dto = new DagExecutionDetailDTO();
        dto.setId(execution.getId());
        dto.setDagId(execution.getDag().getId());
        dto.setDagName(execution.getDagName());
        dto.setStatus(execution.getStatus());
        dto.setStartTime(execution.getStartTime());
        dto.setEndTime(execution.getEndTime());
        dto.setDuration(execution.getDuration());
        dto.setError(execution.getError());
        dto.setCreatedAt(execution.getCreatedAt());

        List<TaskExecution> tasks = execution.getTaskExecutions();
        if (tasks != null) {
            // 设置统计信息
            dto.setTotalTasks(tasks.size());
            dto.setCompletedTasks((int) tasks.stream()
                    .filter(t -> TaskExecution.STATUS_COMPLETED.equals(t.getStatus())).count());
            dto.setPendingTasks((int) tasks.stream()
                    .filter(t -> TaskExecution.STATUS_PENDING.equals(t.getStatus())).count());
            dto.setRunningTasks((int) tasks.stream()
                    .filter(t -> TaskExecution.STATUS_RUNNING.equals(t.getStatus())).count());
            dto.setFailedTasks((int) tasks.stream()
                    .filter(t -> TaskExecution.STATUS_FAILED.equals(t.getStatus())).count());

            // 计算进度
            if (dto.getTotalTasks() > 0) {
                dto.setProgress(dto.getCompletedTasks() * 100.0 / dto.getTotalTasks());
            }

            // 使用 taskSummaries 替代 taskExecutions
            List<DagExecutionDetailDTO.TaskSummary> taskSummaries = tasks.stream()
                    .sorted(Comparator.comparing(TaskExecution::getStartTime, Comparator.nullsLast(Comparator.naturalOrder())))
                    .map(taskExec -> {
                        DagExecutionDetailDTO.TaskSummary summary = new DagExecutionDetailDTO.TaskSummary();
                        summary.setId(taskExec.getId());
                        summary.setNodeId(taskExec.getNodeId());
                        summary.setTaskName(taskExec.getTaskName());
                        summary.setStatus(taskExec.getStatus());
                        summary.setStartTime(taskExec.getStartTime());
                        summary.setEndTime(taskExec.getEndTime());
                        summary.setDuration(taskExec.getDuration());
                        summary.setError(taskExec.getError());
                        summary.setOutput(taskExec.getOutput());
                        return summary;
                    })
                    .collect(Collectors.toList());

            dto.setTaskSummaries(taskSummaries);
        }

        return dto;
    }

    private DagExecutionDTO convertToDTO(DagExecution execution) {
        DagExecutionDTO dto = new DagExecutionDTO();
        dto.setId(execution.getId());
        dto.setDagId(execution.getDag().getId());
        dto.setDagName(execution.getDagName());
        dto.setStatus(execution.getStatus());
        dto.setStartTime(execution.getStartTime());
        dto.setEndTime(execution.getEndTime());
        dto.setDuration(execution.getDuration());
        dto.setError(execution.getError());
        dto.setCreatedAt(execution.getCreatedAt());

        // 添加任务统计信息
        List<TaskExecution> tasks = execution.getTaskExecutions();
        if (tasks != null) {
            dto.setTotalTasks(tasks.size());
            dto.setCompletedTasks((int) tasks.stream().filter(t -> TaskExecution.STATUS_COMPLETED.equals(t.getStatus())).count());
            dto.setPendingTasks((int) tasks.stream().filter(t -> TaskExecution.STATUS_PENDING.equals(t.getStatus())).count());
            dto.setRunningTasks((int) tasks.stream().filter(t -> TaskExecution.STATUS_RUNNING.equals(t.getStatus())).count());
            dto.setFailedTasks((int) tasks.stream().filter(t -> TaskExecution.STATUS_FAILED.equals(t.getStatus())).count());

            // 计算进度
            if (dto.getTotalTasks() > 0) {
                dto.setProgress(dto.getCompletedTasks() * 100.0 / dto.getTotalTasks());
            }

            // 添加任务摘要信息
            List<DagExecutionDTO.TaskSummary> taskSummaries = tasks.stream()
                    .map(task -> {
                        DagExecutionDTO.TaskSummary summary = new DagExecutionDTO.TaskSummary();
                        summary.setNodeId(task.getNodeId());
                        summary.setTaskName(task.getTaskName());
                        summary.setStatus(task.getStatus());
                        summary.setStartTime(task.getStartTime());
                        summary.setEndTime(task.getEndTime());
                        return summary;
                    })
                    .sorted(Comparator.comparing(t -> t.getStartTime(), Comparator.nullsLast(Comparator.naturalOrder())))
                    .collect(Collectors.toList());

            dto.setTaskSummaries(taskSummaries);
        }

        return dto;
    }
}
