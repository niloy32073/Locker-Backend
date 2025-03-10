package com.dbytes.interfaces

import com.dbytes.models.Locker
import com.dbytes.models.LockerStatusUpdateInfo
import com.dbytes.models.Notification
import com.dbytes.models.Reservation

interface LockerRepository {
    suspend fun createLocker(locker:Locker):Long
    suspend fun getAllLocker():List<Locker>
    suspend fun findLockerById(id:Long):Locker?
    suspend fun deleteLocker(id:Long)
    suspend fun updateLockerStatus(lockerStatusUpdateInfo: LockerStatusUpdateInfo):Locker
    suspend fun reserveLocker(userId:Long,reservation: Reservation)
    suspend fun releaseLocker(id: Long)
    suspend fun getAllReservations(): List<Reservation>
    suspend fun findReservationsById(id:Long):Reservation?
    suspend fun getAllReservationsByStatus(status:String): List<Reservation>
    suspend fun getAllReservationsById(id:Long): List<Reservation>
    suspend fun updateReservationStatus(id:Long,status:String)
    suspend fun releaseExpiredReservation()
}