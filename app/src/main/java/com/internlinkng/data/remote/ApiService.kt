package com.internlinkng.data.remote

import com.internlinkng.data.model.Hospital
import com.internlinkng.data.model.LoginRequest
import com.internlinkng.data.model.LoginResponse
import com.internlinkng.data.model.SignupRequest
import com.internlinkng.data.model.SignupResponse
import com.internlinkng.data.model.ApplicationRequest
import com.internlinkng.data.model.ApplicationResponse
import retrofit2.http.*

interface ApiService {
    @GET("hospitals")
    suspend fun getHospitals(): List<Hospital>

    @POST("login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @POST("signup")
    suspend fun signup(@Body request: SignupRequest): SignupResponse

    @GET("profile")
    suspend fun getProfile(): Map<String, Any?>

    @POST("apply")
    suspend fun submitApplication(@Body request: ApplicationRequest): ApplicationResponse

    @POST("admin/hospitals")
    suspend fun addHospital(@Body hospital: Hospital): Hospital

    @PUT("admin/hospitals/{id}")
    suspend fun updateHospital(@Path("id") id: String, @Body hospital: Hospital): Hospital

    @DELETE("admin/hospitals/{id}")
    suspend fun deleteHospital(@Path("id") id: String)

    @PUT("profile")
    suspend fun updateProfile(
        @Header("Authorization") token: String,
        @Body updateRequest: Map<String, String?>
    ): retrofit2.Response<Map<String, String>>
} 