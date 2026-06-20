package com.example.todo

import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.transaction.Transactional
@ApplicationScoped
open class FavouriteTodoService {
    @Inject
    lateinit var repository: FavouriteTodoRepository
    fun findAll(): java.util.List<FavouriteTodo> {
        return repository.listAll()
    }
    fun findById(id: Long): FavouriteTodo? {
        return repository.findById(id)
    }
    @Transactional
    fun create(entity: FavouriteTodo): FavouriteTodo {
        entity.createdAt = java.time.LocalDateTime.now()
        repository.persist(entity)
        return entity
    }
    @Transactional
    fun update(id: Long, entity: FavouriteTodo): FavouriteTodo? {
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
    fun findFavourites(): java.util.List<FavouriteTodo> {
        return repository.findFavourites()
    }
}