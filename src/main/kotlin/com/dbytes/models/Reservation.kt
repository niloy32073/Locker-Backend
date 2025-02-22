package com.dbytes.models

import kotlinx.serialization.Serializable

@Serializable
data class Reservation(val id:Long, val userId:Long,val startDate:Long, val endDate:Long,val lockerID:Long, val status:String)
