package com.dbytes.routes

import com.dbytes.helpers.withAdminRole
import com.dbytes.models.*
import com.dbytes.services.LockerServices
import com.dbytes.services.NotificationServices
import com.dbytes.services.UserServices
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*


fun Application.lockerRoutes(lockerServices: LockerServices,userServices: UserServices,notificationServices: NotificationServices) {
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

            delete("/deleteLocker/{id}") {
                call.withAdminRole(userServices) {
                    try {
                        val id = call.pathParameters["id"]?.toLong() ?: return@withAdminRole call.respond(HttpStatusCode.BadRequest)
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
            post("/reserveLocker") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.getClaim("userId")?.asString()
                println(userId)
                try{
                    val id = userId?.toLong()
                    println(id)
                    if (id != null) {
                        val status = userServices.getUserStatusById(id)
                        if (status != "BLOCKED") {
                            val reservation = call.receive<Reservation>()
                            lockerServices.reserveLocker(id, reservation)
                            lockerServices.updateLockerStatus(
                                LockerStatusUpdateInfo(
                                    id = reservation.lockerID,
                                    status = "RESERVED",
                                )
                            )
                            var adminId = 0L
                            adminId = userServices.getUserIdByRole(role = "ADMIN")!!
                            println(adminId)
                            notificationServices.addNotification(Notification(
                            id = 0,
                            message = "Someone${id} requests for Locker ${reservation.lockerID}",
                            timestamp = System.currentTimeMillis(),
                            userId = adminId
                            ))
                            call.respond(HttpStatusCode.OK, "Reserved Request Successful")
                        }
                        else{
                            call.respond(HttpStatusCode.BadRequest, "You are blocked by Admin")
                        }
                    }
                    else{
                        call.respond(HttpStatusCode.Unauthorized, "User not found")
                    }
                }catch (e: Exception){
                    call.respond(HttpStatusCode.BadRequest, e.message ?: "Unknown error")
                }
            }
            post("/releaseLocker/{id}") {
                try {
                    val id = call.pathParameters["id"]?.toLong() ?: return@post call.respond(HttpStatusCode.BadRequest)
                    lockerServices.releaseLocker(id)
                    lockerServices.updateLockerStatus(LockerStatusUpdateInfo(
                        id = id,
                        status = "AVAILABLE"
                    ))

                    call.respond(HttpStatusCode.OK, "Released Successfully")
                } catch (e: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest, e.message ?: "Unknown error")
                }
            }
            post("/updateReservation") {
                try {
                    val reservationStatusInfo = call.receive<ReservationStatusInfo>()
                    lockerServices.updateReservationStatus(
                        id = reservationStatusInfo.id,
                        status = reservationStatusInfo.status
                    )
                    val reservation = lockerServices.findReservationsById(reservationStatusInfo.id)
                    if (reservation != null) {
                        if(reservation.status == "CLOSED" || reservation.status == "REJECTED") {
                            lockerServices.updateLockerStatus(LockerStatusUpdateInfo(
                                id = reservation.lockerID,
                                status = "AVAILABLE"
                            ))
                        }
                    }
                    call.respond(HttpStatusCode.OK, "${reservationStatusInfo.status} Successfully")

                        notificationServices.addNotification(Notification(
                            id = 0,
                            message = "The status of the Reservation(${reservationStatusInfo.id}) is ${reservationStatusInfo.status}",
                            timestamp = System.currentTimeMillis(),
                            userId = reservation?.userId ?: 0L
                        ))

                    val adminId = userServices.getUserIdByRole(role = "ADMIN")
                    if (adminId != null) {
                            notificationServices.addNotification(Notification(
                                id = 0,
                                message = "The status of the Reservation(${reservationStatusInfo.id}) is ${reservationStatusInfo.status}",
                                timestamp = System.currentTimeMillis(),
                                userId = adminId
                            ))

                    }

                } catch (e: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest, e.message ?: "Unknown error")
                }
            }
            get("/reservations"){
                try {
                    val reservations = lockerServices.getAllReservations()
                    call.respond(HttpStatusCode.OK, reservations)
                } catch (e: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest, e.message ?: "Unknown error")
                }
            }

            get("/myReservations"){
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.getClaim("userId")?.asString()
                try {
                    val id = userId?.toLong()
                    if (id != null) {
                        val reservations = lockerServices.getAllReservationsById(id)
                        call.respond(HttpStatusCode.OK, reservations)
                    }
                    else{
                        call.respond(HttpStatusCode.Unauthorized, "User not found")
                    }
                } catch (e: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest, e.message ?: "Unknown error")
                }
            }

            get("/reservations/{id}"){
                try {
                    val id = call.pathParameters["id"]?.toLong() ?: return@get call.respond(HttpStatusCode.BadRequest)
                    val reservation = lockerServices.findReservationsById(id)
                    if(reservation == null){
                        call.respond(HttpStatusCode.NotFound, "Reserve not found")
                    }
                    else
                        call.respond(HttpStatusCode.OK, reservation)
                } catch (e: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest, e.message ?: "Unknown error")
                }
            }
            get("/reservationsByStatus/{status}"){
                try {
                    val status = call.pathParameters["status"].toString()
                    if(status.isEmpty()){
                        val reservations = lockerServices.getAllReservations()
                        call.respond(HttpStatusCode.OK, reservations)
                    }
                    else{
                        val reservations = lockerServices.getAllReservationsByStatus(status)
                        call.respond(HttpStatusCode.OK, reservations)
                    }
                } catch (e: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest, e.message ?: "Unknown error")
                }
            }
        }
    }
}

