package com.example.todoapp

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.example.todoapp.ui.theme.ToDoAppTheme
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.studentcard.database.dataclass.TodoItem
import com.example.todoapp.database.controller.TodoRepository


/**
 * MainActivity is the entry point of the app, it sets up the content and theme for the app.
 * It contains a ToDo list where the user can add, delete, and toggle the completion status of tasks.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ToDoAppTheme {
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = "openTodos"
                ) {
                    composable("openTodos") {
                        OpenTodosScreen(
                            viewModel = viewModel(factory = TodoViewModelFactory(LocalContext.current)),
                            onNavigateToCompleted = { navController.navigate("completedTodos") }
                        )
                    }
                    composable("completedTodos") {
                        CompletedTodosScreen(
                            viewModel = viewModel(factory = TodoViewModelFactory(LocalContext.current)),
                            onNavigateBack = { navController.navigateUp() }
                        )
                    }
                }
            }
        }
    }
}


class TodoViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TodoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TodoViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
/**
 * The composable function `TodolistScreen` is the main UI for the ToDo list app.
 * It displays a list of tasks and a floating action button to add new tasks.
 * It also includes logic to show a dialog when the button is clicked to enter a new task.
 *
 * @param modifier: Modifier to customize the layout of the screen.
 * @param viewModel: The ViewModel that manages the state of the ToDo list.
 */
@Composable
fun OpenTodosScreen(
    viewModel: TodoViewModel,
    onNavigateToCompleted: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }
    var todoToEdit by remember { mutableStateOf<TodoItem?>(null) }
    var todoText by remember { mutableStateOf("") }
    var todoDescription by remember { mutableStateOf("") }
    var todoPriority by remember { mutableStateOf("Medium") }
    var todoDeadline by remember { mutableStateOf("") }

    val todos by viewModel.todos.observeAsState(emptyList())
    val openTodos = todos.filterNot { it.isCompleted }

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

            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(openTodos) { todo ->
                    TodoItemCard(
                        item = todo,
                        onItemClick = { viewModel.toggleCompletion(it) },
                        onEditClick = {
                            todoToEdit = it
                            todoText = it.title
                            todoDescription = it.description
                            todoPriority = it.priority
                            todoDeadline = it.deadline
                            showDialog = true
                        },
                        onDeleteClick = { viewModel.deleteTodo(it) }
                    )
                }
            }
        }

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
                        TextField(
                            value = todoPriority,
                            onValueChange = { todoPriority = it },
                            label = { Text("Priorität (Low/Medium/High)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        TextField(
                            value = todoDeadline,
                            onValueChange = { todoDeadline = it },
                            label = { Text("Deadline") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            if (todoText.isNotBlank()) {
                                if (todoToEdit == null) {
                                    viewModel.addTodo(
                                        TodoItem(
                                            title = todoText,
                                            description = todoDescription,
                                            priority = todoPriority,
                                            deadline = todoDeadline
                                        )
                                    )
                                } else {
                                    viewModel.updateTodo(
                                        todoToEdit!!.copy(
                                            title = todoText,
                                            description = todoDescription,
                                            priority = todoPriority,
                                            deadline = todoDeadline
                                        )
                                    )
                                }
                                showDialog = false
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
    }
}

@Composable
fun CompletedTodosScreen(
    viewModel: TodoViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val todos by viewModel.todos.observeAsState(emptyList())
    val completedTodos = todos.filter { it.isCompleted }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Zurück"

                )
            }
            Text(
                text = "Erledigte Aufgaben",
                style = MaterialTheme.typography.titleLarge
            )
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(completedTodos) { todo ->
                TodoItemCard(
                    item = todo,
                    onItemClick = { viewModel.toggleCompletion(it) },
                    onEditClick = { /* Keine Bearbeitung für erledigte Todos */ },
                    onDeleteClick = { viewModel.deleteTodo(it) }
                )
            }
        }
    }
}

/**
 * Composable to display each ToDo item in a row with a checkbox and a delete button.
 *
 * @param item: The ToDo item to be displayed.
 * @param onItemClick: A lambda function triggered when the checkbox is clicked to toggle completion status.
 * @param onDeleteClick: A lambda function triggered when the delete icon is clicked to remove the task.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TodoItemCard(
    item: TodoItem,
    onItemClick: (TodoItem) -> Unit,
    onDeleteClick: (TodoItem) -> Unit,
    onEditClick: (TodoItem) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .combinedClickable(
                onClick = { expanded = !expanded },
                onLongClick = { onEditClick(item) }
            ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header - Always visible
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Checkbox(
                        checked = item.isCompleted,
                        onCheckedChange = { onItemClick(item) }
                    )
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.KeyboardArrowUp
                        else Icons.Default.KeyboardArrowDown,
                        contentDescription = if (expanded) "Einklappen" else "Ausklappen"
                    )
                }
            }

            // Expandable content
            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    // Description
                    if (item.description.isNotBlank()) {
                        Text(
                            text = "Beschreibung:",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = item.description,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
                        )
                    }

                    // Priority
                    Row(
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        Text(
                            text = "Priorität: ",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = item.priority,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    // Deadline
                    if (item.deadline.isNotBlank()) {
                        Row(
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            Text(
                                text = "Deadline: ",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = item.deadline,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }

                    // Action buttons
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        IconButton(onClick = { onEditClick(item) }) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Bearbeiten",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        IconButton(onClick = { onDeleteClick(item) }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Löschen",
                                tint = Color.Red
                            )
                        }
                    }

                    // Hint text
                    Text(
                        text = "Lang drücken zum Bearbeiten",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.align(Alignment.End)
                    )
                }
            }
        }
    }
}


class TodoViewModel(context: Context) : ViewModel() {
    private val repository = TodoRepository(context)

    // LiveData, um die ToDo-Liste für die UI zu beobachten
    private val _todos = MutableLiveData<List<TodoItem>>()
    val todos: LiveData<List<TodoItem>> get() = _todos

    init {
        loadTodos() // Beim Initialisieren des ViewModels ToDos laden
    }

    // Lädt alle ToDos aus der Datenbank
    private fun loadTodos() {
        _todos.value = repository.getAllTodos().toList()
    }

    // Fügt ein neues ToDo hinzu
    fun addTodo(todo: TodoItem) {

        repository.insertTodo(todo) // Hinzufügen zur Datenbank
        loadTodos() // Aktualisieren der Liste
    }

    // Aktualisiert ein bestehendes ToDo
    fun updateTodo(todo: TodoItem) {
        repository.updateTodo(todo) // Aktualisieren in der Datenbank
        loadTodos() // Aktualisieren der Liste
    }

    // Löscht ein ToDo
    fun deleteTodo(todo: TodoItem) {
        repository.deleteTodo(todo) // Löschen aus der Datenbank
        loadTodos() // Aktualisieren der Liste
    }

    // Wechselt den Status eines ToDo-Elements (erledigt/nicht erledigt)
    fun toggleCompletion(todo: TodoItem) {
        val updatedTodo = todo.copy(isCompleted = !todo.isCompleted)
        repository.updateTodo(updatedTodo)
        loadTodos()
    }
}

