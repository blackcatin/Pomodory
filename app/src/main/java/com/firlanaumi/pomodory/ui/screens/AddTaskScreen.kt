package com.firlanaumi.pomodory.ui.screens

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft // Perbaikan
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight // Perbaikan
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.firlanaumi.pomodory.R
import androidx.compose.ui.text.style.TextAlign
import com.firlanaumi.pomodory.ui.theme.PomodoryTheme
import com.firlanaumi.pomodory.ui.theme.Cream
import com.firlanaumi.pomodory.ui.theme.Brown
import com.firlanaumi.pomodory.ui.theme.Orange
import com.firlanaumi.pomodory.ui.theme.Yellow // Untuk background card input
import com.firlanaumi.pomodory.model.TaskCategory // Import TaskCategory
import com.firlanaumi.pomodory.Screen // Import Screen untuk navigasi
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date // Penting: untuk deadline di Task
import java.util.Locale
import androidx.lifecycle.viewmodel.compose.viewModel // Import ini
import com.firlanaumi.pomodory.viewmodel.HomeScreenViewModel // Import HomeScreenViewModel
import com.firlanaumi.pomodory.model.Task // Import Task dari model package
import com.firlanaumi.pomodory.ui.components.BottomNavigationBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskScreen(
    navController: NavController,
    // Terima ViewModel sebagai parameter. Defaultnya akan disediakan oleh MainActivity.
    viewModel: HomeScreenViewModel = viewModel()
) {
    var judulTugas by remember { mutableStateOf("") }
    var catatanTambahan by remember { mutableStateOf("") }
    var sesiPomodoro by remember { mutableIntStateOf(1) }
    var expandedCategoryDropdown by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf<TaskCategory?>(null) }
    var deadlineDate by remember { mutableStateOf<Calendar?>(null) } // Menyimpan tanggal deadline

    val context = LocalContext.current

    // DatePickerDialog setup
    val year: Int
    val month: Int
    val day: Int
    val mCalendar = Calendar.getInstance()
    year = mCalendar.get(Calendar.YEAR)
    month = mCalendar.get(Calendar.MONTH)
    day = mCalendar.get(Calendar.DAY_OF_MONTH)

    val mDatePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, mYear: Int, mMonth: Int, mDayOfMonth: Int ->
            val newCalendar = Calendar.getInstance()
            newCalendar.set(mYear, mMonth, mDayOfMonth)
            deadlineDate = newCalendar
        }, year, month, day
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Tambah Tugas Baru",
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
                            tint = Brown
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
                .padding(paddingValues)
                .padding(horizontal = 24.dp, vertical = 16.dp), // Padding konten form
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Judul Tugas
            CustomTextField(
                value = judulTugas,
                onValueChange = { judulTugas = it },
                label = "Judul Tugas",
                placeholder = "Judul Tugas...",
                modifier = Modifier.fillMaxWidth()
            )

            // Catatan
            CustomTextField(
                value = catatanTambahan,
                onValueChange = { catatanTambahan = it },
                label = "Catatan",
                placeholder = "Catatan Tambahan...",
                modifier = Modifier.fillMaxWidth()
            )

            // Sesi Pomodoro dan Kategori
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Sesi Pomodoro
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "Sesi Pomodoro", fontSize = 14.sp, color = Brown)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .background(Yellow, RoundedCornerShape(16.dp))
                            .padding(horizontal = 8.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { if (sesiPomodoro > 1) sesiPomodoro-- }) {
                            Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, "Kurangi Sesi", tint = Brown)
                        }
                        Text(text = "$sesiPomodoro", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Brown)
                        IconButton(onClick = { if (sesiPomodoro < 4) sesiPomodoro++ }) {
                            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, "Tambah Sesi", tint = Brown)
                        }
                    }
                    Text(text = "Pilih antara 1 - 4 sesi", fontSize = 12.sp, color = Brown)
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Kategori Dropdown
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "Kategori :", fontSize = 14.sp, color = Brown)
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Yellow, RoundedCornerShape(16.dp))
                            .clickable { expandedCategoryDropdown = true }
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = selectedCategory?.name?.replace("_", " ") ?: "wajib diisi",
                            color = Brown,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        DropdownMenu(
                            expanded = expandedCategoryDropdown,
                            onDismissRequest = { expandedCategoryDropdown = false },
                            modifier = Modifier.fillMaxWidth(0.45f)
                        ) {
                            TaskCategory.entries.forEach { category ->
                                DropdownMenuItem(
                                    text = { Text(category.name.replace("_", " "), color = Brown) },
                                    onClick = {
                                        selectedCategory = category
                                        expandedCategoryDropdown = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Deadline
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Text(text = "Deadline:", fontSize = 14.sp, color = Brown, modifier = Modifier.padding(start = 8.dp))
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Yellow, RoundedCornerShape(16.dp))
                        .clickable { mDatePickerDialog.show() }
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        text = deadlineDate?.let {
                            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(it.time)
                        } ?: "dd/mm/yyyy",
                        color = Brown,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Tombol Simpan
            Button(
                onClick = {
                    val totalMinutes = sesiPomodoro * 25
                    val durationText = "$sesiPomodoro sesi, $totalMinutes menit"

                    if (judulTugas.isNotBlank() && selectedCategory != null && deadlineDate != null) {
                        val newTask = com.firlanaumi.pomodory.model.Task(
                            // Room akan auto-generate ID jika 0
                            id = 0, // Menggunakan 0 agar Room meng-generate ID
                            title = judulTugas,
                            description = catatanTambahan,
                            duration = durationText,
                            isChecked = false,
                            category = selectedCategory!!,
                            sessions = sesiPomodoro,
                            deadline = deadlineDate!!.time // Menggunakan Date dari Calendar.time
                        )
                        viewModel.insertTask(newTask) // Panggil insertTask dari ViewModel
                        navController.popBackStack()
                    } else {
                        println("Harap lengkapi semua bidang!")
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Orange),
                shape = RoundedCornerShape(24.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .height(56.dp)
            ) {
                Text(text = "Simpan", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Brown)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Column(modifier = modifier) {
        Text(text = "$label:", fontSize = 14.sp, color = Brown, modifier = Modifier.padding(start = 8.dp))
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = Brown.copy(alpha = 0.6f)) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Yellow,
                unfocusedContainerColor = Yellow,
                disabledContainerColor = Yellow,
                cursorColor = Brown,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                focusedTextColor = Brown,
                unfocusedTextColor = Brown
            ),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AddTaskScreenPreview() {
    PomodoryTheme {
        AddTaskScreen(navController = rememberNavController())
    }
}