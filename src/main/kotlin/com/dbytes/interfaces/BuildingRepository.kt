package com.dbytes.interfaces

import com.dbytes.models.Building

interface BuildingRepository {
    suspend fun createBuilding(building: Building):Long
    suspend fun getAllBuilding():List<Building>
    suspend fun findBuildingById(id:Long): Building?
    suspend fun deleteBuilding(id:Long)
    suspend fun updateBuilding(building:Building)
}