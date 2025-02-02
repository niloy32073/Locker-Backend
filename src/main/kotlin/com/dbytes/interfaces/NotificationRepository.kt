package com.dbytes.interfaces

import com.dbytes.models.Notification

interface NotificationRepository {
    suspend fun sendNotification(notification: Notification)
}