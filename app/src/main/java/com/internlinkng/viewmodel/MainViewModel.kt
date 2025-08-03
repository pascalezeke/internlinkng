package com.internlinkng.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.internlinkng.data.model.Hospital
import com.internlinkng.utils.NetworkUtils
import com.internlinkng.utils.AuthResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import android.widget.Toast
import androidx.lifecycle.viewModelScope
import androidx.compose.ui.platform.LocalContext
import android.net.Uri
import android.content.Context
import android.util.Base64
import java.io.ByteArrayOutputStream
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.os.Build
import androidx.core.graphics.drawable.toBitmap
import coil.ImageLoader
import coil.request.ImageRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.auth.ktx.auth
import android.util.Log

data class MainUiState(
    val isLoading: Boolean = false,
    val hospitals: List<Hospital> = emptyList(),
    val filteredHospitals: List<Hospital> = emptyList(),
    val appliedHospitals: List<Hospital> = emptyList(),
    val favouriteHospitalIds: Set<String> = emptySet(),
    val showFavouritesOnly: Boolean = false,
    val error: String? = null,
    val isLoggedIn: Boolean = false,
    val isAdmin: Boolean = false,
    val searchQuery: String = "",
    val selectedProfession: String = "",
    val selectedState: String = "",
    val selectedSalaryRange: String = "",
    val profilePictureLoaded: Boolean = false,
    val currentUser: com.google.firebase.auth.FirebaseUser? = null,
    val userProfile: UserProfile? = null,
    val adminEmail: String = "admin@internlinkng.com",
    val adminPassword: String = "Admin123!"
)

data class UserProfile(
    val email: String,
    val firstname: String,
    val lastname: String,
    val phoneNumber: String,
    val stateOfResidence: String,
    val profession: String,
    val createdAt: com.google.firebase.Timestamp? = null
)

class MainViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    init {
        checkLoginStatus()
        loadHospitalsFromFirebase()
        // Test global connectivity
        viewModelScope.launch {
            val connectivityResults = NetworkUtils.testGlobalConnectivity()
            val firebaseConnected = connectivityResults["firebase"] ?: false
            val internetConnected = connectivityResults["internet"] ?: false
            
            if (!firebaseConnected) {
                if (!internetConnected) {
                    _uiState.value = _uiState.value.copy(
                        error = "No internet connection. Please check your network settings."
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        error = "Firebase connection failed. Please try again later."
                    )
                }
            }
            
            Log.d("MainViewModel", "Connectivity test - Firebase: $firebaseConnected, Internet: $internetConnected")
        }
    }

    private fun checkLoginStatus() {
        val currentUser = auth.currentUser
        val isLoggedIn = currentUser != null
        _uiState.value = _uiState.value.copy(
            isLoggedIn = isLoggedIn,
            currentUser = currentUser
        )
    }

    fun login(email: String, password: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val result = NetworkUtils.login(email, password)
                when (result) {
                    is AuthResult.Success -> {
                        val isAdmin = email == _uiState.value.adminEmail
                _uiState.value = _uiState.value.copy(
                    isLoggedIn = true,
                            currentUser = result.user,
                            isAdmin = isAdmin,
                    isLoading = false
                )
                onSuccess()
                    }
                    is AuthResult.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = result.message
                        )
                        onError(result.message)
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Login failed"
                )
                onError(e.message ?: "Login failed")
            }
        }
    }

    fun signup(email: String, password: String, firstname: String, lastname: String, phoneNumber: String, stateOfResidence: String, profession: String, profilePictureUri: Uri? = null, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val result = NetworkUtils.signUp(email, password)
                when (result) {
                    is AuthResult.Success -> {
                        // Create user profile in Firestore
                        val userData = mapOf(
                            "email" to email,
                            "firstname" to firstname,
                            "lastname" to lastname,
                            "phoneNumber" to phoneNumber,
                            "stateOfResidence" to stateOfResidence,
                            "profession" to profession,
                            "createdAt" to com.google.firebase.Timestamp.now()
                        )
                        
                        result.user?.let { user ->
                            firestore.collection("users")
                                .document(user.uid)
                                .set(userData)
                                .addOnSuccessListener {
                                    // Success
                                }
                                .addOnFailureListener { e ->
                                    // Handle error
                                }
                        }
                
                _uiState.value = _uiState.value.copy(
                    isLoggedIn = true,
                            currentUser = result.user,
                    isLoading = false
                )
                onSuccess()
                    }
                    is AuthResult.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = result.message
                        )
                        onError(result.message)
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Signup failed"
                )
                onError(e.message ?: "Signup failed")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            try {
                NetworkUtils.logout()
        _uiState.value = _uiState.value.copy(
            isLoggedIn = false,
                    currentUser = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Logout failed"
        )
            }
        }
    }

    fun loadHospitals() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val hospitals = NetworkUtils.getHospitals()
                _uiState.value = _uiState.value.copy(
                    hospitals = hospitals,
                    filteredHospitals = hospitals,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Network error: ${e.message ?: "Failed to load hospitals"}"
                )
            }
        }
    }

    private fun loadHospitalsFromFirebase() {
        viewModelScope.launch {
            try {
                val hospitals = NetworkUtils.getHospitalsWithFallback()
                Log.d("MainViewModel", "Loaded ${hospitals.size} hospitals from Firebase")
                _uiState.value = _uiState.value.copy(
                    hospitals = hospitals,
                    filteredHospitals = hospitals
                )
                if (hospitals.isEmpty()) {
                    _uiState.value = _uiState.value.copy(
                        error = "No hospitals available. Please check your internet connection or try again later."
                    )
                }
            } catch (e: Exception) {
                Log.e("MainViewModel", "Failed to load hospitals", e)
                _uiState.value = _uiState.value.copy(
                    error = "Unable to load hospitals. Please check your internet connection."
                )
            }
        }
    }

    fun searchHospitals(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        applyFilters()
    }

    fun filterByProfession(profession: String) {
        _uiState.value = _uiState.value.copy(selectedProfession = profession)
        applyFilters()
    }

    fun filterByState(state: String) {
        _uiState.value = _uiState.value.copy(selectedState = state)
        applyFilters()
    }

    fun filterBySalaryRange(salaryRange: String) {
        _uiState.value = _uiState.value.copy(selectedSalaryRange = salaryRange)
        applyFilters()
    }

    fun toggleFavourite(hospitalId: String) {
        val current = _uiState.value.favouriteHospitalIds
        _uiState.value = _uiState.value.copy(
            favouriteHospitalIds = if (current.contains(hospitalId)) current - hospitalId else current + hospitalId
        )
        applyFilters()
    }
    
    fun isFavourite(hospitalId: String): Boolean = _uiState.value.favouriteHospitalIds.contains(hospitalId)
    
    fun toggleShowFavourites() {
        _uiState.value = _uiState.value.copy(showFavouritesOnly = !_uiState.value.showFavouritesOnly)
        applyFilters()
    }

    private fun applyFilters() {
        val currentState = _uiState.value
        var filtered = currentState.hospitals

        // Apply search query
        if (currentState.searchQuery.isNotEmpty()) {
            filtered = filtered.filter { hospital ->
                hospital.name.contains(currentState.searchQuery, ignoreCase = true) ||
                hospital.state.contains(currentState.searchQuery, ignoreCase = true) ||
                hospital.professions.contains(currentState.searchQuery, ignoreCase = true)
            }
        }

        // Apply profession filter
        if (currentState.selectedProfession.isNotEmpty()) {
            filtered = filtered.filter { hospital ->
                hospital.professions.contains(currentState.selectedProfession)
            }
        }

        // Apply state filter
        if (currentState.selectedState.isNotEmpty()) {
            filtered = filtered.filter { hospital ->
                hospital.state == currentState.selectedState
            }
        }

        // Apply salary range filter
        if (currentState.selectedSalaryRange.isNotEmpty()) {
            filtered = filtered.filter { hospital ->
                hospital.salaryRange.contains(currentState.selectedSalaryRange)
            }
        }

        // Favourites filter
        if (currentState.showFavouritesOnly) {
            filtered = filtered.filter { currentState.favouriteHospitalIds.contains(it.id) }
        }

        _uiState.value = currentState.copy(filteredHospitals = filtered)
    }

    fun markAsApplied(hospitalId: String) {
        // TODO: Implement with Firebase
        viewModelScope.launch {
            try {
                // For now, just update the UI state
                val currentApplied = _uiState.value.appliedHospitals.toMutableList()
                val hospital = _uiState.value.hospitals.find { it.id == hospitalId }
                if (hospital != null && !currentApplied.contains(hospital)) {
                    currentApplied.add(hospital)
                    _uiState.value = _uiState.value.copy(appliedHospitals = currentApplied)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Failed to mark hospital as applied"
                )
            }
        }
    }

    fun unmarkAsApplied(hospitalId: String) {
        // TODO: Implement with Firebase
        viewModelScope.launch {
            try {
                val currentApplied = _uiState.value.appliedHospitals.toMutableList()
                currentApplied.removeAll { it.id == hospitalId }
                _uiState.value = _uiState.value.copy(appliedHospitals = currentApplied)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Failed to unmark hospital"
                )
            }
        }
    }

    fun loadAppliedHospitals() {
        // TODO: Implement with Firebase
        // For now, this is handled in markAsApplied/unmarkAsApplied
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun loadUserProfile() {
        viewModelScope.launch {
            try {
                val currentUser = auth.currentUser
                if (currentUser != null) {
                    firestore.collection("users")
                        .document(currentUser.uid)
                        .get()
                        .addOnSuccessListener { document ->
                            if (document.exists()) {
                                val userData = document.data
                                userData?.let { data ->
                                    val userProfile = UserProfile(
                                        email = data["email"] as? String ?: "",
                                        firstname = data["firstname"] as? String ?: "",
                                        lastname = data["lastname"] as? String ?: "",
                                        phoneNumber = data["phoneNumber"] as? String ?: "",
                                        stateOfResidence = data["stateOfResidence"] as? String ?: "",
                                        profession = data["profession"] as? String ?: "",
                                        createdAt = data["createdAt"] as? com.google.firebase.Timestamp
                                    )
                _uiState.value = _uiState.value.copy(
                                        userProfile = userProfile,
                                        profilePictureLoaded = true
                                    )
            }
        }
    }
                        .addOnFailureListener { e ->
                            // Handle error
                        }
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun updateUserProfile(
        firstname: String,
        lastname: String,
        phoneNumber: String,
        stateOfResidence: String,
        profession: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val currentUser = auth.currentUser
                if (currentUser != null) {
                    val userData = mapOf(
                        "firstname" to firstname,
                        "lastname" to lastname,
                        "phoneNumber" to phoneNumber,
                        "stateOfResidence" to stateOfResidence,
                        "profession" to profession,
                        "updatedAt" to com.google.firebase.Timestamp.now()
                    )
                    
                    firestore.collection("users")
                        .document(currentUser.uid)
                        .update(userData)
                        .addOnSuccessListener {
                            // Reload user profile to update UI state
                            loadUserProfile()
                onSuccess()
                        }
                        .addOnFailureListener { e ->
                            onError(e.message ?: "Failed to update profile")
            }
                } else {
                    onError("User not logged in")
                }
            } catch (e: Exception) {
                onError(e.message ?: "Failed to update profile")
            }
        }
    }

    fun getAvailableProfessions(): List<String> {
        return _uiState.value.hospitals
            .flatMap { it.professions.split(",").map { profession -> profession.trim() } }
            .distinct()
            .sorted()
    }

    fun getAvailableStates(): List<String> {
        // Return all Nigerian states instead of just states from hospitals data
        return listOf(
            "Abia", "Adamawa", "Akwa Ibom", "Anambra", "Bauchi", "Bayelsa", 
            "Benue", "Borno", "Cross River", "Delta", "Ebonyi", "Edo", 
            "Ekiti", "Enugu", "Gombe", "Imo", "Jigawa", "Kaduna", "Kano", 
            "Katsina", "Kebbi", "Kogi", "Kwara", "Lagos", "Nasarawa", 
            "Niger", "Ogun", "Ondo", "Osun", "Oyo", "Plateau", "Rivers", 
            "Sokoto", "Taraba", "Yobe", "Zamfara", "FCT Abuja"
        ).sorted()
    }

    fun getAvailableSalaryRanges(): List<String> {
        return _uiState.value.hospitals
            .map { it.salaryRange }
            .distinct()
            .sorted()
    }

    // Admin Functions
    fun addHospital(
        name: String,
        state: String,
        professions: String,
        salaryRange: String,
        deadline: String,
        created: String,
        onlineApplication: Boolean,
        applicationUrl: String?,
        physicalAddress: String,
        professionSalaries: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                if (!_uiState.value.isAdmin) {
                    onError("Admin access required")
                    return@launch
                }
                
                val hospitalData = mapOf(
                    "name" to name,
                    "state" to state,
                    "professions" to professions,
                    "salary_range" to salaryRange,
                    "deadline" to deadline,
                    "created" to created,
                    "online_application" to onlineApplication,
                    "application_url" to applicationUrl,
                    "physical_address" to physicalAddress,
                    "profession_salaries" to professionSalaries,
                    "created_at" to com.google.firebase.Timestamp.now(),
                    "updated_at" to com.google.firebase.Timestamp.now()
                )
                
                firestore.collection("hospitals")
                    .add(hospitalData)
                    .addOnSuccessListener { documentReference ->
                        // Reload hospitals
                        loadHospitalsFromFirebase()
                        onSuccess()
                    }
                    .addOnFailureListener { e ->
                        onError(e.message ?: "Failed to add hospital")
                    }
            } catch (e: Exception) {
                onError(e.message ?: "Failed to add hospital")
            }
        }
    }
    
    fun updateHospital(
        hospitalId: String,
        name: String,
        state: String,
        professions: String,
        salaryRange: String,
        deadline: String,
        created: String,
        onlineApplication: Boolean,
        applicationUrl: String?,
        physicalAddress: String,
        professionSalaries: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                if (!_uiState.value.isAdmin) {
                    onError("Admin access required")
                    return@launch
                }
                
                val hospitalData = mapOf(
                    "name" to name,
                    "state" to state,
                    "professions" to professions,
                    "salary_range" to salaryRange,
                    "deadline" to deadline,
                    "created" to created,
                    "online_application" to onlineApplication,
                    "application_url" to applicationUrl,
                    "physical_address" to physicalAddress,
                    "profession_salaries" to professionSalaries,
                    "updated_at" to com.google.firebase.Timestamp.now()
                )
                
                firestore.collection("hospitals")
                    .document(hospitalId)
                    .update(hospitalData)
                    .addOnSuccessListener {
                        Log.d("MainViewModel", "Hospital updated successfully, reloading hospitals...")
                        // Reload hospitals
                        loadHospitalsFromFirebase()
                        onSuccess()
                    }
                    .addOnFailureListener { e ->
                        Log.e("MainViewModel", "Failed to update hospital", e)
                        onError(e.message ?: "Failed to update hospital")
                    }
            } catch (e: Exception) {
                onError(e.message ?: "Failed to update hospital")
            }
        }
    }
    
    fun deleteHospital(
        hospitalId: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                if (!_uiState.value.isAdmin) {
                    onError("Admin access required")
                    return@launch
                }
                
                firestore.collection("hospitals")
                    .document(hospitalId)
                    .delete()
                    .addOnSuccessListener {
                        // Reload hospitals
                        loadHospitalsFromFirebase()
                    onSuccess()
                    }
                    .addOnFailureListener { e ->
                        onError(e.message ?: "Failed to delete hospital")
                }
            } catch (e: Exception) {
                onError(e.message ?: "Failed to delete hospital")
            }
        }
    }

    fun getAllUsers(
        onSuccess: (List<UserProfile>) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                if (!_uiState.value.isAdmin) {
                    onError("Admin access required")
                    return@launch
                }
                
                firestore.collection("users")
                    .get()
                    .addOnSuccessListener { documents ->
                        val users = documents.mapNotNull { doc ->
            try {
                                val data = doc.data
                                UserProfile(
                                    email = data["email"] as? String ?: "",
                                    firstname = data["firstname"] as? String ?: "",
                                    lastname = data["lastname"] as? String ?: "",
                                    phoneNumber = data["phoneNumber"] as? String ?: "",
                                    stateOfResidence = data["stateOfResidence"] as? String ?: "",
                                    profession = data["profession"] as? String ?: "",
                                    createdAt = data["createdAt"] as? com.google.firebase.Timestamp
                                )
                            } catch (e: Exception) {
                                null
                            }
                        }
                        onSuccess(users)
                    }
                    .addOnFailureListener { e ->
                        onError(e.message ?: "Failed to get users")
                    }
            } catch (e: Exception) {
                onError(e.message ?: "Failed to get users")
            }
        }
    }
    
    fun getHospitalById(
        hospitalId: String,
        onSuccess: (Hospital) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                firestore.collection("hospitals")
                    .document(hospitalId)
                    .get()
                    .addOnSuccessListener { document ->
                        if (document.exists()) {
                            try {
                                val data = document.data
                                data?.let { hospitalData ->
                                    val hospital = Hospital(
                                        id = document.id,
                                        name = hospitalData["name"] as? String ?: "",
                                        state = hospitalData["state"] as? String ?: "",
                                        professions = hospitalData["professions"] as? String ?: "",
                                        salaryRange = hospitalData["salary_range"] as? String ?: "",
                                        deadline = hospitalData["deadline"] as? String ?: "",
                                        created = hospitalData["created"] as? String ?: "",
                                        onlineApplication = hospitalData["online_application"] as? Boolean ?: false,
                                        applicationUrl = hospitalData["application_url"] as? String,
                                        physicalAddress = hospitalData["physical_address"] as? String ?: "",
                                        professionSalaries = hospitalData["profession_salaries"] as? String ?: ""
                                    )
                                    onSuccess(hospital)
                                }
                            } catch (e: Exception) {
                                onError("Failed to parse hospital data: ${e.message}")
                            }
                        } else {
                            onError("Hospital not found")
                        }
                    }
                    .addOnFailureListener { e ->
                        onError(e.message ?: "Failed to get hospital")
                    }
            } catch (e: Exception) {
                onError(e.message ?: "Failed to get hospital")
            }
        }
    }
} 