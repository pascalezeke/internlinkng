package com.internlinkng.backend.models

import com.typesafe.config.ConfigFactory
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.mindrot.jbcrypt.BCrypt
import java.util.*

object DatabaseFactory {
    fun init() {
        // Read database configuration from environment variables or fall back to application.conf
        val url = System.getenv("DATABASE_URL") ?: ConfigFactory.load().getConfig("database").getString("url")
        val driver = System.getenv("DATABASE_DRIVER") ?: ConfigFactory.load().getConfig("database").getString("driver")
        val user = System.getenv("DATABASE_USER") ?: ConfigFactory.load().getConfig("database").getString("user")
        val password = System.getenv("DATABASE_PASSWORD") ?: ConfigFactory.load().getConfig("database").getString("password")
        
        println("Connecting to database: $url with user: $user")
        Database.connect(url = url, driver = driver, user = user, password = password)
        
        // Create tables
        transaction {
            SchemaUtils.create(Users, Hospitals, Applications)
            
            // Create admin user if not exists
            val adminEmail = "admin@internlinkng.com"
            val adminExists = Users.select { Users.email eq adminEmail }.count() > 0
            println("Admin user exists: $adminExists")
            
            if (!adminExists) {
                // Create admin user with proper hash
                val adminId = Users.insert {
                    it[id] = UUID.randomUUID()
                    it[email] = adminEmail
                    it[passwordHash] = BCrypt.hashpw("Android_Studio1", BCrypt.gensalt())
                    it[isAdmin] = true
                } get Users.id
                println("Admin user created successfully with ID: ${adminId.value}")
            } else {
                // Check if existing admin user has correct isAdmin flag
                val existingAdmin = Users.select { Users.email eq adminEmail }.singleOrNull()?.toUser()
                println("Existing admin user: ${existingAdmin?.email}, isAdmin: ${existingAdmin?.isAdmin}")
            }
            
            // Add sample hospitals if none exist
            val hospitalCount = Hospitals.selectAll().count()
            println("Found $hospitalCount existing hospitals in database")
            
            if (hospitalCount == 0L) {
                println("No hospitals found, inserting sample data...")
                // Sample hospital data
                val sampleHospitals = listOf(
                    mapOf(
                        "name" to "Lagos General Hospital",
                        "state" to "Lagos",
                        "professions" to "Nurse,Medical Laboratory Scientist",
                        "salaryRange" to "N200,000 - N250,000",
                        "deadline" to "2024-08-01",
                        "created" to "2024-01-15",
                        "onlineApplication" to true,
                        "applicationUrl" to "https://lagoshospital.com/apply",
                        "physicalAddress" to "123 Victoria Island, Lagos"
                    ),
                    mapOf(
                        "name" to "Abuja Teaching Hospital",
                        "state" to "Abuja",
                        "professions" to "House Officer,Nurse",
                        "salaryRange" to "N180,000 - N220,000",
                        "deadline" to "2024-07-15",
                        "created" to "2024-01-20",
                        "onlineApplication" to false,
                        "applicationUrl" to null,
                        "physicalAddress" to "456 Central District, Abuja"
                    ),
                    mapOf(
                        "name" to "Kano Medical Center",
                        "state" to "Kano",
                        "professions" to "Medical Laboratory Scientist,House Officer",
                        "salaryRange" to "N150,000 - N200,000",
                        "deadline" to "2024-09-30",
                        "created" to "2024-02-01",
                        "onlineApplication" to true,
                        "applicationUrl" to "https://kanomedical.com/careers",
                        "physicalAddress" to "789 Kano Central, Kano"
                    ),
                    mapOf(
                        "name" to "Port Harcourt Specialist Hospital",
                        "state" to "Rivers",
                        "professions" to "Nurse,Medical Laboratory Scientist,House Officer",
                        "salaryRange" to "N220,000 - N280,000",
                        "deadline" to "2024-08-20",
                        "created" to "2024-02-15",
                        "onlineApplication" to true,
                        "applicationUrl" to "https://phshospital.com/internships",
                        "physicalAddress" to "321 GRA Phase 2, Port Harcourt"
                    ),
                    mapOf(
                        "name" to "Ibadan University Teaching Hospital",
                        "state" to "Oyo",
                        "professions" to "House Officer,Nurse",
                        "salaryRange" to "N160,000 - N210,000",
                        "deadline" to "2024-07-30",
                        "created" to "2024-03-01",
                        "onlineApplication" to false,
                        "applicationUrl" to null,
                        "physicalAddress" to "654 University Road, Ibadan"
                    )
                )
                
                sampleHospitals.forEach { hospitalData ->
                    val hospital = Hospitals.insert {
                        it[id] = UUID.randomUUID()
                        it[name] = hospitalData["name"] as String
                        it[state] = hospitalData["state"] as String
                        it[professions] = hospitalData["professions"] as String
                        it[salaryRange] = hospitalData["salaryRange"] as String
                        it[deadline] = hospitalData["deadline"] as String
                        it[created] = hospitalData["created"] as String
                        it[onlineApplication] = hospitalData["onlineApplication"] as Boolean
                        it[applicationUrl] = hospitalData["applicationUrl"] as String?
                        it[physicalAddress] = hospitalData["physicalAddress"] as String
                    }
                    println("Inserted hospital: ${hospitalData["name"]} with ID: ${hospital[Hospitals.id].value}")
                }
                println("Sample hospitals inserted successfully")
            }
        }
    }
} 