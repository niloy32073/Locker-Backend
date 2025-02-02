package com.dbytes.interfaces

import com.dbytes.models.User

interface AuthRepository {
    suspend fun createUser(user:User):User
    suspend fun findUserByEmail(email:String):User?
}
