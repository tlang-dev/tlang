package com.example.app

@Entity
@Table(name = "Post")
open class Post : PanacheEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
    @Column(name = "${field.name}")
    var ${field.name}: String? = null
    @Column(name = "${field.name}")
    var ${field.name}: String? = null
    @Column(name = "${field.name}")
    var ${field.name}: String? = null
    @Column(name = "created_at")
    var createdAt: java.time.LocalDateTime? = null
    @Column(name = "updated_at")
    var updatedAt: java.time.LocalDateTime? = null
}