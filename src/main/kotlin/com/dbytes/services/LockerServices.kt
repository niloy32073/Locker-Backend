package com.dbytes.services

import com.dbytes.interfaces.LockerRepository
import com.dbytes.models.Locker
import com.dbytes.models.LockerStatusUpdateInfo
import com.dbytes.models.Reservation
import org.jetbrains.exposed.sql.transactions.transaction

class LockerServices(private val lockerRepository: LockerRepository) {
    suspend fun createLocker(locker: Locker):Long{
        return lockerRepository.createLocker(locker)
    }

    suspend fun deleteLocker(id: Long){
        val locker = lockerRepository.findLockerById(id)
        if(locker != null){
            lockerRepository.deleteLocker(id)
        }
        else{
            throw IllegalArgumentException("Locker with id $id not found")
        }
    }

    suspend fun updateLockerStatus(lockerStatusUpdateInfo: LockerStatusUpdateInfo){
        val locker = lockerRepository.findLockerById(lockerStatusUpdateInfo.id)
        if(locker != null){
            lockerRepository.updateLockerStatus(lockerStatusUpdateInfo)
        }
        else{
            throw IllegalArgumentException("Locker with id ${lockerStatusUpdateInfo.id} not found")
        }
    }

    suspend fun getAllLocker(): List<Locker> {
        return lockerRepository.getAllLocker()
    }

    suspend fun  releaseLocker(id: Long){
        return lockerRepository.releaseLocker(id)
    }
    suspend fun  reserveLocker(userId:Long,reservation: Reservation){
        return lockerRepository.reserveLocker(userId, reservation)
    }
    suspend fun findReservationsById(id: Long): Reservation?{
        return lockerRepository.findReservationsById(id)
    }
    suspend fun getAllReservationsByStatus(status: String): List<Reservation>{
        return lockerRepository.getAllReservationsByStatus(status)
    }
    suspend fun getAllReservations(): List<Reservation>{
        return lockerRepository.getAllReservations()
    }
    suspend fun updateReservationStatus(id: Long, status: String) {
        lockerRepository.updateReservationStatus(id, status)
    }
}