package com.example.todoapp.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.studentcard.database.dataclass.TodoItem


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

            // Zeigt die Priorität der Aufgabe unter dem Titel an (immer sichtbar)
            Text(
                text = "Priorität: ${item.priority}",
                style = MaterialTheme.typography.bodyMedium,  // Kleinere Schriftart für die Priorität
                color = MaterialTheme.colorScheme.primary,  // Primärfarbe für den Text
                modifier = Modifier.padding(top = 4.dp)  // Abstand oben
            )

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
                            text = "Beschreibung: ",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary  // Primärfarbe für den Titel
                        )
                        Text(
                            text = item.description,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)  // Abstand zur linken Seite und unten
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
