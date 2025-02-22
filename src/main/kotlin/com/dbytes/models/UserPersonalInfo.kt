package com.dbytes.models

import kotlinx.serialization.Serializable

@Serializable
data class UserPersonalInfo(val name: String, val email: String, val phone:String, val roles: String,val status:String)