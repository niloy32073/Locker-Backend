package com.dbytes.repositories

import com.dbytes.interfaces.LockerRepository
import com.dbytes.models.Locker
import com.dbytes.models.LockerStatusUpdateInfo
import com.dbytes.models.Reservation
import com.dbytes.tables.LockerTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

class LockerRepositoryImpl: LockerRepository {
    override suspend fun createLocker(locker: Locker): Long = transaction {
        LockerTable.insert {
            it[buildingId] = locker.buildingId
            it[status] = locker.status
            it[location] = locker.location
            it[type] = locker.type
        } get LockerTable.id
    }

    override suspend fun getAllLocker(): List<Locker> = transaction {
        LockerTable.selectAll().map {
            Locker(id = it[LockerTable.id], buildingId = it[LockerTable.buildingId], status = it[LockerTable.status], location = it[LockerTable.location], type = it[LockerTable.type])
        }
    }

    override suspend fun findLockerById(id: Long): Locker? = transaction {
        LockerTable.selectAll().where(LockerTable.id eq id).map{
            Locker(id = it[LockerTable.id], buildingId = it[LockerTable.buildingId], status = it[LockerTable.status], location = it[LockerTable.location], type = it[LockerTable.type])
        }.singleOrNull()
    }

    override suspend fun deleteLocker(id: Long) {
        transaction { LockerTable.deleteWhere { LockerTable.id eq id } }
    }

    override suspend fun updateLockerStatus(lockerStatusUpdateInfo: LockerStatusUpdateInfo): Locker =
        transaction {
            LockerTable.update({ LockerTable.id eq lockerStatusUpdateInfo.id } ) {
            it[LockerTable.status] = lockerStatusUpdateInfo.status}
            LockerTable.selectAll().where { LockerTable.id eq lockerStatusUpdateInfo.id }.map{Locker(
                id = it[LockerTable.id],
                buildingId = it[LockerTable.buildingId],
                status = it[LockerTable.status],
                type = it[LockerTable.type],
                location = it[LockerTable.location]
            )}.single()
        }


}