package com.dbytes.tables

import org.jetbrains.exposed.sql.Table

object NotificationTable : Table("notifications") {
    val id = long("id").autoIncrement()
    val message = varchar("message", 255)
    val timestamp = long("timestamp")
    val userId = long("user_id").references(UserTable.id)

    override val primaryKey = PrimaryKey(id)
}