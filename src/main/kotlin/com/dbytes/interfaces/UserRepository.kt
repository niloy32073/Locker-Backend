package com.dbytes.interfaces

import com.dbytes.models.Student
import com.dbytes.models.User
import com.dbytes.models.UserPersonalInfo

interface UserRepository {
    suspend fun getUserById(id: Long):User?
    suspend fun getUserRoleById(id:Long):String?
    suspend fun getUserStatusById(id:Long):String?
    suspend fun getUserFirebaseTokenById(id:Long):String?
    suspend fun deleteUserById(id:Long)
    suspend fun updateUserStatusById(id:Long, status:String)
    suspend fun checkUserPasswordById(id:Long,oldPassword:String):Boolean
    suspend fun changeUserPasswordById(id:Long, newPassword: String)
    suspend fun createStudent(id: Long,student: Student)
    suspend fun updateUserNameById(id:Long, name:String)
    suspend fun updateUserPhoneById(id:Long, phone:String)
    suspend fun updateUserFirebaseTokenById(id:Long, firebaseToken:String)
}