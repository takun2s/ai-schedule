package com.scheduler.service;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;

@Service
public class ZookeeperLockService {
    
    @Autowired
    private CuratorFramework curatorFramework;
    
    public boolean acquireLock(String taskId, long timeout) {
        try {
            InterProcessMutex mutex = new InterProcessMutex(curatorFramework, "/scheduler/locks/" + taskId);
            return mutex.acquire(timeout, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            return false;
        }
    }
    
    public void releaseLock(String taskId) {
        try {
            InterProcessMutex mutex = new InterProcessMutex(curatorFramework, "/scheduler/locks/" + taskId);
            mutex.release();
        } catch (Exception e) {
            // 处理异常
        }
    }
}
