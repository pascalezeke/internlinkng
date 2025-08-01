package com.internlinkng.backend.routes

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.internlinkng.backend.auth.JwtConfig
import com.internlinkng.backend.models.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.update
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import java.util.*
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

fun getIsAdminFromToken(call: ApplicationCall): Boolean {
    val authHeader = call.request.headers["Authorization"] ?: return false
    println("=== ADMIN AUTH DEBUG ===")
    println("Auth header: $authHeader")
    
    val token = authHeader.removePrefix("Bearer ").trim()
    println("Token: $token")
    
    return try {
        val decoded = JWT.require(Algorithm.HMAC256("your-secret-key-here"))
            .withIssuer("internlinkng")
            .withAudience("internlinkng-users")
            .build()
            .verify(token)
        
        val isAdmin = decoded.getClaim("isAdmin").asBoolean() == true
        println("Token decoded successfully, isAdmin: $isAdmin")
        isAdmin
    } catch (e: Exception) {
        println("Token verification failed: ${e.message}")
        e.printStackTrace()
        false
    }
}

fun Route.hospitalRoutes() {
    // Public endpoints
    route("/hospitals") {
        get {
            try {
                println("=== HOSPITAL GET ENDPOINT CALLED ===")
                println("Fetching hospitals from database...")
                val hospitals = transaction {
                    val count = Hospitals.selectAll().count()
                    println("Found $count hospitals in database")
                    val hospitalList = Hospitals.selectAll().map { it.toHospital() }
                    println("Mapped ${hospitalList.size} hospitals from database")
                    hospitalList
                }
                println("Returning ${hospitals.size} hospitals to client")
                call.respond(hospitals)
            } catch (e: Exception) {
                println("Error fetching hospitals: ${e.message}")
                e.printStackTrace()
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to e.message))
            }
        }
        
        post {
            try {
                val hospitalData = call.receive<Map<String, Any>>()
                println("Creating hospital: ${hospitalData["name"]}")
                
                val professionSalariesJson = (hospitalData["professionSalaries"] as? Map<*, *>)?.let {
                    jacksonObjectMapper().writeValueAsString(it)
                } ?: (hospitalData["professionSalaries"] as? String)
                
                val hospital = transaction {
                    Hospitals.insert {
                        it[id] = UUID.randomUUID()
                        it[name] = hospitalData["name"] as String
                        it[state] = hospitalData["state"] as String
                        it[professions] = (hospitalData["professions"] as List<String>).joinToString(",")
                        it[salaryRange] = hospitalData["salaryRange"] as String
                        it[deadline] = hospitalData["deadline"] as String
                        it[onlineApplication] = hospitalData["onlineApplication"] as Boolean
                        it[applicationUrl] = hospitalData["applicationUrl"] as String?
                        it[physicalAddress] = hospitalData["physicalAddress"] as String
                        it[created] = hospitalData["created"] as String
                        it[professionSalaries] = professionSalariesJson as String?
                    }
                }
                
                println("Hospital created with ID: ${hospital[Hospitals.id].value}")
                call.respond(HttpStatusCode.Created, mapOf("message" to "Hospital created successfully", "id" to hospital[Hospitals.id].value.toString()))
            } catch (e: Exception) {
                println("Error creating hospital: ${e.message}")
                e.printStackTrace()
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
            }
        }
        
        // Test endpoint to check database connectivity
        route("/test-db") {
            get {
                try {
                    println("=== TESTING DATABASE CONNECTIVITY ===")
                    val result = transaction {
                        val count = Hospitals.selectAll().count()
                        println("Database test: Found $count hospitals")
                        mapOf(
                            "database_connected" to true,
                            "hospital_count" to count,
                            "message" to "Database connection successful"
                        )
                    }
                    call.respond(result)
                } catch (e: Exception) {
                    println("Database test failed: ${e.message}")
                    e.printStackTrace()
                    call.respond(HttpStatusCode.InternalServerError, mapOf(
                        "database_connected" to false,
                        "error" to e.message
                    ))
                }
            }
        }
        
        // Temporary endpoint to add missing hospitals
        route("/add-sample") {
            post {
                try {
                    val sampleHospitals = listOf(
                        mapOf(
                            "name" to "Kano Medical Center",
                            "state" to "Kano",
                            "professions" to "Medical Laboratory Scientist,House Officer",
                            "salaryRange" to "N150,000 - N200,000",
                            "deadline" to "2024-09-30",
                            "onlineApplication" to true,
                            "applicationUrl" to "https://kanomedical.com/careers",
                            "physicalAddress" to "789 Kano Central, Kano",
                            "created" to "2024-07-28"
                        ),
                        mapOf(
                            "name" to "Port Harcourt Specialist Hospital",
                            "state" to "Rivers",
                            "professions" to "Nurse,Medical Laboratory Scientist,House Officer",
                            "salaryRange" to "N220,000 - N280,000",
                            "deadline" to "2024-08-20",
                            "onlineApplication" to true,
                            "applicationUrl" to "https://phshospital.com/internships",
                            "physicalAddress" to "321 GRA Phase 2, Port Harcourt",
                            "created" to "2024-07-28"
                        ),
                        mapOf(
                            "name" to "Ibadan University Teaching Hospital",
                            "state" to "Oyo",
                            "professions" to "House Officer,Nurse",
                            "salaryRange" to "N160,000 - N210,000",
                            "deadline" to "2024-07-30",
                            "onlineApplication" to false,
                            "applicationUrl" to null,
                            "physicalAddress" to "654 University Road, Ibadan",
                            "created" to "2024-07-28"
                        )
                    )
                    
                    val insertedIds = mutableListOf<String>()
                    
                    transaction {
                        sampleHospitals.forEach { hospitalData ->
                            val hospital = Hospitals.insert {
                                it[id] = UUID.randomUUID()
                                it[name] = hospitalData["name"] as String
                                it[state] = hospitalData["state"] as String
                                it[professions] = hospitalData["professions"] as String
                                it[salaryRange] = hospitalData["salaryRange"] as String
                                it[deadline] = hospitalData["deadline"] as String
                                it[onlineApplication] = hospitalData["onlineApplication"] as Boolean
                                it[applicationUrl] = hospitalData["applicationUrl"] as String?
                                it[physicalAddress] = hospitalData["physicalAddress"] as String
                                it[created] = hospitalData["created"] as String
                            }
                            insertedIds.add(hospital[Hospitals.id].value.toString())
                        }
                    }
                    
                    call.respond(HttpStatusCode.Created, mapOf(
                        "message" to "Sample hospitals added successfully",
                        "inserted_count" to insertedIds.size,
                        "inserted_ids" to insertedIds
                    ))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
                }
            }
        }
    }

    // Admin endpoints
    route("/admin/hospitals") {
        // Add hospital (admin only)
        post {
            println("=== ADMIN HOSPITAL CREATION ENDPOINT CALLED ===")
            val isAdmin = getIsAdminFromToken(call)
            println("Is admin: $isAdmin")
            
            if (!isAdmin) {
                println("Admin access denied - returning 403")
                call.respond(HttpStatusCode.Forbidden, mapOf("error" to "Admin access required"))
                return@post
            }
            
            try {
                val hospitalData = call.receive<Map<String, Any>>()
                println("Received hospital data: $hospitalData")
                
                val professionSalariesJson = (hospitalData["professionSalaries"] as? Map<*, *>)?.let {
                    jacksonObjectMapper().writeValueAsString(it)
                } ?: (hospitalData["professionSalaries"] as? String)
                
                val hospital = transaction {
                    Hospitals.insert {
                        it[id] = UUID.randomUUID()
                        it[name] = hospitalData["name"] as String
                        it[state] = hospitalData["state"] as String
                        it[professions] = (hospitalData["professions"] as List<String>).joinToString(",")
                        it[salaryRange] = hospitalData["salaryRange"] as String
                        it[deadline] = hospitalData["deadline"] as String
                        it[created] = hospitalData["created"] as String
                        it[onlineApplication] = hospitalData["onlineApplication"] as Boolean
                        it[applicationUrl] = hospitalData["applicationUrl"] as String?
                        it[physicalAddress] = hospitalData["physicalAddress"] as String
                        it[professionSalaries] = professionSalariesJson as String?
                    }
                }
                println("Hospital created successfully with ID: ${hospital[Hospitals.id].value}")
                call.respond(HttpStatusCode.Created, mapOf("message" to "Hospital created successfully"))
            } catch (e: Exception) {
                println("Error creating hospital: ${e.message}")
                e.printStackTrace()
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
            }
        }
        // Edit hospital (admin only)
        put("/{id}") {
            if (!getIsAdminFromToken(call)) {
                call.respond(HttpStatusCode.Forbidden, mapOf("error" to "Admin access required"))
                return@put
            }
            val idParam = call.parameters["id"] ?: return@put call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Missing id"))
            val hospitalId = try { UUID.fromString(idParam) } catch (e: Exception) { return@put call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid id")) }
            try {
                val hospitalData = call.receive<Map<String, Any>>()
                val professionSalariesJson = (hospitalData["professionSalaries"] as? Map<*, *>)?.let {
                    jacksonObjectMapper().writeValueAsString(it)
                } ?: (hospitalData["professionSalaries"] as? String)
                val updated = transaction {
                    Hospitals.update({ Hospitals.id eq hospitalId }) {
                        it[name] = hospitalData["name"] as String
                        it[state] = hospitalData["state"] as String
                        it[professions] = (hospitalData["professions"] as List<String>).joinToString(",")
                        it[salaryRange] = hospitalData["salaryRange"] as String
                        it[deadline] = hospitalData["deadline"] as String
                        it[created] = hospitalData["created"] as String
                        it[onlineApplication] = hospitalData["onlineApplication"] as Boolean
                        it[applicationUrl] = hospitalData["applicationUrl"] as String?
                        it[physicalAddress] = hospitalData["physicalAddress"] as String
                        it[professionSalaries] = professionSalariesJson as String?
                    }
                }
                call.respond(HttpStatusCode.OK, mapOf("message" to "Hospital updated successfully"))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
            }
        }
        // Delete hospital (admin only)
        delete("/{id}") {
            if (!getIsAdminFromToken(call)) {
                call.respond(HttpStatusCode.Forbidden, mapOf("error" to "Admin access required"))
                return@delete
            }
            val idParam = call.parameters["id"] ?: return@delete call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Missing id"))
            val hospitalId = try { UUID.fromString(idParam) } catch (e: Exception) { return@delete call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid id")) }
            try {
                val deleted = transaction {
                    Hospitals.deleteWhere { Hospitals.id eq hospitalId }
                }
                call.respond(HttpStatusCode.OK, mapOf("message" to "Hospital deleted successfully"))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
            }
        }
    }
} 