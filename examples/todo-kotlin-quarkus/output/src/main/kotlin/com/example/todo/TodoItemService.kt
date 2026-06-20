package com.example.todo

import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.transaction.Transactional
@ApplicationScoped
open class TodoItemService {
    @Inject
    lateinit var repository: TodoItemRepository
    fun findAll(): java.util.List<TodoItem> {
        return repository.listAll()
    }
    fun findById(id: Long): TodoItem? {
        return repository.findById(id)
    }
    @Transactional
    fun create(entity: TodoItem): TodoItem {
        entity.createdAt = java.time.LocalDateTime.now()
        repository.persist(entity)
        return entity
    }
    @Transactional
    fun update(id: Long, entity: TodoItem): TodoItem? {
        val existing = repository.findById(id) ?: return null
        existing.name = entity.name
        existing.description = entity.description
        existing.updatedAt = java.time.LocalDateTime.now()
        repository.persist(existing)
        return existing
    }
    @Transactional
    fun deleteById(id: Long): Boolean {
        return repository.deleteById(id)
    }
}