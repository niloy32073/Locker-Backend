package com.dbytes.models

import kotlinx.serialization.Serializable

@Serializable
data class ChangePasswordInfo(val oldPassword:String, val newPassword:String)