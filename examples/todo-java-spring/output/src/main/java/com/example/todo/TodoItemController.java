package com.example.todo;

import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.http.HttpStatus
import jakarta.validation.Valid
@RestController
@RequestMapping("/api/todos")
public class TodoItemController {
    private final TodoItemService service;
    public TodoItemController(TodoItemService service) {
        this.service = service;
    }
    @GetMapping
    public java.util.List<TodoItem> findAll() {
        return service.findAll();
    }
    @GetMapping("/{id}")
    public TodoItem findById(@PathVariable java.util.UUID id) {
        return service.findById(id);
    }
    @PostMapping
    @ResponseStatus(code = org.springframework.http.HttpStatus.CREATED)
    public TodoItem create(@RequestBody @Valid TodoItem entity) {
        return service.create(entity);
    }
    @PutMapping("/{id}")
    public TodoItem update(@PathVariable java.util.UUID id, @RequestBody @Valid TodoItem entity) {
        return service.update(id, entity);
    }
    @DeleteMapping("/{id}")
    @ResponseStatus(code = org.springframework.http.HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable java.util.UUID id) {
        service.deleteById(id);
    }
}