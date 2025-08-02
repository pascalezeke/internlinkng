package com.internlinkng.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.internlinkng.data.model.Hospital

@Composable
fun AdminHospitalFormScreen(
    initialHospital: Hospital? = null,
    onSubmit: (Hospital) -> Unit,
    onCancel: () -> Unit
) {
    var name by remember { mutableStateOf(initialHospital?.name ?: "") }
    var state by remember { mutableStateOf(initialHospital?.state ?: "") }
    // Dynamic list of profession-salary pairs
    var professionSalaryList by remember {
        mutableStateOf(
            if (initialHospital != null) {
                val professions = initialHospital.professions.split(",").map { it.trim() }
                val salaries = initialHospital.professionSalaries?.let { salariesStr ->
                    try {
                        // Simple parsing of profession salaries
                        salariesStr.trim('{', '}').split(",")
                            .map { pair -> 
                                val keyValue = pair.split(":")
                                if (keyValue.size == 2) {
                                    keyValue[0].trim('"') to keyValue[1].trim('"')
                                } else null
                            }
                            .filterNotNull()
                            .toMap()
                    } catch (e: Exception) {
                        emptyMap()
                    }
                } ?: emptyMap()
                
                professions.mapIndexed { index, profession ->
                    profession to (salaries[profession] ?: "")
                }.toMutableList()
            } else {
                mutableListOf(Pair("", ""))
            }
        )
    }
    var salaryRange by remember { mutableStateOf(initialHospital?.salaryRange ?: "") }
    var deadline by remember { mutableStateOf(initialHospital?.deadline ?: "") }
    var created by remember { mutableStateOf(initialHospital?.created ?: "2024-07-28") }
    var onlineApplication by remember { mutableStateOf(initialHospital?.onlineApplication ?: false) }
    var applicationUrl by remember { mutableStateOf(initialHospital?.applicationUrl ?: "") }
    var physicalAddress by remember { mutableStateOf(initialHospital?.physicalAddress ?: "") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(if (initialHospital == null) "Add Hospital" else "Edit Hospital", style = MaterialTheme.typography.headlineSmall)
        
        if (errorMessage != null) {
            Text(
                text = errorMessage!!,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
        
        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") }, modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp))
        OutlinedTextField(value = state, onValueChange = { state = it }, label = { Text("State") }, modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp))
        // Professions and Salaries
        Text("Professions & Salaries", style = MaterialTheme.typography.labelLarge, modifier = Modifier.padding(top = 8.dp, bottom = 4.dp))
        professionSalaryList.forEachIndexed { idx, pair ->
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp)) {
                OutlinedTextField(
                    value = pair.first,
                    onValueChange = { newProfession ->
                        professionSalaryList = professionSalaryList.toMutableList().also { it[idx] = it[idx].copy(first = newProfession) }
                    },
                    label = { Text("Profession") },
                    modifier = Modifier.weight(1f).padding(end = 4.dp)
                )
                OutlinedTextField(
                    value = pair.second,
                    onValueChange = { newSalary ->
                        professionSalaryList = professionSalaryList.toMutableList().also { it[idx] = it[idx].copy(second = newSalary) }
                    },
                    label = { Text("Salary") },
                    modifier = Modifier.weight(1f).padding(start = 4.dp)
                )
                if (professionSalaryList.size > 1) {
                    IconButton(onClick = { professionSalaryList = professionSalaryList.toMutableList().also { it.removeAt(idx) } }) {
                        Icon(Icons.Default.Delete, contentDescription = "Remove")
                    }
                }
            }
        }
        Button(onClick = { professionSalaryList = (professionSalaryList + Pair("", "")).toMutableList() }, modifier = Modifier.align(Alignment.End)) {
            Text("Add Profession")
        }
        OutlinedTextField(value = salaryRange, onValueChange = { salaryRange = it }, label = { Text("Salary Range") }, modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp))
        OutlinedTextField(value = deadline, onValueChange = { deadline = it }, label = { Text("Deadline (YYYY-MM-DD)") }, modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp))
        OutlinedTextField(value = created, onValueChange = { created = it }, label = { Text("Created Date (YYYY-MM-DD)") }, modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp))
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
            Checkbox(checked = onlineApplication, onCheckedChange = { onlineApplication = it })
            Text("Online Application Allowed")
        }
        OutlinedTextField(value = applicationUrl, onValueChange = { applicationUrl = it }, label = { Text("Application URL (if online)") }, modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp))
        OutlinedTextField(value = physicalAddress, onValueChange = { physicalAddress = it }, label = { Text("Physical Address") }, modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp))
        
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            Button(
                onClick = onCancel, 
                modifier = Modifier.padding(top = 16.dp),
                enabled = !isLoading
            ) { 
                Text("Cancel") 
            }
            Button(
                onClick = {
                    if (name.isBlank() || state.isBlank() || professionSalaryList.any { it.first.isBlank() || it.second.isBlank() } || deadline.isBlank() || physicalAddress.isBlank()) {
                        errorMessage = "Please fill in all required fields"
                        return@Button
                    }
                    
                    isLoading = true
                    errorMessage = null
                    
                    try {
                        val professionsString = professionSalaryList.map { it.first.trim() }.joinToString(",")
                        val professionSalariesString = professionSalaryList
                            .filter { it.first.isNotBlank() && it.second.isNotBlank() }
                            .joinToString(",") { "\"${it.first}\":\"${it.second}\"" }
                            .let { if (it.isNotBlank()) "{$it}" else null }
                        
                        onSubmit(
                            Hospital(
                                id = initialHospital?.id ?: "", // Backend should generate ID for new
                                name = name,
                                state = state,
                                professions = professionsString,
                                salaryRange = salaryRange,
                                deadline = deadline,
                                created = created,
                                onlineApplication = onlineApplication,
                                applicationUrl = applicationUrl.ifBlank { null },
                                physicalAddress = physicalAddress,
                                professionSalaries = professionSalariesString
                            )
                        )
                    } catch (e: Exception) {
                        errorMessage = "Error: ${e.message}"
                    } finally {
                        isLoading = false
                    }
                },
                modifier = Modifier.padding(top = 16.dp),
                enabled = !isLoading
            ) { 
                Text(if (isLoading) "Saving..." else if (initialHospital == null) "Add Hospital" else "Update Hospital") 
            }
        }
    }
} 