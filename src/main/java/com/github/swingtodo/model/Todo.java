package com.github.swingtodo.model;

import java.time.LocalDate;
import java.util.UUID;

public class Todo {

    private final String id;
    private String name;
    private LocalDate dueDate;
    private boolean completed;

    public Todo(String name, LocalDate dueDate) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.dueDate = dueDate;
        this.completed = false;
    }

    public String getId() { return id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }

    public String getStatus() {
        return completed ? "Done" : "Pending";
    }
}
