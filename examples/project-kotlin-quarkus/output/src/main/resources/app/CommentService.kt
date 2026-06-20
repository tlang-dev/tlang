package com.example.app

import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.transaction.Transactional
@ApplicationScoped
open class CommentService {
    @Inject
    lateinit var repository: CommentRepository
    fun findAll(): java.util.List<Comment> {
        return repository.listAll()
    }
    fun findById(id: Long): Comment? {
        return repository.findById(id)
    }
    @Transactional
    fun create(entity: Comment): Comment {
        entity.createdAt = java.time.LocalDateTime.now()
        repository.persist(entity)
        return entity
    }
    @Transactional
    fun update(id: Long, entity: Comment): Comment? {
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