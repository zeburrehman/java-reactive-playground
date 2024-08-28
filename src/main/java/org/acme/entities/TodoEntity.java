package org.acme.entities;

import io.quarkus.hibernate.reactive.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "todos")
public class TodoEntity extends PanacheEntity {
    @Column
    public String title;
    @Column
    public String description;
}
