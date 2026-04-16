package ru.ssau.todo.dto;

import java.util.List;

public class UserDto {
    private Long id;
    private String username;
    private String password;
    private List<String> roles;

    // Пустой конструктор (обязателен для Jackson!)
    public UserDto() {
    }

    // Конструктор с параметрами
    public UserDto(Long id, String username, List<String> roles) {
        this.id = id;
        this.username = username;
        this.roles = roles;
    }

    // Геттеры и сеттеры
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}