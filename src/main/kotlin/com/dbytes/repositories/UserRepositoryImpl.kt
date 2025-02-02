package com.dbytes.repositories

import com.dbytes.interfaces.UserRepository
import com.dbytes.models.User
import com.dbytes.models.UserPersonalInfo
import com.dbytes.tables.UserTable
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class UserRepositoryImpl:UserRepository {
    override suspend fun getUserById(id: Long): User? = transaction {
        UserTable.selectAll().where { UserTable.id eq id }.map {
            User(
                id = it[UserTable.id],
                name = it[UserTable.name],
                email = it[UserTable.email],
                phone = it[UserTable.phone],
                roles =  it[UserTable.roles],
                password =  it[UserTable.password],
                status =  it[UserTable.status],
                firebaseToken =  it[UserTable.firebaseToken],
            )
        }.singleOrNull()
    }

    override suspend fun getUserRoleById(id: Long): String? = transaction {
        UserTable.select(UserTable.roles).where { UserTable.id eq id }.map{it[UserTable.roles]}.singleOrNull()
    }

    override suspend fun getUserStatusById(id: Long): String? =  transaction {
        UserTable.select(UserTable.status).where { UserTable.id eq id }.map{it[UserTable.status]}.singleOrNull()
    }

}