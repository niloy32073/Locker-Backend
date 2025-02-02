package com.dbytes.interfaces

import com.dbytes.models.User
import com.dbytes.models.UserPersonalInfo

interface UserRepository {
    suspend fun getUserById(id: Long):User?
    suspend fun getUserRoleById(id:Long):String?
    suspend fun getUserStatusById(id:Long):String?
}