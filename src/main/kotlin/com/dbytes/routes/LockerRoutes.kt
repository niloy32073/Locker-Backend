package com.dbytes.routes

import com.dbytes.helpers.FcmService
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
                try{
                    val id = userId?.toLong()
                    if (id != null) {
                        val reservation = call.receive<Reservation>()
                        lockerServices.reserveLocker(id, reservation)
                        call.respond(HttpStatusCode.OK, "Reserved Request Successful")
                        val adminId = userServices.getUserIdByRole(role = "ADMIN")
                        if (adminId != null) {
                            val adminFcmToken = userServices.getUserFirebaseTokenById(adminId)
                            if (adminFcmToken != null) {
                                FcmService.sendNotification(
                                    targetToken = adminFcmToken,
                                    title = "New Reservation Request",
                                    body = "Someone${id} requests for Locker ${reservation.lockerID}"
                                )
                                notificationServices.addNotification(Notification(
                                    id = 0,
                                    message = "Someone${id} requests for Locker ${reservation.lockerID}",
                                    timestamp = System.currentTimeMillis(),
                                    userId = adminId
                                ))
                            }
                        }
                    }
                    else{
                        call.respond(HttpStatusCode.Unauthorized, "User not found")
                    }
                }catch (e: Exception){
                    call.respond(HttpStatusCode.InternalServerError, "Something went wrong")
                }
            }
            post("/releaseLocker/{id}") {
                try {
                    val id = call.pathParameters["id"]?.toLong() ?: return@post call.respond(HttpStatusCode.BadRequest)
                    lockerServices.releaseLocker(id)
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
                    val token = reservation?.let { userServices.getUserFirebaseTokenById(it.userId) }
                    if (token != null) {
                        FcmService.sendNotification(
                            targetToken = token,
                            title = "Reservation Request Updated",
                            body = "The status of the Reservation(${reservationStatusInfo.id}) is ${reservationStatusInfo.status}"
                        )
                        notificationServices.addNotification(Notification(
                            id = 0,
                            message = "The status of the Reservation(${reservationStatusInfo.id}) is ${reservationStatusInfo.status}",
                            timestamp = System.currentTimeMillis(),
                            userId = reservation.userId
                        ))
                    }
                    val adminId = userServices.getUserIdByRole(role = "ADMIN")
                    if (adminId != null) {
                        val adminFcmToken = userServices.getUserFirebaseTokenById(adminId)
                        if (adminFcmToken != null) {
                            FcmService.sendNotification(
                                targetToken = adminFcmToken,
                                title = "Reservation Request Updated",
                                body = "The status of the Reservation(${reservationStatusInfo.id}) is ${reservationStatusInfo.status}"
                            )
                            notificationServices.addNotification(Notification(
                                id = 0,
                                message = "The status of the Reservation(${reservationStatusInfo.id}) is ${reservationStatusInfo.status}",
                                timestamp = System.currentTimeMillis(),
                                userId = adminId
                            ))
                        }
                    }
                    call.respond(HttpStatusCode.OK, "${reservationStatusInfo.status} Successfully")
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

