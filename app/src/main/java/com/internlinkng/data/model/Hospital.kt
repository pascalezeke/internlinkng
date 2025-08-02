package com.internlinkng.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Hospital(
    @SerialName("id")
    val id: String,
    @SerialName("name")
    val name: String,
    @SerialName("state")
    val state: String,
    @SerialName("professions")
    val professions: String, // Supabase stores as comma-separated string
    @SerialName("salary_range")
    val salaryRange: String,
    @SerialName("deadline")
    val deadline: String,
    @SerialName("created")
    val created: String,
    @SerialName("online_application")
    val onlineApplication: Boolean,
    @SerialName("application_url")
    val applicationUrl: String?,
    @SerialName("physical_address")
    val physicalAddress: String,
    @SerialName("profession_salaries")
    val professionSalaries: String? = null, // JSON string from Supabase
    @SerialName("created_at")
    val createdAt: String? = null,
    @SerialName("updated_at")
    val updatedAt: String? = null
) {
    // Helper function to convert comma-separated professions string to List
    fun getProfessionsList(): List<String> {
        return professions.split(",").map { it.trim() }
    }
    
    // Helper function to parse profession salaries JSON
    fun getProfessionSalariesMap(): Map<String, String>? {
        return professionSalaries?.let {
            try {
                // Simple JSON parsing - you might want to use a proper JSON library
                val pairs = it.trim('{', '}').split(",")
                    .map { pair -> 
                        val keyValue = pair.split(":")
                        if (keyValue.size == 2) {
                            keyValue[0].trim('"') to keyValue[1].trim('"')
                        } else null
                    }
                    .filterNotNull()
                    .toMap()
                pairs
            } catch (e: Exception) {
                null
            }
        }
    }
} 