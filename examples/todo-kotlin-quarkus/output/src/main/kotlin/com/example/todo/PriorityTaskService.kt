package com.example.todo

import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.transaction.Transactional
@ApplicationScoped
open class PriorityTaskService {
    @Inject
    lateinit var repository: PriorityTaskRepository
    fun findAll(): java.util.List<PriorityTask> {
        return repository.listAll()
    }
    fun findById(id: Long): PriorityTask? {
        return repository.findById(id)
    }
    @Transactional
    fun create(entity: PriorityTask): PriorityTask {
        entity.createdAt = java.time.LocalDateTime.now()
        repository.persist(entity)
        return entity
    }
    @Transactional
    fun update(id: Long, entity: PriorityTask): PriorityTask? {
        val existing = repository.findById(id) ?: return null
        existing.title = entity.title
        existing.description = entity.description
        existing.completed = entity.completed
        existing.dueDate = entity.dueDate
        existing.updatedAt = java.time.LocalDateTime.now()
        repository.persist(existing)
        return existing
    }
    @Transactional
    fun complete(id: Long): PriorityTask? {
        val existing = repository.findById(id) ?: return null
        existing.completed = true
        existing.updatedAt = java.time.LocalDateTime.now()
        repository.persist(existing)
        return existing
    }
    @Transactional
    fun reopen(id: Long): PriorityTask? {
        val existing = repository.findById(id) ?: return null
        existing.completed = false
        existing.updatedAt = java.time.LocalDateTime.now()
        repository.persist(existing)
        return existing
    }
    @Transactional
    fun deleteById(id: Long): Boolean {
        return repository.deleteById(id)
    }
    fun findByCompleted(completed: Boolean): java.util.List<PriorityTask> {
        return repository.findByCompleted(completed)
    }
    fun findByPriority(priority: String): java.util.List<PriorityTask> {
        return repository.findByPriority(priority)
    }
}