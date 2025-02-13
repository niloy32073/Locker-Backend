 package com.dbytes.routes

import com.dbytes.models.*
import com.dbytes.services.LockerServices
import com.dbytes.services.UserServices
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.userRoutes(userServices: UserServices) {
    routing {
        authenticate("auth-jwt") {
            get("/user") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.getClaim("userId")?.asString()
                try{
                    val id = userId?.toLong()
                    if (id != null) {
                        val user = userServices.getUserById(id)
                        if (user != null) {
                            call.respond(HttpStatusCode.OK,user)
                        }
                        else{
                            call.respond(HttpStatusCode.InternalServerError, "User's data not found")
                        }
                    }
                    else{
                        call.respond(HttpStatusCode.Unauthorized, "User not found")
                    }
                }
                catch (e: Exception){
                    call.respond(HttpStatusCode.Unauthorized, "User not found")
                }
            }

            post("/changePassword") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.getClaim("userId")?.asString()
                try{
                    val id = userId?.toLong()
                    if (id != null) {
                        val changePasswordInfo = call.receive<ChangePasswordInfo>()
                        if(userServices.checkUserPassword(id,changePasswordInfo.oldPassword)){
                            userServices.updateUserPassword(id,changePasswordInfo.newPassword)
                            call.respond(HttpStatusCode.OK,"Password successfully updated")
                        }
                        else{
                            call.respond(HttpStatusCode.OK, "Password doesn't match")
                        }
                    }
                    else{
                        call.respond(HttpStatusCode.Unauthorized, "User not found")
                    }
                }
                catch (e: Exception){
                    call.respond(HttpStatusCode.Unauthorized, "User not found")
                }
            }

            post("/updateName") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.getClaim("userId")?.asString()
                try{
                    val id = userId?.toLong()
                    if (id != null) {
                        val name = call.receive<Name>()
                        userServices.updateUserNameById(id,name.name)
                        call.respond(HttpStatusCode.OK,"Name successfully updated")
                    }
                    else{
                        call.respond(HttpStatusCode.Unauthorized, "User not found")
                    }
                }
                catch (e: Exception){
                    call.respond(HttpStatusCode.Unauthorized, "User not found")
                }
            }

            post("/updatePhone") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.getClaim("userId")?.asString()
                try{
                    val id = userId?.toLong()
                    if (id != null) {
                        val phone = call.receive<Phone>()
                        userServices.updateUserPhoneById(id,phone.phone)
                        call.respond(HttpStatusCode.OK,"Phone successfully updated")
                    }
                    else{
                        call.respond(HttpStatusCode.Unauthorized, "User not found")
                    }
                }
                catch (e: Exception){
                    call.respond(HttpStatusCode.Unauthorized, "User not found")
                }
            }

            post("/updateFirebaseToken") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.getClaim("userId")?.asString()
                try{
                    val id = userId?.toLong()
                    if (id != null) {
                        val firebaseToken = call.receive<FirebaseToken>()
                        userServices.updateUserFirebaseTokenById(id,firebaseToken.firebaseToken)
                        call.respond(HttpStatusCode.OK,"Token successfully updated")
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