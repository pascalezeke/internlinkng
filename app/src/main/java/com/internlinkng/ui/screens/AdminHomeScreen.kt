package com.internlinkng.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.internlinkng.viewmodel.MainViewModel
import com.internlinkng.data.model.Hospital
import kotlinx.coroutines.launch

@Composable
fun AdminHomeScreen(
    viewModel: MainViewModel,
    snackbarHostState: SnackbarHostState,
    onLogout: () -> Unit = {},
    onAddHospital: (() -> Unit)? = null,
    onEditHospital: ((Hospital) -> Unit)? = null,
    refreshKey: Int = 0,
    onRefresh: (() -> Unit)? = null
) {
    val coroutineScope = rememberCoroutineScope()
    val uiState by viewModel.uiState.collectAsState()
    var hospitalToDelete by remember { mutableStateOf<Hospital?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadHospitals()
    }

    if (hospitalToDelete != null) {
        AlertDialog(
            onDismissRequest = { hospitalToDelete = null },
            title = { Text("Delete Hospital") },
            text = { Text("Are you sure you want to delete '${hospitalToDelete?.name}'?") },
            confirmButton = {
                Button(onClick = {
                    coroutineScope.launch {
                        try {
                            viewModel.apiServiceInstance?.deleteHospital(hospitalToDelete!!.id)
                            snackbarHostState.showSnackbar("Hospital deleted!")
                            hospitalToDelete = null
                            viewModel.loadHospitals()
                        } catch (e: Exception) {
                            snackbarHostState.showSnackbar("Failed: ${e.localizedMessage}")
                        }
                    }
                }) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { hospitalToDelete = null }) { Text("Cancel") }
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(16.dp)) {
            Text("Admin Dashboard", style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(bottom = 16.dp))
            if (uiState.isLoading) {
                CircularProgressIndicator()
            } else if (uiState.error != null) {
                Text(uiState.error!!, color = MaterialTheme.colorScheme.error)
            } else {
                uiState.hospitals.forEach { hospital ->
                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text(hospital.name, style = MaterialTheme.typography.titleMedium)
                            Text(hospital.state)
                            Text(hospital.salaryRange)
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                                Button(onClick = { onEditHospital?.invoke(hospital) }, modifier = Modifier.padding(end = 8.dp)) {
                                    Text("Edit")
                                }
                                Button(onClick = {
                                    hospitalToDelete = hospital
                                }) {
                                    Text("Delete")
                                }
                            }
                        }
                    }
                }
            }
            Button(onClick = { onAddHospital?.invoke() }, modifier = Modifier.padding(top = 16.dp)) {
                Text("Add Hospital")
            }
            Button(onClick = onLogout, modifier = Modifier.padding(top = 32.dp)) {
                Text("Logout")
            }
        }
    }
} 