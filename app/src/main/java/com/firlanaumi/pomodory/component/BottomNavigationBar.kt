package com.firlanaumi.pomodory.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.firlanaumi.pomodory.R
import com.firlanaumi.pomodory.Screen // Import Screen sealed class
import com.firlanaumi.pomodory.ui.theme.Brown
import com.firlanaumi.pomodory.ui.theme.Cream
import com.firlanaumi.pomodory.ui.theme.Orange

@Composable
fun BottomNavigationBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val items = listOf("Home", "Statistik", "Kalender")
    val routes = listOf(Screen.Home.route, Screen.Statistics.route, Screen.Calendar.route) // Rute yang sesuai
    val icons = listOf(R.drawable.home, R.drawable.statistics, R.drawable.planner)

    BottomAppBar(
        containerColor = Cream,
        modifier = Modifier.height(70.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEachIndexed { index, item ->
                val isSelected = currentRoute == routes[index]
                NavigationBarItem(
                    selected = isSelected,
                    onClick = {
                        if (currentRoute != routes[index]) {
                            navController.navigate(routes[index]) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Brown,
                        unselectedIconColor = Brown,
                        selectedTextColor = Brown,
                        unselectedTextColor = Brown,
                        indicatorColor = Color.Transparent
                    ),
                    icon = {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .height(48.dp)
                                .aspectRatio(1f)
                        ) {
                            if (isSelected) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(RoundedCornerShape(58))
                                        .background(Orange),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Image(
                                        painter = painterResource(id = icons[index]),
                                        contentDescription = item,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                            } else {
                                Image(
                                    painter = painterResource(id = icons[index]),
                                    contentDescription = item,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                    },
                    label = {
                        Text(
                            text = item,
                            fontSize = 12.sp,
                            color = Brown,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            modifier = Modifier.offset(y = (-2).dp)
                        )
                    }
                )
            }
        }
    }
}