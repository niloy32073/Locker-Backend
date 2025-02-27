package com.dbytes.repositories

import com.dbytes.interfaces.LockerRepository
import com.dbytes.models.Locker
import com.dbytes.models.LockerStatusUpdateInfo
import com.dbytes.models.Reservation
import com.dbytes.tables.LockerTable
import com.dbytes.tables.ReservationTable
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

    override suspend fun reserveLocker(userId:Long,reservation: Reservation) {
        transaction { ReservationTable.insert {
            it[ReservationTable.userId] = userId
            it[ReservationTable.lockerId] = reservation.lockerID
            it[status] = reservation.status
            it[ReservationTable.endDate] = reservation.endDate
            it[ReservationTable.startDate] = reservation.startDate
        } }
    }

    override suspend fun releaseLocker(id: Long) {
        transaction {
            ReservationTable.update({ReservationTable.id eq id}){
                it[ReservationTable.status] = "RELEASED"
            }
        }
    }

    override suspend fun getAllReservations(): List<Reservation> = transaction {
        ReservationTable.selectAll().map { Reservation(id = it[ReservationTable.id],userId = it[ReservationTable.userId],status = it[ReservationTable.status], startDate = it[ReservationTable.startDate], endDate = it[ReservationTable.endDate], lockerID = it[ReservationTable.id]) }
    }

    override suspend fun findReservationsById(id: Long): Reservation? = transaction {
        ReservationTable.selectAll().where(ReservationTable.id eq id).map {
            Reservation(id = it[ReservationTable.id],userId = it[ReservationTable.userId],status = it[ReservationTable.status], startDate = it[ReservationTable.startDate], endDate = it[ReservationTable.endDate], lockerID = it[ReservationTable.id])
        }.singleOrNull()
    }

    override suspend fun getAllReservationsByStatus(status: String): List<Reservation> = transaction {
        ReservationTable.selectAll().where(ReservationTable.status eq status).map {
            Reservation(id = it[ReservationTable.id],userId = it[ReservationTable.userId],status = it[ReservationTable.status], startDate = it[ReservationTable.startDate], endDate = it[ReservationTable.endDate], lockerID = it[ReservationTable.id])
        }
    }

    override suspend fun updateReservationStatus(id: Long, status: String) {
        ReservationTable.update({ReservationTable.id eq id}){
            it[ReservationTable.status] = status
        }
    }

}