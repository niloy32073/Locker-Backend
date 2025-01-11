package com.dbytes.services

import com.dbytes.interfaces.AuthRepository
import com.dbytes.models.User
import com.dbytes.models.UserSignInInfo
import java.lang.IllegalArgumentException

class AuthServices(private val authRepository: AuthRepository) {
    suspend fun registerUser(user: User):User {
        val existingUser = authRepository.findUserByEmail(user.email)
        if (existingUser != null) {
            throw IllegalArgumentException("User with ${existingUser.email} already exists.")
        }

        val newUser = authRepository.createUser(user)
        return newUser
    }

    suspend fun loginUser(userSignInInfo: UserSignInInfo):User {
        val existingUser = authRepository.findUserByEmail(userSignInInfo.email)
            ?: throw IllegalArgumentException("User doesn't exist with ${userSignInInfo.email}.")
        if (existingUser.password != userSignInInfo.password)
            throw IllegalArgumentException("Incorrect password.")
        else
            return existingUser
    }
}