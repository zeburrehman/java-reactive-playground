package org.acme.controllers;

import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.acme.entities.TodoEntity;
import org.acme.services.TodoService;

import java.util.List;

@Path("/api/todos")
public class TodoResource {
    @Inject
    TodoService todoService;
    @GET
    public Uni<List<TodoEntity>> getTodos() {
        return this.todoService.getAll();
    }

    @GET
    @Path("/{id}")
    public Uni<TodoEntity> getById(@PathParam("id") long id) {
        return
                this.todoService.getById(id);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Uni<Response> create(TodoEntity todoEntity) {
        return this.todoService.save(todoEntity)
                .replaceWith(Response.ok(todoEntity).status(Response.Status.CREATED).build());
    }

    @PUT
    @Path("/{id}")
    public Uni<Response> update(@PathParam("id") long id, TodoEntity updateEntity) {
        return this.todoService.update(id, updateEntity)
                .onItem().transform(entity -> Response.ok(entity).build())
                .onFailure().recoverWithUni(() -> Uni.createFrom().item(Response.status(Response.Status.NOT_FOUND).build()));
    }

    @DELETE
    @Path("/{id}")
    public Uni<Response> delete(@PathParam("id") long id) {
        return this.todoService.delete(id)
                .map(deleted -> deleted? Response.noContent().build(): Response.ok().status(Response.Status.NOT_FOUND).build());
    }
}
