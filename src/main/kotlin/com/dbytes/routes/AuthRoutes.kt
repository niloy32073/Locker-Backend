package com.dbytes.routes

import com.dbytes.models.User
import com.dbytes.models.UserSignInInfo
import com.dbytes.models.UserSignUpInfo
import com.dbytes.services.AuthServices
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.IllegalArgumentException

fun Application.authRoutes(authServices: AuthServices) {
    install(ContentNegotiation) {
        json() // Ensure this is installed and configured
    }

    routing {
        post("/signup"){
            val request = call.receive<User>()
            try {
                val user = authServices.registerUser(request)
                call.respond(HttpStatusCode.Created, user)
            }catch(e:IllegalArgumentException){
                call.respond(HttpStatusCode.BadRequest,e.message ?: "Invalid request.")
            }
        }

        post("/signin") {
            val request = call.receive<UserSignInInfo>()
            try{
                val user = withContext(Dispatchers.IO) {
                    authServices.loginUser(request)
                }
                call.respond(HttpStatusCode.OK, user)
            }catch(e:IllegalArgumentException){
                call.respond(HttpStatusCode.BadRequest,e.message ?: "Invalid request.")
            }
        }
    }
}