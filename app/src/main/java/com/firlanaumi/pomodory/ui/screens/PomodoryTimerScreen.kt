package com.firlanaumi.pomodory.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.firlanaumi.pomodory.ui.theme.Yellow // Untuk Category Card

import java.util.Calendar
import java.util.Locale

import com.firlanaumi.pomodory.model.Task
import com.firlanaumi.pomodory.model.TaskCategory
import androidx.lifecycle.viewmodel.compose.viewModel
import com.firlanaumi.pomodory.viewmodel.PomodoroTimerViewModel
import androidx.compose.runtime.collectAsState
import com.firlanaumi.pomodory.ui.components.BottomNavigationBar
import com.firlanaumi.pomodory.ui.theme.LightYellow


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PomodoroTimerScreen(
    taskId: Int?,
    navController: NavController,
    viewModel: PomodoroTimerViewModel = viewModel()
) {
    val currentTask by viewModel.currentTask.collectAsState()
    val remainingTimeSeconds by viewModel.remainingTime.collectAsState()
    val elapsedTimeSeconds by viewModel.elapsedTimeSeconds.collectAsState() // Dapatkan waktu yang berlalu
    val isRunning by viewModel.isRunning.collectAsState()
    val currentSessionCount by viewModel.currentSessionCount.collectAsState()
    val isBreakTime by viewModel.isBreakTime.collectAsState()

    if (currentTask == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Brown)
            Text(text = "Memuat Tugas...", modifier = Modifier.offset(y = 40.dp), color = Brown)
        }
        return
    }

    val task = currentTask!!

    val minutes = remainingTimeSeconds / 60
    val seconds = remainingTimeSeconds % 60

    // Untuk waktu yang berlalu
    val elapsedMinutes = elapsedTimeSeconds / 60
    val elapsedSeconds = elapsedTimeSeconds % 60

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = task.title,
                        color = Brown,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Brown,
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO: Aksi Notifikasi */ }) {
                        Icon(
                            painter = painterResource(id = R.drawable.alarm), // Ikon Notifikasi
                            contentDescription = "Notification",
                            tint = Brown,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Cream)
            )
        },
        bottomBar = {
            BottomNavigationBar(navController = navController)
        },
        containerColor = Cream
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Cream)
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
            // Perbaiki Arrangement. Top untuk menempatkan konten lebih tinggi
            verticalArrangement = Arrangement.Top // Mengubah ke Top
        ) {
            // Spacer disesuaikan agar konten lebih ke atas
            Spacer(modifier = Modifier.height(32.dp)) // Jarak dari Top Bar

            // Timer Circle
            Box(
                modifier = Modifier
                    .size(250.dp)
                    .clip(CircleShape)
                    .background(Pink)
                    .border(2.dp, Orange, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // Waktu yang berlalu (timer kecil)
                    Text(
                        text = String.format(Locale.getDefault(), "%02d:%02d:%02d", 0, elapsedMinutes, elapsedSeconds),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Normal,
                        color = Brown
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Waktu Utama dan Ikon Play/Pause
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds),
                            fontSize = 50.sp,
                            fontWeight = FontWeight.Bold,
                            color = Brown
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(onClick = { viewModel.toggleTimer() }) {
                            Icon(
                                painter = painterResource(id = if (isRunning) R.drawable.pause else R.drawable.play), // Asumsi ada aset ini
                                contentDescription = if (isRunning) "Pause" else "Play",
                                tint = Brown,
                                modifier = Modifier.size(50.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isBreakTime) "Waktu Istirahat!" else "Jeda: $currentSessionCount waktu",
                        fontSize = 14.sp,
                        color = Brown
                    )
                }
            }

            // Progress Hearts
            Spacer(modifier = Modifier.height(18.dp)) // Menambah jarak
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(task.sessions) { index ->
                    Image(
                        painter = painterResource(id = if (index < currentSessionCount) R.drawable.heart_filled else R.drawable.heart_empty),
                        contentDescription = "Heart ${index + 1}",
                        modifier = Modifier.size(32.dp).padding(horizontal = 4.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(18.dp)) // Menambah jarak

            // Control Buttons (Play, Check, Cancel) - Perbaiki ikon dan latar belakang
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Play/Pause Button (ini akan jadi Play/Pause dari gambar)
                Button(
                    onClick = { viewModel.toggleTimer() }, // Ini harusnya toggle, bukan hanya pause
                    colors = ButtonDefaults.buttonColors(containerColor = Brown), // Warna background Brown
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.size(56.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(
                        // Menggunakan ikon play/pause kecil, jika tidak ada pause_big/play_big di tengah
                        painter = painterResource(id = if (isRunning) R.drawable.pause else R.drawable.play),
                        contentDescription = if (isRunning) "Pause" else "Play",
                        tint = Cream,
                        modifier = Modifier.size(28.dp)
                    )
                }
                // Check Button
                Button(
                    onClick = {
                        viewModel.markTaskCompleted()
                        navController.popBackStack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Brown),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.size(56.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.check),
                        contentDescription = "Check",
                        tint = Cream,
                        modifier = Modifier.size(28.dp)
                    )
                }
                // Cancel Button
                Button(
                    onClick = { viewModel.resetTimer() }, // Hanya reset timer, tidak langsung popBackStack
                    colors = ButtonDefaults.buttonColors(containerColor = Brown), // Warna background Brown
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.size(56.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.cancel),
                        contentDescription = "Cancel",
                        tint = Cream,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp)) // Jarak ke Session/Break Card

            // Session/Break Settings Card
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(120.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = LightYellow),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SessionSettingColumn(title = "Sesi", value = "${task.sessions}", unit = "")
                    SessionSettingColumn(title = "Short Break", value = "5", unit = "Menit")
                    SessionSettingColumn(title = "Long Break", value = "30", unit = "Menit")
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Category Card
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(80.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = task.category.getColor()),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Kategori :",
                        fontSize = 16.sp,
                        color = Brown
                    )
                    Text(
                        text = task.category.name.replace("_", " "),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Brown,
                    )
                }
            }
            // Spacer di sini mungkin tidak diperlukan atau harus lebih kecil jika layout terlalu panjang
            // Spacer(modifier = Modifier.height(16.dp))

            // Completion Time
            Text(
                text = "Timer akan selesai pada ${calculateCompletionTimeFromSeconds(remainingTimeSeconds)}",
                fontSize = 14.sp,
                color = Brown
            )
            Spacer(modifier = Modifier.height(16.dp)) // Padding ke BottomNav
        }
    }
}

@Composable
fun SessionSettingColumn(title: String, value: String, unit: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = title, fontSize = 14.sp, color = Brown)
        Text(text = value, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Brown)
        if (unit.isNotEmpty()) {
            Text(text = unit, fontSize = 12.sp, color = Brown)
        }
    }
}

fun calculateCompletionTimeFromSeconds(totalSecondsRemaining: Int): String {
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.SECOND, totalSecondsRemaining)

    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val minute = calendar.get(Calendar.MINUTE)

    return String.format(Locale.getDefault(), "%02d:%02d", hour, minute)
}

@Preview(showBackground = true)
@Composable
fun PomodoroTimerScreenPreview() {
    PomodoryTheme {
        PomodoroTimerScreen(taskId = 1, navController = rememberNavController())
    }
}