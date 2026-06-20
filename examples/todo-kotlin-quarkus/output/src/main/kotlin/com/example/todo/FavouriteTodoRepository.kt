package com.example.todo

import jakarta.enterprise.context.ApplicationScoped
import io.quarkus.hibernate.orm.panache.PanacheRepository
@ApplicationScoped
open class FavouriteTodoRepository : PanacheRepository<FavouriteTodo> {
    fun findByFavourite(favourite: Boolean): java.util.List<FavouriteTodo> {
        return find("favourite", favourite).list()
    }
}