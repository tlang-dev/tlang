package com.example.todo;

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
@Service
public class PriorityTaskService {
    private final PriorityTaskRepository repository;
    public PriorityTaskService(PriorityTaskRepository repository) {
        this.repository = repository;
    }
    public java.util.List<PriorityTask> findAll() {
        return repository.findAll();
    }
    public PriorityTask findById(java.util.UUID id) {
        java.util.Optional<PriorityTask> opt = repository.findById(id);
        if (!opt.isPresent()) {
            throw new java.util.NoSuchElementException("PriorityTask not found: " + id);
        }
        return opt.get();
    }
    @Transactional
    public PriorityTask create(PriorityTask entity) {
        entity.setCreatedAt(java.time.LocalDateTime.now());
        return repository.save(entity);
    }
    @Transactional
    public PriorityTask update(java.util.UUID id, PriorityTask entity) {
        PriorityTask existing = findById(id);
        existing.setTitle(entity.getTitle());
        existing.setDescription(entity.getDescription());
        existing.setCompleted(entity.isCompleted());
        existing.setDueDate(entity.getDueDate());
        existing.setUpdatedAt(java.time.LocalDateTime.now());
        return repository.save(existing);
    }
    @Transactional
    public PriorityTask complete(java.util.UUID id) {
        PriorityTask existing = findById(id);
        existing.setCompleted(true);
        existing.setUpdatedAt(java.time.LocalDateTime.now());
        return repository.save(existing);
    }
    @Transactional
    public PriorityTask reopen(java.util.UUID id) {
        PriorityTask existing = findById(id);
        existing.setCompleted(false);
        existing.setUpdatedAt(java.time.LocalDateTime.now());
        return repository.save(existing);
    }
    @Transactional
    public void deleteById(java.util.UUID id) {
        repository.deleteById(id);
    }
    public java.util.List<PriorityTask> findByCompleted(boolean completed) {
        return repository.findByCompleted(completed);
    }
    public java.util.List<PriorityTask> findByDueDateBefore(java.time.LocalDate date) {
        return repository.findByDueDateBefore(date);
    }
    public java.util.List<PriorityTask> findByPriority(String priority) {
        return repository.findByPriority(priority);
    }
}