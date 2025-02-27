package com.dbytes.models

import kotlinx.serialization.Serializable

@Serializable
data class UserLogInResult(val userId: Long,val roles: String,val status:String,val token: String)
