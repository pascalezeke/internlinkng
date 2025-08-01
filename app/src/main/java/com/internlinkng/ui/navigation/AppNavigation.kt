package com.internlinkng.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.internlinkng.data.model.Hospital
import com.internlinkng.ui.screens.*
import com.internlinkng.viewmodel.MainViewModel

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
    object AdminHospitalFormEdit : Screen("admin_hospital_form/{hospitalId}") {
        fun createRoute(hospitalId: String) = "admin_hospital_form/$hospitalId"
    }
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
                apiService = viewModel.apiServiceInstance,
                onLoginSuccess = {
                    if (com.internlinkng.data.model.UserSession.isAdmin) {
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
                    viewModel = viewModel,
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }
        }
        
        composable(Screen.AppliedHospitals.route) {
            AppliedHospitalsScreen(
                viewModel = viewModel,
                onHospitalClick = { hospital ->
                    navController.navigate(Screen.HospitalDetails.createRoute(hospital.id))
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(Screen.Settings.route) {
            viewModel.loadUserProfile()
            SettingsScreen(
                onLogout = {
                    viewModel.logout()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onAdminLogin = {
                    navController.navigate(Screen.AdminHome.route)
                },
                viewModel = viewModel
            )
        }
        
        composable(Screen.AdminHome.route) {
            AdminHomeScreen(
                viewModel = viewModel,
                snackbarHostState = androidx.compose.material3.SnackbarHostState(),
                onLogout = {
                    viewModel.logout()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onAddHospital = {
                    navController.navigate(Screen.AdminHospitalForm.route)
                },
                onEditHospital = { hospital ->
                    navController.navigate(Screen.AdminHospitalFormEdit.createRoute(hospital.id))
                }
            )
        }
        
        composable(Screen.AdminHospitalForm.route) {
            AdminHospitalFormScreen(
                initialHospital = null,
                onSubmit = { hospital ->
                    viewModel.addHospital(
                        hospital = hospital,
                        onSuccess = {
                            navController.popBackStack()
                        },
                        onError = { errorMsg ->
                            // Show error message - the form will handle this
                            android.util.Log.e("AdminHospitalForm", "Error adding hospital: $errorMsg")
                        }
                    )
                },
                onCancel = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(Screen.AdminHospitalFormEdit.route) { backStackEntry ->
            val hospitalId = backStackEntry.arguments?.getString("hospitalId")
            val initialHospital = if (hospitalId != null) {
                viewModel.uiState.collectAsState().value.hospitals.find { it.id == hospitalId }
            } else {
                null
            }

            AdminHospitalFormScreen(
                initialHospital = initialHospital,
                onSubmit = { hospital ->
                    viewModel.updateHospital(
                        hospital = hospital,
                        onSuccess = {
                            navController.popBackStack()
                        },
                        onError = { errorMsg ->
                            // Optionally show a snackbar or dialog
                        }
                    )
                },
                onCancel = {
                    navController.popBackStack()
                }
            )
        }
    }
} 