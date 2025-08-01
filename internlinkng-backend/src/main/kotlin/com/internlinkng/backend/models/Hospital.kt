package com.internlinkng.backend.models

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ResultRow
import java.util.*

object Hospitals : UUIDTable() {
    val name = varchar("name", 255)
    val state = varchar("state", 100)
    val professions = varchar("professions", 255) // Comma-separated
    val salaryRange = varchar("salary_range", 100)
    val deadline = varchar("deadline", 50)
    val created = varchar("created", 50).default("2024-07-28") // Added created date
    val onlineApplication = bool("online_application")
    val applicationUrl = varchar("application_url", 255).nullable()
    val physicalAddress = varchar("physical_address", 255)
    val professionSalaries = text("profession_salaries").nullable()
}

data class Hospital(
    val id: UUID,
    val name: String,
    val state: String,
    val professions: List<String>,
    val salaryRange: String,
    val deadline: String,
    val created: String, // Added created date
    val onlineApplication: Boolean,
    val applicationUrl: String?,
    val physicalAddress: String,
    val professionSalaries: Map<String, String>? = null // NEW FIELD
)

fun ResultRow.toHospital() = Hospital(
    id = this[Hospitals.id].value,
    name = this[Hospitals.name],
    state = this[Hospitals.state],
    professions = this[Hospitals.professions].split(","),
    salaryRange = this[Hospitals.salaryRange],
    deadline = this[Hospitals.deadline],
    created = this[Hospitals.created], // Map created
    onlineApplication = this[Hospitals.onlineApplication],
    applicationUrl = this[Hospitals.applicationUrl],
    physicalAddress = this[Hospitals.physicalAddress],
    professionSalaries = this[Hospitals.professionSalaries]?.let {
        try {
            jacksonObjectMapper().readValue<Map<String, String>>(it)
        } catch (e: Exception) {
            null
        }
    }
) 