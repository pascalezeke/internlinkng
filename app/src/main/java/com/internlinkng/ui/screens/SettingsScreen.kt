package com.internlinkng.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.PasswordVisualTransformation
import com.internlinkng.viewmodel.MainViewModel
import androidx.compose.foundation.clickable
import com.internlinkng.data.model.UserSession
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.border
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.Image
import androidx.compose.ui.platform.LocalContext
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import coil.compose.rememberAsyncImagePainter
import android.net.Uri
import android.util.Base64
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onLogout: () -> Unit = {}, onAdminLogin: () -> Unit = {}, viewModel: MainViewModel? = null) {
    var showAdminDialog by remember { mutableStateOf(false) }
    var adminPassword by remember { mutableStateOf("") }
    var adminError by remember { mutableStateOf<String?>(null) }
    var showEditDialog by remember { mutableStateOf(false) }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Settings",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Profile Section
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showEditDialog = true },
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = "User Account",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "Manage your account settings",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    
                }
            }
            if (showEditDialog) {
                EditUserDialog(
                    viewModel = viewModel,
                    onDismiss = { showEditDialog = false }
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Settings Options
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
            ) {
                Column {
                    // Notifications
                    ListItem(
                        headlineContent = { Text("Notifications") },
                        supportingContent = { Text("Manage notification preferences") },
                        leadingContent = {
                            Icon(
                                Icons.Default.Notifications,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        trailingContent = {
                            Icon(
                                Icons.Default.KeyboardArrowRight,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    )
                    
                    Divider(modifier = Modifier.padding(horizontal = 16.dp))
                    
                    // Privacy
                    ListItem(
                        headlineContent = { Text("Privacy") },
                        supportingContent = { Text("Manage your privacy settings") },
                        leadingContent = {
                            Icon(
                                Icons.Default.Settings,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        trailingContent = {
                            Icon(
                                Icons.Default.KeyboardArrowRight,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    )
                    
                    Divider(modifier = Modifier.padding(horizontal = 16.dp))
                    
                    // About
                    ListItem(
                        headlineContent = { Text("About") },
                        supportingContent = { Text("App version and information") },
                        leadingContent = {
                            Icon(
                                Icons.Default.Info,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        trailingContent = {
                            Icon(
                                Icons.Default.KeyboardArrowRight,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    )
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Admin Login Button
            Button(
                onClick = { showAdminDialog = true },
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                shape = MaterialTheme.shapes.large
            ) {
                Icon(
                    Icons.Default.Settings,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Admin Login",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
                )
            }
            if (showAdminDialog) {
                AlertDialog(
                    onDismissRequest = { showAdminDialog = false; adminPassword = ""; adminError = null },
                    title = { Text("Admin Login") },
                    text = {
                        Column {
                            OutlinedTextField(
                                value = adminPassword,
                                onValueChange = { adminPassword = it },
                                label = { Text("Admin Password") },
                                singleLine = true,
                                visualTransformation = PasswordVisualTransformation(),
                                modifier = Modifier.fillMaxWidth()
                            )
                            if (adminError != null) {
                                Text(adminError!!, color = MaterialTheme.colorScheme.error)
                            }
                        }
                    },
                    confirmButton = {
                        Button(onClick = {
                            if (adminPassword == "Android_Studio1") {
                                showAdminDialog = false
                                adminPassword = ""
                                adminError = null
                                onAdminLogin()
                            } else {
                                adminError = "Incorrect password"
                            }
                        }) {
                            Text("Login")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showAdminDialog = false; adminPassword = ""; adminError = null }) {
                            Text("Cancel")
                        }
                    }
                )
            }
            
            // Logout Button
            Button(
                onClick = onLogout,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                ),
                shape = MaterialTheme.shapes.large
            ) {
                Icon(
                    Icons.Default.ExitToApp,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Logout",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditUserDialog(viewModel: MainViewModel?, onDismiss: () -> Unit) {
    if (viewModel == null) return
    val uiState by viewModel.uiState.collectAsState()
    var firstname by remember { mutableStateOf("") }
    var lastname by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var state by remember { mutableStateOf("") }
    var profession by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var selectedImageUri by remember { mutableStateOf<android.net.Uri?>(null) }
    var isImageLoading by remember { mutableStateOf(false) }
    val professions = viewModel.getAvailableProfessions()
    val states = viewModel.getAvailableStates()
    val context = androidx.compose.ui.platform.LocalContext.current
    var localProfilePicture by remember { mutableStateOf(UserSession.profilePicture) }
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.GetContent(),
        onResult = { uri ->
            selectedImageUri = uri
            isImageLoading = true
            if (uri != null) {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val inputStream = context.contentResolver.openInputStream(uri)
                        val bytes = inputStream?.readBytes() ?: return@launch
                        inputStream?.close()
                        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                        val outputStream = ByteArrayOutputStream()
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
                        val compressedBytes = outputStream.toByteArray()
                        outputStream.close()
                        val base64Image = Base64.encodeToString(compressedBytes, Base64.DEFAULT)
                        withContext(Dispatchers.Main) {
                            localProfilePicture = base64Image
                            isImageLoading = false
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            isImageLoading = false
                        }
                    }
                }
            }
        }
    )
    LaunchedEffect(UserSession.firstname, UserSession.lastname, UserSession.phoneNumber, UserSession.stateOfResidence, UserSession.profession) {
        firstname = UserSession.firstname ?: ""
        lastname = UserSession.lastname ?: ""
        phone = UserSession.phoneNumber ?: ""
        state = UserSession.stateOfResidence ?: ""
        profession = UserSession.profession ?: ""
    }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Account Details") },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Profile Picture Preview
                val currentProfilePicture = remember(localProfilePicture, selectedImageUri) {
                    when {
                        selectedImageUri != null -> null // Will be handled by Coil
                        else -> localProfilePicture?.let {
                            try {
                                val bytes = android.util.Base64.decode(it, android.util.Base64.DEFAULT)
                                val bmp = android.graphics.BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                                bmp.asImageBitmap()
                            } catch (e: Exception) { null }
                        }
                    }
                }

                if (currentProfilePicture != null) {
                    Image(
                        painter = remember { androidx.compose.ui.graphics.painter.BitmapPainter(currentProfilePicture) },
                        contentDescription = "Profile Picture",
                        modifier = Modifier.size(80.dp).clip(CircleShape).border(2.dp, MaterialTheme.colorScheme.primary, CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else if (selectedImageUri != null) {
                    // Show newly selected image or loading indicator
                    if (isImageLoading) {
                        Box(
                            modifier = Modifier.size(80.dp).clip(CircleShape).border(2.dp, MaterialTheme.colorScheme.primary, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    } else {
                        Image(
                            painter = coil.compose.rememberAsyncImagePainter(selectedImageUri),
                            contentDescription = "Selected Profile Picture",
                            modifier = Modifier.size(80.dp).clip(CircleShape).border(2.dp, MaterialTheme.colorScheme.primary, CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    }
                } else {
                    // Show placeholder (no profile picture)
                    Icon(
                        Icons.Default.Person,
                        contentDescription = "No Profile Picture",
                        modifier = Modifier.size(80.dp).clip(CircleShape).border(2.dp, MaterialTheme.colorScheme.primary, CircleShape),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                Row(modifier = Modifier.padding(top = 8.dp)) {
                    Button(onClick = { imagePickerLauncher.launch("image/*") }) {
                        Text("Change")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedButton(onClick = { 
                        selectedImageUri = null // Clear the selected image
                        localProfilePicture = null // Clear from local state for instant UI update
                    }) {
                        Text("Remove")
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = firstname,
                    onValueChange = { firstname = it },
                    label = { Text("First Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = lastname,
                    onValueChange = { lastname = it },
                    label = { Text("Last Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone Number") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                // State Dropdown
                var stateExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = stateExpanded,
                    onExpandedChange = { stateExpanded = !stateExpanded }
                ) {
                    OutlinedTextField(
                        value = state,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("State of Residence") },
                        trailingIcon = { Icon(Icons.Default.ArrowDropDown, null) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = stateExpanded,
                        onDismissRequest = { stateExpanded = false }
                    ) {
                        states.forEach { s ->
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
                Spacer(modifier = Modifier.height(8.dp))
                // Profession Dropdown
                var profExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = profExpanded,
                    onExpandedChange = { profExpanded = !profExpanded }
                ) {
                    OutlinedTextField(
                        value = profession,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Profession") },
                        trailingIcon = { Icon(Icons.Default.ArrowDropDown, null) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = profExpanded,
                        onDismissRequest = { profExpanded = false }
                    ) {
                        professions.forEach { p ->
                            DropdownMenuItem(
                                text = { Text(p) },
                                onClick = {
                                    profession = p
                                    profExpanded = false
                                }
                            )
                        }
                    }
                }
                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(errorMessage!!, color = MaterialTheme.colorScheme.error)
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                UserSession.profilePicture = localProfilePicture // Update UserSession only on save
                viewModel.updateUserDetails(
                    firstname = firstname,
                    lastname = lastname,
                    phoneNumber = phone,
                    stateOfResidence = state,
                    profession = profession,
                    profilePictureUri = selectedImageUri, // Pass the selected image URI
                    context = context,
                    onSuccess = {
                        viewModel.loadUserProfile()
                        onDismiss()
                    },
                    onError = { error -> errorMessage = error }
                )
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
} 