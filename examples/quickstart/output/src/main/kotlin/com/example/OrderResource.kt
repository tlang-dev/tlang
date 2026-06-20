package com.example

import jakarta.enterprise.context.ApplicationScoped
import jakarta.transaction.Transactional
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.DELETE
import jakarta.ws.rs.GET
import jakarta.ws.rs.POST
import jakarta.ws.rs.PUT
import jakarta.ws.rs.Path
import jakarta.ws.rs.PathParam
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response

@Path("/api/orders")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class OrderResource {

    @GET
    fun list(): List<Order> = Order.listAll()

    @GET
    @Path("/{id}")
    fun findById(@PathParam("id") id: Long): Response =
        Order.findById(id)?.let { Response.ok(it).build() }
            ?: Response.status(404).build()

    @POST
    @Transactional
    fun create(entity: Order): Response {
        entity.persist()
        return Response.status(201).entity(entity).build()
    }

    @PUT
    @Path("/{id}")
    @Transactional
    fun update(@PathParam("id") id: Long, entity: Order): Response {
        val existing = Order.findById(id) ?: return Response.status(404).build()
        existing.name = entity.name
        existing.description = entity.description
        return Response.ok(existing).build()
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    fun delete(@PathParam("id") id: Long): Response {
        val deleted = Order.deleteById(id)
        return if (deleted) Response.noContent().build()
               else Response.status(404).build()
    }
}