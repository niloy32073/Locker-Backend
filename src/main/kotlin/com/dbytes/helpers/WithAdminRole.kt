package com.dbytes.helpers

import com.dbytes.services.UserServices
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*

suspend fun ApplicationCall.withAdminRole(
    userServices: UserServices,
    action: suspend () -> Unit
) {
    val principal = principal<JWTPrincipal>()
    val userId = principal?.payload?.getClaim("userId")?.asString()
    if (userId != null) {
        val userRole = userServices.getUserRoleById(userId.toLong())
        if (userRole == "ADMIN") {
            action()
        } else {
            respond(HttpStatusCode.Unauthorized, "You don't have permission to perform this action")
        }
    } else {
        respond(HttpStatusCode.BadRequest, "Unknown error")
    }
}