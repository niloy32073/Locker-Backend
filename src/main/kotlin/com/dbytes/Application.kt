package com.dbytes

import com.dbytes.repositories.*
import com.dbytes.routes.*
import com.dbytes.services.*
import com.dbytes.uitils.DatabaseConfig
import com.dbytes.uitils.JWTConfig
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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
    val notificationRepositoryImpl = NotificationRepositoryImpl()
    val notificationServices = NotificationServices(notificationRepositoryImpl)
    lockerRoutes(lockerServices,userServices,notificationServices)
    userRoutes(userServices)
    val buildingRepositoryImpl = BuildingRepositoryImpl()
    val buildingServices = BuildingServices(buildingRepositoryImpl)
    buildingRoutes(buildingServices,userServices)
    notificationRoutes(notificationServices)
    launch {
        while (true) {
            delay(60_000) // Check every minute
            println("Check")
            lockerServices.releaseExpiredReservation()
        }
    }
}
