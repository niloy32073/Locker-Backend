package com.dbytes.models

import kotlinx.serialization.Serializable

@Serializable
data class User(val id: Long? = null, val name: String, val email: String, val phone:String, val roles: String,val password:String,val status:String,val firebaseToken:String? = null)
