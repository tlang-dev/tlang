package com.example.todo;

import jakarta.persistence.Entity
import jakarta.persistence.Table
import jakarta.persistence.Id
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Column
@Entity
@Table(name = "FavouriteTodo")
public class FavouriteTodo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private java.util.UUID id;
    @Column(nullable = false)
    private String name;
    @Column(length = 500)
    private String description;
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
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
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
    @Column(name = "favourite", nullable = false)
    private boolean favourite;
    public boolean isFavourite() {
        return favourite;
    }
}