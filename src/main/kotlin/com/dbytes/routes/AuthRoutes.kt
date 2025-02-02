package com.dbytes.routes

import com.dbytes.models.User
import com.dbytes.models.UserSignInInfo
import com.dbytes.models.UserSignUpInfo
import com.dbytes.services.AuthServices
import com.dbytes.uitils.JWTConfig
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.IllegalArgumentException

fun Application.authRoutes(authServices: AuthServices) {

    routing {
        post("/signup"){
            try {
                val request = call.receive<User>()
                val user = authServices.registerUser(request)
                call.respond(HttpStatusCode.Created, user)
            }catch(e:IllegalArgumentException){
                call.respond(HttpStatusCode.BadRequest,e.message ?: "Invalid request.")
            }
        }

        post("/signin") {
            try{
                val request = call.receive<UserSignInInfo>()
                val user = withContext(Dispatchers.IO) {
                    authServices.loginUser(request)
                }
                val token = JWTConfig.generateToken(user.id.toString())
                call.respond(HttpStatusCode.OK,"token" to token)
            }catch(e:IllegalArgumentException){
                call.respond(HttpStatusCode.BadRequest,e.message ?: "Invalid request.")
            }
        }
    }
}