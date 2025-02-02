package com.dbytes.models

import kotlinx.serialization.Serializable

@Serializable
data class Locker(val id:Long? = null, val buildingId:Long, val status:String, val type:String,val location:String)
