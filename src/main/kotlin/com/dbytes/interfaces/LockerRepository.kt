package com.dbytes.interfaces

import com.dbytes.models.Locker
import com.dbytes.models.LockerStatusUpdateInfo
import com.dbytes.models.Reservation

interface LockerRepository {
    suspend fun createLocker(locker:Locker):Long
    suspend fun findLockerById(id:Long):Locker?
    suspend fun deleteLocker(id:Long)
    suspend fun updateLockerStatus(lockerStatusUpdateInfo: LockerStatusUpdateInfo):Locker
}