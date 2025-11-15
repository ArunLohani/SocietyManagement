package com.project.societyManagement.entity.types;

public enum Actions {
    CREATE (30),
    UPDATE (20),
    READ (10);


    private final Integer priority;

    Actions(Integer priority) {
        this.priority = priority;
    }

    public Integer getPriority() {
        return priority;
    }

}
