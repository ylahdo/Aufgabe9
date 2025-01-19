package com.example.todoapp.database.controller

import android.content.ContentValues
import android.content.Context
import android.util.Log
import com.example.studentcard.database.DbHelper
import com.example.studentcard.database.dataclass.TodoItem

/**
 * Repository-Klasse, die mit der Todo-Datenbank interagiert.
 * Bietet Methoden zum Abrufen, Einfügen, Aktualisieren und Löschen von Aufgaben.
 * Verwendet eine SQLite-Datenbank zur Speicherung und Verwaltung der Todo-Elemente.
 *
 * @param context Der Kontext der Anwendung, der für den Zugriff auf die Datenbank erforderlich ist.
 */
class TodoRepository(context: Context) {
    private val dbHelper = DbHelper(context)

    /**
     * Ruft alle Todo-Elemente aus der Datenbank ab.
     * Die Todo-Elemente werden in absteigender Reihenfolge der ID (neueste zuerst) geladen.
     *
     * @return Eine Liste der geladenen Todo-Elemente.
     */
    fun getAllTodos(): List<TodoItem> {
        val todos = mutableListOf<TodoItem>()
        val db = dbHelper.readableDatabase
        val cursor = db.query("todo", null, null, null, null, null, "id DESC")
        Log.d("TodoRepository", "Query executed: SELECT * FROM todo")

        // Alle Zeilen des Cursors durchgehen und die Todo-Elemente erstellen
        while (cursor.moveToNext()) {
            todos.add(
                TodoItem(
                    id = cursor.getLong(cursor.getColumnIndexOrThrow("id")),
                    title = cursor.getString(cursor.getColumnIndexOrThrow("name")),
                    description = cursor.getString(cursor.getColumnIndexOrThrow("description")),
                    priority = cursor.getString(cursor.getColumnIndexOrThrow("priority")),
                    deadline = cursor.getString(cursor.getColumnIndexOrThrow("deadline")),
                    isCompleted = cursor.getInt(cursor.getColumnIndexOrThrow("status")) == 1
                )
            )
        }
        cursor.close()

        Log.d("TodoRepository", "Loaded ${todos.size} todos from the database")
        return todos
    }

    /**
     * Fügt ein neues Todo-Element in die Datenbank ein.
     *
     * @param todo Das Todo-Element, das in die Datenbank eingefügt werden soll.
     */
    fun insertTodo(todo: TodoItem) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("name", todo.title)
            put("description", todo.description)
            put("priority", todo.priority)
            put("deadline", todo.deadline)
            put("status", if (todo.isCompleted) 1 else 0)
        }

        // Einfügen des neuen Todos in die Tabelle
        val result = db.insert("todo", null, values)
        Log.d("TodoRepository", "Insert operation result: $result")
    }

    /**
     * Aktualisiert ein bestehendes Todo-Element in der Datenbank.
     *
     * @param todo Das Todo-Element mit den neuen Daten, das aktualisiert werden soll.
     */
    fun updateTodo(todo: TodoItem) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("name", todo.title)
            put("description", todo.description)
            put("priority", todo.priority)
            put("deadline", todo.deadline)
            put("status", if (todo.isCompleted) 1 else 0)
        }

        // Aktualisierung des Todos in der Tabelle anhand der ID
        val result = db.update("todo", values, "id = ?", arrayOf(todo.id.toString()))
        Log.d("TodoRepository", "Update operation result: $result")
    }

    /**
     * Löscht ein Todo-Element aus der Datenbank.
     *
     * @param todo Das Todo-Element, das gelöscht werden soll.
     */
    fun deleteTodo(todo: TodoItem) {
        val db = dbHelper.writableDatabase
        // Löschen des Todos anhand der ID
        val result = db.delete("todo", "id = ?", arrayOf(todo.id.toString()))
        Log.d("TodoRepository", "Delete operation result: $result")
    }
}
