package com.internlinkng.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.internlinkng.data.model.Hospital
import com.internlinkng.viewmodel.MainViewModel
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.layout.ContentScale
import coil.compose.rememberAsyncImagePainter
import android.util.Base64
import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import com.internlinkng.data.model.UserSession
import java.io.ByteArrayInputStream
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Star

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    onHospitalClick: (Hospital) -> Unit,
    onSearchClick: () -> Unit,
    onAppliedClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    val uiState by viewModel.uiState.collectAsState()
    val showFavourites = uiState.showFavouritesOnly

    LaunchedEffect(Unit) {
        viewModel.loadHospitals()
        viewModel.loadAppliedHospitals()
        viewModel.loadUserProfile()
    }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Greeting on the left
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    val userProfile = uiState.userProfile
                    val firstname = userProfile?.firstname ?: ""
                    val lastname = userProfile?.lastname ?: ""
                    Text(
                        text = if (firstname.isNotBlank() || lastname.isNotBlank()) "Hi, $firstname $lastname" else "Hi!",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "Have a great internship search",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                // Message Icon (non-functional)
                IconButton(onClick = { /* Non-functional */ }) {
                    Icon(
                        Icons.Default.Mail,
                        contentDescription = "Messages",
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                // Profile Picture Button (top right only)
                IconButton(onClick = onSettingsClick) {
                    // For now, just show the default person icon
                    // TODO: Implement profile picture from Firebase Storage
                        Icon(
                            Icons.Default.Person,
                            contentDescription = "Profile",
                            modifier = Modifier.size(32.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                }
            }
        },
        bottomBar = {
            androidx.compose.material.Surface(
                elevation = 8.dp,
                color = Color.Transparent
            ) {
                BottomNavigation(
                    backgroundColor = Color.White,
                    contentColor = Color.Black,
                    modifier = Modifier.height(64.dp)
                ) {
                    BottomNavigationItem(
                        icon = {
                            if (selectedTab == 0)
                                Icon(Icons.Filled.Home, contentDescription = "Home", tint = Color.Black)
                            else
                                Icon(Icons.Outlined.Home, contentDescription = "Home", tint = Color.Black.copy(alpha = 0.4f))
                        },
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        label = { Text("Home") },
                        selectedContentColor = Color.Black,
                        unselectedContentColor = Color.Black.copy(alpha = 0.4f)
                    )
                    BottomNavigationItem(
                        icon = {
                            Icon(
                                Icons.Filled.Search,
                                contentDescription = "Search",
                                tint = if (selectedTab == 1) Color.Black else Color.Black.copy(alpha = 0.4f)
                            )
                        },
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        label = { Text("Search") },
                        selectedContentColor = Color.Black,
                        unselectedContentColor = Color.Black.copy(alpha = 0.4f)
                    )
                    BottomNavigationItem(
                        icon = {
                            if (selectedTab == 2)
                                Icon(Icons.Filled.Star, contentDescription = "Favourites", tint = Color.Black)
                            else
                                Icon(Icons.Outlined.Star, contentDescription = "Favourites", tint = Color.Black.copy(alpha = 0.4f))
                        },
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        label = { Text("Favourites") },
                        selectedContentColor = Color.Black,
                        unselectedContentColor = Color.Black.copy(alpha = 0.4f)
                    )
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when (selectedTab) {
                0 -> HospitalListScreen(viewModel, onHospitalClick)
                1 -> SearchScreen(viewModel, onHospitalClick)
                2 -> FavouritesScreen(viewModel, onHospitalClick)
            }
        }
    }
}

@Composable
fun HospitalListScreen(viewModel: MainViewModel, onHospitalClick: (Hospital) -> Unit) {
    val uiState by viewModel.uiState.collectAsState()
    if (uiState.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
    } else if (uiState.filteredHospitals.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    Icons.Default.Info,
                    contentDescription = null,
                    modifier = Modifier.size(80.dp),
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "No hospitals found",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Try adjusting your search or filters.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(uiState.filteredHospitals) { hospital ->
                HospitalCard(
                    hospital = hospital,
                    onClick = { onHospitalClick(hospital) },
                    onMarkApplied = { viewModel.markAsApplied(hospital.id) },
                    onUnmarkApplied = { viewModel.unmarkAsApplied(hospital.id) },
                    isApplied = uiState.appliedHospitals.any { it.id == hospital.id },
                    isFavourite = viewModel.isFavourite(hospital.id),
                    onToggleFavourite = { viewModel.toggleFavourite(hospital.id) }
                )
            }
        }
    }
}

@Composable
fun SearchScreen(viewModel: MainViewModel, onHospitalClick: (Hospital) -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        SearchAndFilterSection(viewModel)
        HospitalListScreen(viewModel, onHospitalClick)
    }
}

@Composable
fun FavouritesScreen(viewModel: MainViewModel, onHospitalClick: (Hospital) -> Unit) {
    val uiState by viewModel.uiState.collectAsState()
    val favouriteHospitals = uiState.filteredHospitals.filter { viewModel.isFavourite(it.id) }
    if (favouriteHospitals.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    Icons.Default.Star,
                    contentDescription = null,
                    modifier = Modifier.size(80.dp),
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "No favourite hospitals",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(favouriteHospitals) { hospital ->
                HospitalCard(
                    hospital = hospital,
                    onClick = { onHospitalClick(hospital) },
                    onMarkApplied = { viewModel.markAsApplied(hospital.id) },
                    onUnmarkApplied = { viewModel.unmarkAsApplied(hospital.id) },
                    isApplied = uiState.appliedHospitals.any { it.id == hospital.id },
                    isFavourite = true,
                    onToggleFavourite = { viewModel.toggleFavourite(hospital.id) }
                )
            }
        }
    }
}

