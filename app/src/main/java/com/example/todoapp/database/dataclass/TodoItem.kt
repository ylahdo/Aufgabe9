package com.example.studentcard.database.dataclass

/**
 * Eine Datenklasse, die ein Todo-Element repräsentiert.
 * Diese Klasse speichert die grundlegenden Eigenschaften eines Todos wie Titel, Beschreibung,
 * Priorität, Deadline und den Status, ob das Todo erledigt ist oder nicht.
 *
 * @param id Die eindeutige ID des Todo-Elements (Standardwert: 0).
 * @param title Der Titel des Todo-Elements.
 * @param description Eine optionale Beschreibung des Todo-Elements (Standardwert: leerer String).
 * @param priority Die Priorität des Todo-Elements (Standardwert: "Medium").
 * @param deadline Das Fälligkeitsdatum des Todo-Elements (Standardwert: leerer String).
 * @param isCompleted Der Status, ob das Todo-Element als erledigt markiert ist (Standardwert: false).
 */
data class TodoItem(
    val id: Long = 0, // Die eindeutige ID des Todo-Elements
    val title: String, // Der Titel des Todo-Elements
    val description: String = "", // Optionale Beschreibung des Todo-Elements
    val priority: String = "Medium", // Priorität des Todo-Elements (Standard: "Medium")
    val deadline: String = "", // Optionales Fälligkeitsdatum des Todo-Elements
    val isCompleted: Boolean = false // Status des Todos (ob erledigt oder nicht)
)
