package com.example.app

import jakarta.enterprise.context.ApplicationScoped
import io.quarkus.hibernate.orm.panache.PanacheRepository
@ApplicationScoped
open class BookmarkRepository : PanacheRepository<Bookmark> {
    fun findByFavourite(favourite: Boolean): java.util.List<Bookmark> {
        return find("favourite", favourite).list()
    }
}