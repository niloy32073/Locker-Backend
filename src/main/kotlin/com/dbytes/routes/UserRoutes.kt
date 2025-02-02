package com.dbytes.routes

import com.dbytes.models.Locker
import com.dbytes.models.LockerStatusUpdateInfo
import com.dbytes.services.LockerServices
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.userRoutes(lockerServices: LockerServices) {
    routing {
        authenticate("auth-jwt") {
            post("/user") {
                try {
                    val locker = call.receive<Locker>()
                    val id = lockerServices.createLocker(locker)
                    call.respond(HttpStatusCode.OK, "Created Successfully with id: $id")
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, e.message ?: "Unknown error")
                }
            }

            post("/userstatus") {
                try {
                    val id = call.receive<Long>()
                    lockerServices.deleteLocker(id)
                    call.respond(HttpStatusCode.OK, "Deleted Successfully")
                } catch (e: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest, e.message ?: "Unknown error")
                }
            }

            post("/userrole") {
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