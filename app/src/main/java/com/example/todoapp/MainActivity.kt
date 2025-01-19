package com.example.todoapp


import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.ui.unit.dp
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
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.TextAlign
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
                val repository = TodoRepository(LocalContext.current)

                NavHost(
                    navController = navController,
                    startDestination = "openTodos"
                ) {
                    composable("openTodos") {
                        OpenTodosScreen(
                            repository = repository,
                            onNavigateToCompleted = { navController.navigate("completedTodos") }
                        )
                    }
                    composable("completedTodos") {
                        CompletedTodosScreen(
                            repository = repository,
                            onNavigateBack = { navController.navigateUp() }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Composable-Funktion für den Bildschirm, der offene Aufgaben anzeigt.
 * Ermöglicht es dem Benutzer, neue Aufgaben zu erstellen, bestehende Aufgaben zu bearbeiten oder zu löschen.
 * Zeigt eine Liste aller offenen Aufgaben aus dem Repository und bietet die Möglichkeit,
 * zu den erledigten Aufgaben zu navigieren.
 *
 * @param repository Das Repository, das die To-Do-Daten verwaltet und abruft.
 * @param onNavigateToCompleted Eine Lambda-Funktion, die aufgerufen wird, um zu den erledigten Aufgaben zu navigieren.
 * @param modifier Ein optionaler Modifier, der auf die Composable angewendet wird.
 */
@Composable
fun OpenTodosScreen(
    repository: TodoRepository,
    onNavigateToCompleted: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Zustände für Dialog und Todo-Details
    var showDialog by remember { mutableStateOf(false) }
    var todoToEdit by remember { mutableStateOf<TodoItem?>(null) }
    var todoText by remember { mutableStateOf("") }
    var todoDescription by remember { mutableStateOf("") }
    var todoPriority by remember { mutableStateOf("Medium") }
    var todoDeadline by remember { mutableStateOf("") }

    // Holen der offenen Todos
    val todos = remember { mutableStateOf(repository.getAllTodos()) }
    val openTodos = todos.value.filterNot { it.isCompleted }

    val context = LocalContext.current

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    // Setzt die Werte für das Hinzufügen einer neuen Aufgabe zurück
                    todoToEdit = null
                    todoText = ""
                    todoDescription = ""
                    todoPriority = "Medium"
                    todoDeadline = ""
                    showDialog = true
                }
            ) {
                Text("+") // Symbol für das Hinzufügen einer neuen Aufgabe
            }
        }
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()  // Die Composable füllt den gesamten Bildschirm
                .padding(padding)  // Padding für die gesamte Column
        ) {
            // Header mit Titel und Button für erledigte Aufgaben
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Offene Aufgaben",
                    style = MaterialTheme.typography.titleLarge  // Titel im großen Stil
                )
                Button(onClick = onNavigateToCompleted) {
                    Text("Erledigte Aufgaben")  // Button, um zu erledigten Aufgaben zu navigieren
                }
            }

            // Wenn keine offenen Todos vorhanden sind
            if (openTodos.isEmpty()) {
                Text(
                    text = "Keine ToDos",  // Nachricht, wenn keine offenen Aufgaben vorhanden sind
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .fillMaxWidth()  // Der Text füllt die gesamte Breite
                        .padding(16.dp),  // Padding rund um den Text
                    textAlign = TextAlign.Center  // Text zentrieren
                )
            } else {
                // Liste der offenen Todos
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(openTodos) { todo ->
                        // Anzeige der einzelnen Aufgabenkarte
                        TodoItemCard(
                            item = todo,
                            onItemClick = { repository.updateTodo(it.copy(isCompleted = !it.isCompleted))
                                todos.value = repository.getAllTodos() },  // Aufgabe als erledigt markieren
                            onEditClick = {
                                // Aufgabe zum Bearbeiten laden
                                todoToEdit = it
                                todoText = it.title
                                todoDescription = it.description
                                todoPriority = it.priority
                                todoDeadline = it.deadline
                                showDialog = true
                            },
                            onDeleteClick = {
                                repository.deleteTodo(it)
                                todos.value = repository.getAllTodos()  // Aufgabe löschen und Liste aktualisieren
                            }
                        )
                    }
                }
            }
        }

        // Dialog zur Eingabe von Aufgabeninformationen
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text(if (todoToEdit == null) "Neue Aufgabe" else "Aufgabe bearbeiten") },
                text = {
                    Column {
                        // Eingabefelder für Titel, Beschreibung, Priorität und Deadline
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
                            // Validierung und Speicherung der neuen oder bearbeiteten Aufgabe
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
                                todos.value = repository.getAllTodos() // Liste aktualisieren
                            } else {
                                Toast.makeText(context, "Titel darf nicht leer sein", Toast.LENGTH_SHORT).show()
                            }
                        }
                    ) {
                        Text("Speichern")  // Speichern-Button
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("Abbrechen")  // Abbrechen-Button
                    }
                }
            )
        }
    }
}

