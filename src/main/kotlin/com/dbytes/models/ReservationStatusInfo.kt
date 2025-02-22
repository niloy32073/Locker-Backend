package com.dbytes.models

import kotlinx.serialization.Serializable

@Serializable
data class ReservationStatusInfo(
    val id: Long,
    val status: String
)
