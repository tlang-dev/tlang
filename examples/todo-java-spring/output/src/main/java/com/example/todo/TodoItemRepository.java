package com.example.todo;

import org.springframework.stereotype.Repository
import org.springframework.data.jpa.repository.JpaRepository
@Repository
public interface TodoItemRepository extends JpaRepository<TodoItem, java.util.UUID> {
}