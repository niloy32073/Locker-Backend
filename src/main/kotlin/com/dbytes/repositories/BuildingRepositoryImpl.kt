package com.dbytes.repositories

import com.dbytes.interfaces.BuildingRepository
import com.dbytes.models.Building
import com.dbytes.tables.BuildingTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

class BuildingRepositoryImpl: BuildingRepository {
    override suspend fun createBuilding(building: Building): Long = transaction {
        BuildingTable.insert {
            it[location] = building.location
            it[name] = building.name
            it[totalLocker] = building.totalLocker
        } get BuildingTable.id
    }

    override suspend fun getAllBuilding(): List<Building> = transaction {
        BuildingTable.selectAll().map {
            Building(id = it[BuildingTable.id], location = it[BuildingTable.location], name = it[BuildingTable.name], totalLocker = it[BuildingTable.totalLocker])
        }
    }

    override suspend fun findBuildingById(id: Long): Building? = transaction {
        BuildingTable.selectAll().where{BuildingTable.id eq id }.map {
            Building(id = it[BuildingTable.id], location = it[BuildingTable.location], name = it[BuildingTable.name], totalLocker = it[BuildingTable.totalLocker])
        }.singleOrNull()
    }

    override suspend fun deleteBuilding(id: Long) {
        transaction {
            BuildingTable.deleteWhere { BuildingTable.id eq id }
        }
    }

    override suspend fun updateBuilding(building: Building) {
        transaction {
            BuildingTable.update({ BuildingTable.id eq building.id }) {
                it[location] = building.location
                it[name] = building.name
                it[totalLocker] = building.totalLocker
            }
        }
    }
}