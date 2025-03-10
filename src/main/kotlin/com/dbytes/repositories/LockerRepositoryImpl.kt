package com.dbytes.repositories

import com.dbytes.interfaces.LockerRepository
import com.dbytes.models.Locker
import com.dbytes.models.LockerStatusUpdateInfo
import com.dbytes.models.Notification
import com.dbytes.models.Reservation
import com.dbytes.tables.LockerTable
import com.dbytes.tables.NotificationTable
import com.dbytes.tables.ReservationTable
import com.dbytes.tables.UserTable
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime

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
            ReservationTable.update({ReservationTable.lockerId eq id}){
                it[ReservationTable.status] = "RELEASED"
            }
        }
    }

    override suspend fun getAllReservations(): List<Reservation> = transaction {
        ReservationTable.selectAll().map { Reservation(id = it[ReservationTable.id],userId = it[ReservationTable.userId],status = it[ReservationTable.status], startDate = it[ReservationTable.startDate], endDate = it[ReservationTable.endDate], lockerID = it[ReservationTable.lockerId]) }
    }

    override suspend fun findReservationsById(id: Long): Reservation? = transaction {
        ReservationTable.selectAll().where(ReservationTable.id eq id).map {
            Reservation(id = it[ReservationTable.id],userId = it[ReservationTable.userId],status = it[ReservationTable.status], startDate = it[ReservationTable.startDate], endDate = it[ReservationTable.endDate], lockerID = it[ReservationTable.lockerId])
        }.singleOrNull()
    }

    override suspend fun getAllReservationsByStatus(status: String): List<Reservation> = transaction {
        ReservationTable.selectAll().where(ReservationTable.status eq status).map {
            Reservation(id = it[ReservationTable.id],userId = it[ReservationTable.userId],status = it[ReservationTable.status], startDate = it[ReservationTable.startDate], endDate = it[ReservationTable.endDate], lockerID = it[ReservationTable.lockerId])
        }
    }

    override suspend fun getAllReservationsById(id: Long): List<Reservation> = transaction {
        ReservationTable.selectAll().where(ReservationTable.userId eq id).map {
            Reservation(id = it[ReservationTable.id],userId = it[ReservationTable.userId],status = it[ReservationTable.status], startDate = it[ReservationTable.startDate], endDate = it[ReservationTable.endDate], lockerID = it[ReservationTable.lockerId])
        }
    }

    override suspend fun updateReservationStatus(id: Long, status: String) {
        transaction {
            ReservationTable.update({ ReservationTable.id eq id }) {
                it[ReservationTable.status] = status
            }
        }
    }

    override suspend fun releaseExpiredReservation() {
        transaction {
                    val now = System.currentTimeMillis()
                    val expiredReservations = ReservationTable.selectAll()
                        .where { ReservationTable.endDate less now and (ReservationTable.status eq "APPROVED") }
                        .toList()
                    expiredReservations.forEach { reservation ->
                        val reservationId = reservation[ReservationTable.id].toLong()
                        val lockerId = reservation[ReservationTable.lockerId].toLong()
                        val userId = reservation[ReservationTable.userId].toLong()
                        val adminId = UserTable.selectAll().where { UserTable.roles eq "ADMIN" }.map {
                            it[UserTable.id]
                        }.singleOrNull()

                            ReservationTable.update({ ReservationTable.id eq reservationId }) {
                                it[ReservationTable.status] = "CLOSED"
                            }
                            LockerTable.update({ LockerTable.id eq lockerId } ) {
                                it[LockerTable.status] = "AVAILABLE"}

                        NotificationTable.insert {
                            it[message] = "Reservation ${reservationId} for Locker ${reservation[ReservationTable.lockerId].toLong()} has expired and been automatically closed."
                            it[NotificationTable.userId] = userId
                            it[timestamp] = System.currentTimeMillis()
                        }
                        if (adminId != null) {
                            NotificationTable.insert {
                                it[message] =
                                    "Reservation ${reservationId} for Locker ${reservation[ReservationTable.lockerId].toLong()} has expired and been automatically closed."
                                it[NotificationTable.userId] = adminId
                                it[timestamp] = System.currentTimeMillis()
                            }
                        }
                    }
                }
            }

}