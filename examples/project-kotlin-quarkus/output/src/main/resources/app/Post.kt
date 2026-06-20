package com.example.app

import jakarta.persistence.Entity
import jakarta.persistence.Table
import jakarta.persistence.Id
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Column
import io.quarkus.hibernate.orm.panache.PanacheEntity
@Entity
@Table(name = "Post")
open class Post : PanacheEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
    @Column(nullable = false)
    var name: String = ""
    @Column(length = 500)
    var description: String? = null
    @Column(name = "created_at")
    var createdAt: java.time.LocalDateTime? = null
    @Column(name = "updated_at")
    var updatedAt: java.time.LocalDateTime? = null
}