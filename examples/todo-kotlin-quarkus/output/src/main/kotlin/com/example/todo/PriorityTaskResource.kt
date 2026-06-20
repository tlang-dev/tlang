package com.example.todo

import jakarta.enterprise.context.RequestScoped
import jakarta.inject.Inject
import jakarta.transaction.Transactional
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.DELETE
import jakarta.ws.rs.GET
import jakarta.ws.rs.PATCH
import jakarta.ws.rs.POST
import jakarta.ws.rs.PUT
import jakarta.ws.rs.Path
import jakarta.ws.rs.PathParam
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
@Path("/api/priority-tasks")
@Produces("application/json")
@Consumes("application/json")
class PriorityTaskResource {
    @Inject
    lateinit var service: PriorityTaskService
    @GET
    fun findAll(): java.util.List<PriorityTask> {
        return service.findAll()
    }
    @GET
    @Path("/{id}")
    fun findById(@PathParam("id") id: Long): jakarta.ws.rs.core.Response {
        val entity = service.findById(id)
        if (entity == null) {
            return jakarta.ws.rs.core.Response.status(404).build()
        }
        return jakarta.ws.rs.core.Response.ok(entity).build()
    }
    @POST
    fun create(entity: PriorityTask): jakarta.ws.rs.core.Response {
        val created = service.create(entity)
        return jakarta.ws.rs.core.Response.status(201).entity(created).build()
    }
    @PUT
    @Path("/{id}")
    fun update(@PathParam("id") id: Long, entity: PriorityTask): jakarta.ws.rs.core.Response {
        val updated = service.update(id, entity)
        if (updated == null) {
            return jakarta.ws.rs.core.Response.status(404).build()
        }
        return jakarta.ws.rs.core.Response.ok(updated).build()
    }
    @DELETE
    @Path("/{id}")
    fun deleteById(@PathParam("id") id: Long): jakarta.ws.rs.core.Response {
        val deleted = service.deleteById(id)
        if (!deleted) {
            return jakarta.ws.rs.core.Response.status(404).build()
        }
        return jakarta.ws.rs.core.Response.noContent().build()
    }
    @PATCH
    @Path("/{id}/complete")
    fun complete(@PathParam("id") id: Long): jakarta.ws.rs.core.Response {
        val updated = service.complete(id)
        if (updated == null) {
            return jakarta.ws.rs.core.Response.status(404).build()
        }
        return jakarta.ws.rs.core.Response.ok(updated).build()
    }
    @PATCH
    @Path("/{id}/reopen")
    fun reopen(@PathParam("id") id: Long): jakarta.ws.rs.core.Response {
        val updated = service.reopen(id)
        if (updated == null) {
            return jakarta.ws.rs.core.Response.status(404).build()
        }
        return jakarta.ws.rs.core.Response.ok(updated).build()
    }
    @GET
    @Path("/completed/{status}")
    fun findByCompleted(@PathParam("status") status: Boolean): java.util.List<PriorityTask> {
        return service.findByCompleted(status)
    }
    @GET
    @Path("/priority/{level}")
    fun findByPriority(@PathParam("level") level: String): java.util.List<PriorityTask> {
        return service.findByPriority(level)
    }
}