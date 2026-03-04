package com.github.swingtodo.service;

import com.github.swingtodo.model.Todo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TodoService {

    private final List<Todo> todos = new ArrayList<>();

    public TodoService() {
        Todo t1 = new Todo("Buy groceries", LocalDate.now().plusDays(1));
        Todo t2 = new Todo("Write unit tests", LocalDate.now().plusDays(3));
        Todo t3 = new Todo("Clean the kitchen", LocalDate.now().minusDays(1));
        t3.setCompleted(true);
        Todo t4 = new Todo("Read Effective Java", LocalDate.now().plusDays(7));

        todos.add(t1);
        todos.add(t2);
        todos.add(t3);
        todos.add(t4);
    }

    public List<Todo> getAllTodos() {
        return new ArrayList<>(todos);
    }

    public List<Todo> getIncompleteTodos() {
        List<Todo> result = new ArrayList<>();
        for (Todo t : todos) {
            if (!t.isCompleted()) {
                result.add(t);
            }
        }
        return result;
    }

    public void add(Todo todo) {
        todos.add(todo);
    }

    public void update(Todo updated) {
        for (Todo t : todos) {
            if (t.getId().equals(updated.getId())) {
                t.setName(updated.getName());
                t.setDueDate(updated.getDueDate());
                return;
            }
        }
    }

    public void toggleComplete(String id) {
        for (Todo t : todos) {
            if (t.getId().equals(id)) {
                t.setCompleted(!t.isCompleted());
                return;
            }
        }
    }
}