/**
 * Composable-Funktion für den Bildschirm, der die erledigten Aufgaben anzeigt.
 * Zeigt eine Liste von erledigten Aufgaben an, die aus dem Repository abgerufen werden.
 * Ermöglicht das Zurücknavigieren zum vorherigen Bildschirm und bietet Aktionen
 * zum Markieren von Aufgaben als unerledigt oder Löschen von Aufgaben.
 *
 * @param repository Das Repository, das die To-Do-Daten verwaltet und abruft.
 * @param onNavigateBack Eine Lambda-Funktion, die beim Zurücknavigieren aufgerufen wird.
 * @param modifier Ein optionaler Modifier, der auf die Composable angewendet wird.
 */
@Composable
fun CompletedTodosScreen(
    repository: TodoRepository,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Holen der erledigten Todos
    val todos = remember { mutableStateOf(repository.getAllTodos()) }
    val completedTodos = todos.value.filter { it.isCompleted }

    Column(
        modifier = modifier
            .fillMaxSize()  // Die Composable füllt den gesamten Bildschirm
            .padding(16.dp)  // Abstand zu den Rändern
    ) {
        Spacer(modifier = Modifier.height(25.dp))  // Abstand nach oben
        Row(
            modifier = Modifier
                .fillMaxWidth()  // Die Zeile nimmt die gesamte Breite ein
                .padding(bottom = 16.dp),  // Abstand unten
            horizontalArrangement = Arrangement.SpaceBetween,  // Elemente links und rechts anordnen
            verticalAlignment = Alignment.CenterVertically  // Vertikale Zentrierung
        ) {
            // Zurück-Button, um auf den vorherigen Bildschirm zu navigieren
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Zurück"  // Beschreibung des Icons
                )
            }
            // Titel des Bildschirms
            Text(
                text = "Erledigte Aufgaben",
                style = MaterialTheme.typography.titleLarge  // Stil für den Titel
            )
        }

        // Wenn keine erledigten Aufgaben vorhanden sind
        if (completedTodos.isEmpty()) {
            Text(
                text = "Keine erledigten Aufgaben",  // Nachricht, wenn keine erledigten Aufgaben vorhanden sind
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,  // Textfarbe
                modifier = Modifier
                    .fillMaxWidth()  // Der Text füllt die gesamte Breite
                    .padding(16.dp),  // Abstand zum Rand
                textAlign = TextAlign.Center  // Text zentrieren
            )
        } else {
            // Liste der erledigten Aufgaben
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)  // Vertikaler Abstand zwischen den Listenelementen
            ) {
                items(completedTodos) { todo ->
                    // Anzeige der einzelnen Aufgabe als Karte
                    TodoItemCard(
                        item = todo,
                        onItemClick = { repository.updateTodo(it.copy(isCompleted = !it.isCompleted))
                            todos.value = repository.getAllTodos() },  // Beim Klick wird die Aufgabe als unerledigt markiert
                        onEditClick = { },
                        onDeleteClick = { repository.deleteTodo(it)
                            todos.value = repository.getAllTodos() }  // Beim Löschen wird die Aufgabe entfernt
                    )
                }
            }
        }
    }
}



