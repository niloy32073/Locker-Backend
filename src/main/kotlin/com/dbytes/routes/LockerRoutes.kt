package com.dbytes.routes

import com.dbytes.models.Locker
import com.dbytes.models.LockerStatusUpdateInfo
import com.dbytes.services.LockerServices
import com.dbytes.services.UserServices
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.lockerRoutes(lockerServices: LockerServices,userServices: UserServices) {
    routing {
        authenticate("auth-jwt") {
            post("/addlocker") {
                try {
                    val principal = call.principal<JWTPrincipal>()
                    val userId = principal?.payload?.getClaim("userId")?.asString()
                    if (userId != null) {
                        println(userId)
                        val userRole = userServices.getUserRoleById(userId.toLong())
                        println(userRole)
                        if (userRole == "admin") {
                            val locker = call.receive<Locker>()
                            val id = lockerServices.createLocker(locker)
                            call.respond(HttpStatusCode.OK, "Created Successfully with id: $id")
                        }
                        else call.respond(HttpStatusCode.Unauthorized, "You don't have permission to create a new locker")
                    }
                    else{
                        call.respond(HttpStatusCode.BadRequest, "Unknown error")
                    }
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, e.message ?: "Unknown error")
                }
            }

            post("/deletelocker") {
                try {
                    val id = call.receive<Long>()
                    lockerServices.deleteLocker(id)
                    call.respond(HttpStatusCode.OK, "Deleted Successfully")
                } catch (e: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest, e.message ?: "Unknown error")
                }
            }

            post("/updatelocker") {
                try {
                    val lockerStatusUpdateInfo = call.receive<LockerStatusUpdateInfo>()
                    lockerServices.updateLockerStatus(lockerStatusUpdateInfo )
                    call.respond(HttpStatusCode.OK, "Updated Successfully")
                } catch (e: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest, e.message ?: "Unknown error")
                }
            }
        }
    }
}