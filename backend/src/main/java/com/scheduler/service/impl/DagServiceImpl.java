package com.scheduler.service.impl;

import com.scheduler.model.Dag;
import com.scheduler.model.DagTask;
import com.scheduler.model.Task;
import com.scheduler.repository.DagRepository;
import com.scheduler.repository.DagTaskRepository;
import com.scheduler.repository.TaskRepository;  // 添加这个导入
import com.scheduler.service.DagService;
import com.scheduler.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scheduler.service.executor.DagExecutor;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.Date;
import java.util.TimeZone;

@Slf4j
@Service
@RequiredArgsConstructor
public class DagServiceImpl implements DagService {

    private final DagRepository dagRepository;
    private final DagTaskRepository dagTaskRepository;
    private final TaskRepository taskRepository;  // 添加这个字段
    private final TaskService taskService;
    private final ObjectMapper objectMapper;
    private final DagExecutor dagExecutor;

    // 存储正在运行的DAG实例
    private final Map<Long, DagExecutionContext> runningDags = new ConcurrentHashMap<>();

    @Override
    public List<Dag> getAllDags() {
        List<Dag> dags = dagRepository.findAll();
        dags.forEach(this::calculateNextExecuteTime);
        return dags;
    }

    @Override
    public Dag getDagById(Long id) {
        return dagRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("DAG not found"));
    }

    @Override
    @Transactional
    public Dag createDag(Dag dag) {
        validateDag(dag);
        dag.setCreateTime(LocalDateTime.now());
        dag.setUpdateTime(LocalDateTime.now());
        calculateNextExecuteTime(dag);
        Dag savedDag = dagRepository.save(dag);
        
        // 处理节点和边的信息
        try {
            Map<String, DagTask> nodeIdToTask = new HashMap<>();
            List<Map<String, Object>> nodes = objectMapper.readValue(dag.getNodes(), List.class);
            List<Map<String, Object>> edges = objectMapper.readValue(dag.getEdges(), List.class);

            // 创建DagTask
            int sequence = 1;  // 初始化序号
            for (Map<String, Object> node : nodes) {
                // 先获取Task
                Number taskIdNum = (Number) node.get("taskId");
                if (taskIdNum == null) {
                    throw new RuntimeException("Task ID is required for node: " + node.get("id"));
                }
                Long taskId = taskIdNum.longValue();
                Task task = taskRepository.findById(taskId)
                    .orElseThrow(() -> new RuntimeException("Task not found: " + taskId));

                // 创建DagTask
                DagTask dagTask = new DagTask();
                dagTask.setDag(savedDag);
                dagTask.setTask(task);  // 确保设置task
                dagTask.setNodeId(node.get("id").toString());
                dagTask.setSequence(sequence++);
                
                // 设置其他必要字段
                dagTask.setRetryCount(0);
                dagTask.setMaxRetryCount(3);
                dagTask.setIsRetry(false);
                dagTask.setLevel(1);
                dagTask.setRetryInterval(0);
                dagTask.setDownstreamTasks(new ArrayList<>());
                dagTask.setUpstreamTasks(new ArrayList<>());
                
                // 保存并刷新
                dagTask = dagTaskRepository.saveAndFlush(dagTask);
                nodeIdToTask.put(dagTask.getNodeId(), dagTask);
            }

            // 建立依赖关系
            for (Map<String, Object> edge : edges) {
                String sourceId = edge.get("source").toString();
                String targetId = edge.get("target").toString();
                
                DagTask sourceTask = nodeIdToTask.get(sourceId);
                DagTask targetTask = nodeIdToTask.get(targetId);

                if (sourceTask != null && targetTask != null) {
                    sourceTask.getDownstreamTasks().add(targetTask);
                    targetTask.getUpstreamTasks().add(sourceTask);
                    
                    dagTaskRepository.saveAndFlush(sourceTask);
                    dagTaskRepository.saveAndFlush(targetTask);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to process DAG structure: " + e.getMessage());
        }
        
        return savedDag;
    }

    @Override
    @Transactional
    public Dag updateDag(Long id, Dag dag) {
        validateDag(dag);
        Dag existingDag = getDagById(id);
        if (existingDag == null) {
            throw new RuntimeException("DAG not found: " + id);
        }
        
        // 清理现有的关系
        List<DagTask> existingTasks = dagTaskRepository.findByDagId(id);
        for (DagTask task : existingTasks) {
            task.getDownstreamTasks().clear();
            task.getUpstreamTasks().clear();
            dagTaskRepository.save(task);
        }
        dagTaskRepository.deleteAll(existingTasks);
        
        // 更新DAG基本信息
        existingDag.setName(dag.getName());
        existingDag.setDescription(dag.getDescription());
        existingDag.setCronExpression(dag.getCronExpression());
        existingDag.setNodes(dag.getNodes());
        existingDag.setEdges(dag.getEdges());
        existingDag.setUpdateTime(LocalDateTime.now());
        
        // 保存更新后的DAG
        existingDag = dagRepository.saveAndFlush(existingDag);
        
        // 创建新的任务关系
        try {
            Map<String, DagTask> nodeIdToTask = new HashMap<>();
            List<Map<String, Object>> nodes = objectMapper.readValue(dag.getNodes(), List.class);
            List<Map<String, Object>> edges = objectMapper.readValue(dag.getEdges(), List.class);

            // 先创建所有任务节点
            int sequence = 1;
            for (Map<String, Object> node : nodes) {
                String nodeId = node.get("id").toString();
                Number taskIdNum = (Number) node.get("taskId");
                if (taskIdNum == null) {
                    throw new RuntimeException("Task ID is required for node: " + nodeId);
                }
                Long taskId = taskIdNum.longValue();

                // 先获取Task实体
                Task task = taskRepository.findById(taskId)
                    .orElseThrow(() -> new RuntimeException("Task not found: " + taskId));

                // 创建并初始化DagTask
                DagTask dagTask = new DagTask();
                dagTask.setDag(existingDag);
                dagTask.setTask(task);  // 确保设置task
                dagTask.setNodeId(nodeId);
                dagTask.setSequence(sequence++);
                dagTask.setRetryCount(0);
                dagTask.setMaxRetryCount(3);
                dagTask.setIsRetry(false);
                dagTask.setLevel(1);
                dagTask.setDownstreamTasks(new ArrayList<>());
                dagTask.setUpstreamTasks(new ArrayList<>());

                // 保存并刷新
                dagTask = dagTaskRepository.saveAndFlush(dagTask);
                nodeIdToTask.put(nodeId, dagTask);
            }

            // 建立依赖关系
            for (Map<String, Object> edge : edges) {
                String sourceId = edge.get("source").toString();
                String targetId = edge.get("target").toString();
                
                DagTask sourceTask = nodeIdToTask.get(sourceId);
                DagTask targetTask = nodeIdToTask.get(targetId);

                if (sourceTask != null && targetTask != null) {
                    sourceTask.getDownstreamTasks().add(targetTask);
                    targetTask.getUpstreamTasks().add(sourceTask);
                    
                    dagTaskRepository.saveAndFlush(sourceTask);
                    dagTaskRepository.saveAndFlush(targetTask);
                }
            }
        } catch (Exception e) {
            log.error("Failed to update DAG structure", e);
            throw new RuntimeException("Failed to update DAG structure: " + e.getMessage(), e);
        }
        
        return existingDag;
    }

    @Override
    @Transactional
    public void deleteDag(Long id) {
        Dag dag = getDagById(id);
        if (dag == null) {
            throw new RuntimeException("DAG not found: " + id);
        }
        
        dagRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void executeDag(Long dagId) {
        Dag dag = getDagById(dagId);
        List<DagTask> tasks = dagTaskRepository.findByDagId(dagId);
        dagExecutor.execute(dag, tasks);
    }

    @Override
    public void stopDag(Long dagId) {
        dagExecutor.stop(dagId);
    }

    private void validateDag(Dag dag) {
        if (dag.getName() == null || dag.getName().trim().isEmpty()) {
            throw new RuntimeException("DAG name cannot be empty");
        }
        
        // 只在cronExpression不为空时才验证
        if (dag.getCronExpression() != null && !dag.getCronExpression().trim().isEmpty()) {
            try {
                new CronTrigger(dag.getCronExpression());
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Invalid cron expression: " + e.getMessage());
            }
        }
        
        try {
            // 验证nodes和edges的JSON格式
            if (dag.getNodes() != null) {
                objectMapper.readTree(dag.getNodes());
            }
            if (dag.getEdges() != null) {
                objectMapper.readTree(dag.getEdges());
            }
        } catch (Exception e) {
            throw new RuntimeException("Invalid DAG definition: " + e.getMessage());
        }
    }

    private void calculateNextExecuteTime(Dag dag) {
        // 只在 cronExpression 不为空且不为空字符串时计算下次执行时间
        if (dag.getCronExpression() != null && !dag.getCronExpression().trim().isEmpty()) {
            try {
                CronTrigger trigger = new CronTrigger(dag.getCronExpression());
                TimeZone timeZone = TimeZone.getDefault();
                Date now = new Date();
                Date nextDate = trigger.nextExecutionTime(
                    new org.springframework.scheduling.TriggerContext() {
                        @Override
                        public Date lastScheduledExecutionTime() {
                            return now;
                        }
                        @Override
                        public Date lastActualExecutionTime() {
                            return now;
                        }
                        @Override
                        public Date lastCompletionTime() {
                            return now;
                        }
                    }
                );
                dag.setNextExecuteTime(
                    LocalDateTime.ofInstant(nextDate.toInstant(), timeZone.toZoneId())
                );
            } catch (IllegalArgumentException e) {
                log.error("Invalid cron expression: {}", dag.getCronExpression(), e);
                // 无效的cron表达式时，清除下次执行时间
                dag.setNextExecuteTime(null);
            }
        } else {
            // cron表达式为空时，清除下次执行时间
            dag.setNextExecuteTime(null);
        }
    }

    private List<DagTask> parseDagDefinition(Dag dag) throws Exception {
        // 从JSON解析节点和边
        List<DagTask> tasks = new ArrayList<>();
        if (dag.getNodes() != null && dag.getEdges() != null) {
            // 解析节点
            Map<String, Object> nodesMap = objectMapper.readValue(dag.getNodes(), Map.class);
            Map<String, Object> edgesMap = objectMapper.readValue(dag.getEdges(), Map.class);
            
            // 构建任务依赖关系
            buildDagTasks(nodesMap, edgesMap, tasks);
        }
        return tasks;
    }

    private void buildDagTasks(Map<String, Object> nodes, Map<String, Object> edges, List<DagTask> tasks) {
        // 存储节点ID和任务的映射关系
        Map<String, DagTask> taskMap = new HashMap<>();
        
        // 1. 首先创建所有任务节点
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> nodesList = (List<Map<String, Object>>) nodes.get("nodes");
        if (nodesList != null) {
            for (Map<String, Object> node : nodesList) {
                String nodeId = (String) node.get("id");
                Long taskId = Long.parseLong((String) node.get("taskId"));
                
                DagTask dagTask = new DagTask();
                dagTask.setTask(taskService.getTaskById(taskId));
                dagTask.setNodeId(nodeId);
                dagTask.setSequence(tasks.size() + 1);
                dagTask.setRetryCount(0);
                dagTask.setDependencies(new ArrayList<>());
                
                // 从节点数据中获取重试配置
                Map<String, Object> data = (Map<String, Object>) node.get("data");
                if (data != null) {
                    dagTask.setIsRetry((Boolean) data.getOrDefault("isRetry", false));
                    dagTask.setMaxRetryCount((Integer) data.getOrDefault("maxRetryCount", 0));
                    dagTask.setRetryInterval((Integer) data.getOrDefault("retryInterval", 0));
                }
                
                tasks.add(dagTask);
                taskMap.put(nodeId, dagTask);
            }
        }
        
        // 2. 构建任务依赖关系
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> edgesList = (List<Map<String, Object>>) edges.get("edges");
        if (edgesList != null) {
            for (Map<String, Object> edge : edgesList) {
                String sourceId = (String) edge.get("source");
                String targetId = (String) edge.get("target");
                
                DagTask targetTask = taskMap.get(targetId);
                DagTask sourceTask = taskMap.get(sourceId);
                
                if (targetTask != null && sourceTask != null) {
                    targetTask.getDependencies().add(sourceTask);
                }
            }
        }
        
        // 3. 验证DAG是否有环
        if (hasCycle(tasks)) {
            throw new RuntimeException("DAG contains cycle, invalid definition");
        }
        
        // 4. 计算任务的层级
        calculateTaskLevels(tasks);
    }

    // 检测DAG是否存在环
    private boolean hasCycle(List<DagTask> tasks) {
        Set<DagTask> visited = new HashSet<>();
        Set<DagTask> recStack = new HashSet<>();
        
        for (DagTask task : tasks) {
            if (hasCycleUtil(task, visited, recStack)) {
                return true;
            }
        }
        return false;
    }

    private boolean hasCycleUtil(DagTask task, Set<DagTask> visited, Set<DagTask> recStack) {
        if (recStack.contains(task)) {
            return true;
        }
        if (visited.contains(task)) {
            return false;
        }
        
        visited.add(task);
        recStack.add(task);
        
        for (DagTask dep : task.getDependencies()) {
            if (hasCycleUtil(dep, visited, recStack)) {
                return true;
            }
        }
        
        recStack.remove(task);
        return false;
    }

    // 计算任务层级
    private void calculateTaskLevels(List<DagTask> tasks) {
        Map<DagTask, Integer> levels = new HashMap<>();
        Queue<DagTask> queue = new LinkedList<>();
        
        // 找出没有依赖的任务，设置为第一层
        tasks.stream()
            .filter(task -> task.getDependencies().isEmpty())
            .forEach(task -> {
                levels.put(task, 1);
                queue.offer(task);
            });
        
        // 广度优先遍历计算层级
        while (!queue.isEmpty()) {
            DagTask task = queue.poll();
            int level = levels.get(task);
            
            // 获取所有依赖此任务的下游任务
            List<DagTask> downstream = tasks.stream()
                .filter(t -> t.getDependencies().contains(task))
                .collect(Collectors.toList());
            
            // 更新下游任务的层级
            for (DagTask next : downstream) {
                int maxDependencyLevel = next.getDependencies().stream()
                    .mapToInt(dep -> levels.getOrDefault(dep, 0))
                    .max()
                    .orElse(0);
                    
                if (maxDependencyLevel == level) {
                    levels.put(next, level + 1);
                    queue.offer(next);
                }
            }
        }
        
        // 设置任务层级
        tasks.forEach(task -> task.setLevel(levels.getOrDefault(task, 1)));
    }

    @lombok.Data
    private static class DagExecutionContext {
        private final Dag dag;
        private final List<DagTask> tasks;
        private final Map<Long, DagTask> taskMap;  // 改回使用Long作为key
        
        public DagExecutionContext(Dag dag, List<DagTask> tasks) {
            this.dag = dag;
            this.tasks = tasks;
            this.taskMap = tasks.stream()
                .collect(Collectors.toMap(
                    DagTask::getId,  // 使用task的id作为key
                    task -> task
                ));
        }
    }
}
