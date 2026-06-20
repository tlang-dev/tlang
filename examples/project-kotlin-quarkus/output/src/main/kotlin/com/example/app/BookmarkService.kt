package com.example.app

import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.transaction.Transactional
@ApplicationScoped
open class BookmarkService {
    @Inject
    lateinit var repository: BookmarkRepository
    fun findAll(): java.util.List<Bookmark> {
        return repository.listAll()
    }
    fun findById(id: Long): Bookmark? {
        return repository.findById(id)
    }
    @Transactional
    fun create(entity: Bookmark): Bookmark {
        entity.createdAt = java.time.LocalDateTime.now()
        repository.persist(entity)
        return entity
    }
    @Transactional
    fun update(id: Long, entity: Bookmark): Bookmark? {
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
    fun findFavourites(): java.util.List<Bookmark> {
        return repository.findFavourites()
    }
}