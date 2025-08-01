package com.internlinkng.data.repository

import com.internlinkng.data.local.HospitalDao
import com.internlinkng.data.local.HospitalEntity
import com.internlinkng.data.local.toEntity
import com.internlinkng.data.local.toModel
import com.internlinkng.data.model.Hospital
import com.internlinkng.data.remote.ApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class HospitalRepository(
    private val apiService: ApiService,
    private val hospitalDao: HospitalDao
) {
    
    fun getHospitals(): Flow<List<Hospital>> = flow {
        try {
            // First emit cached data
            val cachedHospitals = hospitalDao.getAllHospitals().map { it.toModel() }
            emit(cachedHospitals)
            
            // Then fetch from network
            val networkHospitals = apiService.getHospitals()
            
            // Save to local database
            networkHospitals.forEach { hospital ->
                hospitalDao.insertOrUpdate(hospital.toEntity())
            }
            
            // Emit updated data
            emit(networkHospitals)
        } catch (e: Exception) {
            // If network fails, emit cached data
            val cachedHospitals = hospitalDao.getAllHospitals().map { it.toModel() }
            emit(cachedHospitals)
            throw e
        }
    }
    
    suspend fun getAppliedHospitals(): List<Hospital> {
        return hospitalDao.getAppliedHospitals().map { it.toModel() }
    }
    
    suspend fun markAsApplied(hospitalId: String, isApplied: Boolean) {
        hospitalDao.markAsApplied(hospitalId, isApplied)
    }
    
    suspend fun submitApplication(applicationRequest: com.internlinkng.data.model.ApplicationRequest): com.internlinkng.data.model.ApplicationResponse {
        return apiService.submitApplication(applicationRequest)
    }
    
    suspend fun addHospital(hospital: Hospital): Hospital {
        val response = apiService.addHospital(hospital)
        hospitalDao.insertOrUpdate(response.toEntity())
        return response
    }
    
    suspend fun updateHospital(id: String, hospital: Hospital): Hospital {
        val response = apiService.updateHospital(id, hospital)
        hospitalDao.insertOrUpdate(response.toEntity())
        return response
    }
    
    suspend fun deleteHospital(id: String) {
        apiService.deleteHospital(id)
        // Note: You might want to remove from local DB as well
    }
} 