/**
 * Composable-Funktion, die eine To-Do-Aufgabe als Karte darstellt.
 * Zeigt den Titel der Aufgabe, eine Checkbox für den Status und zusätzliche Details,
 * wenn die Karte ausgeklappt wird. Ermöglicht das Bearbeiten und Löschen der Aufgabe.
 *
 * @param item Das To-Do-Element, das in der Karte angezeigt wird.
 * @param onItemClick Eine Lambda-Funktion, die aufgerufen wird, wenn die Checkbox angeklickt wird,
 * um den Status der Aufgabe zu ändern (z.B. erledigt/nicht erledigt).
 * @param onDeleteClick Eine Lambda-Funktion, die aufgerufen wird, wenn das Löschen-Symbol angeklickt wird,
 * um die Aufgabe zu löschen.
 * @param onEditClick Eine Lambda-Funktion, die aufgerufen wird, wenn die Aufgabe lang gedrückt wird,
 * um sie zu bearbeiten.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TodoItemCard(
    item: TodoItem,
    onItemClick: (TodoItem) -> Unit,
    onDeleteClick: (TodoItem) -> Unit,
    onEditClick: (TodoItem) -> Unit
) {
    // Zustandsvariable für das Erweitern/Reduzieren der Anzeige von Details
    var expanded by remember { mutableStateOf(false) }

    // Karte für das To-Do-Element
    Card(
        modifier = Modifier
            .fillMaxWidth()  // Karte nimmt die volle Breite ein
            .padding(8.dp)   // Abstand zur Umrandung der Karte
            .combinedClickable(
                onClick = { expanded = !expanded },  // Umklappen der Karte bei einfachem Klick
                onLongClick = { onEditClick(item) }  // Bearbeitungsmodus bei langem Klick
            ),
        elevation = CardDefaults.cardElevation(4.dp)  // Kartenelevation für Schatteneffekt
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // Zeile für den Titel und das Status-Checkbox
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,  // Elemente links und rechts anordnen
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)  // Abstand zwischen Checkbox und Titel
                ) {
                    // Checkbox für den erledigt-Status
                    Checkbox(
                        checked = item.isCompleted,
                        onCheckedChange = { onItemClick(item) }  // Status ändern bei Klick
                    )
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.titleMedium  // Stil für den Titel
                    )
                }
                // Pfeilsymbol für das Auf- oder Zuklappen der Details
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.KeyboardArrowUp
                        else Icons.Default.KeyboardArrowDown,
                        contentDescription = if (expanded) "Einklappen" else "Ausklappen"
                    )
                }
            }

            // Erweiterter Bereich, der angezeigt wird, wenn die Karte aufgeklappt ist
            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)  // Abstand zum oberen Rand
                ) {
                    // Beschreibung der Aufgabe, wenn vorhanden
                    if (item.description.isNotBlank()) {
                        Text(
                            text = "Beschreibung:",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary  // Primärfarbe für den Titel
                        )
                        Text(
                            text = item.description,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)  // Abstand zur linken Seite und unten
                        )
                    }

                    // Zeigt die Priorität der Aufgabe
                    Row(
                        modifier = Modifier.padding(vertical = 4.dp)  // Vertikaler Abstand
                    ) {
                        Text(
                            text = "Priorität: ",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary  // Primärfarbe für das Label
                        )
                        Text(
                            text = item.priority,
                            style = MaterialTheme.typography.bodyMedium  // Stil für den Text
                        )
                    }

                    // Zeigt die Deadline der Aufgabe, wenn vorhanden
                    if (item.deadline.isNotBlank()) {
                        Row(
                            modifier = Modifier.padding(vertical = 4.dp)  // Vertikaler Abstand
                        ) {
                            Text(
                                text = "Deadline: ",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary  // Primärfarbe für das Label
                            )
                            Text(
                                text = item.deadline,
                                style = MaterialTheme.typography.bodyMedium  // Stil für den Text
                            )
                        }
                    }

                    // Aktionstasten für Bearbeiten und Löschen
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),  // Abstand zum oberen Rand
                        horizontalArrangement = Arrangement.End  // Anordnung der Symbole am rechten Rand
                    ) {
                        // Bearbeiten-Symbol
                        IconButton(onClick = { onEditClick(item) }) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Bearbeiten",  // Beschreibung des Symbols
                                tint = MaterialTheme.colorScheme.primary  // Primärfarbe für das Symbol
                            )
                        }
                        // Löschen-Symbol
                        IconButton(onClick = { onDeleteClick(item) }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Löschen",  // Beschreibung des Symbols
                                tint = Color.Red  // Rote Farbe für das Löschen-Symbol
                            )
                        }
                    }

                    // Hinweistext für Benutzer, dass langes Drücken zum Bearbeiten führt
                    Text(
                        text = "Lang drücken zum Bearbeiten",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,  // Farbton für den Text
                        modifier = Modifier.align(Alignment.End)  // Text am rechten Rand ausrichten
                    )
                }
            }
        }
    }
}


