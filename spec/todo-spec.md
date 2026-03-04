# Swing ToDo Application — Specification

## Overview

A simple Java Swing desktop ToDo application with clean layered architecture.
No third-party dependencies — pure JDK 17.

## Architecture

```
┌──────────────────────────────────────────┐
│                  App.java                │  Entry point
│          SwingUtilities.invokeLater      │
└──────────────────┬───────────────────────┘
                   │
┌──────────────────▼───────────────────────┐
│              MainView (JFrame)           │  Mediator / Controller
│  ┌─────────────────────────────────────┐ │
│  │  FormPanel           (NORTH)        │ │
│  │  [Name] [Due Date] [Add] [☐ Hide]  │ │
│  ├─────────────────────────────────────┤ │
│  │  TablePanel          (CENTER)       │ │
│  │  ┌─────┬──────┬──────┬────────┐    │ │
│  │  │Done │ Name │ Due  │ Status │    │ │
│  │  └─────┴──────┴──────┴────────┘    │ │
│  └─────────────────────────────────────┘ │
│                    │                     │
│           TodoService (in-memory)        │
└──────────────────────────────────────────┘
```

## Package Structure

```
com.github.swingtodo
├── App.java                  Entry point
├── model/
│   └── Todo.java             Domain model
├── service/
│   └── TodoService.java      In-memory CRUD service
├── panel/
│   ├── FormPanel.java        Input form (name, date, add/save, hide toggle)
│   └── TablePanel.java       Table display
└── view/
    └── MainView.java         JFrame mediator wiring panels ↔ service
```

## Model — `Todo`

| Field       | Type         | Notes                           |
|-------------|--------------|---------------------------------|
| `id`        | `String`     | Auto-generated UUID             |
| `name`      | `String`     | Task description                |
| `dueDate`   | `LocalDate`  | Target completion date          |
| `completed` | `boolean`    | Defaults to `false`             |

- `getStatus()` → returns `"Done"` if completed, `"Pending"` otherwise.

## Service — `TodoService`

In-memory `ArrayList<Todo>`. Seeded with 3–4 example items (at least one marked completed).

| Method                          | Description                                |
|---------------------------------|--------------------------------------------|
| `List<Todo> getAllTodos()`      | Returns defensive copy of all todos        |
| `List<Todo> getIncompleteTodos()` | Returns only todos where `!completed`    |
| `void add(Todo)`               | Appends a new todo                         |
| `void update(Todo)`            | Finds by ID, replaces name and dueDate     |
| `void toggleComplete(String id)` | Flips the `completed` flag by ID         |

## FormPanel

Horizontal `FlowLayout`:

```
[Name: ________ ] [Due: 2026-03-04 ▲▼] [  Add  ] [☐ Hide completed]
```

- `JTextField` for name
- `JSpinner` with `SpinnerDateModel` + `DateEditor("yyyy-MM-dd")`
- `JButton` — label toggles between **Add** and **Save**
- `JCheckBox` — "Hide completed"

Dual mode via `editingTodoId`:
- `null` → Add mode (button says "Add")
- non-null → Edit mode (button says "Save")

Public API: `getNameText()`, `getSelectedDate()`, `setTodo(Todo)`, `clear()`,
`getEditingTodoId()`, `isHideCompletedSelected()`, `getAddButton()`, `getHideCompletedCheckBox()`

**Does NOT reference TodoService.**

## TablePanel

`JTable` inside `JScrollPane`, placed at `BorderLayout.CENTER`.

| Column   | Type      | Editable |
|----------|-----------|----------|
| Done     | Boolean   | Yes (checkbox) |
| Name     | String    | No       |
| Due Date | String    | No       |
| Status   | String    | No       |

Custom `DefaultTableModel` overrides:
- `getColumnClass(0)` → `Boolean.class`
- `isCellEditable` → only column 0

Public API: `setTodos(List<Todo>)`, `getSelectedTodo()`, `getTable()`

**Does NOT reference TodoService.**

## MainView — Listener Wiring

MainView owns `TodoService`, `FormPanel`, `TablePanel` and calls `wireListeners()`:

1. **Add / Save button** `ActionListener`:
   - Read form fields → create or update Todo via service → `formPanel.clear()` → `refreshTable()`

2. **Table row selection** `ListSelectionListener`:
   - On selection change → `formPanel.setTodo(selectedTodo)`

3. **Table model change** `TableModelListener`:
   - If column 0 changed → `service.toggleComplete(id)` → `refreshTable()`

4. **Hide-completed checkbox** `ActionListener`:
   - → `refreshTable()`

### `refreshTable()`

Single reconciliation point. Checks `formPanel.isHideCompletedSelected()` to decide
between `service.getAllTodos()` and `service.getIncompleteTodos()`. Uses a `refreshing`
boolean guard to suppress spurious listener events during table data replacement.

## Build & Run

```bash
mvn clean package
java -jar target/swing-todo-1.0-SNAPSHOT.jar
```

## Key Design Decisions

- **No third-party dependencies** — pure JDK Swing + `java.time`
- **UUID identity** — each Todo gets a UUID at construction time
- **Mediator pattern** — MainView wires panels ↔ service; panels are decoupled
- **JSpinner for dates** — avoids third-party date pickers
- **`refreshTable()` as single reconciliation point** — all mutations funnel through one method
- **`refreshing` guard flag** — prevents listener re-entrance during table refresh
