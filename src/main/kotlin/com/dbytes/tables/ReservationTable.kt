package com.dbytes.tables

import org.jetbrains.exposed.sql.Table

object ReservationTable : Table("reservations") {
    val id = long("id").autoIncrement()
    val userId = long("user_id").references(UserTable.id)
    val startDate = long("start_date")
    val endDate = long("end_date")
    val lockerId = long("locker_id").references(LockerTable.id)
    val status = varchar("status", 50)
    override val primaryKey = PrimaryKey(id)
}