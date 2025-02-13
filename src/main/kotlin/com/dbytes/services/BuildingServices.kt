package com.dbytes.services

import com.dbytes.interfaces.BuildingRepository
import com.dbytes.models.Building

class BuildingServices( private val buildingRepository: BuildingRepository) {
    suspend fun findBuildingById(id:Long): Building? {
        val building = buildingRepository.findBuildingById(id = id)
        return building
    }
    suspend fun deleteBuilding(id:Long){
        buildingRepository.deleteBuilding(id = id)
    }
    suspend fun createBuilding(building: Building):Long {
        val id = buildingRepository.createBuilding(building = building)
        return id
    }
    suspend fun getAllBuildings(): List<Building> {
        val buildings = buildingRepository.getAllBuilding()
        return buildings
    }
}