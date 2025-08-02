package com.internlinkng.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.internlinkng.data.local.HospitalDao
import com.internlinkng.data.local.toEntity
import com.internlinkng.data.local.toModel
import com.internlinkng.data.model.Hospital
import com.internlinkng.data.model.UserSession
import com.internlinkng.data.remote.ApiService
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
    val profilePictureLoaded: Boolean = false
)

class MainViewModel(
    private val apiService: ApiService,
    private val hospitalDao: HospitalDao
) : ViewModel() {

    // Expose apiService for navigation
    val apiServiceInstance: ApiService get() = apiService

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    init {
        checkLoginStatus()
        loadHospitalsFromLocal()
    }

    private fun checkLoginStatus() {
        val isLoggedIn = UserSession.token != null
        val isAdmin = UserSession.isAdmin
        _uiState.value = _uiState.value.copy(
            isLoggedIn = isLoggedIn,
            isAdmin = isAdmin
        )
    }

    fun login(email: String, password: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                android.util.Log.d("MainViewModel", "Attempting login for email: $email")
                val response = apiService.login(com.internlinkng.data.model.LoginRequest(email, password))
                android.util.Log.d("MainViewModel", "Login successful, token received")
                UserSession.token = response.token
                UserSession.userId = response.userId
                UserSession.isAdmin = response.isAdmin
                android.util.Log.d("MainViewModel", "UserSession.isAdmin after login: ${UserSession.isAdmin}")
                
                // Load user profile to get profile picture
                loadUserProfile()
                
                _uiState.value = _uiState.value.copy(
                    isLoggedIn = true,
                    isAdmin = response.isAdmin,
                    isLoading = false
                )
                onSuccess()
            } catch (e: Exception) {
                android.util.Log.e("MainViewModel", "Login failed", e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Login failed: ${e.message ?: "Network error"}"
                )
                onError(e.message ?: "Login failed")
            }
        }
    }

    fun signup(email: String, password: String, firstname: String, lastname: String, phoneNumber: String, stateOfResidence: String, profession: String, profilePictureUri: Uri? = null, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                // Convert image to base64 if provided
                val profilePictureBase64 = profilePictureUri?.let { uri ->
                    withContext(Dispatchers.IO) {
                        try {
                            val context = apiServiceInstance.javaClass.classLoader?.loadClass("android.app.ActivityThread")?.getMethod("currentApplication")?.invoke(null) as? Context
                            context?.let { ctx ->
                                val inputStream = ctx.contentResolver.openInputStream(uri)
                                val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                                    val source = ImageDecoder.createSource(ctx.contentResolver, uri)
                                    ImageDecoder.decodeBitmap(source)
                                } else {
                                    BitmapFactory.decodeStream(inputStream)
                                }
                                val outputStream = ByteArrayOutputStream()
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
                                val byteArray = outputStream.toByteArray()
                                Base64.encodeToString(byteArray, Base64.DEFAULT)
                            } ?: ""
                        } catch (e: Exception) {
                            android.util.Log.e("MainViewModel", "Error converting image to base64", e)
                            ""
                        }
                    }
                }
                
                val response = apiService.signup(com.internlinkng.data.model.SignupRequest(email, password, firstname, lastname, phoneNumber, stateOfResidence, profession, profilePictureBase64))
                UserSession.token = response.token
                UserSession.userId = response.userId
                UserSession.isAdmin = false
                
                // Store the profile picture in UserSession
                UserSession.profilePicture = profilePictureBase64
                
                _uiState.value = _uiState.value.copy(
                    isLoggedIn = true,
                    isAdmin = false,
                    isLoading = false
                )
                onSuccess()
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
        UserSession.token = null
        UserSession.userId = null
        UserSession.isAdmin = false
        _uiState.value = _uiState.value.copy(
            isLoggedIn = false,
            isAdmin = false
        )
    }

    fun loadHospitals() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                android.util.Log.d("MainViewModel", "Attempting to load hospitals from API...")
                val hospitals = apiService.getHospitals()
                android.util.Log.d("MainViewModel", "Successfully loaded ${hospitals.size} hospitals from API")
                
                _uiState.value = _uiState.value.copy(
                    hospitals = hospitals,
                    filteredHospitals = hospitals,
                    isLoading = false
                )
                // Save to local database
                hospitals.forEach { hospital ->
                    hospitalDao.insertOrUpdate(hospital.toEntity())
                }
                android.util.Log.d("MainViewModel", "Hospitals saved to local database")
            } catch (e: Exception) {
                android.util.Log.e("MainViewModel", "Failed to load hospitals from API", e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Network error: ${e.message ?: "Failed to load hospitals"}"
                )
            }
        }
    }

    private fun loadHospitalsFromLocal() {
        viewModelScope.launch {
            try {
                val hospitalEntities = hospitalDao.getAllHospitals()
                val hospitals = hospitalEntities.map { it.toModel() }
                _uiState.value = _uiState.value.copy(
                    hospitals = hospitals,
                    filteredHospitals = hospitals
                )
            } catch (e: Exception) {
                // Handle local loading error
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
        applyFilters() // update filtered list if needed
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
        viewModelScope.launch {
            try {
                hospitalDao.markAsApplied(hospitalId, true)
                loadAppliedHospitals()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Failed to mark hospital as applied"
                )
            }
        }
    }

    fun unmarkAsApplied(hospitalId: String) {
        viewModelScope.launch {
            try {
                hospitalDao.markAsApplied(hospitalId, false)
                loadAppliedHospitals()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Failed to unmark hospital"
                )
            }
        }
    }

    fun loadAppliedHospitals() {
        viewModelScope.launch {
            try {
                val appliedEntities = hospitalDao.getAppliedHospitals()
                val appliedHospitals = appliedEntities.map { it.toModel() }
                _uiState.value = _uiState.value.copy(appliedHospitals = appliedHospitals)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun submitApplication(
        applicationRequest: com.internlinkng.data.model.ApplicationRequest,
        onResult: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            try {
                apiService.submitApplication(applicationRequest)
                onResult(true)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Failed to submit application"
                )
                onResult(false)
            }
        }
    }

    fun addHospital(
        hospital: Hospital,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                apiService.addHospital(hospital)
                loadHospitals()
                _uiState.value = _uiState.value.copy(isLoading = false)
                onSuccess()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
                onError(e.message ?: "Failed to add hospital")
            }
        }
    }

    fun updateHospital(
        hospital: Hospital,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                apiService.updateHospital(hospital.id, hospital)
                loadHospitals()
                _uiState.value = _uiState.value.copy(isLoading = false)
                onSuccess()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
                onError(e.message ?: "Failed to update hospital")
            }
        }
    }

    fun refreshProfilePicture() {
        viewModelScope.launch {
            try {
                android.util.Log.d("MainViewModel", "Refreshing profile picture...")
                val profileData = apiService.getProfile()
                val profilePicture = profileData["profilePicture"] as? String
                UserSession.profilePicture = profilePicture
                _uiState.value = _uiState.value.copy(profilePictureLoaded = true)
                android.util.Log.d("MainViewModel", "Profile picture refreshed: ${UserSession.profilePicture != null}")
            } catch (e: Exception) {
                android.util.Log.e("MainViewModel", "Failed to refresh profile picture", e)
            }
        }
    }

    fun loadUserProfile() {
        viewModelScope.launch {
            try {
                android.util.Log.d("MainViewModel", "Loading user profile...")
                val profileData = apiService.getProfile()
                android.util.Log.d("MainViewModel", "Profile data received: $profileData")
                UserSession.firstname = profileData["firstname"] as? String
                UserSession.lastname = profileData["lastname"] as? String
                UserSession.phoneNumber = profileData["phoneNumber"] as? String
                UserSession.stateOfResidence = profileData["stateOfResidence"] as? String
                UserSession.profession = profileData["profession"] as? String
                val profilePicture = profileData["profilePicture"] as? String
                android.util.Log.d("MainViewModel", "Profile picture: ${if (profilePicture != null) "Found" else "null"}")
                UserSession.profilePicture = profilePicture
                android.util.Log.d("MainViewModel", "Profile picture stored in UserSession: ${UserSession.profilePicture != null}")
                _uiState.value = _uiState.value.copy(profilePictureLoaded = true)
            } catch (e: Exception) {
                android.util.Log.e("MainViewModel", "Failed to load user profile", e)
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
        return _uiState.value.hospitals
            .map { it.state }
            .distinct()
            .sorted()
    }

    fun getAvailableSalaryRanges(): List<String> {
        return _uiState.value.hospitals
            .map { it.salaryRange }
            .distinct()
            .sorted()
    }

    fun updateUserDetails(
        firstname: String,
        lastname: String,
        phoneNumber: String,
        stateOfResidence: String,
        profession: String,
        profilePictureUri: android.net.Uri? = null,
        context: android.content.Context? = null,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val token = UserSession.token ?: throw Exception("Not logged in")
                val updateRequest = mutableMapOf<String, String?>(
                    "firstname" to firstname,
                    "lastname" to lastname,
                    "phoneNumber" to phoneNumber,
                    "stateOfResidence" to stateOfResidence,
                    "profession" to profession
                )
                
                // Handle profile picture
                if (profilePictureUri != null) {
                    // User selected a new image, convert it to Base64
                    val base64Image = convertUriToBase64(profilePictureUri, context)
                    updateRequest["profilePicture"] = base64Image
                } else if (UserSession.profilePicture == null) {
                    // User wants to remove profile picture (send empty string to backend)
                    updateRequest["profilePicture"] = ""
                }
                // If UserSession.profilePicture is not null, keep existing picture
                
                val response = apiService.updateProfile("Bearer $token", updateRequest)
                if (response.isSuccessful) {
                    // Update local UserSession
                    UserSession.firstname = firstname
                    UserSession.lastname = lastname
                    UserSession.phoneNumber = phoneNumber
                    UserSession.stateOfResidence = stateOfResidence
                    UserSession.profession = profession
                    
                    // Update profile picture in UserSession
                    if (profilePictureUri != null) {
                        val base64Image = convertUriToBase64(profilePictureUri, context)
                        UserSession.profilePicture = base64Image
                    }
                    // UserSession.profilePicture is already updated by the UI, so no need to change it here
                    
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = null
                    )
                    onSuccess()
                } else {
                    throw Exception(response.errorBody()?.string() ?: "Failed to update profile")
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
                onError(e.message ?: "Failed to update user details")
            }
        }
    }

    private suspend fun convertUriToBase64(uri: android.net.Uri, context: android.content.Context?): String {
        return withContext(Dispatchers.IO) {
            try {
                val ctx = context ?: throw Exception("Context is required for image processing")
                val inputStream = ctx.contentResolver.openInputStream(uri)
                val bytes = inputStream?.readBytes() ?: throw Exception("Failed to read image")
                inputStream?.close()
                
                // Decode and re-encode to ensure consistent format
                val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                val outputStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
                val compressedBytes = outputStream.toByteArray()
                outputStream.close()
                
                Base64.encodeToString(compressedBytes, Base64.DEFAULT)
            } catch (e: Exception) {
                throw Exception("Failed to process image: ${e.message}")
            }
        }
    }
} 