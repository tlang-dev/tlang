package com.example.todo;

import org.springframework.stereotype.Repository
import org.springframework.data.jpa.repository.JpaRepository
@Repository
public interface FavouriteTodoRepository extends JpaRepository<FavouriteTodo, java.util.UUID> {
    java.util.List<FavouriteTodo> findByFavouriteTrue();
    java.util.List<FavouriteTodo> findByFavourite(boolean favourite);
}