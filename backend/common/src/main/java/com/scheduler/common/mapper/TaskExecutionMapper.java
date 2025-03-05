package com.scheduler.common.mapper;

import com.scheduler.common.model.TaskExecution;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 任务执行记录Mapper接口
 */
@Mapper
public interface TaskExecutionMapper {
    
    /**
     * 根据ID查询执行记录
     */
    TaskExecution selectById(Long id);
    
    /**
     * 查询执行记录列表
     */
    List<TaskExecution> selectList(TaskExecution execution);
    
    /**
     * 新增执行记录
     */
    int insert(TaskExecution execution);
    
    /**
     * 修改执行记录
     */
    int update(TaskExecution execution);
    
    /**
     * 删除执行记录
     */
    int deleteById(Long id);
    
    /**
     * 修改执行状态
     */
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
    
    /**
     * 查询任务最近一次执行记录
     */
    TaskExecution selectLastByTaskId(Long taskId);
    
    /**
     * 查询任务执行记录数量
     */
    int selectCountByTaskId(Long taskId);
} 