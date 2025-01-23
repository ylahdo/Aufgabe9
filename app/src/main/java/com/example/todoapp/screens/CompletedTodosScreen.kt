package com.example.todoapp.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.todoapp.database.controller.TodoRepository


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