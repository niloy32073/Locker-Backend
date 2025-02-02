package com.dbytes

import com.dbytes.repositories.AuthRepositoryImpl
import com.dbytes.repositories.LockerRepositoryImpl
import com.dbytes.repositories.UserRepositoryImpl
import com.dbytes.routes.authRoutes
import com.dbytes.routes.lockerRoutes
import com.dbytes.services.AuthServices
import com.dbytes.services.LockerServices
import com.dbytes.services.UserServices
import com.dbytes.uitils.DatabaseConfig
import com.dbytes.uitils.JWTConfig
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    install(ContentNegotiation) {
        json()
    }
    install(Authentication){
        jwt("auth-jwt"){
            realm = "Access token"
            verifier(JWTConfig.verifyToken())
            validate { credential ->
                if (credential.payload.getClaim("userId").asString().isNotEmpty()) {
                    JWTPrincipal(credential.payload)
                } else null
            }
        }
    }
    DatabaseConfig.connect()
    val authRepository = AuthRepositoryImpl()
    val authServices = AuthServices(authRepository)
    authRoutes(authServices)
    val lockerRepositoryImpl = LockerRepositoryImpl()
    val lockerServices = LockerServices(lockerRepositoryImpl)

    val userRepositoryImpl = UserRepositoryImpl()
    val userServices = UserServices(userRepositoryImpl)
    lockerRoutes(lockerServices,userServices)
}
