package com.scheduler.service;

import com.scheduler.model.Dashboard;
import java.util.Map;

public interface DashboardService {
    Dashboard getStats();
    Dashboard getLatestStats();
    Map<String, Object> getTaskStats();
    Map<String, Object> getDagStats();  // 添加这个方法
}
