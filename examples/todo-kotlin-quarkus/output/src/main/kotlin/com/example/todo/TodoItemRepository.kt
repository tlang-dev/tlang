package com.example.todo

import jakarta.enterprise.context.ApplicationScoped
import io.quarkus.hibernate.orm.panache.PanacheRepository
@ApplicationScoped
open class TodoItemRepository : PanacheRepository<TodoItem> {
}