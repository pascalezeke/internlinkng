package com.internlinkng.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.internlinkng.data.model.Hospital
import com.internlinkng.viewmodel.MainViewModel
import androidx.compose.foundation.clickable
import androidx.compose.ui.platform.LocalContext
import android.content.Intent
import android.net.Uri

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HospitalDetailsScreen(
    hospital: Hospital,
    viewModel: MainViewModel,
    onBackClick: () -> Unit
) {
    var showApplicationDialog by remember { mutableStateOf(false) }
    var showCourierDialog by remember { mutableStateOf(false) }
    var showProfessionsDialog by remember { mutableStateOf(false) }
    var showSalaryDialog by remember { mutableStateOf(false) }
    val professionsList = hospital.professions.split(",").map { it.trim() }
    val professionsPreview = if (professionsList.size > 2) {
        professionsList.take(2).joinToString(", ") + ",..."
    } else {
        professionsList.joinToString(", ")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = hospital.name,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        maxLines = 1
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Hospital Header Card
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = hospital.name,
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = hospital.state,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Key Information Grid
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        InfoCard(
                            icon = Icons.Default.Person,
                            title = "Professions",
                            value = professionsPreview,
                            modifier = Modifier.weight(1f)
                            .let { mod ->
                                if (professionsList.size > 2) mod.clickable { showProfessionsDialog = true } else mod
                            }
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        InfoCard(
                            icon = Icons.Default.Star,
                            title = "Salary",
                            value = hospital.salaryRange,
                            modifier = Modifier.weight(1f)
                            .let { mod ->
                                if (!hospital.professionSalaries.isNullOrEmpty()) mod.clickable { showSalaryDialog = true } else mod
                            }
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Center the second row as a group
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        InfoCard(
                            icon = Icons.Default.DateRange,
                            title = "Deadline",
                            value = hospital.deadline,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        InfoCard(
                            icon = Icons.Default.Description,
                            title = "Application",
                            value = if (hospital.onlineApplication) "Online" else "Physical",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
            
            // Action Buttons
            Column(
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                if (hospital.onlineApplication) {
                    val context = LocalContext.current
                    Button(
                        onClick = {
                            hospital.applicationUrl?.let { url ->
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                context.startActivity(intent)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        shape = MaterialTheme.shapes.large,
                        enabled = !hospital.applicationUrl.isNullOrBlank()
                    ) {
                        Icon(
                            Icons.Default.Send,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Apply Online",
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
                        )
                    }
                } else {
                    val context = LocalContext.current
                    Button(
                        onClick = {
                            hospital.physicalAddress?.let { address ->
                                try {
                                    // Check if it's a valid URL
                                    val uri = Uri.parse(address)
                                    if (uri.scheme != null && (uri.scheme == "http" || uri.scheme == "https")) {
                                        // It's a valid URL, open in browser
                                        val intent = Intent(Intent.ACTION_VIEW, uri)
                                        context.startActivity(intent)
                                    } else {
                                        // It's a text address, open in maps
                                        val mapsUri = Uri.parse("geo:0,0?q=${Uri.encode(address)}")
                                        val intent = Intent(Intent.ACTION_VIEW, mapsUri)
                                        intent.setPackage("com.google.android.apps.maps")
                                        if (intent.resolveActivity(context.packageManager) != null) {
                                            context.startActivity(intent)
                                        } else {
                                            // Fallback to any maps app
                                            val fallbackIntent = Intent(Intent.ACTION_VIEW, mapsUri)
                                            context.startActivity(fallbackIntent)
                                        }
                                    }
                                } catch (e: Exception) {
                                    // Handle invalid URI
                                    val mapsUri = Uri.parse("geo:0,0?q=${Uri.encode(address)}")
                                    val intent = Intent(Intent.ACTION_VIEW, mapsUri)
                                    intent.setPackage("com.google.android.apps.maps")
                                    if (intent.resolveActivity(context.packageManager) != null) {
                                        context.startActivity(intent)
                                    } else {
                                        val fallbackIntent = Intent(Intent.ACTION_VIEW, mapsUri)
                                        context.startActivity(fallbackIntent)
                                    }
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary
                        ),
                        shape = MaterialTheme.shapes.large,
                        enabled = !hospital.physicalAddress.isNullOrBlank()
                    ) {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Arrange Courier Service",
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                OutlinedButton(
                    onClick = { /* TODO: Share hospital details */ },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.large
                ) {
                    Icon(
                        Icons.Default.Share,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Share Details",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
    
    // Application Dialog
    if (showApplicationDialog) {
        ApplicationDialog(
            hospital = hospital,
            onDismiss = { showApplicationDialog = false },
            onSubmit = { profession, coverLetter ->
                viewModel.submitApplication(
                    com.internlinkng.data.model.ApplicationRequest(
                        userId = "user-123", // TODO: Get actual user ID
                        hospitalId = hospital.id,
                        profession = profession,
                        coverLetter = coverLetter
                    )
                ) { success ->
                    if (success) {
                        showApplicationDialog = false
                    }
                }
            }
        )
    }
    
    // Courier Dialog
    if (showCourierDialog) {
        CourierDialog(
            hospital = hospital,
            onDismiss = { showCourierDialog = false },
            onConfirm = { 
                showCourierDialog = false
                // TODO: Implement courier service
            }
        )
    }

    if (showProfessionsDialog) {
        AlertDialog(
            onDismissRequest = { showProfessionsDialog = false },
            title = { Text("All Professions") },
            text = {
                Column {
                    professionsList.forEach {
                        Text(it)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showProfessionsDialog = false }) {
                    Text("Close")
                }
            }
        )
    }

    if (showSalaryDialog) {
        AlertDialog(
            onDismissRequest = { showSalaryDialog = false },
            title = { Text("Professions & Salaries") },
            text = {
                Column {
                    hospital.professionSalaries?.forEach { (profession, salary) ->
                        Text("$profession: $salary")
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showSalaryDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
fun InfoCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                maxLines = 2
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApplicationDialog(
    hospital: Hospital,
    onDismiss: () -> Unit,
    onSubmit: (String, String) -> Unit
) {
    var profession by remember { mutableStateOf("") }
    var coverLetter by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Apply to ${hospital.name}",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column {
                OutlinedTextField(
                    value = profession,
                    onValueChange = { profession = it },
                    label = { Text("Your Profession") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = coverLetter,
                    onValueChange = { coverLetter = it },
                    label = { Text("Cover Letter (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSubmit(profession, coverLetter) },
                enabled = profession.isNotBlank()
            ) {
                Text("Submit Application")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourierDialog(
    hospital: Hospital,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Courier Service",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Text(
                text = "This hospital requires physical application submission. Would you like to arrange a courier service to deliver your application documents?",
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Arrange Courier")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
} 