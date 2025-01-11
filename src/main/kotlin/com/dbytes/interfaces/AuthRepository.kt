package com.dbytes.interfaces

import com.dbytes.models.User
import com.dbytes.models.UserPersonalInfo
import com.dbytes.models.UserSignInInfo
import com.dbytes.models.UserSignUpInfo

interface AuthRepository {
    suspend fun createUser(user:User):User
    suspend fun findUserByEmail(email:String):User?
}
