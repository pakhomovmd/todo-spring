package ru.ssau.todo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.ssau.todo.dto.CreateTaskDto;
import ru.ssau.todo.dto.TaskDto;
import ru.ssau.todo.dto.UpdateTaskDto;
import ru.ssau.todo.entity.User;
import ru.ssau.todo.repository.UserRepository;
import ru.ssau.todo.service.TaskService;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;
    private final UserRepository userRepository;  // ← ДОБАВИЛИ!

    @Autowired
    public TaskController(TaskService taskService, UserRepository userRepository) {  // ← ДОБАВИЛИ параметр
        this.taskService = taskService;
        this.userRepository = userRepository;  // ← ДОБАВИЛИ
    }

    @GetMapping
    public ResponseEntity<List<TaskDto>> getAllTasks(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestParam Long userId) {

        LocalDateTime startDate = from != null ? from : LocalDateTime.of(1970, 1, 1, 0, 0);
        LocalDateTime endDate = to != null ? to : LocalDateTime.of(2100, 12, 31, 23, 59);

        List<TaskDto> tasks = taskService.findAllTasks(startDate, endDate, userId);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskDto> getTaskById(@PathVariable Long id) {
        try {
            TaskDto task = taskService.findTaskById(id);
            return ResponseEntity.ok(task);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<?> createTask(@RequestBody CreateTaskDto createDto) {
        try {
            // Получаем текущего авторизованного пользователя
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();

            // Находим пользователя в БД по username
            User user = userRepository.findByUsername(username)  // ← теперь работает!
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Создаём задачу, используя ID из БД
            TaskDto createdTask = taskService.createTask(createDto, user.getId());

            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(createdTask.getId())
                    .toUri();

            return ResponseEntity.created(location).body(createdTask);

        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTask(@PathVariable Long id, @RequestBody UpdateTaskDto updateDto) {
        try {
            taskService.updateTask(id, updateDto);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        try {
            taskService.deleteTask(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/active/count")
    public ResponseEntity<Long> countActiveTasks(@RequestParam Long userId) {
        long count = taskService.countActiveTasks(userId);
        return ResponseEntity.ok(count);
    }
}