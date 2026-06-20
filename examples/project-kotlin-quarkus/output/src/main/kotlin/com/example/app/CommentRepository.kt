package com.example.app

import jakarta.enterprise.context.ApplicationScoped
import io.quarkus.hibernate.orm.panache.PanacheRepository
@ApplicationScoped
open class CommentRepository : PanacheRepository<Comment> {
}