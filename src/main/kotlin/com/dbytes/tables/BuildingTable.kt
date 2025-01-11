package com.dbytes.tables

import org.jetbrains.exposed.sql.Table

object BuildingTable : Table("buildings") {
    val id = long("id").autoIncrement()
    val location = varchar("location", 255)
    val name = varchar("name", 100)
    val totalLocker = integer("total_locker")

    override val primaryKey = PrimaryKey(id)
}