@Composable
fun SearchAndFilterSection(viewModel: MainViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    var showFilters by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Search Bar
        OutlinedTextField(
            value = uiState.searchQuery,
            onValueChange = { viewModel.searchHospitals(it) },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search hospitals, states, or professions...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            trailingIcon = {
                if (uiState.searchQuery.isNotEmpty()) {
                    IconButton(onClick = { viewModel.searchHospitals("") }) {
                        Icon(Icons.Default.Close, contentDescription = "Clear")
                    }
                }
            },
            singleLine = true,
            shape = MaterialTheme.shapes.large
        )
        
        // Filter Toggle
        TextButton(
            onClick = { showFilters = !showFilters },
            modifier = Modifier.align(Alignment.End)
        ) {
            Icon(
                if (showFilters) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(if (showFilters) "Hide Filters" else "Show Filters")
        }
        
        // Filters
        if (showFilters) {
            FilterSection(viewModel)
        }
    }
}

@Composable
fun FilterSection(viewModel: MainViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val professions = viewModel.getAvailableProfessions()
    val states = viewModel.getAvailableStates()
    val salaryRanges = viewModel.getAvailableSalaryRanges()
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
    ) {
        // Profession Filter
        if (professions.isNotEmpty()) {
            Text(
                text = "Profession",
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                item {
                    FilterChip(
                        selected = uiState.selectedProfession.isEmpty(),
                        onClick = { viewModel.filterByProfession("") },
                        label = { Text("All") },
                        shape = MaterialTheme.shapes.medium
                    )
                }
                items(professions) { profession ->
                    FilterChip(
                        selected = uiState.selectedProfession == profession,
                        onClick = { viewModel.filterByProfession(profession) },
                        label = { Text(profession) },
                        shape = MaterialTheme.shapes.medium
                    )
                }
            }
        }
        
        // State Filter
        if (states.isNotEmpty()) {
            Text(
                text = "State",
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                item {
                    FilterChip(
                        selected = uiState.selectedState.isEmpty(),
                        onClick = { viewModel.filterByState("") },
                        label = { Text("All") },
                        shape = MaterialTheme.shapes.medium
                    )
                }
                items(states) { state ->
                    FilterChip(
                        selected = uiState.selectedState == state,
                        onClick = { viewModel.filterByState(state) },
                        label = { Text(state) },
                        shape = MaterialTheme.shapes.medium
                    )
                }
            }
        }
        
        // Salary Range Filter
        if (salaryRanges.isNotEmpty()) {
            Text(
                text = "Salary Range",
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                item {
                    FilterChip(
                        selected = uiState.selectedSalaryRange.isEmpty(),
                        onClick = { viewModel.filterBySalaryRange("") },
                        label = { Text("All") },
                        shape = MaterialTheme.shapes.medium
                    )
                }
                items(salaryRanges) { salaryRange ->
                    FilterChip(
                        selected = uiState.selectedSalaryRange == salaryRange,
                        onClick = { viewModel.filterBySalaryRange(salaryRange) },
                        label = { Text(salaryRange) },
                        shape = MaterialTheme.shapes.medium
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HospitalCard(
    hospital: Hospital,
    onClick: () -> Unit,
    onMarkApplied: () -> Unit,
    onUnmarkApplied: () -> Unit,
    isApplied: Boolean,
    isFavourite: Boolean = false,
    onToggleFavourite: () -> Unit = {}
) {
    ElevatedCard(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .shadow(2.dp, MaterialTheme.shapes.large),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
    ) {
        Box(Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = hospital.name,
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.LocationOn,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = hospital.state,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                // Professions
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    items(hospital.professions.split(",").map { it.trim() }) { profession ->
                        Surface(
                            shape = MaterialTheme.shapes.medium,
                            color = Color(0xFFF2F2F2), // light gray background, tweak as needed
                            tonalElevation = 2.dp,
                            shadowElevation = 0.dp
                        ) {
                            Text(
                                text = profession,
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                color = Color.Black,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                // Salary and Deadline
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Salary: ${hospital.salaryRange}",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Created: ${hospital.created}",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Deadline: ${hospital.deadline}",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                // Application Type
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Description,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (hospital.onlineApplication) "Online Application" else "Physical Application",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            // Star icon at bottom right
            IconButton(
                onClick = onToggleFavourite,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp)
            ) {
                Icon(
                    if (isFavourite) Icons.Filled.Star else Icons.Outlined.Star,
                    contentDescription = if (isFavourite) "Remove from Favourites" else "Add to Favourites",
                    tint = if (isFavourite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
} 