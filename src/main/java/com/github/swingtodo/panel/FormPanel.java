package com.github.swingtodo.panel;

import com.github.swingtodo.model.Todo;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

public class FormPanel extends JPanel {

    private final JTextField nameField;
    private final JSpinner dateSpinner;
    private final JButton addButton;
    private final JCheckBox hideCompletedCheckBox;
    private String editingTodoId;

    public FormPanel() {
        setLayout(new FlowLayout(FlowLayout.LEFT));

        add(new JLabel("Name:"));
        nameField = new JTextField(15);
        add(nameField);

        add(new JLabel("Due:"));
        SpinnerDateModel dateModel = new SpinnerDateModel(new Date(), null, null, Calendar.DAY_OF_MONTH);
        dateSpinner = new JSpinner(dateModel);
        dateSpinner.setEditor(new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd"));
        add(dateSpinner);

        addButton = new JButton("Add");
        add(addButton);

        hideCompletedCheckBox = new JCheckBox("Hide completed");
        add(hideCompletedCheckBox);
    }

    public String getNameText() {
        return nameField.getText().trim();
    }

    public LocalDate getSelectedDate() {
        Date date = (Date) dateSpinner.getValue();
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public void setTodo(Todo todo) {
        if (todo == null) {
            clear();
            return;
        }
        editingTodoId = todo.getId();
        nameField.setText(todo.getName());
        LocalDate ld = todo.getDueDate();
        Date date = Date.from(ld.atStartOfDay(ZoneId.systemDefault()).toInstant());
        dateSpinner.setValue(date);
        addButton.setText("Save");
    }

    public void clear() {
        editingTodoId = null;
        nameField.setText("");
        dateSpinner.setValue(new Date());
        addButton.setText("Add");
    }

    public String getEditingTodoId() {
        return editingTodoId;
    }

    public boolean isHideCompletedSelected() {
        return hideCompletedCheckBox.isSelected();
    }

    public JButton getAddButton() {
        return addButton;
    }

    public JCheckBox getHideCompletedCheckBox() {
        return hideCompletedCheckBox;
    }
}
