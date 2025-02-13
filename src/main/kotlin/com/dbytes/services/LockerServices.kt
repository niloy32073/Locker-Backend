package com.dbytes.services

import com.dbytes.interfaces.LockerRepository
import com.dbytes.models.Locker
import com.dbytes.models.LockerStatusUpdateInfo
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
}