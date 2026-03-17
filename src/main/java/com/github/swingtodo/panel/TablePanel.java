package com.github.swingtodo.panel;

import com.github.swingtodo.model.Todo;
import com.github.swingtodo.service.TodoService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class TablePanel extends JPanel {

    private final TodoService service;
    private final JTable table;
    private final DefaultTableModel tableModel;
    private List<Todo> currentTodos = new ArrayList<>();
    private boolean refreshing;
    private boolean hideCompleted = false; 

    private static final String[] COLUMNS = { "Done", "Name", "Due Date", "Status" };

    public TablePanel() {
        service = new TodoService();
        setLayout(new BorderLayout());

        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 0)
                    return Boolean.class;
                return String.class;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0;
            }
        };

        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(0).setMaxWidth(60);

        add(new JScrollPane(table), BorderLayout.CENTER);

    }

    public void setTodos(List<Todo> todos) {
        currentTodos = new ArrayList<>(todos);
        tableModel.setRowCount(0);
        for (Todo t : todos) {
            tableModel.addRow(new Object[] {
                    t.isCompleted(),
                    t.getName(),
                    t.getDueDate().toString(),
                    t.getStatus()
            });
        }
    }

    public Todo getTodoAtRow(int row) {
        if (row < 0 || row >= currentTodos.size())
            return null;
        return currentTodos.get(row);
    }

    public Todo getSelectedTodo() {
        return getTodoAtRow(table.getSelectedRow());
    }

    public JTable getTable() {
        return table;
    }

    public boolean isRefreshing() {
        return refreshing;
    }

    public TodoService getService(){
        return service; 
    }

    public void setHideCompleted(boolean hideCompleted){
        this.hideCompleted = hideCompleted; 
    }

    public void refreshTable() {

        try {
            List<Todo> todos = hideCompleted
                    ? service.getIncompleteTodos()
                    : service.getAllTodos();
            setTodos(todos);
        } finally {
            refreshing = false;
        }
    }
}
