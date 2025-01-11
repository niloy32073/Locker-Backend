package com.dbytes

import com.dbytes.repositories.AuthRepositoryImpl
import com.dbytes.routes.authRoutes
import com.dbytes.services.AuthServices
import com.dbytes.tables.*
import com.dbytes.uitilities.DatabaseConfig
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    DatabaseConfig.connect()
    val authRepository = AuthRepositoryImpl()
    val authServices = AuthServices(authRepository)
    authRoutes(authServices)
}
