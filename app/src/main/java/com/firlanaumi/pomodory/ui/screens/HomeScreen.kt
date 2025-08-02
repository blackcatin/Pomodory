package com.firlanaumi.pomodory.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items // Pastikan ini diimpor
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController // Untuk Preview
import com.firlanaumi.pomodory.R
import com.firlanaumi.pomodory.ui.theme.PomodoryTheme
import com.firlanaumi.pomodory.ui.theme.Cream
import com.firlanaumi.pomodory.ui.theme.Brown
import com.firlanaumi.pomodory.ui.theme.Yellow
import com.firlanaumi.pomodory.ui.theme.Red
import com.firlanaumi.pomodory.ui.theme.Pink
import androidx.compose.runtime.getValue
import com.firlanaumi.pomodory.Screen
import androidx.lifecycle.viewmodel.compose.viewModel // Impor ini
import com.firlanaumi.pomodory.viewmodel.HomeScreenViewModel
import androidx.compose.runtime.collectAsState
import com.firlanaumi.pomodory.ui.theme.LightYellow
import com.firlanaumi.pomodory.ui.components.BottomNavigationBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeScreenViewModel = viewModel()
) {

    val tasks by viewModel.tasks.collectAsState()

    // Menghitung statistik berdasarkan tugas dari ViewModel
    val completedTasksCount = tasks.count { it.isChecked }
    val totalTasksCount = tasks.size // Total tugas aktual
    val progress = if (totalTasksCount > 0) completedTasksCount.toFloat() / totalTasksCount else 0f
    val streakText = "$completedTasksCount/$totalTasksCount" // Menghitung dari total tugas aktual, bukan statis 10


    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navController) // Teruskan navController
        },
        containerColor = Cream
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Cream)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(top = paddingValues.calculateTopPadding()),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Info",
                    tint = Brown,
                    modifier = Modifier.size(28.dp)
                )

                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Pomodory",
                    modifier = Modifier.size(80.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Welcome Section
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .background(LightYellow, RoundedCornerShape(16.dp))
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.hai),
                    contentDescription = "Cactus Mascot",
                    modifier = Modifier.size(80.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = "Hai Popo!",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Brown
                    )
                    Text(
                        text = "Siap fokus bareng Pomodory hari ini?\uD83C\uDFAF",
                        fontSize = 14.sp,
                        color = Brown
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Streak Progress Bar
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.fire),
                        contentDescription = "Fire Icon",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Ayo kejar streak-mu hari ini!",
                        fontSize = 16.sp,
                        color = Brown
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = progress,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(5.dp)),
                    color = Red,
                    trackColor = Pink
                )
                Text(
                    text = streakText,
                    fontSize = 12.sp,
                    color = Brown,
                    modifier = Modifier.align(Alignment.End)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Task List
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = paddingValues.calculateBottomPadding()),
                contentPadding = PaddingValues(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    items = tasks,
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
                            viewModel.updateTask(updatedTask) // Update status di database
                        },
                        onPlayClick = {
                            navController.navigate(Screen.PomodoroTimer.createRoute(task.id))
                            println("Navigating to Pomodoro Timer for Task ID: ${task.id}")
                        }
                    )
                }

                // "Lihat lainnya..." button
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Button(
                            onClick = {
                                navController.navigate(Screen.AddTask.route)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = LightYellow), // Gunakan Yellow, atau LightYellow jika ada
                            shape = RoundedCornerShape(24.dp),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
                            modifier = Modifier.height(46.dp)
                        ) {
                            Text(text = "Lihat lainnya...", color = Brown)
                        }
                    }
                }
            }
        }
    }
}

// --- TaskItem Composable (Disarankan untuk dipindahkan ke file terpisah: TaskItem.kt) ---
@Composable
fun TaskItem(
    title: String,
    description: String,
    duration: String,
    isChecked: Boolean,
    backgroundColor: Color,
    onCheckedChange: (Boolean) -> Unit,
    onPlayClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = isChecked,
                    onCheckedChange = onCheckedChange,
                    colors = CheckboxDefaults.colors(
                        checkedColor = Brown,
                        uncheckedColor = Brown,
                        checkmarkColor = Cream
                    ),
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Brown
                    )
                    Text(
                        text = description,
                        fontSize = 14.sp,
                        color = Brown
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.alarm),
                            contentDescription = "Timer Icon",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = duration,
                            fontSize = 12.sp,
                            color = Brown
                        )
                    }
                }
            }
            Button(
                onClick = onPlayClick,
                colors = ButtonDefaults.buttonColors(containerColor = Brown),
                shape = RoundedCornerShape(50),
                modifier = Modifier.size(48.dp),
                contentPadding = PaddingValues(0.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.play),
                    contentDescription = "Play",
                    tint = Cream,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    PomodoryTheme {
        HomeScreen(navController = rememberNavController())
    }
}