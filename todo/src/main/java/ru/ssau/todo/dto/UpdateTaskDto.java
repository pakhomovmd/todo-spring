package ru.ssau.todo.dto;

import ru.ssau.todo.entity.TaskStatus;

public class UpdateTaskDto {
    private String title;
    private TaskStatus status;

    // Геттеры и сеттеры
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }
}