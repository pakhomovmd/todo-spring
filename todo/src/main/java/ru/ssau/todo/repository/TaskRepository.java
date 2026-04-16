package ru.ssau.todo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.ssau.todo.entity.Task;
import ru.ssau.todo.entity.TaskStatus;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    // Метод получения задач с фильтрацией (нативный SQL)
    @Query(value = "SELECT * FROM task t WHERE t.created_by = :userId " +
            "AND t.created_at BETWEEN :from AND :to",
            nativeQuery = true)
    List<Task> findAllByDateRangeAndUserId(@Param("from") LocalDateTime from,
                                           @Param("to") LocalDateTime to,
                                           @Param("userId") Long userId);

    // Метод получения количества активных задач (JPQL)
    @Query("SELECT COUNT(t) FROM Task t WHERE t.createdBy.id = :userId " +
            "AND t.status IN :activeStatuses")
    long countActiveTasksByUserId(@Param("userId") Long userId,
                                  @Param("activeStatuses") List<TaskStatus> activeStatuses);
}