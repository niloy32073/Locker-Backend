package com.dbytes.models

import kotlinx.serialization.Serializable

@Serializable
data class UserStatusUpdateInfo(val id: Long, val status: String)
