package com.dbytes.repositories

import com.dbytes.interfaces.UserRepository
import com.dbytes.models.Student
import com.dbytes.models.User
import com.dbytes.models.UserPersonalInfo
import com.dbytes.tables.StudentTable
import com.dbytes.tables.UserTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
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

    override suspend fun getUserIdByRole(role: String): Long? = transaction {
        UserTable.selectAll().where { UserTable.roles eq role }.map {
            it[UserTable.id]
        }.singleOrNull()
    }

    override suspend fun getUserRoleById(id: Long): String? = transaction {
        UserTable.select(UserTable.roles).where { UserTable.id eq id }.map{it[UserTable.roles]}.singleOrNull()
    }

    override suspend fun getUserStatusById(id: Long): String? =  transaction {
        UserTable.select(UserTable.status).where { UserTable.id eq id }.map{it[UserTable.status]}.singleOrNull()
    }

    override suspend fun getUserFirebaseTokenById(id: Long): String? = transaction{
        UserTable.select(UserTable.firebaseToken).where { UserTable.id eq id }.map{it[UserTable.firebaseToken]}.singleOrNull()
    }

    override suspend fun deleteUserById(id: Long) {
        transaction {
            UserTable.deleteWhere { UserTable.id eq id }
        }
    }

    override suspend fun updateUserStatusById(id: Long, status: String) {
         transaction {
             UserTable.update({ UserTable.id eq id }) {
                 it[UserTable.status] = status
             }
         }
    }

    override suspend fun checkUserPasswordById(id: Long, oldPassword: String): Boolean {
        val idExist = transaction {UserTable.select(UserTable.id).where { (UserTable.id eq id)  and (UserTable.password eq oldPassword)}.map {
            it[UserTable.id]
        }.singleOrNull()}
        return idExist != null
    }

    override suspend fun changeUserPasswordById(id: Long, newPassword: String) {
        transaction {
            UserTable.update({ UserTable.id eq id }) {
                it[UserTable.password] = newPassword
            }
        }

    }

    override suspend fun createStudent(id: Long, student: Student) {
        transaction {
            StudentTable.insert {
                it[userId] = id
                it[academicID] = student.academicID
                it[graduationYear] = student.graduationYear
            }
        }
    }

    override suspend fun updateUserNameById(id: Long, name: String) {
        transaction {
            UserTable.update({ UserTable.id eq id }) {
                it[UserTable.name] = name
            }
        }
    }

    override suspend fun updateUserPhoneById(id: Long, phone: String) {
        transaction {
            UserTable.update({ UserTable.id eq id }) {
                it[UserTable.phone] = phone
            }
        }
    }

    override suspend fun updateUserFirebaseTokenById(id: Long, firebaseToken: String) {
        transaction {
            UserTable.update({ UserTable.id eq id }) {
                it[UserTable.firebaseToken] = firebaseToken
            }
        }
    }

    override suspend fun getAllUsers(): List<User> = transaction {
        UserTable.selectAll().map {
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
        }
    }

}