package com.dbytes.repositories

import com.dbytes.interfaces.NotificationRepository
import com.dbytes.models.Notification

class NotificationRepositoryImpl:NotificationRepository {
    override suspend fun sendNotification(notification: Notification) {
        TODO("Not yet implemented")
    }

}