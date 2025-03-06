package com.dbytes.routes

import com.dbytes.services.NotificationServices
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.notificationRoutes(notificationServices: NotificationServices) {
    routing {
        authenticate("auth-jwt") {
            get("/notification") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.getClaim("userId")?.asString()
                try{
                    val id = userId?.toLong()
                    if (id != null) {
                        val notifications = notificationServices.getNotifications(id)

                        call.respond(HttpStatusCode.OK,notifications)
                    }
                    else{
                        call.respond(HttpStatusCode.Unauthorized, "User not found")
                    }
                }
                catch (e: Exception){
                    call.respond(HttpStatusCode.Unauthorized, "User not found")
                }
            }
        }
    }
}