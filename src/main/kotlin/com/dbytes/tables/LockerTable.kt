package com.dbytes.tables

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object LockerTable : Table("lockers") {
    val id = long("id").autoIncrement()
    val buildingId = reference("building_id", BuildingTable.id, ReferenceOption.CASCADE)
    val status = varchar("status", 50)
    val location = varchar("location", 255)
    val type = varchar("type", 50)

    override val primaryKey = PrimaryKey(id)
}