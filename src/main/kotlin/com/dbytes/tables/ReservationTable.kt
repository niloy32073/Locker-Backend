package com.dbytes.tables

import org.jetbrains.exposed.sql.Table

object ReservationTable : Table("reservations") {
    val id = long("id").autoIncrement()
    val userId = long("user_id").references(UserTable.id)
    val startDate = long("start_date")
    val endDate = long("end_date")
    val lockerId = long("locker_id").references(LockerTable.id)

    override val primaryKey = PrimaryKey(id)
}