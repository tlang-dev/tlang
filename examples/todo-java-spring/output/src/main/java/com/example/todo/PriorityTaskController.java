package com.example.todo;

import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.http.HttpStatus
import jakarta.validation.Valid
@RestController
@RequestMapping("/api/priority-tasks")
public class PriorityTaskController {
    private final PriorityTaskService service;
    public PriorityTaskController(PriorityTaskService service) {
        this.service = service;
    }
    @GetMapping
    public java.util.List<PriorityTask> findAll() {
        return service.findAll();
    }
    @GetMapping("/{id}")
    public PriorityTask findById(@PathVariable java.util.UUID id) {
        return service.findById(id);
    }
    @PostMapping
    @ResponseStatus(code = org.springframework.http.HttpStatus.CREATED)
    public PriorityTask create(@RequestBody @Valid PriorityTask entity) {
        return service.create(entity);
    }
    @PutMapping("/{id}")
    public PriorityTask update(@PathVariable java.util.UUID id, @RequestBody @Valid PriorityTask entity) {
        return service.update(id, entity);
    }
    @DeleteMapping("/{id}")
    @ResponseStatus(code = org.springframework.http.HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable java.util.UUID id) {
        service.deleteById(id);
    }
    @PatchMapping("/{id}/complete")
    public PriorityTask complete(@PathVariable java.util.UUID id) {
        return service.complete(id);
    }
    @PatchMapping("/{id}/reopen")
    public PriorityTask reopen(@PathVariable java.util.UUID id) {
        return service.reopen(id);
    }
    @GetMapping("/completed/{status}")
    public java.util.List<PriorityTask> findByCompleted(@PathVariable boolean status) {
        return service.findByCompleted(status);
    }
    @GetMapping("/priority/{level}")
    public java.util.List<PriorityTask> findByPriority(@PathVariable String level) {
        return service.findByPriority(level);
    }
}