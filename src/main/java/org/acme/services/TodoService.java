package org.acme.services;

import io.quarkus.hibernate.reactive.panache.Panache;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.NotFoundException;
import org.acme.entities.TodoEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@ApplicationScoped
public class TodoService {
    private Logger logger = LoggerFactory.getLogger(TodoService.class);
    public Uni<List<TodoEntity>> getAll() {
        return TodoEntity.listAll();
    }

    public Uni<TodoEntity> getById(long id) {
        return TodoEntity.findById(id);
    }

    public Uni<TodoEntity> save(TodoEntity entity) {
        return Panache.withTransaction(entity::persist);
    }

    public Uni<TodoEntity> update(long id, TodoEntity updateEntity) {
        return Panache.withTransaction(() -> TodoEntity.<TodoEntity>findById(id)
                .onItem()
                    .ifNotNull().invoke(entity -> {
                        entity.title = updateEntity.title;
                        entity.description = updateEntity.description;
                    }))
                .onItem()
                    .ifNull().failWith(() -> {
                        var message = "Todo with id " + id + " not found.";
                        logger.error(message);
                        return new NotFoundException(message);
                    });
    }

    public Uni<Boolean> delete(long id) {
        return Panache.withTransaction(() -> TodoEntity.deleteById(id));
    }
}
