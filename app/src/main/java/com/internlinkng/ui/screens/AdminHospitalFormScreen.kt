package com.internlinkng.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.internlinkng.data.model.Hospital
import com.internlinkng.viewmodel.MainViewModel
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminHospitalFormScreen(
    viewModel: MainViewModel,
    hospital: Hospital? = null,
    onSuccess: () -> Unit,
    onCancel: () -> Unit
) {
    val context = LocalContext.current
    
    var name by remember { mutableStateOf(hospital?.name ?: "") }
    var state by remember { mutableStateOf(hospital?.state ?: "") }
    var professions by remember { mutableStateOf(hospital?.professions ?: "") }
    var salaryRange by remember { mutableStateOf(hospital?.salaryRange ?: "") }
    var deadline by remember { mutableStateOf(hospital?.deadline ?: "") }
    var created by remember { mutableStateOf(hospital?.created ?: "2024") }
    var onlineApplication by remember { mutableStateOf(hospital?.onlineApplication ?: false) }
    var applicationUrl by remember { mutableStateOf(hospital?.applicationUrl ?: "") }
    var physicalAddress by remember { mutableStateOf(hospital?.physicalAddress ?: "") }
    var professionSalaries by remember { mutableStateOf(hospital?.professionSalaries ?: "") }
    
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val nigerianStates = viewModel.getAvailableStates()
    var stateExpanded by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
            Text(
                        text = if (hospital == null) "Add New Hospital" else "Edit Hospital",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onCancel) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Hospital Name
            item {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Hospital Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
            
            // State Selection
            item {
                ExposedDropdownMenuBox(
                    expanded = stateExpanded,
                    onExpandedChange = { stateExpanded = !stateExpanded }
                ) {
                    OutlinedTextField(
                        value = state,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("State") },
                        trailingIcon = { Icon(Icons.Default.ArrowDropDown, null) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = stateExpanded,
                        onDismissRequest = { stateExpanded = false }
                    ) {
                        nigerianStates.forEach { s ->
                            DropdownMenuItem(
                                text = { Text(s) },
                                onClick = {
                                    state = s
                                    stateExpanded = false
                                }
                            )
                        }
                    }
                }
            }
            
            // Professions
            item {
                OutlinedTextField(
                    value = professions,
                    onValueChange = { professions = it },
                    label = { Text("Professions (comma-separated)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    placeholder = { Text("e.g., Medicine, Nursing, Pharmacy") }
                )
            }
            
            // Salary Range
            item {
                OutlinedTextField(
                    value = salaryRange,
                    onValueChange = { salaryRange = it },
                    label = { Text("Salary Range") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    placeholder = { Text("e.g., ₦50,000 - ₦100,000") }
                )
            }
            
            // Deadline
            item {
                OutlinedTextField(
                    value = deadline,
                    onValueChange = { deadline = it },
                    label = { Text("Application Deadline") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    placeholder = { Text("e.g., December 31, 2024") }
                )
            }
            
            // Created Date
            item {
                OutlinedTextField(
                    value = created,
                    onValueChange = { created = it },
                    label = { Text("Created Date") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    placeholder = { Text("e.g., 2024") }
                )
            }
            
            // Online Application Toggle
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Online Application Available",
                        modifier = Modifier.weight(1f)
                    )
                    Switch(
                        checked = onlineApplication,
                        onCheckedChange = { onlineApplication = it }
                    )
                }
            }
            
            // Application URL (if online application)
            if (onlineApplication) {
                item {
                    OutlinedTextField(
                        value = applicationUrl,
                        onValueChange = { applicationUrl = it },
                        label = { Text("Application URL") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        placeholder = { Text("https://example.com/apply") }
                    )
                }
            }
            
            // Physical Address
            item {
                OutlinedTextField(
                    value = physicalAddress,
                    onValueChange = { physicalAddress = it },
                    label = { Text("Physical Address") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    placeholder = { Text("Hospital address for courier applications") }
                )
            }
            
            // Profession Salaries
            item {
                OutlinedTextField(
                    value = professionSalaries,
                    onValueChange = { professionSalaries = it },
                    label = { Text("Profession Salaries (JSON)") },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("{\"Medicine\":\"₦80,000\",\"Nursing\":\"₦60,000\"}") }
                )
            }
            
            // Error Message
            if (errorMessage != null) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Text(
                            text = errorMessage!!,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
            
            // Action Buttons
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                onClick = onCancel, 
                        modifier = Modifier.weight(1f)
            ) { 
                Text("Cancel") 
            }
                    
            Button(
                onClick = {
                            if (name.isBlank() || state.isBlank() || professions.isBlank()) {
                        errorMessage = "Please fill in all required fields"
                        return@Button
                    }
                    
                    isLoading = true
                    errorMessage = null
                    
                            if (hospital == null) {
                                // Add new hospital
                                viewModel.addHospital(
                                    name = name,
                                    state = state,
                                    professions = professions,
                                    salaryRange = salaryRange,
                                    deadline = deadline,
                                    created = created,
                                    onlineApplication = onlineApplication,
                                    applicationUrl = if (applicationUrl.isNotBlank()) applicationUrl else null,
                                    physicalAddress = physicalAddress,
                                    professionSalaries = professionSalaries,
                                    onSuccess = {
                                        Toast.makeText(context, "Hospital added successfully!", Toast.LENGTH_SHORT).show()
                                        onSuccess()
                                    },
                                    onError = { error ->
                                        errorMessage = error
                                        isLoading = false
                                    }
                                )
                            } else {
                                // Update existing hospital
                                viewModel.updateHospital(
                                    hospitalId = hospital.id,
                                    name = name,
                                    state = state,
                                    professions = professions,
                                    salaryRange = salaryRange,
                                    deadline = deadline,
                                    created = created,
                                    onlineApplication = onlineApplication,
                                    applicationUrl = if (applicationUrl.isNotBlank()) applicationUrl else null,
                                    physicalAddress = physicalAddress,
                                    professionSalaries = professionSalaries,
                                    onSuccess = {
                                        Toast.makeText(context, "Hospital updated successfully!", Toast.LENGTH_SHORT).show()
                                        onSuccess()
                                    },
                                    onError = { error ->
                                        errorMessage = error
                                        isLoading = false
                                    }
                                )
                    }
                }, 
                        modifier = Modifier.weight(1f),
                enabled = !isLoading
            ) { 
                if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                } else {
                            Text(if (hospital == null) "Add Hospital" else "Update Hospital")
                        }
                    }
                }
            }
        }
    }
} 