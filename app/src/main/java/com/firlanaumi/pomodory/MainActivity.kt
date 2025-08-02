package com.firlanaumi.pomodory

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.NavType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel

import com.firlanaumi.pomodory.ui.screens.StartScreen
import com.firlanaumi.pomodory.ui.screens.HomeScreen
import com.firlanaumi.pomodory.ui.theme.PomodoryTheme
import com.firlanaumi.pomodory.ui.screens.PomodoroTimerScreen
import com.firlanaumi.pomodory.ui.screens.AddTaskScreen
import com.firlanaumi.pomodory.ui.screens.CalendarScreen
import com.firlanaumi.pomodory.ui.screens.StatisticsScreen // Import StatisticsScreen (tetap ada)

import com.firlanaumi.pomodory.data.database.AppDatabase
import com.firlanaumi.pomodory.data.repository.TaskRepository
import com.firlanaumi.pomodory.viewmodel.HomeScreenViewModel
import com.firlanaumi.pomodory.viewmodel.HomeScreenViewModelFactory
import com.firlanaumi.pomodory.viewmodel.PomodoroTimerViewModel
import com.firlanaumi.pomodory.viewmodel.PomodoroTimerViewModelFactory
import com.firlanaumi.pomodory.viewmodel.CalendarViewModel
import com.firlanaumi.pomodory.viewmodel.CalendarViewModelFactory
// Hapus impor ini jika Anda menghapus file ViewModel/Factory-nya
// import com.firlanaumi.pomodory.viewmodel.StatisticsViewModel
// import com.firlanaumi.pomodory.viewmodel.StatisticsViewModelFactory


sealed class Screen(val route: String) {
    object Start : Screen("start_screen")
    object Home : Screen("home_screen")
    object PomodoroTimer : Screen("pomodoro_timer_screen/{taskId}") {
        fun createRoute(taskId: Int) = "pomodoro_timer_screen/$taskId"
    }
    object AddTask : Screen("add_task_screen")
    object Calendar : Screen("calendar_screen")
    object Statistics : Screen("statistics_screen") // Tetap ada di Screen sealed class
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PomodoryTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PomodoryApp()
                }
            }
        }
    }
}

@Composable
fun PomodoryApp() {
    val navController = rememberNavController()

    val context = LocalContext.current
    val database = remember { AppDatabase.getDatabase(context) }
    val repository = remember { TaskRepository(database.taskDao()) }

    val homeViewModelFactory = remember { HomeScreenViewModelFactory(repository) }
    val pomodoroViewModelFactory = remember { PomodoroTimerViewModelFactory(repository, null) }
    val calendarViewModelFactory = remember { CalendarViewModelFactory(repository) }
    // Hapus inisialisasi factory ini
    // val statisticsViewModelFactory = remember { StatisticsViewModelFactory(repository) }

    NavHost(navController = navController, startDestination = Screen.Start.route) {
        composable(Screen.Start.route) {
            StartScreen(navController = navController)
        }
        composable(Screen.Home.route) {
            val homeViewModel: HomeScreenViewModel = viewModel(factory = homeViewModelFactory)
            HomeScreen(navController = navController, viewModel = homeViewModel)
        }
        composable(Screen.AddTask.route) {
            val homeViewModel: HomeScreenViewModel = viewModel(factory = homeViewModelFactory)
            AddTaskScreen(navController = navController, viewModel = homeViewModel)
        }
        composable(
            route = Screen.PomodoroTimer.route,
            arguments = listOf(navArgument("taskId") { type = NavType.IntType })
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getInt("taskId")
            val currentPomodoroViewModelFactory = remember {
                PomodoroTimerViewModelFactory(repository, taskId)
            }
            val pomodoroViewModel: PomodoroTimerViewModel = viewModel(factory = currentPomodoroViewModelFactory)
            PomodoroTimerScreen(taskId = taskId, navController = navController, viewModel = pomodoroViewModel)
        }
        composable(Screen.Calendar.route) {
            val calendarViewModel: CalendarViewModel = viewModel(factory = calendarViewModelFactory)
            val homeViewModel: HomeScreenViewModel = viewModel(factory = homeViewModelFactory)
            CalendarScreen(navController = navController, viewModel = calendarViewModel, homeViewModel = homeViewModel)
        }
        composable(Screen.Statistics.route) {
            StatisticsScreen(navController = navController) // Panggil StatisticsScreen hanya dengan navController
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    PomodoryTheme {
        PomodoryApp()
    }
}