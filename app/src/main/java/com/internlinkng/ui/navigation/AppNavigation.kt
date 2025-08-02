package com.internlinkng.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.internlinkng.ui.screens.*
import com.internlinkng.viewmodel.MainViewModel
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Button
import androidx.compose.ui.unit.dp
import com.internlinkng.data.model.Hospital
import androidx.compose.material3.MaterialTheme

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Home : Screen("home")
    object HospitalDetails : Screen("hospital_details/{hospitalId}") {
        fun createRoute(hospitalId: String) = "hospital_details/$hospitalId"
    }
    object AppliedHospitals : Screen("applied_hospitals")
    object Settings : Screen("settings")
    object AdminHome : Screen("admin_home")
    object AdminHospitalForm : Screen("admin_hospital_form")
    object AdminHospitalEdit : Screen("admin_hospital_edit/{hospitalId}") {
        fun createRoute(hospitalId: String) = "admin_hospital_edit/$hospitalId"
    }
    object AdminHospitalManagement : Screen("admin_hospital_management")
    object AdminUsers : Screen("admin_users")
    object AdminApplications : Screen("admin_applications")
}

@Composable
fun AppNavigation(
    viewModel: MainViewModel,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    // Check if user is admin
                    if (viewModel.uiState.value.isAdmin) {
                        navController.navigate(Screen.AdminHome.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    } else {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                },
                viewModel = viewModel
            )
        }
        
        composable(Screen.Home.route) {
            HomeScreen(
                viewModel = viewModel,
                onHospitalClick = { hospital ->
                    navController.navigate(Screen.HospitalDetails.createRoute(hospital.id))
                },
                onSearchClick = {
                    // TODO: Implement search screen
                },
                onAppliedClick = {
                    navController.navigate(Screen.AppliedHospitals.route)
                },
                onSettingsClick = {
                    navController.navigate(Screen.Settings.route)
                }
            )
        }
        
        composable(Screen.HospitalDetails.route) { backStackEntry ->
            val hospitalId = backStackEntry.arguments?.getString("hospitalId")
            val uiState by viewModel.uiState.collectAsState()
            val hospital = uiState.hospitals.find { it.id == hospitalId }
            
            hospital?.let { hospitalData ->
                HospitalDetailsScreen(
                    hospital = hospitalData,
                    onBackClick = { navController.popBackStack() },
                    viewModel = viewModel
                )
            }
        }
        
        composable(Screen.AppliedHospitals.route) {
            AppliedHospitalsScreen(
                onBackClick = { navController.popBackStack() },
                viewModel = viewModel,
                onHospitalClick = { hospital ->
                    navController.navigate(Screen.HospitalDetails.createRoute(hospital.id))
                }
            )
        }
        
        composable(Screen.Settings.route) {
            SettingsScreen(
                onLogout = {
                    viewModel.logout()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onAdminLogin = {
                    // Navigate to admin home if user is admin
                    if (viewModel.uiState.value.isAdmin) {
                        navController.navigate(Screen.AdminHome.route) {
                            popUpTo(Screen.Settings.route) { inclusive = true }
                        }
                    }
                },
                viewModel = viewModel
            )
        }
        
        // Admin Routes
        composable(Screen.AdminHome.route) {
            AdminHomeScreen(
                viewModel = viewModel,
                onAddHospital = {
                    navController.navigate(Screen.AdminHospitalForm.route)
                },
                onEditHospital = { hospital ->
                    navController.navigate(Screen.AdminHospitalEdit.createRoute(hospital.id))
                },
                onManageHospitals = {
                    navController.navigate(Screen.AdminHospitalManagement.route)
                },
                onViewUsers = {
                    navController.navigate(Screen.AdminUsers.route)
                },
                onViewApplications = {
                    navController.navigate(Screen.AdminApplications.route)
                }
            )
        }
        
        composable(Screen.AdminHospitalForm.route) {
            AdminHospitalFormScreen(
                viewModel = viewModel,
                hospital = null, // New hospital
                onSuccess = {
                    navController.popBackStack()
                },
                onCancel = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(Screen.AdminHospitalEdit.route) { backStackEntry ->
            val hospitalId = backStackEntry.arguments?.getString("hospitalId")
            var hospital by remember { mutableStateOf<Hospital?>(null) }
            var isLoading by remember { mutableStateOf(true) }
            var error by remember { mutableStateOf<String?>(null) }
            
            LaunchedEffect(hospitalId) {
                if (hospitalId != null) {
                    viewModel.getHospitalById(
                        hospitalId = hospitalId,
                        onSuccess = { hospitalData ->
                            hospital = hospitalData
                            isLoading = false
                        },
                        onError = { errorMessage ->
                            error = errorMessage
                            isLoading = false
                        }
                    )
                } else {
                    error = "Invalid hospital ID"
                    isLoading = false
                }
            }
            
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                error != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Error",
                                style = MaterialTheme.typography.titleLarge
                            )
                            Text(
                                text = error!!,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { navController.popBackStack() }
                            ) {
                                Text("Go Back")
                            }
                        }
                    }
                }
                hospital != null -> {
                    AdminHospitalFormScreen(
                        viewModel = viewModel,
                        hospital = hospital,
                        onSuccess = {
                            navController.popBackStack()
                        },
                        onCancel = {
                            navController.popBackStack()
                        }
                    )
                }
            }
        }
        
        composable(Screen.AdminHospitalManagement.route) {
            AdminHospitalManagementScreen(
                viewModel = viewModel,
                onEditHospital = { hospital ->
                    navController.navigate(Screen.AdminHospitalEdit.createRoute(hospital.id))
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(Screen.AdminUsers.route) {
            // TODO: Implement AdminUsersScreen
            Text("Admin Users Screen - Coming Soon")
        }
        
        composable(Screen.AdminApplications.route) {
            // TODO: Implement AdminApplicationsScreen
            Text("Admin Applications Screen - Coming Soon")
        }
    }
} 