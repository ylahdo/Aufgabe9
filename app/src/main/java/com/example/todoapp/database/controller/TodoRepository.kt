package com.example.todoapp.database.controller

import android.content.ContentValues
import android.content.Context
import android.util.Log
import com.example.studentcard.database.DbHelper
import com.example.studentcard.database.dataclass.TodoItem


class TodoRepository(context: Context) {
    private val dbHelper = DbHelper(context)

    fun getAllTodos(): List<TodoItem> {
        val todos = mutableListOf<TodoItem>()
        val db = dbHelper.readableDatabase
        val cursor = db.query("todo", null, null, null, null, null, "id DESC")
        Log.d("TodoRepository", "Query executed: SELECT * FROM todo")

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

    fun insertTodo(todo: TodoItem) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("name", todo.title)
            put("description", todo.description)
            put("priority", todo.priority)
            put("deadline", todo.deadline)
            put("status", if (todo.isCompleted) 1 else 0)
        }

        val result = db.insert("todo", null, values)
        Log.d("TodoRepository", "Insert operation result: $result")
    }

    fun updateTodo(todo: TodoItem) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("name", todo.title)
            put("description", todo.description)
            put("priority", todo.priority)
            put("deadline", todo.deadline)
            put("status", if (todo.isCompleted) 1 else 0)
        }

        val result = db.update("todo", values, "id = ?", arrayOf(todo.id.toString()))
        Log.d("TodoRepository", "Update operation result: $result")
    }

    fun deleteTodo(todo: TodoItem) {
        val db = dbHelper.writableDatabase
        val result = db.delete("todo", "id = ?", arrayOf(todo.id.toString()))
        Log.d("TodoRepository", "Delete operation result: $result")
    }
}