package com.dbytes.routes

import com.dbytes.helpers.withAdminRole
import com.dbytes.models.Locker
import com.dbytes.models.LockerStatusUpdateInfo
import com.dbytes.services.LockerServices
import com.dbytes.services.UserServices
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*


fun Application.lockerRoutes(lockerServices: LockerServices,userServices: UserServices) {
    routing {
        authenticate("auth-jwt") {
            get("/lockers"){
                try {
                    val lockers = lockerServices.getAllLocker()
                    call.respond(HttpStatusCode.OK, lockers)
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, e.message ?: "Unknown error")
                }
            }
            post("/addLocker") {
                call.withAdminRole(userServices) {
                    try {
                        val locker = call.receive<Locker>()
                        val id = lockerServices.createLocker(locker)
                        call.respond(HttpStatusCode.OK, "Created Successfully with id: $id")
                    } catch (e: Exception) {
                        call.respond(HttpStatusCode.BadRequest, e.message ?: "Unknown error")
                    }
                }
            }

            post("/deleteLocker") {
                call.withAdminRole(userServices) {
                    try {
                        val id = call.receive<Long>()
                        lockerServices.deleteLocker(id)
                        call.respond(HttpStatusCode.OK, "Deleted Successfully")
                    } catch (e: IllegalArgumentException) {
                        call.respond(HttpStatusCode.BadRequest, e.message ?: "Unknown error")
                    }
                }
            }

            post("/updateLocker") {
                call.withAdminRole(userServices) {
                    try {
                        val lockerStatusUpdateInfo = call.receive<LockerStatusUpdateInfo>()
                        lockerServices.updateLockerStatus(lockerStatusUpdateInfo)
                        call.respond(HttpStatusCode.OK, "Updated Successfully")
                    } catch (e: IllegalArgumentException) {
                        call.respond(HttpStatusCode.BadRequest, e.message ?: "Unknown error")
                    }
                }
            }
        }
    }
}

