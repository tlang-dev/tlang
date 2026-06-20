package com.example.todo

import jakarta.persistence.Entity
import jakarta.persistence.Table
import jakarta.persistence.Id
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Column
import io.quarkus.hibernate.orm.panache.PanacheEntity
@Entity
@Table(name = "PriorityTask")
open class PriorityTask : PanacheEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
    @Column(nullable = false)
    var title: String = ""
    @Column(length = 1000)
    var description: String? = null
    @Column(nullable = false)
    var completed: Boolean = false
    @Column(name = "due_date")
    var dueDate: java.time.LocalDate? = null
    @Column(name = "created_at")
    var createdAt: java.time.LocalDateTime? = null
    @Column(name = "updated_at")
    var updatedAt: java.time.LocalDateTime? = null
    @Column(length = 10, nullable = false)
    var priority: String = "MEDIUM"
}