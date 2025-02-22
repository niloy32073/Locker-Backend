package com.dbytes.models

import kotlinx.serialization.Serializable

@Serializable
data class Building(val id:Long, val location:String, val name:String, val totalLocker:Int)
