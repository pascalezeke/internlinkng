package com.internlinkng.backend

import com.internlinkng.backend.models.DatabaseFactory
import com.internlinkng.backend.routes.authRoutes
import com.internlinkng.backend.routes.hospitalRoutes
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.serialization.gson.*
import io.ktor.http.*
import io.ktor.server.plugins.cors.routing.*

fun main() {
    DatabaseFactory.init()
    
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        install(ContentNegotiation) { 
            gson() 
        }
        
        install(CORS) {
            anyHost()
            allowMethod(HttpMethod.Get)
            allowMethod(HttpMethod.Post)
            allowMethod(HttpMethod.Put)
            allowMethod(HttpMethod.Delete)
            allowHeader(HttpHeaders.Authorization)
            allowHeader(HttpHeaders.ContentType)
        }
        
        routing {
            get("/") {
                call.respondText("InternLinkNG Backend is running!")
            }
            
            authRoutes()
            hospitalRoutes()
        }
    }.start(wait = true)
} 