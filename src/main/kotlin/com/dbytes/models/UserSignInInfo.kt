package com.dbytes.models

import kotlinx.serialization.Serializable

@Serializable
data class UserSignInInfo(val email: String,val password:String)
