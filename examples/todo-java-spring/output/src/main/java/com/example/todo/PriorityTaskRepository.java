package com.example.todo;

import org.springframework.stereotype.Repository
import org.springframework.data.jpa.repository.JpaRepository
import java.util.List
import java.util.UUID
import java.time.LocalDate
@Repository
public interface PriorityTaskRepository extends JpaRepository<PriorityTask, java.util.UUID> {
    java.util.List<PriorityTask> findByCompleted(boolean completed);
    java.util.List<PriorityTask> findByDueDateBefore(java.time.LocalDate date);
    java.util.List<PriorityTask> findByPriority(String priority);
    java.util.List<PriorityTask> findByCompletedAndPriority(boolean completed, String priority);
}