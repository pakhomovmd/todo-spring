package ru.ssau.todo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ssau.todo.dto.CreateTaskDto;
import ru.ssau.todo.dto.TaskDto;
import ru.ssau.todo.dto.UpdateTaskDto;
import ru.ssau.todo.entity.Task;
import ru.ssau.todo.entity.TaskStatus;
import ru.ssau.todo.entity.User;
import ru.ssau.todo.repository.TaskRepository;
import ru.ssau.todo.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    @Autowired
    public TaskService(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    public TaskDto createTask(CreateTaskDto createDto, Long userId) {
        // Проверка на количество активных задач
        long activeTasksCount = taskRepository.countActiveTasksByUserId(userId,
                List.of(TaskStatus.OPEN, TaskStatus.IN_PROGRESS));

        if (activeTasksCount >= 10) {
            throw new IllegalStateException("User cannot have more than 10 active tasks");
        }

        // Находим пользователя
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Создаём задачу
        Task task = new Task();
        task.setTitle(createDto.getTitle());
        task.setStatus(createDto.getStatus());
        task.setCreatedBy(user);
        task.setCreatedAt(LocalDateTime.now());

        Task savedTask = taskRepository.save(task);

        return convertToDto(savedTask);
    }

    public TaskDto findTaskById(long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        return convertToDto(task);
    }

    public List<TaskDto> findAllTasks(LocalDateTime from, LocalDateTime to, long userId) {
        List<Task> tasks = taskRepository.findAllByDateRangeAndUserId(from, to, userId);
        return tasks.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public void updateTask(long id, UpdateTaskDto updateDto) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        task.setTitle(updateDto.getTitle());
        task.setStatus(updateDto.getStatus());

        taskRepository.save(task);
    }

    public void deleteTask(long id) {
        Task task = taskRepository.findById(id)
                .orElse(null);

        if (task != null) {

            if (task.getCreatedAt().isAfter(LocalDateTime.now().minusMinutes(5))) {
                throw new IllegalStateException("Cannot delete task created less than 5 minutes ago");
            }

            taskRepository.deleteById(id);
        }
    }

    public long countActiveTasks(long userId) {
        List<TaskStatus> activeStatuses = List.of(TaskStatus.OPEN, TaskStatus.IN_PROGRESS);
        return taskRepository.countActiveTasksByUserId(userId, activeStatuses);
    }

    // Конвертация Entity -> DTO
    private TaskDto convertToDto(Task task) {
        return new TaskDto(
                task.getId(),
                task.getTitle(),
                task.getStatus(),
                task.getCreatedBy().getId(),  // Берем только ID пользователя
                task.getCreatedAt()
        );
    }
}