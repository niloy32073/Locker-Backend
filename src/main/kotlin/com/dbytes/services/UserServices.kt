package com.dbytes.services

import com.dbytes.interfaces.UserRepository
import com.dbytes.models.User

class UserServices(private val userRepository: UserRepository) {
    suspend fun getUserById(id: Long): User? {
        return userRepository.getUserById(id)
    }
    suspend fun getUserRoleById(id: Long): String? {
        return userRepository.getUserRoleById(id)
    }
    suspend fun getUserStatusById(id: Long): String?{
        return userRepository.getUserStatusById(id)
    }
}