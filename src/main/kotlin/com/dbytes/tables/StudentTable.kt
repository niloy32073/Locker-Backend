package com.dbytes.tables

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object StudentTable : Table("students") {
    val id = long("id").autoIncrement()
    val userId = reference("user_id", UserTable.id, ReferenceOption.CASCADE)
    val academicID = long("academic_id")
    val graduationYear = integer("graduation_year")

    override val primaryKey = PrimaryKey(id)
}