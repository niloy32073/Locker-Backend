package com.dbytes.tables

import org.jetbrains.exposed.sql.Table

object LockerTable : Table("lockers") {
    val id = long("id").autoIncrement()
    val buildingId = long("building_id").references(BuildingTable.id)
    val status = varchar("status", 50)
    val type = varchar("type", 50)

    override val primaryKey = PrimaryKey(id)
}