package com.dbytes.models

import kotlinx.serialization.Serializable

@Serializable
data class Notification(val id:Long, val message:String,val timestamp:Long,val userId:Long)
