package ru.ssau.todo.dto;

import ru.ssau.todo.entity.TaskStatus;
import java.time.LocalDateTime;

public class TaskDto {
    private Long id;
    private String title;
    private TaskStatus status;
    private Long createdBy;  // ID пользователя, а не объект User
    private LocalDateTime createdAt;

    // Конструкторы
    public TaskDto() {}

    public TaskDto(Long id, String title, TaskStatus status, Long createdBy, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.status = status;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
    }

    // Геттеры и сеттеры
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}