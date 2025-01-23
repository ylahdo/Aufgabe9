package com.example.todoapp.screen

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TimeInput
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.studentcard.database.dataclass.TodoItem
import com.example.todoapp.database.controller.TodoRepository
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Composable function to display the Open Todos screen.
 * This screen displays a list of incomplete tasks and allows adding, editing, and deleting tasks.
 * It also provides a date and time picker for setting a deadline.
 *
 * @param repository The repository to fetch, update, and delete todo items.
 * @param onNavigateToCompleted Lambda function to navigate to the completed tasks screen.
 * @param modifier Modifier to customize the layout of the screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OpenTodosScreen(
    repository: TodoRepository,
    onNavigateToCompleted: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }
    var todoToEdit by remember { mutableStateOf<TodoItem?>(null) }
    var todoText by remember { mutableStateOf("") }
    var todoDescription by remember { mutableStateOf("") }
    var todoPriority by remember { mutableStateOf("Medium") }
    var todoDeadline by remember { mutableStateOf("") }

    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    var expanded by remember { mutableStateOf(false) }
    val priorities = listOf("Low", "Medium", "High")

    val datePickerState = rememberDatePickerState()
    val timePickerState = rememberTimePickerState()

    val todos = remember { mutableStateOf(repository.getAllTodos()) }
    val openTodos = todos.value.filterNot { it.isCompleted }

    val context = LocalContext.current

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    todoToEdit = null
                    todoText = ""
                    todoDescription = ""
                    todoPriority = "Medium"
                    todoDeadline = ""
                    showDialog = true
                }
            ) {
                Text("+")
            }
        }
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Offene Aufgaben",
                    style = MaterialTheme.typography.titleLarge
                )
                Button(onClick = onNavigateToCompleted) {
                    Text("Erledigte Aufgaben")
                }
            }

            if (openTodos.isEmpty()) {
                Text(
                    text = "Keine ToDos",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    textAlign = TextAlign.Center
                )
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(openTodos) { todo ->
                        TodoItemCard(
                            item = todo,
                            onItemClick = {
                                repository.updateTodo(it.copy(isCompleted = !it.isCompleted))
                                todos.value = repository.getAllTodos()
                            },
                            onEditClick = {
                                todoToEdit = it
                                todoText = it.title
                                todoDescription = it.description
                                todoPriority = it.priority
                                todoDeadline = it.deadline
                                showDialog = true
                            },
                            onDeleteClick = {
                                repository.deleteTodo(it)
                                todos.value = repository.getAllTodos()
                            }
                        )
                    }
                }
            }
        }

        // Show dialog for creating or editing a todo item
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text(if (todoToEdit == null) "Neue Aufgabe" else "Aufgabe bearbeiten") },
                text = {
                    Column {
                        TextField(
                            value = todoText,
                            onValueChange = { todoText = it },
                            label = { Text("Titel") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        TextField(
                            value = todoDescription,
                            onValueChange = { todoDescription = it },
                            label = { Text("Beschreibung") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        ) {
                            ExposedDropdownMenuBox(
                                expanded = expanded,
                                onExpandedChange = { expanded = !expanded }
                            ) {
                                OutlinedTextField(
                                    value = todoPriority,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Priorität") },
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                                    },
                                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .menuAnchor()
                                )

                                ExposedDropdownMenu(
                                    expanded = expanded,
                                    onDismissRequest = { expanded = false }
                                ) {
                                    priorities.forEach { priority ->
                                        DropdownMenuItem(
                                            text = { Text(priority) },
                                            onClick = {
                                                todoPriority = priority
                                                expanded = false
                                            },
                                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                                        )
                                    }
                                }
                            }
                        }

                        TextField(
                            value = todoDeadline,
                            onValueChange = { todoDeadline = it },
                            label = { Text("Deadline") },
                            modifier = Modifier.fillMaxWidth(),
                            trailingIcon = {
                                IconButton(onClick = { showDatePicker = true }) {
                                    Icon(
                                        imageVector = Icons.Default.CalendarToday,
                                        contentDescription = "Datum und Zeit auswählen"
                                    )
                                }
                            }
                        )
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            if (todoText.isNotBlank()) {
                                if (todoToEdit == null) {
                                    val newTodo = TodoItem(
                                        title = todoText,
                                        description = todoDescription,
                                        priority = todoPriority,
                                        deadline = todoDeadline
                                    )
                                    repository.insertTodo(newTodo)
                                } else {
                                    val updatedTodo = todoToEdit!!.copy(
                                        title = todoText,
                                        description = todoDescription,
                                        priority = todoPriority,
                                        deadline = todoDeadline
                                    )
                                    repository.updateTodo(updatedTodo)
                                }
                                showDialog = false
                                todos.value = repository.getAllTodos()
                            } else {
                                Toast.makeText(context, "Titel darf nicht leer sein", Toast.LENGTH_SHORT).show()
                            }
                        }
                    ) {
                        Text("Speichern")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("Abbrechen")
                    }
                }
            )
        }

        // Date picker dialog
        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            datePickerState.selectedDateMillis?.let {
                                showDatePicker = false
                                showTimePicker = true
                            }
                        }
                    ) {
                        Text("Weiter")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) {
                        Text("Abbrechen")
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }

        if (showTimePicker) {
            AlertDialog(
                onDismissRequest = { showTimePicker = false },
                title = { Text("Zeit auswählen") },
                text = {
                    TimeInput(state = timePickerState)
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            datePickerState.selectedDateMillis?.let { millis ->
                                val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.GERMANY)
                                val date = Date(millis)
                                date.hours = timePickerState.hour
                                date.minutes = timePickerState.minute

                                todoDeadline = dateFormat.format(date)
                            }
                            showTimePicker = false
                        }
                    ) {
                        Text("Auswählen")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showTimePicker = false }) {
                        Text("Abbrechen")
                    }
                }
            )
        }
    }
}
