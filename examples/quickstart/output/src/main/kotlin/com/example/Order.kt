package com.example

import io.quarkus.hibernate.orm.panache.kotlin.PanacheEntity
import io.quarkus.hibernate.orm.panache.kotlin.PanacheCompanionBase
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "Orders")
class Order(
    var name: String = "",
    var description: String = ""
) : PanacheEntity() {
    companion object : PanacheCompanionBase<Order, Long>
}