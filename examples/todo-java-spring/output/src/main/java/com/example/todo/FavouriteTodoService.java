package com.example.todo;

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
@Service
public class FavouriteTodoService {
    private final FavouriteTodoRepository repository;
    public FavouriteTodoService(FavouriteTodoRepository repository) {
        this.repository = repository;
    }
    public java.util.List<FavouriteTodo> findAll() {
        return repository.findAll();
    }
    public FavouriteTodo findById(java.util.UUID id) {
        java.util.Optional<FavouriteTodo> opt = repository.findById(id);
        if (!opt.isPresent()) {
            throw new java.util.NoSuchElementException("FavouriteTodo not found: " + id);
        }
        return opt.get();
    }
    @Transactional
    public FavouriteTodo create(FavouriteTodo entity) {
        entity.setCreatedAt(java.time.LocalDateTime.now());
        return repository.save(entity);
    }
    @Transactional
    public FavouriteTodo update(java.util.UUID id, FavouriteTodo entity) {
        FavouriteTodo existing = findById(id);
        existing.setName(entity.getName());
        existing.setDescription(entity.getDescription());
        existing.setUpdatedAt(java.time.LocalDateTime.now());
        return repository.save(existing);
    }
    @Transactional
    public void deleteById(java.util.UUID id) {
        repository.deleteById(id);
    }
    public java.util.List<FavouriteTodo> findFavourites() {
        return repository.findByFavouriteTrue();
    }
}