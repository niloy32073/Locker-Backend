package com.dbytes.services

import com.dbytes.interfaces.UserRepository
import com.dbytes.models.User

class UserServices(private val userRepository: UserRepository) {
    suspend fun getUserById(id: Long): User? {
        return userRepository.getUserById(id)
    }
    suspend fun getUserIdByRole(role: String):Long?{
        return userRepository.getUserIdByRole(role)
    }
    suspend fun getUserRoleById(id: Long): String? {
        return userRepository.getUserRoleById(id)
    }
    suspend fun getUserStatusById(id: Long): String?{
        return userRepository.getUserStatusById(id)
    }
    suspend fun checkUserPassword(id: Long, oldPassword: String): Boolean {
        return userRepository.checkUserPasswordById(id, oldPassword)
    }
    suspend fun updateUserPassword(id: Long, newPassword: String) {
        userRepository.changeUserPasswordById(id, newPassword)
    }
    suspend fun deleteUserById(id: Long) {
        userRepository.deleteUserById(id)
    }
    suspend fun updateUserStatusById(id: Long, status: String) {
        userRepository.updateUserStatusById(id, status)
    }
    suspend fun updateUserNameById(id: Long, name: String) {
        userRepository.updateUserNameById(id, name)
    }
    suspend fun updateUserPhoneById(id: Long, phone: String) {
        userRepository.updateUserPhoneById(id, phone)
    }
    suspend fun updateUserFirebaseTokenById(id: Long, firebaseToken: String) {
        userRepository.updateUserFirebaseTokenById(id, firebaseToken)
    }
    suspend fun getUserFirebaseTokenById(id: Long): String? {
        return userRepository.getUserFirebaseTokenById(id)
    }
    suspend fun getAllUsers(): List<User> {
        return userRepository.getAllUsers()
    }
}