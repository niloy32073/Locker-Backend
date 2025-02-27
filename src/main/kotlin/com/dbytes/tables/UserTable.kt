package com.dbytes.tables

import org.jetbrains.exposed.sql.Table

object UserTable : Table("Users") {
    val id = long("id").autoIncrement()
    val name = varchar("name", 255)
    val email = varchar("email", 255).uniqueIndex()
    val phone = varchar("phone", 20)
    val roles = varchar("roles", 255)
    val password = varchar("password", 255)
    val status = varchar("status", 50)
    val firebaseToken = varchar("firebaseToken", 255).nullable()
    override val primaryKey = PrimaryKey(id)
}