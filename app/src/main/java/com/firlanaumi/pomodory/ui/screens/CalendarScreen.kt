package com.firlanaumi.pomodory.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.firlanaumi.pomodory.R
import com.firlanaumi.pomodory.ui.theme.PomodoryTheme
import com.firlanaumi.pomodory.ui.theme.Cream
import com.firlanaumi.pomodory.ui.theme.Brown
import com.firlanaumi.pomodory.ui.theme.Orange
import com.firlanaumi.pomodory.ui.theme.Pink
import com.firlanaumi.pomodory.ui.theme.Red
import com.firlanaumi.pomodory.ui.theme.Yellow
import com.firlanaumi.pomodory.ui.theme.DarkYellow
import com.firlanaumi.pomodory.model.Task
import com.firlanaumi.pomodory.model.TaskCategory
import com.firlanaumi.pomodory.Screen
import androidx.lifecycle.viewmodel.compose.viewModel
import com.firlanaumi.pomodory.viewmodel.CalendarViewModel
import com.firlanaumi.pomodory.viewmodel.HomeScreenViewModel
import androidx.compose.runtime.collectAsState
import com.firlanaumi.pomodory.ui.components.BottomNavigationBar // <-- Pastikan ini diimpor
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.Date


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    navController: NavController,
    viewModel: CalendarViewModel = viewModel(),
    homeViewModel: HomeScreenViewModel = viewModel() // Pastikan ViewModel ini juga diinject via Factory di MainActivity
) {
    val selectedDate by viewModel.selectedDate.collectAsState()
    val tasksForSelectedDate by viewModel.tasksForSelectedDate.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Jadwal Harian",
                        color = Brown,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                },
                navigationIcon = {
                    // Jika Anda ingin tombol kembali di CalendarScreen, tambahkan di sini
                    /*
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Brown
                        )
                    }
                    */
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Cream)
            )
        },
        bottomBar = {
            BottomNavigationBar(navController = navController) // <-- Teruskan navController di sini
        },
        containerColor = Cream,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Screen.AddTask.route) },
                containerColor = Orange,
                contentColor = Cream,
                shape = RoundedCornerShape(50)
            ) {
                Icon(Icons.Filled.Add, "Tambah Tugas Baru", modifier = Modifier.size(24.dp))
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Cream)
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Date Selector (Horizontal Scrollable)
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                items(generateDates(selectedDate.time, -7, 7)) { date ->
                    val isSelected = isSameDay(date, selectedDate.time)
                    DateItem(date = date, isSelected = isSelected) {
                        val newCalendar = Calendar.getInstance().apply { time = date }
                        viewModel.selectDate(newCalendar)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Greeting
            Text(
                text = "Yuk mulai fokus bareng Pomodory~",
                fontSize = 16.sp,
                color = Brown,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (tasksForSelectedDate.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Tidak ada tugas untuk tanggal ini.",
                        fontSize = 18.sp,
                        color = Brown.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Tekan '+' untuk menambahkan tugas baru!",
                        fontSize = 14.sp,
                        color = Brown.copy(alpha = 0.5f)
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = tasksForSelectedDate,
                        key = { task -> task.id }
                    ) { task ->
                        TaskItem(
                            title = task.title,
                            description = task.description,
                            duration = task.duration,
                            isChecked = task.isChecked,
                            backgroundColor = task.category.getColor(),
                            onCheckedChange = { isChecked ->
                                val updatedTask = task.copy(isChecked = isChecked)
                                homeViewModel.updateTask(updatedTask)
                            },
                            onPlayClick = {
                                navController.navigate(Screen.PomodoroTimer.createRoute(task.id))
                                println("Navigating to Pomodoro Timer for Task ID: ${task.id}")
                            }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Button(
                                onClick = { /* TODO: Implementasi Lihat Lainnya */ },
                                colors = ButtonDefaults.buttonColors(containerColor = Yellow),
                                shape = RoundedCornerShape(24.dp),
                                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
                                modifier = Modifier.height(46.dp)
                            ) {
                                Text(text = "Lihat lainnya...", color = Brown)
                            }
                        }
                    }
                    item { Spacer(modifier = Modifier.height(16.dp)) }
                }
            }
        }
    }
}

@Composable
fun DateItem(date: Date, isSelected: Boolean, onClick: () -> Unit) {
    val dayOfWeekFormat = SimpleDateFormat("EEE", Locale.getDefault())
    val dayOfMonthFormat = SimpleDateFormat("d", Locale.getDefault())
    val monthFormat = SimpleDateFormat("MMM", Locale.getDefault())

    Card(
        modifier = Modifier
            .size(64.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) DarkYellow else Cream
        ),
        border = if (isSelected) BorderStroke(2.dp, Brown) else null,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = dayOfMonthFormat.format(date), fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Brown)
            Text(text = monthFormat.format(date), fontSize = 12.sp, color = Brown)
        }
    }
}

fun generateDates(centerDate: Date, daysBefore: Int, daysAfter: Int): List<Date> {
    val calendar = Calendar.getInstance()
    calendar.time = centerDate

    calendar.add(Calendar.DAY_OF_YEAR, daysBefore)

    val dates = mutableListOf<Date>()
    repeat(daysAfter - daysBefore + 1) {
        dates.add(calendar.time)
        calendar.add(Calendar.DAY_OF_YEAR, 1)
    }
    return dates
}

fun isSameDay(date1: Date, date2: Date): Boolean {
    val cal1 = Calendar.getInstance().apply { time = date1 }
    val cal2 = Calendar.getInstance().apply { time = date2 }
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
            cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
            cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH)
}

@Preview(showBackground = true)
@Composable
fun CalendarScreenPreview() {
    PomodoryTheme {
        CalendarScreen(navController = rememberNavController())
    }
}