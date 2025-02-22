package com.dbytes.interfaces

import com.dbytes.models.Notification

interface NotificationRepository {
    suspend fun addNotification(notification: Notification)
    suspend fun getNotifications(id:Long): List<Notification>
}