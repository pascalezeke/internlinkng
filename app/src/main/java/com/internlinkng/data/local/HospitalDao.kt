package com.internlinkng.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface HospitalDao {
    @Query("SELECT * FROM hospitals")
    fun getAllHospitals(): List<HospitalEntity>
    
    @Query("SELECT * FROM hospitals WHERE isApplied = 1")
    fun getAppliedHospitals(): List<HospitalEntity>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHospital(hospital: HospitalEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHospitals(hospitals: List<HospitalEntity>)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(hospital: HospitalEntity)
    
    @Update
    suspend fun updateHospital(hospital: HospitalEntity)
    
    @Query("UPDATE hospitals SET isApplied = :isApplied WHERE id = :hospitalId")
    suspend fun updateAppliedStatus(hospitalId: String, isApplied: Boolean)
    
    @Query("UPDATE hospitals SET isApplied = :isApplied WHERE id = :hospitalId")
    suspend fun markAsApplied(hospitalId: String, isApplied: Boolean)
    
    @Delete
    suspend fun deleteHospital(hospital: HospitalEntity)
} 