package com.dbytes.tables

import org.jetbrains.exposed.sql.Table

object StudentTable : Table("students") {
    val id = long("id").autoIncrement()
    val userId = long("user_id")
    val academicID = long("academic_id")
    val graduationYear = integer("graduation_year")

    override val primaryKey = PrimaryKey(id)
}