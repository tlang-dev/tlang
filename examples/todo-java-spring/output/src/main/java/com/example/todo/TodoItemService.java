package com.example.todo;

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
@Service
public class TodoItemService {
    private final TodoItemRepository repository;
    public TodoItemService(TodoItemRepository repository) {
        this.repository = repository;
    }
    public java.util.List<TodoItem> findAll() {
        return repository.findAll();
    }
    public TodoItem findById(java.util.UUID id) {
        java.util.Optional<TodoItem> opt = repository.findById(id);
        if (!opt.isPresent()) {
            throw new java.util.NoSuchElementException("TodoItem not found: " + id);
        }
        return opt.get();
    }
    @Transactional
    public TodoItem create(TodoItem entity) {
        entity.setCreatedAt(java.time.LocalDateTime.now());
        return repository.save(entity);
    }
    @Transactional
    public TodoItem update(java.util.UUID id, TodoItem entity) {
        TodoItem existing = findById(id);
        existing.setName(entity.getName());
        existing.setDescription(entity.getDescription());
        existing.setUpdatedAt(java.time.LocalDateTime.now());
        return repository.save(existing);
    }
    @Transactional
    public void deleteById(java.util.UUID id) {
        repository.deleteById(id);
    }
}