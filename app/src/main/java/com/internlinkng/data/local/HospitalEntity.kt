package com.internlinkng.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.internlinkng.data.model.Hospital
import com.google.gson.Gson

@Entity(tableName = "hospitals")
data class HospitalEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val state: String,
    val professions: String, // Comma-separated
    val salaryRange: String,
    val deadline: String,
    val created: String, // Added created field
    val onlineApplication: Boolean,
    val applicationUrl: String?,
    val physicalAddress: String,
    val isApplied: Boolean = false,
    val professionSalaries: String? = null // NEW FIELD
)

// Mapping function from Hospital model to HospitalEntity
fun Hospital.toEntity(): HospitalEntity {
    return HospitalEntity(
        id = id,
        name = name,
        state = state,
        professions = professions, // Already a string in new model
        salaryRange = salaryRange,
        deadline = deadline,
        created = created, // Map created field
        onlineApplication = onlineApplication,
        applicationUrl = applicationUrl,
        physicalAddress = physicalAddress,
        professionSalaries = professionSalaries // Already a string in new model
    )
}

// Mapping function from HospitalEntity to Hospital model
fun HospitalEntity.toModel(): Hospital {
    return Hospital(
        id = id,
        name = name,
        state = state,
        professions = professions, // Already a string
        salaryRange = salaryRange,
        deadline = deadline,
        created = created, // Map created field
        onlineApplication = onlineApplication,
        applicationUrl = applicationUrl,
        physicalAddress = physicalAddress,
        professionSalaries = professionSalaries // Already a string
    )
} 