package com.github.swingtodo.view;

import com.github.swingtodo.model.Todo;
import com.github.swingtodo.panel.FormPanel;
import com.github.swingtodo.panel.TablePanel;
import com.github.swingtodo.service.TodoService;


import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableModelEvent;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class MainView extends JFrame {

    private final FormPanel formPanel;
    private final TablePanel tablePanel;
    

    public MainView() {
        super("Swing ToDo");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 400);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        formPanel = new FormPanel();
        tablePanel = new TablePanel();

        add(formPanel, BorderLayout.NORTH);
        add(tablePanel, BorderLayout.CENTER);

        wireListeners();
        refreshTable();
    }

    private void wireListeners() {
        // 1. Add / Save button
        formPanel.getAddButton().addActionListener(e -> {
            String name = formPanel.getNameText();
            if (name.isEmpty()) return;
            LocalDate date = formPanel.getSelectedDate();

            String editingId = formPanel.getEditingTodoId();
            if (editingId == null) {
                tablePanel.getService().add(new Todo(name, date));
            } else {
                Todo proxy = new Todo(name, date);
                // Todo constructor generates a new ID; we need the original
                // Use reflection-free approach: update via service which matches by ID
                // Build a temporary holder that carries the editing ID
                tablePanel.getService().getAllTodos().stream()
                        .filter(t -> t.getId().equals(editingId))
                        .findFirst()
                        .ifPresent(t -> {
                            t.setName(name);
                            t.setDueDate(date);
                            tablePanel.getService().update(t);
                        });
            }
            formPanel.clear();
            refreshTable();
        });

        // 2. Table row selection → populate form
        tablePanel.getTable().getSelectionModel().addListSelectionListener(
                (ListSelectionEvent e) -> {
                    if (e.getValueIsAdjusting() || tablePanel.isRefreshing()) return;
                    Todo selected = tablePanel.getSelectedTodo();
                    formPanel.setTodo(selected);
                });

        // 3. Table model change on col 0 → toggle complete
        tablePanel.getTable().getModel().addTableModelListener(
                (TableModelEvent e) -> {
                    if (tablePanel.isRefreshing()) return;
                    if (e.getType() == TableModelEvent.UPDATE && e.getColumn() == 0) {
                        Todo todo = tablePanel.getTodoAtRow(e.getFirstRow());
                        if (todo != null) {
                            tablePanel.getService().toggleComplete(todo.getId());
                            refreshTable();
                        }
                    }
                });

        // 4. Hide-completed checkbox
        formPanel.getHideCompletedCheckBox().addActionListener(e -> {
            tablePanel.setHideCompleted(formPanel.isHideCompletedSelected()); 
            refreshTable(); 
        });
    }

    private void refreshTable() {
        tablePanel.refreshTable();
    }
}
