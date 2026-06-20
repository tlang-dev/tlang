package com.example.app

import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.transaction.Transactional
@ApplicationScoped
open class PostService {
    @Inject
    lateinit var repository: PostRepository
    fun findAll(): java.util.List<Post> {
        return repository.listAll()
    }
    fun findById(id: Long): Post? {
        return repository.findById(id)
    }
    @Transactional
    fun create(entity: Post): Post {
        entity.createdAt = java.time.LocalDateTime.now()
        repository.persist(entity)
        return entity
    }
    @Transactional
    fun update(id: Long, entity: Post): Post? {
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