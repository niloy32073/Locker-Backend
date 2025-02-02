package com.dbytes.models

import kotlinx.serialization.Serializable

@Serializable
data class LockerStatusUpdateInfo(val id: Long, val status: String)
