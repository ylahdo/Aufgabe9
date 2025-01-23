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
import com.example.todoapp.screen.CompletedTodosScreen
import com.example.todoapp.screen.OpenTodosScreen


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
