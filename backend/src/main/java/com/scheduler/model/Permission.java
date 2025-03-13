package com.scheduler.model;

import lombok.Data;

@Data
public class Permission {
    private Long id;
    private String resourceType;  // TASK, DAG, USER, ALERT
    private Long resourceId;
    private String action;        // READ, WRITE, EXECUTE, DELETE
}
