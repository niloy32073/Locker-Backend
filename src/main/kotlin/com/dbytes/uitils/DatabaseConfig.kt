package com.dbytes.uitils

import com.dbytes.tables.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseConfig {
    fun connect() {
        Database.connect(
            url = "jdbc:postgresql://localhost:5432/postgres",
            driver = "org.postgresql.Driver",
            user = "postgres",
            password = "password"
        )
        transaction {
            SchemaUtils.create(UserTable)
            SchemaUtils.create(BuildingTable)
            SchemaUtils.create(LockerTable)
            SchemaUtils.create(ReservationTable)
            SchemaUtils.create(NotificationTable)
            SchemaUtils.create(StudentTable)
        }
    }
}