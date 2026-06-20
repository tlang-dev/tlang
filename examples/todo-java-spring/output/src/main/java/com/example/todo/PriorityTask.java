package com.example.todo;

import jakarta.persistence.Entity
import jakarta.persistence.Table
import jakarta.persistence.Id
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Column
@Entity
@Table(name = "PriorityTask")
public class PriorityTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private java.util.UUID id;
    @Column(nullable = false)
    private String title;
    @Column(length = 1000)
    private String description;
    @Column(nullable = false)
    private boolean completed;
    @Column(name = "due_date")
    private java.time.LocalDate dueDate;
    @Column(name = "created_at", nullable = false, updatable = false)
    private java.time.LocalDateTime createdAt;
    @Column(name = "updated_at")
    private java.time.LocalDateTime updatedAt;
    public java.util.UUID getId() {
        return id;
    }
    public void setId(java.util.UUID id) {
        this.id = id;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public boolean isCompleted() {
        return completed;
    }
    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
    public java.time.LocalDate getDueDate() {
        return dueDate;
    }
    public void setDueDate(java.time.LocalDate dueDate) {
        this.dueDate = dueDate;
    }
    public java.time.LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(java.time.LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public java.time.LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    public void setUpdatedAt(java.time.LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    @Column(length = 10, nullable = false)
    private String priority;
    public String getPriority() {
        return priority;
    }
}