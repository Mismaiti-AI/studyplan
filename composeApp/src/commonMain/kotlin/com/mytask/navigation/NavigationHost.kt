package com.mytask.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.mytask.presentation.assignmentdetail.AssignmentDetailScreen
import com.mytask.presentation.assignmentlist.AssignmentListScreen
import com.mytask.presentation.dashboard.DashboardScreen
import com.mytask.presentation.examdetail.ExamDetailScreen
import com.mytask.presentation.examlist.ExamListScreen
import com.mytask.presentation.projectdetail.ProjectDetailScreen
import com.mytask.presentation.projectlist.ProjectListScreen
import com.mytask.presentation.settings.SettingsScreen
import com.mytask.presentation.sheeturlconfig.SheetUrlConfigScreen
import com.mytask.presentation.splash.SplashScreen

@Composable
fun NavigationHost() {
    val navController = rememberNavController()
    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStack?.destination?.route

    val bottomNavItems = listOf(
        BottomNavItem(
            route = Dashboard,
            icon = Icons.Default.Dashboard,
            label = "Dashboard"
        ),
        BottomNavItem(
            route = Assignments,
            icon = Icons.AutoMirrored.Filled.List,
            label = "Assignments"
        ),
        BottomNavItem(
            route = Exams,
            icon = Icons.Default.Event,
            label = "Exams"
        ),
        BottomNavItem(
            route = Projects,
            icon = Icons.Default.Work,
            label = "Projects"
        )
    )

    val showBottomBar = currentDestination in bottomNavItems.map { it.route::class.simpleName }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomAppBar {
                    bottomNavItems.forEach { item ->
                        NavigationBarItem(
                            selected = currentDestination == item.route::class.simpleName,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Config,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable<Config> {
                SplashScreen(
                    onConfigSuccess = { navController.navigate(Dashboard) }
                )
            }
            composable<Dashboard> {
                DashboardScreen(
                    onAssignmentClick = { id -> navController.navigate(AssignmentDetail(id)) },
                    onExamClick = { id -> navController.navigate(ExamDetail(id)) },
                    onProjectClick = { id -> navController.navigate(ProjectDetail(id)) }
                )
            }
            composable<Assignments> {
                AssignmentListScreen(
                    onAssignmentClick = { id -> navController.navigate(AssignmentDetail(id)) }
                )
            }
            composable<AssignmentDetail> { backStackEntry ->
                val route = backStackEntry.toRoute<AssignmentDetail>()
                AssignmentDetailScreen(
                    assignmentId = route.assignmentId,
                    onBack = { navController.popBackStack() }
                )
            }
            composable<Exams> {
                ExamListScreen(
                    onExamClick = { id -> navController.navigate(ExamDetail(id)) }
                )
            }
            composable<ExamDetail> { backStackEntry ->
                val route = backStackEntry.toRoute<ExamDetail>()
                ExamDetailScreen(
                    examId = route.examId,
                    onBack = { navController.popBackStack() }
                )
            }
            composable<Projects> {
                ProjectListScreen(
                    onProjectClick = { id -> navController.navigate(ProjectDetail(id)) }
                )
            }
            composable<ProjectDetail> { backStackEntry ->
                val route = backStackEntry.toRoute<ProjectDetail>()
                ProjectDetailScreen(
                    projectId = route.projectId,
                    onBack = { navController.popBackStack() }
                )
            }
            composable<Settings> {
                SettingsScreen(
                    onSheetUrlConfig = { navController.navigate(SheetUrlConfig) },
                    onBack = { navController.popBackStack() }
                )
            }
            composable<SheetUrlConfig> {
                SheetUrlConfigScreen(
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}

data class BottomNavItem(
    val route: Any,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val label: String
)