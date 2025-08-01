package com.internlinkng.backend.routes

import com.internlinkng.backend.auth.JwtConfig
import com.internlinkng.backend.models.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.insert
import org.mindrot.jbcrypt.BCrypt
import com.internlinkng.backend.models.Users
import com.internlinkng.backend.models.toUser
import org.jetbrains.exposed.sql.update
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

data class LoginRequest(val email: String, val password: String)
data class SignupRequest(
    val email: String,
    val password: String,
    val firstname: String,
    val lastname: String,
    val phoneNumber: String,
    val stateOfResidence: String,
    val profession: String,
    val profilePicture: String? = null
)
data class AuthResponse(val token: String, val userId: String, val isAdmin: Boolean)
data class ApplicationRequest(val userId: String, val hospitalId: String, val profession: String, val coverLetter: String?)
data class ApplicationResponse(val id: String, val message: String)

fun Route.authRoutes() {
    route("/login") {
        post {
            try {
                val request = call.receive<LoginRequest>()
                println("=== LOGIN ATTEMPT ===")
                println("Email: ${request.email}")
                
                val user = transaction {
                    Users.select { Users.email eq request.email }.singleOrNull()?.toUser()
                }
                
                if (user == null) {
                    println("User not found")
                    call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "User not found"))
                    return@post
                }
                
                println("User found: ${user.email}, isAdmin: ${user.isAdmin}")
                
                if (!BCrypt.checkpw(request.password, user.passwordHash)) {
                    println("Invalid password")
                    call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Invalid password"))
                    return@post
                }
                
                val token = JwtConfig.makeToken(user.id.toString(), user.isAdmin)
                println("Token generated, isAdmin in token: ${user.isAdmin}")
                call.respond(AuthResponse(token, user.id.toString(), user.isAdmin))
            } catch (e: Exception) {
                println("Login error: ${e.message}")
                e.printStackTrace()
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
            }
        }
    }
    
    route("/signup") {
        post {
            try {
                val request = call.receive<SignupRequest>()
                val existing = transaction {
                    Users.select { Users.email eq request.email }.count() > 0
                }
                if (existing) {
                    call.respond(HttpStatusCode.Conflict, mapOf("error" to "Email already registered"))
                    return@post
                }
                val userId = transaction {
                    Users.insert {
                        it[id] = UUID.randomUUID()
                        it[email] = request.email
                        it[passwordHash] = BCrypt.hashpw(request.password, BCrypt.gensalt())
                        it[isAdmin] = false // Only DB seeding creates admin
                        it[phoneNumber] = request.phoneNumber
                        it[stateOfResidence] = request.stateOfResidence
                        it[profession] = request.profession
                        it[profilePicture] = request.profilePicture
                        it[firstname] = request.firstname
                        it[lastname] = request.lastname
                    } get Users.id
                }
                val token = JwtConfig.makeToken(userId.value.toString(), false)
                call.respond(AuthResponse(token, userId.value.toString(), false))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
            }
        }
    }
    
    route("/apply") {
        post {
            try {
                val request = call.receive<ApplicationRequest>()
                
                // Validate request
                if (request.userId.isBlank() || request.hospitalId.isBlank() || request.profession.isBlank()) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Missing required fields"))
                    return@post
                }
                
                val applicationId = "app-${UUID.randomUUID()}"
                val response = ApplicationResponse(
                    id = applicationId,
                    message = "Application submitted successfully"
                )
                
                call.respond(HttpStatusCode.Created, response)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
            }
        }
    }
    
    route("/profile") {
        get {
            try {
                val token = call.request.header("Authorization")?.removePrefix("Bearer ")
                if (token == null) {
                    call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "No token provided"))
                    return@get
                }
                val userId = JwtConfig.verifyToken(token)
                if (userId == null) {
                    call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Invalid token"))
                    return@get
                }
                val user = transaction {
                    Users.select { Users.id eq UUID.fromString(userId) }.singleOrNull()?.toUser()
                }
                if (user == null) {
                    call.respond(HttpStatusCode.NotFound, mapOf("error" to "User not found"))
                    return@get
                }
                val profileData = mapOf(
                    "id" to user.id.toString(),
                    "email" to user.email,
                    "isAdmin" to user.isAdmin,
                    "phoneNumber" to user.phoneNumber,
                    "stateOfResidence" to user.stateOfResidence,
                    "profession" to user.profession,
                    "profilePicture" to user.profilePicture,
                    "firstname" to user.firstname,
                    "lastname" to user.lastname
                )
                call.respond(profileData)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
            }
        }
        put {
            try {
                val token = call.request.header("Authorization")?.removePrefix("Bearer ")
                if (token == null) {
                    call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "No token provided"))
                    return@put
                }
                val userId = JwtConfig.verifyToken(token)
                if (userId == null) {
                    call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Invalid token"))
                    return@put
                }
                val updateRequest = call.receive<Map<String, String>>()
                val updated = transaction {
                    Users.update({ Users.id eq UUID.fromString(userId) }) {
                        updateRequest["firstname"]?.let { it1 -> it[firstname] = it1 }
                        updateRequest["lastname"]?.let { it1 -> it[lastname] = it1 }
                        updateRequest["phoneNumber"]?.let { it1 -> it[phoneNumber] = it1 }
                        updateRequest["stateOfResidence"]?.let { it1 -> it[stateOfResidence] = it1 }
                        updateRequest["profession"]?.let { it1 -> it[profession] = it1 }
                        // Handle profilePicture removal (empty string means remove)
                        if (updateRequest.containsKey("profilePicture")) {
                            val pic = updateRequest["profilePicture"]
                            it[profilePicture] = if (pic.isNullOrEmpty()) null else pic
                        }
                    }
                }
                if (updated > 0) {
                    call.respond(HttpStatusCode.OK, mapOf("message" to "Profile updated successfully"))
                } else {
                    call.respond(HttpStatusCode.NotFound, mapOf("error" to "User not found or no changes"))
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
            }
        }
    }
} 