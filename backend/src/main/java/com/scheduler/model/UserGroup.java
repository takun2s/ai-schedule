package com.scheduler.model;

import lombok.Data;
import java.util.List;

@Data
public class UserGroup {
    private Long id;
    private String name;
    private String description;
    private List<User> users;
    private List<Permission> permissions;
}
