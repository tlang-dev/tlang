package com.example.todo

import jakarta.enterprise.context.ApplicationScoped
import io.quarkus.hibernate.orm.panache.PanacheRepository
@ApplicationScoped
open class PriorityTaskRepository : PanacheRepository<PriorityTask> {
    fun findByCompleted(completed: Boolean): java.util.List<PriorityTask> {
        return find("completed", completed).list()
    }
    fun findByDueDateBefore(date: java.time.LocalDate): java.util.List<PriorityTask> {
        return find("dueDate < ?1", date).list()
    }
    fun findByPriority(priority: String): java.util.List<PriorityTask> {
        return find("priority", priority).list()
    }
}