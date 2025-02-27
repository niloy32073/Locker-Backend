package com.dbytes.repositories

import com.dbytes.interfaces.AuthRepository
import com.dbytes.models.User
import com.dbytes.models.UserSignInInfo
import com.dbytes.models.UserSignUpInfo
import com.dbytes.tables.UserTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class AuthRepositoryImpl : AuthRepository {
    override suspend fun createUser(user: User): User = transaction {
        val userId = UserTable.insert {
            it[name] = user.name
            it[email] = user.email
            it[password] = user.password
            it[phone] = user.phone
            it[roles] = user.roles
            it[status] = user.status
        } get UserTable.id
        user.copy(id = userId)
    }


    override suspend fun findUserByEmail(email: String): User? = transaction {
        UserTable.selectAll().where { UserTable.email eq email }.map{
            User(id = it[UserTable.id],name = it[UserTable.name], email = it[UserTable.email], phone = it[UserTable.phone],password = it[UserTable.password], roles = it[UserTable.roles],status = it[UserTable.status],firebaseToken = it[UserTable.firebaseToken])
        }.singleOrNull()
    }

}