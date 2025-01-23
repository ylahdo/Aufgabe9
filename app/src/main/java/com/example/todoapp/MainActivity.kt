package com.example.todoapp


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.platform.LocalContext
import com.example.todoapp.ui.theme.ToDoAppTheme
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
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
